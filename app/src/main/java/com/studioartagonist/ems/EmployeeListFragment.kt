package com.studioartagonist.ems

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.studioartagonist.ems.databinding.FragmentEmployeeListBinding


class EmployeeListFragment : Fragment() {
    private val viewModel: EmployeeViewModel by activityViewModels()
    private lateinit var binding: FragmentEmployeeListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_logout -> viewModel.logout()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmployeeListBinding.inflate(inflater, container, false)

        val adapter = EmployeeAdapter{
            val bundle = bundleOf("id" to it)
            findNavController().navigate(R.id.action_employeeListFragment_to_employeeDetailsFragment, bundle)
        }
        binding.empRV.layoutManager = LinearLayoutManager(requireActivity())
        binding.empRV.adapter = adapter
        viewModel.authLD.observe(viewLifecycleOwner) {
            if (it == EmployeeViewModel.Auth.UNAUTHENTICATED) {
                findNavController().navigate(R.id.action_employeeListFragment_to_loginFragment)
            }
        }

        viewModel.errMsgLD.observe(viewLifecycleOwner) {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.getAllEmployees().observe(viewLifecycleOwner) {
            Toast.makeText(requireActivity(), "${it.size}", Toast.LENGTH_SHORT).show()
            if (it.isEmpty()) {
                binding.emptyListMsgTV.visibility = View.VISIBLE
            }else {
                binding.emptyListMsgTV.visibility = View.GONE
            }
            adapter.submitList(it)
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_employeeListFragment_to_newEmployeeFragment)
        }
        return binding.root
    }

}