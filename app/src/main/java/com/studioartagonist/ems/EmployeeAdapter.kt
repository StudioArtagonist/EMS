package com.studioartagonist.ems

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studioartagonist.ems.databinding.EmployeeRowBinding

class EmployeeAdapter(val callback: (String) -> Unit) : ListAdapter<Employee, EmployeeAdapter.EmployeeViewHolder>(EmployeeDiffUtil()){

    class EmployeeViewHolder(val binding: EmployeeRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: Employee) {
            binding.employee = employee
        }
    }

    class EmployeeDiffUtil : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = EmployeeRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = getItem(position)
        holder.bind(employee)
        holder.binding.empRow.setOnClickListener {
            callback(employee.id)
        }
    }
}