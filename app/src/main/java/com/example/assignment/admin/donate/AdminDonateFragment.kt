package com.example.assignment.admin.donate

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.CheckConnection
import com.example.assignment.GlobalVariables.anyChange
import com.example.assignment.GlobalVariables.firstIn
import com.example.assignment.R
//import com.example.assignment.admin.dashboard.DashboardViewModel
//import com.example.assignment.admin.user.AdminUserAddFragment
//import com.example.assignment.admin.user.AdminUserDetailFragment
//import com.example.assignment.admin.user.AdminUserFragment
//import com.example.assignment.admin.user.UserAdapter
import com.example.assignment.database.donate.Donate
import com.example.assignment.database.donate.DonateDatabase
import com.example.assignment.database.donate.DonatePeople
import com.example.assignment.database.donate.DonatePeopleDatabase
import com.example.assignment.database.donate.donateList
import com.example.assignment.database.donate.donatePeopleList
import com.example.assignment.databinding.FragmentAdminDonateBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

class AdminDonateFragment : Fragment() {



    private var _binding: FragmentAdminDonateBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar : ProgressBar
    private lateinit var loadingText : TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: DonateAdapter
    private lateinit var searchView : SearchView
    private lateinit var moveAddBtn : FloatingActionButton
    private lateinit var allRadioButton: RadioButton
    private lateinit var animalRadioButton: RadioButton
    private lateinit var oceanRadioButton: RadioButton

    lateinit var builder : AlertDialog.Builder
    private lateinit var URL : String
    private val URLDelete :String = "http://10.0.2.2/Assignment(Mobile)/deleteDonation.php"
    private val URLgetpeople :String = "http://10.0.2.2/Assignment(Mobile)/getDonatePeople.php"

    private lateinit var appDb : DonateDatabase
    private lateinit var appDb1 : DonatePeopleDatabase
    var first = firstIn
    var anychange = anyChange

    var adminID = 0



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val AdminDonateViewModel =
            ViewModelProvider(this).get(AdminDonateViewModel::class.java)

        _binding = FragmentAdminDonateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //change toolbar title
        (activity as AppCompatActivity).supportActionBar?.title = "Donate"

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userRole = sharedPreferences.getString("userRole", "users")
        val adminId = sharedPreferences.getInt("userId",-1)

        adminID = adminId

        appDb = DonateDatabase.getDatabase(requireContext())
        appDb1 = DonatePeopleDatabase.getDatabase(requireContext())
        progressBar = binding.donateBar
        loadingText = binding.loadingText
        recyclerView = binding.donateRecycleView
        searchView = binding.adminDonateSearch
        moveAddBtn = binding.moveAddDonateFragment
        allRadioButton = binding.radioButtonAllDonate
        animalRadioButton = binding.radioButtonAnimal
        oceanRadioButton = binding.radioButtonOcean

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        builder = AlertDialog.Builder(requireContext())

        if(CheckConnection.checkForInternet(requireContext())){

            if(userRole == "admin"){
                URL = "http://10.0.2.2/Assignment(Mobile)/getAllDonations.php"
            }else{
                URL = "http://10.0.2.2/Assignment(Mobile)/getDonations.php"

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
                            donateList.clear()

                            if(first == 0){
                                deleteAllDonation()
                                deleteAllDonationPeople()
                            }else if(anychange == 1){
                                deleteAllDonation()
                            }

                            for (i in 0 until dataArray.length()) {
                                val dataObject = dataArray.getJSONObject(i)

                                getDonatePeople(
                                    dataObject.getInt("donateId"),
                                    dataObject.getInt("adminId"),
                                    dataObject.getString("donateName").toString(),
                                    dataObject.getString("donateImage").toString(),
                                    dataObject.getString("donateCategory").toString(),
                                    dataObject.getString("donateOrgname").toString(),
                                    dataObject.getString("donateStartTime").toString(),
                                    dataObject.getString("donateEndTime").toString(),
                                    dataObject.getDouble("totalDonation"),
                                    dataObject.getString("donateDescription").toString(),
                                    dataObject.getInt("isDeleted")
                                )



                            }

                            progressBar.visibility = View.GONE
                            loadingText.visibility = View.GONE




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
                    data["id"] = adminID.toString()
                    return data
                }
            }
            val requestQueue = Volley.newRequestQueue(requireContext())
            requestQueue.add(stringRequest)



        }else{

            getLocalDonation()

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

        allRadioButton.setOnClickListener { getCategory("All") }
        animalRadioButton.setOnClickListener { getCategory("Marine Animal") }
        oceanRadioButton.setOnClickListener { getCategory("Ocean Conservation") }

        firstIn++
        anyChange = 0

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
                if(i.donateName?.lowercase(Locale.ROOT)?.contains(query) == true) {
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

        moveAddBtn.setOnClickListener {
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

    }

    private fun onActionClick(type : String, donate : Donate){
        if(type == "delete"){

            if(CheckConnection.checkForInternet(requireContext())){
                donate.donateId?.let { deleteDonate(it) }
            }else{
                builder.setMessage("Please change to an available network connection before deleting donation")
                    .setCancelable(true)
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss() // Dismiss the second dialog
                    })
                    .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }



        }else if(type == "view"){

            val bundle = Bundle()
            bundle.putString("id", donate.donateId.toString())
            bundle.putString("adminID", donate.adminId.toString())
            bundle.putString("donateName", donate.donateName)
            bundle.putString("donateImage", donate.donateImage)
            bundle.putString("category", donate.donateCategory)
            bundle.putString("organization", donate.donateOrgname)
            bundle.putString("startDate", donate.donateStartTime)
            bundle.putString("endDate", donate.donateEndTime)
            bundle.putString("totalDonation", donate.target?.toInt().toString())
            bundle.putString("description", donate.donateDescription)
            bundle.putString("isDeleted", donate.isDeleted.toString())
            bundle.putString("percentages", donate.percentages.toString())

            val fragmentDonateDetail = AdminDonateDetailFragment()
            fragmentDonateDetail.arguments = bundle

            loadFragment(fragmentDonateDetail)

        }


    }

    private fun deleteDonate(id : Int){

        builder.setMessage("Confirm Delete Donation")
            .setCancelable(true)
            .setPositiveButton("Confirm", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss() // Dismiss the second dialog
                val stringRequest: StringRequest = object : StringRequest(
                    Request.Method.POST, URLDelete,
                    Response.Listener { response ->
                        Log.d("Register",response)
                        if (response == "success") {
                            builder.setMessage("The Donation is Deleted")
                                .setCancelable(true)
                                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                                    dialogInterface.dismiss() // Dismiss the second dialog
                                    anyChange = 1
                                    loadFragment(AdminDonateFragment())
                                })
                                .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                                    dialogInterface.cancel()
                                }).show()
                        } else if (response == "failure") {
                            builder.setMessage("Failure to Delete the Donation")
                                .setCancelable(true)
                                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                                    dialogInterface.dismiss() // Dismiss the second dialog
                                    //testOtherScreen()
                                })
                                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
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
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.cancel()
            })
            .show()


    }


    fun getDonatePeople(donateId : Int, adminId : Int, donateName : String, donateImage : String, donateCategory : String ,
                        donateOrgname : String, donateStartTime : String, donateEndTime : String, totalDonation : Double,
                        donateDescription : String, isDeleted : Int) {


        var progressTest: Double = 0.0
        var donate : Donate? = null
        val stringRequest: StringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST, URLgetpeople,
            Response.Listener { response ->
                Log.d("res", response)
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("status")
                    if (message == "success") {
                        val dataArray = jsonObject.getJSONArray("data")
                        donatePeopleList.clear()
                        var current = 0
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)

                            current += dataObject.getInt("userTotalDonate")

                            var donatePeople = DonatePeople(
                                dataObject.getInt("donatePersonId"), dataObject.getInt("donateId"),
                                dataObject.getInt("userId"), dataObject.getString("userEmail").toString(),
                                dataObject.getString("userName").toString(), dataObject.getString("userImage").toString(),
                                dataObject.getDouble("userTotalDonate"), dataObject.getString("paymentMethod").toString(),
                                dataObject.getString("createDate").toString()
                            )

                            if(first == 0){
                                addLocalDonationPeople(donatePeople)
                            }



                        }

                        progressTest = (current.toDouble() / totalDonation) * 100.0
//                        val formattedProgress = String.format("%.2f", progressTest)
//                        percentages.text = "$formattedProgress %"

                        donate = Donate(
                            donateId, adminId, donateName,
                            donateImage, donateCategory, donateOrgname,
                            donateStartTime, donateEndTime, totalDonation,
                            donateDescription, isDeleted, progressTest.toInt()
                        )




                    } else if (message == "failure") {
                        donate = Donate(
                            donateId, adminId, donateName,
                            donateImage, donateCategory, donateOrgname,
                            donateStartTime, donateEndTime, totalDonation,
                            donateDescription, isDeleted, 0
                        )

                    }

                    donate?.let { donateList.add(it) }


                    if(first == 0){
                        donate?.let { addLocalDonation(it) }
                    }else if(anychange == 1){
                        donate?.let { addLocalDonation(it) }
                    }

                    getAll()

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
                data["id"] = donateId.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)

    }

    private fun getCategory(category: String) {

        val categoryList = mutableListOf<Donate>()

        if(category == "All"){
            for (donate in donateList) {
                categoryList.add(donate)
            }
        }else{
            for (donate in donateList) {
                if (donate.donateCategory == category) {
                    categoryList.add(donate)
                }
            }
        }


        adapter = DonateAdapter(categoryList){ type, donate ->
            onActionClick(type, donate)
        }

        recyclerView.adapter = adapter
    }


    private fun addLocalDonation(donate : Donate){


        GlobalScope.launch(Dispatchers.IO) {
            appDb.donateDao().insert(donate)


        }

        //Toast.makeText(requireContext(),"1", Toast.LENGTH_SHORT).show()
    }

    private fun deleteAllDonation(){

        GlobalScope.launch(Dispatchers.IO) {
            appDb.donateDao().deleteAll()
        }

        //Toast.makeText(requireContext(),"2", Toast.LENGTH_SHORT).show()


    }

    private fun addLocalDonationPeople(donatePeople : DonatePeople){


        GlobalScope.launch(Dispatchers.IO) {
            appDb1.donatePeopleDao().insert(donatePeople)
        }

        //Toast.makeText(requireContext(),"3", Toast.LENGTH_SHORT).show()

    }

    private fun deleteAllDonationPeople(){

        GlobalScope.launch(Dispatchers.IO) {
            appDb1.donatePeopleDao().deleteAll()
        }

        //Toast.makeText(requireContext(),"4", Toast.LENGTH_SHORT).show()

    }


    private fun getLocalDonation() {

        GlobalScope.launch(Dispatchers.IO) {
            var donate = appDb.donateDao().getAll()

            withContext(Dispatchers.Main) {
                adapter = DonateAdapter(donate) { type, donate ->
                    onActionClick(type, donate)
                }

                recyclerView.adapter = adapter
            }
        }

        progressBar.visibility = View.GONE
        loadingText.visibility = View.GONE
        }

}