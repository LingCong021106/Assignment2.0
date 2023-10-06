package com.example.assignment.admin.user

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.admin.event.AdminEventFragment
import com.example.assignment.database.AppDatabase
import com.example.assignment.databinding.FragmentAdminUserBinding
import com.example.assignment.databinding.FragmentUserDonateBinding
import com.example.assignment.databinding.FragmentUserEventBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentAdminUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var appDb: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var radioAllUser: RadioButton
    private lateinit var radioAdmin: RadioButton
    private lateinit var radioIndividual: RadioButton
    private lateinit var radioOrganization: RadioButton
    private lateinit var addOrgButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private val URL: String = "http://10.0.2.2/Assignment(Mobile)/getAllUser.php"
    private val deleteUrl: String = "http://10.0.2.2/Assignment(Mobile)/deleteUser.php"
    private val imageList: ArrayList<String> = ArrayList()
    private val userNameList: ArrayList<String> = ArrayList()
    private val userEmailList: ArrayList<String> = ArrayList()
    private lateinit var newArrayList: ArrayList<ListUsers>
    private lateinit var role: String
    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminUserBinding.inflate(inflater, container, false)
        val view : View = binding.root

        progressBar = view.findViewById(R.id.progressBar)
        appDb = AppDatabase.getInstance(requireContext())
        val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), PERMISSION_REQUEST_CODE)
        }
        role = "all"
        newArrayList = arrayListOf<ListUsers>()
        recyclerView = view.findViewById(R.id.userRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = UserAdapter(newArrayList, role) { type, user ->
            onActionClick(type, user)

        }

        addOrgButton = view.findViewById(R.id.addOrgBtn)
//        deleteBtn = findViewById(R.id.userDeleteBtn)
        addOrgButton.visibility = View.GONE
        radioAllUser = view.findViewById(R.id.radioButtonAllUser)
        radioAdmin = view.findViewById(R.id.radioButtonAdmin)
        radioIndividual = view.findViewById(R.id.radioButtonIndividual)
        radioOrganization = view.findViewById(R.id.radioButtonOrganization)

        radioAllUser.setOnClickListener {
            role = "all"
//            deleteBtn.visibility = View.VISIBLE
            addOrgButton.visibility = View.GONE
            getUsers()

        }
        radioAdmin.setOnClickListener {
            role = "admin"
//            deleteBtn.visibility = View.GONE
            addOrgButton.visibility = View.GONE
            getUsers()

        }

        radioIndividual.setOnClickListener {
            role = "individual"
//            deleteBtn.visibility = View.VISIBLE
            addOrgButton.visibility = View.GONE
            getUsers()

        }

        radioOrganization.setOnClickListener {
            role = "organization"
//            deleteBtn.visibility = View.VISIBLE
            addOrgButton.visibility = View.VISIBLE
            getUsers()

        }

        addOrgButton.setOnClickListener{
            var intent = Intent(context, com.example.assignment.AddOrganization()::class.java)
            startActivity(intent)
        }

        val stringRequest: StringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST, URL,
            Response.Listener { response ->
                Log.d("res", response)
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("status")
                    if (message == "success") {
                        val dataArray = jsonObject.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)
                            val userName = dataObject.getString("userName").toString()
                            val userEmail = dataObject.getString("userEmail").toString()
                            val photo = dataObject.getString("photo").toString()

                            Log.d(
                                "UserData",
                                "UserName: $userName, UserEmail: $userEmail, Photo: $photo"
                            )
                            userNameList.add(userName)
                            userEmailList.add(userEmail)
                            imageList.add(photo)
                        }
                        getUserdata()
                    } else if (message == "failure") {
                        Toast.makeText(
                            context,
                            "Invalid Id",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Handle JSON parsing error here
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    context,
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()

            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                data["role"] = role
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)

        progressBar.visibility= View.GONE

        searchView = view.findViewById(R.id.adminUserSearch)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission (if needed)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query changes and filter the RecyclerView data
                filterRecyclerViewData(newText)
                return true
            }
        })

        return view
    }

    private fun filterRecyclerViewData(query: String?) {
        // Filter your RecyclerView data here based on the query
        val filteredList = ArrayList<ListUsers>()

        // Example: Filter based on user name
        for (user in newArrayList) {
            if (user.name.toLowerCase(Locale.getDefault())
                    .contains(query?.toLowerCase(Locale.getDefault()) ?: "")
            ) {
                filteredList.add(user)
            }
        }

        recyclerView.adapter = UserAdapter(filteredList, role) { type, user ->
            onActionClick(type, user)
        }

    }


    fun onActionClick(type: String, users: ListUsers) {

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Confirm Delete")
        alertDialogBuilder.setMessage("Are you sure you want to delete this user?")
        alertDialogBuilder.setPositiveButton("Delete") { dialog, which ->
            val email = users.email
            Log.d("email",email)
            val stringRequest: StringRequest = object : StringRequest(
                com.android.volley.Request.Method.POST, deleteUrl,
                Response.Listener { response ->
                    Log.d("res", response)
                    try {
                        val jsonObject = JSONObject(response)
                        val message = jsonObject.getString("status")
                        val logmeesage =jsonObject.getString("message")
                        if (message == "success") {
                            CoroutineScope(Dispatchers.IO).launch {
                                val user = appDb.userDao().getUserByEmail(email)
                                val admin = appDb.adminDao().getAdminByEmail(email)

                                if(user!= null){
                                    appDb.userDao().deleteuser(email)
                                }else if(admin!=null){
                                    appDb.adminDao().deleteorganization(email)
                                }else{
                                    activity?.runOnUiThread {
                                        Toast.makeText(
                                            context,
                                            "Does not match",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            activity?.runOnUiThread {
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                                fetchDataAndRefresh()
                            }

                            val fragmentManager = (context as FragmentActivity).supportFragmentManager
                            val transaction = fragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container, AdminUserFragment())
                            transaction.commit()
                        } else if (message == "failure") {
                            Toast.makeText(
                                context,
                                "Invalid Id",
                                Toast.LENGTH_SHORT
                            ).show()

                            Log.d("log message",logmeesage)

                            try {
                                // Code that may throw an exception
                                val result = 1 / 0
                            } catch (e: Exception) {
                                Log.e("MyTag", "An error occurred:", e)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        // Handle JSON parsing error here
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        context,
                        error.toString().trim { it <= ' ' },
                        Toast.LENGTH_SHORT
                    ).show()

                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val data: MutableMap<String, String> = HashMap()
                    data["userEmail"] = email
                    return data
                }
            }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.show()
    }



    fun getUsers() {

        userNameList.clear()
        userEmailList.clear()
        imageList.clear()
        newArrayList.clear()

        val stringRequest: StringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST, URL,
            Response.Listener { response ->
                Log.d("res", response)
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("status")
                    if (message == "success") {
                        val dataArray = jsonObject.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)
                            val userName = dataObject.getString("userName").toString()
                            val userEmail = dataObject.getString("userEmail").toString()
                            val photo = dataObject.getString("photo").toString()
                            userNameList.add(userName)
                            userEmailList.add(userEmail)
                            imageList.add(photo)
                        }
                        getUserdata()
                    } else if (message == "failure") {
                        Toast.makeText(
                            context,
                            "Invalid Id",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Handle JSON parsing error here
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    context,
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()

            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                data["role"] = role
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }


    private fun getUserdata() {

        val nameArray = userNameList.toTypedArray()
        val emailArray = userEmailList.toTypedArray()
        val imageArray = imageList.toTypedArray()

        for (i in 0 until userNameList.size) {
            val people = ListUsers(imageArray[i], nameArray[i],emailArray[i])
            newArrayList.add(people)
        }

        recyclerView.adapter = UserAdapter(newArrayList, role) { type, user ->
            onActionClick(type, user)
        }

        binding.progressBar.visibility = View.GONE
        binding.loadingTxt.visibility = View.GONE
    }


    fun AddOrganization(view: View?) {
        val intent = Intent(context, com.example.assignment.AddOrganization::class.java)
        startActivity(intent)
        activity?.finish()
    }


    private fun deleteUserRemotely(userEmailToDelete: String) {


        val stringRequest = object : StringRequest(
            Request.Method.POST,
            deleteUrl,
            Response.Listener { response ->
                // Handle the response from the server (e.g., show a toast message)
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                // Handle error (e.g., show an error message)
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                // Pass the email address to delete on the server
                val params = HashMap<String, String>()
                params["userEmail"] = userEmailToDelete

                return params
            }
        }

        // Add the request to the request queue
        Volley.newRequestQueue(context).add(stringRequest)
    }

    private fun fetchDataAndRefresh() {
        // Clear your data lists
        userNameList.clear()
        userEmailList.clear()
        imageList.clear()
        newArrayList.clear()

        recyclerView.adapter?.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}