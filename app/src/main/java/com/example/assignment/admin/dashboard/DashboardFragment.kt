package com.example.assignment.admin.dashboard

import android.R.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.assignment.R
import com.example.assignment.admin.donate.AdminDonateFragment
import com.example.assignment.admin.news.AdminNewsFragment
import com.example.assignment.admin.report.AdminReportFragment
//import com.example.assignment.admin.user.AdminUserFragment
import com.example.assignment.admin.user.AdminUserDetailFragment
import com.example.assignment.admin.user.AdminUserFragment
import com.example.assignment.admin.volunteer.AdminVolunteerFragment
import com.example.assignment.databinding.FragmentDashboardBinding
import com.google.android.material.navigation.NavigationView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val DashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.text1

        DashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var fragment = parentFragmentManager.findFragmentById(R.id.fragment_container)


        binding.dashboardUserBtn.setOnClickListener {
            if (savedInstanceState == null) {
                parentFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, AdminUserFragment()).commit()


                binding.dashboardDonateBtn.setOnClickListener {
                    if (savedInstanceState == null) {
                        parentFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.fragment_container, AdminDonateFragment()).commit()
                    }
                }

                binding.dashboardVolunteerBtn.setOnClickListener {
                    if (savedInstanceState == null) {
                        parentFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.fragment_container, AdminVolunteerFragment()).commit()
                    }
                }

                binding.dashboardNewsBtn.setOnClickListener {
                    if (savedInstanceState == null) {
                        parentFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.fragment_container, AdminNewsFragment()).commit()
                    }
                }

                binding.dashboardReportBtn.setOnClickListener {
                    if (savedInstanceState == null) {
                        parentFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.fragment_container, AdminReportFragment()).commit()
                    }
                }
            }
        }
    }
}