package com.studioartagonist.ems

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.studioartagonist.ems.databinding.FragmentEmployeeDetailsBinding


class EmployeeDetailsFragment : Fragment() {
    private lateinit var binding: FragmentEmployeeDetailsBinding
    private val viewModel: EmployeeViewModel by activityViewModels()
    private var id = ""
    val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val bitmap = it.data?.extras?.get("data") as Bitmap
            viewModel.uploadImage(id, bitmap)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            resultLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmployeeDetailsBinding.inflate(inflater, container, false)
        id = arguments?.getString("id")!!
        viewModel.errMsgLD.observe(viewLifecycleOwner) {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
        viewModel.getEmployeeById(id).observe(viewLifecycleOwner) {
            binding.employee = it
        }

        binding.captureBtn.setOnClickListener {
            dispatchTakePictureIntent()
        }
        return binding.root
    }

}