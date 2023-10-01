package com.example.assignment.admin.user

import android.app.AlertDialog
import android.content.Intent
import android.media.Image
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.assignment.R
import com.example.assignment.admin.donate.AdminDonateViewModel
import com.example.assignment.databinding.FragmentAdminUserBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.admin.donate.AdminDonateFragment
import com.example.assignment.admin.news.AdminNewsFragment
import com.example.assignment.admin.report.AdminReportFragment
import com.example.assignment.admin.volunteer.AdminVolunteerFragment
import com.example.assignment.admin.volunteer.test
import com.example.assignment.database.user.User
import com.example.assignment.database.user.UserDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class AdminUserFragment : Fragment() {

    private var _binding: FragmentAdminUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView : RecyclerView
    private lateinit var searchView : SearchView
    private lateinit var radioAllUser : RadioButton
    private lateinit var radioAdmin : RadioButton
    private lateinit var radioIndividual : RadioButton
    private lateinit var radioOrganization : RadioButton

    //private  var userList = ArrayList<User>()
    private lateinit var adapter: UserAdapter

    private lateinit var appDb : UserDatabase

    lateinit var usersList : List<User>

    lateinit var profile : ImageView

    private lateinit var type : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val AdminUserViewModel =
            ViewModelProvider(this).get(AdminUserViewModel::class.java)

        _binding = FragmentAdminUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.text1
//        AdminUserViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

//        val button1 : Button = binding.button2
//        button1.setOnClickListener {
//            findNavController().navigate(R.id.action_fragmentA_to_fragmentB)
//        }


        appDb = UserDatabase.getDatabase(requireContext())
        recyclerView = binding.userRecycleView
        searchView = binding.adminUserSearch
        radioAllUser = binding.radioButtonAllUser
        radioAdmin = binding.radioButtonAdmin
        radioIndividual = binding.radioButtonIndividual
        radioOrganization = binding.radioButtonOrganization



        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        getAll()
        //getAdmin("Admin")

        radioAllUser.setOnClickListener{
            getUser("All")
        }
        radioAdmin.setOnClickListener {
            getUser("Admin")
        }
        radioIndividual.setOnClickListener{
            getUser("Individual")
        }
        radioOrganization.setOnClickListener{
            getUser("Organization")
        }






        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterList(newText)
                return true
            }

        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun filterList(query :String){
        if(query != null){
            val filteredList = ArrayList<User>()
            for(i in usersList){
                if(i.userName?.lowercase(Locale.ROOT)?.contains(query) == true) {
                    filteredList.add(i)
                }
            }

            if(filteredList.isEmpty()){
                Toast.makeText(requireContext(), "No User Found", Toast.LENGTH_LONG).show()
            }else{
                adapter.setFilteredList(filteredList)
            }
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val button1 = view.findViewById<FloatingActionButton>(R.id.moveAddFragment)
        button1.setOnClickListener {
            loadFragment(AdminUserAddFragment())
        }
    }


    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    private fun getAll() {

        GlobalScope.launch {
            var users = appDb.userDao().getAll()
            // Handle the list of students as needed (e.g., update the UI)
            usersList = users

            adapter = UserAdapter(usersList) { type, user ->
                onActionClick(type, user)
            }
            recyclerView.adapter = adapter
        }
    }

    private fun getUser(role: String) {

        val adminUsers = mutableListOf<User>()

        if(role == "All"){
            for (user in usersList) {
                adminUsers.add(user)
            }
        }else{
            for (user in usersList) {
                if (user.role == role) {
                    adminUsers.add(user)
                }
            }
        }


        adapter = UserAdapter(adminUsers) { type, user ->
            onActionClick(type, user)
        }
        recyclerView.adapter = adapter
    }


    private fun onActionClick(type : String, user : User){
        if(type == "delete"){
            AlertDialog.Builder(requireContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes") { _, _ ->
                    // Delete the user from the Room database
                    GlobalScope.launch {
                        appDb.userDao().delete(user)
                        // Refresh the RecyclerView after deletion
                        //getAll()
                    }

                    loadFragment(AdminUserFragment())
                }
                .setNegativeButton("No", null)
                .show()
        }else if(type == "view"){

            val bundle = Bundle()
            bundle.putString("id", user.id.toString())
            bundle.putString("name", user.userName)
            bundle.putString("role", user.role)
            bundle.putString("profile", user.profile)
            val fragmentUserDetail = AdminUserDetailFragment()
            fragmentUserDetail.arguments = bundle

            loadFragment(fragmentUserDetail)

        }


    }

}