package com.example.assignment.admin.user

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import com.example.assignment.R
import com.example.assignment.admin.donate.AdminDonateViewModel
import com.example.assignment.databinding.FragmentAdminUserBinding

class AdminUserFragment : Fragment() {

    private var _binding: FragmentAdminUserBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val AdminUserViewModel =
            ViewModelProvider(this).get(AdminUserViewModel::class.java)

        _binding = FragmentAdminUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.text1
        AdminUserViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}