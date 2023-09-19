package com.example.assignment.admin.report

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.assignment.R
import com.example.assignment.admin.news.AdminNewsViewModel
import com.example.assignment.databinding.FragmentAdminReportBinding

class AdminReportFragment : Fragment() {

    private var _binding: FragmentAdminReportBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val AdminReportViewModel =
            ViewModelProvider(this).get(AdminReportViewModel::class.java)

        _binding = FragmentAdminReportBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.text1
        AdminReportViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}