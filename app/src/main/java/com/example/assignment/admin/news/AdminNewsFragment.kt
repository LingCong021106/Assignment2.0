package com.example.assignment.admin.news

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.assignment.R
import com.example.assignment.admin.donate.AdminDonateViewModel
import com.example.assignment.databinding.FragmentAdminNewsBinding

class AdminNewsFragment : Fragment() {

    private var _binding: FragmentAdminNewsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val AdminNewsViewModel =
            ViewModelProvider(this).get(AdminNewsViewModel::class.java)

        _binding = FragmentAdminNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.text1
        AdminNewsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}