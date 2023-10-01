package com.example.assignment.admin.volunteer

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.assignment.R
import com.example.assignment.admin.donate.AdminDonateViewModel
import com.example.assignment.databinding.FragmentAdminVolunteerBinding

class AdminVolunteerFragment : Fragment() {

    private var _binding: FragmentAdminVolunteerBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val AdminVolunteerViewModel =
            ViewModelProvider(this).get(AdminVolunteerViewModel::class.java)

        _binding = FragmentAdminVolunteerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.text1
        AdminVolunteerViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val bundle = Bundle()
        bundle.putString("test", "123")
        val fragmentB = test()
        fragmentB.arguments = bundle


        loadFragment(fragmentB)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

}