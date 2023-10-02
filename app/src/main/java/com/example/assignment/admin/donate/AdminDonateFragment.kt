package com.example.assignment.admin.donate

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.database.donate.Donate
import com.example.assignment.database.donate.donateList
import com.example.assignment.databinding.FragmentAdminDonateBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

class AdminDonateFragment : Fragment() {

//    val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
//    val userRole = sharedPreferences.getString("userRole", "users")
//    val userName= sharedPreferences.getString("userName", "")
//    val userEmail = sharedPreferences.getString("userEmail", "")
//    val userId = sharedPreferences.getString("userId","")

    private var _binding: FragmentAdminDonateBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar : ProgressBar
    private lateinit var loadingText : TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: DonateAdapter
    private lateinit var searchView : SearchView

    lateinit var builder : AlertDialog.Builder
    private val URL :String = "http://192.168.0.192/Assignment(Mobile)/getAllDonations.php"
    private val URLDelete :String = "http://192.168.0.192/Assignment(Mobile)/deleteDonation.php"





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val AdminDonateViewModel =
            ViewModelProvider(this).get(AdminDonateViewModel::class.java)

        _binding = FragmentAdminDonateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        progressBar = binding.donateBar
        loadingText = binding.loadingText
        recyclerView = binding.donateRecycleView
        searchView = binding.adminDonateSearch

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        //var testing : TextView = binding.testing

        builder = AlertDialog.Builder(requireContext())
        val stringRequest: StringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST, URL,
            Response.Listener { response ->
                Log.d("res", response)
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("status")
                    if (message == "success") {
                        val dataArray = jsonObject.getJSONArray("data")
                        donateList.clear()
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)

                            val donate = Donate(
                                dataObject.getInt("donateID") , dataObject.getString("donateImage").toString(),
                                dataObject.getString("organization").toString(), dataObject.getString("title").toString(),
                                dataObject.getInt("target"), dataObject.getString("startDate").toString(),
                                dataObject.getString("endDate").toString(), dataObject.getString("Description").toString(),
                                dataObject.getInt("userID")
                            )

                            donateList.add(donate)
                            //test = dataObject.getInt("donateID").toString()

                        }
                        //testing.setText(test)

                        getAll()


                    } else if (message == "failure") {
                        Toast.makeText(
                            requireContext(),
                            "No Donation",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    progressBar.visibility = View.GONE
                    loadingText.visibility = View.GONE
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Handle JSON parsing error here
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    requireContext(),
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                data["id"] = "funId"
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)



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
            val filteredList = ArrayList<Donate>()
            for(i in donateList){
                if(i.title?.lowercase(Locale.ROOT)?.contains(query) == true) {
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

        val button1 = view.findViewById<FloatingActionButton>(R.id.moveAddDonateFragment)
        button1.setOnClickListener {
            loadFragment(AdminDonateAddFragment())
        }
    }


    private fun loadFragment(fragment: Fragment) {
        donateList.clear()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    private fun getAll() {
            adapter = DonateAdapter(donateList){ type, donate ->
                onActionClick(type, donate)
            }

            recyclerView.adapter = adapter


        progressBar.visibility = View.GONE
        loadingText.visibility = View.GONE

    }

    private fun onActionClick(type : String, donate : Donate){
        if(type == "delete"){

            donate.donateID?.let { deleteDonate(it) }

            //deleteDonate(donate.donateID)


        }else if(type == "view"){

            val bundle = Bundle()
            bundle.putString("id", donate.donateID.toString())
            bundle.putString("donateImage", donate.donateImage)
            bundle.putString("organization", donate.organization)
            bundle.putString("title", donate.title)
            bundle.putString("target", donate.target.toString())
            bundle.putString("startDate", donate.startDate)
            bundle.putString("endDate", donate.endDate)
            bundle.putString("description", donate.description)
            bundle.putString("userID", donate.userID.toString())
            val fragmentDonateDetail = AdminDonateDetailFragment()
            fragmentDonateDetail.arguments = bundle

            loadFragment(fragmentDonateDetail)

        }


    }

    private fun deleteDonate(id : Int){

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, URLDelete,
            Response.Listener { response ->
                Log.d("Register",response)
                if (response == "success") {
                    builder.setMessage("Confirm Delete Donation")
                        .setCancelable(true)
                        .setPositiveButton("Confirm", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss() // Dismiss the second dialog
                            loadFragment(AdminDonateFragment())
                        })
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                } else if (response == "failure") {
                    builder.setMessage("So sorry, this is full already, please try to join other event")
                        .setCancelable(true)
                        .setPositiveButton("back to home", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss() // Dismiss the second dialog
                            //testOtherScreen()
                        })
                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    requireContext(),
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                data["id"] = id.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

}