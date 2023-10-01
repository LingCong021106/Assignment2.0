package com.example.assignment.admin.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.assignment.databinding.FragmentDashboardBinding
import androidx.navigation.NavController
import com.example.assignment.R
import androidx.navigation.fragment.findNavController
import com.example.assignment.admin.donate.AdminDonateFragment
import com.example.assignment.admin.news.AdminNewsFragment
import com.example.assignment.admin.report.AdminReportFragment
//import com.example.assignment.admin.user.AdminUserFragment
import com.example.assignment.admin.volunteer.AdminVolunteerFragment

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

    private lateinit var navController: NavController



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

//
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


//        val button1 = view.findViewById<Button>(R.id.dashboardUserBtn)
//        button1.setOnClickListener {
//            loadFragment(AdminUserFragment())
//        }

        val button2 = view.findViewById<Button>(R.id.dashboardDonateBtn)
        button2.setOnClickListener {
            loadFragment(AdminDonateFragment())
        }

        val button3 = view.findViewById<Button>(R.id.dashboardVolunteerBtn)
        button3.setOnClickListener {
            loadFragment(AdminVolunteerFragment())
        }

        val button4 = view.findViewById<Button>(R.id.dashboardNewsBtn)
        button4.setOnClickListener {
            loadFragment(AdminNewsFragment())
        }

        val button5 = view.findViewById<Button>(R.id.dashboardReportBtn)
        button5.setOnClickListener {
            loadFragment(AdminReportFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}