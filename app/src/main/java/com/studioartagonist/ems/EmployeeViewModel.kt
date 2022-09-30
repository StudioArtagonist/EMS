package com.studioartagonist.ems

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EmployeeViewModel : ViewModel() {
    private val collectionEmployee = "Employees"
    private val db = FirebaseFirestore.getInstance()
    enum class Auth {
        AUTHENTICATED, UNAUTHENTICATED
    }
    val fireAuth = FirebaseAuth.getInstance()
    var user: FirebaseUser? = fireAuth.currentUser
    val authLD : MutableLiveData<Auth> = MutableLiveData()
    val errMsgLD : MutableLiveData<String> = MutableLiveData()

    init {
        if (user != null) {
            authLD.value = Auth.AUTHENTICATED
        }else {
            authLD.value = Auth.UNAUTHENTICATED
        }
    }

    fun login(email: String, password: String) {
        fireAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    authLD.value = Auth.AUTHENTICATED
                    user = fireAuth.currentUser
                }
            }
            .addOnFailureListener {
                errMsgLD.value = it.localizedMessage
            }
    }

    fun insertEmployee(employee: Employee, callback: () -> Unit) {
        val docRef = db.collection(collectionEmployee).document()
        employee.id = docRef.id
        docRef.set(employee)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                errMsgLD.value = "could not add"
            }
    }

    fun getAllEmployees() : LiveData<List<Employee>> {
        val empListLD: MutableLiveData<List<Employee>> = MutableLiveData()
        db.collection(collectionEmployee)
            //.whereEqualTo("department", "Marketing")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    errMsgLD.value = error.localizedMessage
                    return@addSnapshotListener
                }
                val tempList = mutableListOf<Employee>()
                for (doc in value!!.documents) {
                    tempList.add(doc.toObject(Employee::class.java)!!)
                }
                empListLD.value = tempList
            }
        return empListLD
    }

    fun getEmployeeById(id: String) : LiveData<Employee> {
        val empLD: MutableLiveData<Employee> = MutableLiveData()
        db.collection(collectionEmployee)
            .document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    errMsgLD.value = error.localizedMessage
                    return@addSnapshotListener
                }
                empLD.value = value!!.toObject(Employee::class.java)
            }
        return empLD
    }

    fun logout() {
        if (user != null) {
            fireAuth.signOut()
            authLD.value = Auth.UNAUTHENTICATED
        }
    }

    fun isEmailVerified() : Boolean = user!!.isEmailVerified
    fun uploadImage(id: String, bitmap: Bitmap) {
        val photoRef = FirebaseStorage.getInstance().reference
            .child("images/${System.currentTimeMillis()}")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos)
        val data: ByteArray = baos.toByteArray()
        val uploadTask = photoRef.putBytes(data)
        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    errMsgLD.value = it.localizedMessage
                    throw it
                }
            }
            photoRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                updateEmployeeImage(downloadUri, id)
            } else {
                // Handle failures
                // ...
            }
        }

    }

    private fun updateEmployeeImage(downloadUri: String, id: String) {
        val docRef = db.collection(collectionEmployee).document(id)
        docRef.update("imageUrl", downloadUri)
            .addOnFailureListener {
                errMsgLD.value = it.localizedMessage
            }
            .addOnSuccessListener {

            }
    }
}