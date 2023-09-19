package com.example.assignment.admin.donate

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.assignment.R
import com.example.assignment.admin.dashboard.DashboardViewModel
import com.example.assignment.databinding.FragmentAdminDonateBinding

class AdminDonateFragment : Fragment() {

    private var _binding: FragmentAdminDonateBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val AdminDonateViewModel =
            ViewModelProvider(this).get(AdminDonateViewModel::class.java)

        _binding = FragmentAdminDonateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.text1
        AdminDonateViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}