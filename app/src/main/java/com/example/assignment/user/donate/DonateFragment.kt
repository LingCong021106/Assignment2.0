package com.example.assignment.user.donate

import android.R.*
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.CheckConnection
import com.example.assignment.R
import com.example.assignment.databinding.FragmentUserDonateBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DonateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DonateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentUserDonateBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: DonateAdapter
    private lateinit var mToolbar : Toolbar
    private lateinit var donateDB : DonateDatabase
    private var connection : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(com.example.assignment.R.menu.top_nav_menu,menu)
        val searchItem = menu.findItem(R.id.search)
        if(searchItem != null){
            val searchView = searchItem.actionView as SearchView
            searchView.queryHint = "Type here to search..."
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if(newText!!.isNotEmpty()){
                        com.example.assignment.user.donate.searchList.clear()
                        val search = newText.lowercase(Locale.getDefault())
                        donateList.forEach {
                            if(it.donateName.lowercase(Locale.getDefault()).contains(search)){
                                com.example.assignment.user.donate.searchList.add(it)
                            }
                        }
                        adapter = DonateAdapter(searchList)
                        binding.donateRecycleView.adapter  = adapter
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                    else{
                        com.example.assignment.user.donate.searchList.clear()
                        com.example.assignment.user.donate.searchList.addAll(donateList)
                        adapter = DonateAdapter(com.example.assignment.user.donate.searchList)
                        binding.donateRecycleView.adapter  = adapter
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserDonateBinding.inflate(inflater, container, false)
        val rootView: View = binding.root


        //check connection
        if(CheckConnection.checkForInternet(requireContext())){
            connection = true
        }

        //refresh buton
        binding.refreshBtn.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, DonateFragment()).commit()
        }

        setHasOptionsMenu(true)
        mToolbar = requireActivity().findViewById(R.id.toolbar)
        if (mToolbar != null) {
            (activity as AppCompatActivity?)?.setSupportActionBar(mToolbar)
        }
        mToolbar.title = "Donate"

        donateList.clear()
        donatePersonList.clear()
        recyclerView = binding.donateRecycleView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        //initial donate room database
        donateDB = DonateDatabase.getInstance(requireContext())

        //check connection, if yes initial donate room database
        if(connection) {
            CoroutineScope(Dispatchers.IO).launch {
                if (donateDB.donateDatabaseDao().getAllDonate().size > 0) {
                    donateDB.donateDatabaseDao().deleteAllFromDonate()
                }
                if (donateDB.donateDatabaseDao().getAllDonatePerson().size > 0) {
                    donateDB.donateDatabaseDao().deleteAllFromDonatePerson()
                }
            }
            getDonatePerson()
            donateGetAll()
        }
        else{
            //get data from room
            CoroutineScope(Dispatchers.IO).launch {
                donateList = donateDB.donateDatabaseDao().getAllDonate()
                donatePersonList = donateDB.donateDatabaseDao().getAllDonatePerson()
                if(donateList.size>0){
                    adapter = DonateAdapter(donateList)
                    binding.donateRecycleView.adapter  = adapter

                    binding.progressBar.visibility = View.GONE
                    binding.loading.visibility = View.GONE

                }
                else{
                    binding.progressBar.visibility = View.GONE
                    binding.loading.visibility = View.GONE
                    binding.refreshBtn.visibility = View.VISIBLE
                }

                Snackbar.make(rootView, "No connection now!", Snackbar.LENGTH_SHORT).show()
            }
        }



        binding.imageButton6.setOnClickListener{
            donateList.clear()
            donateGetAll()
        }
        binding.imageButton7.setOnClickListener{
            val donate = searchByCategory("Ocean Conservation")
            adapter = DonateAdapter(donate)
            binding.donateRecycleView.adapter  = adapter
        }
        binding.imageButton8.setOnClickListener{
            val donate = searchByCategory("Marine Animal")
            adapter = DonateAdapter(donate)
            binding.donateRecycleView.adapter  = adapter
        }

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DonateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DonateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getDonatePerson(){
        val url = "http://10.0.2.2/Assignment(Mobile)/donatePersonGetAll.php"
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("donatePerson", response)
                try {
                    if (response != null) {
                        val array = JSONArray(response)

                        for (i in 0 until array.length()) {
                            val donatePerson = array.getJSONObject(i)
                            val data = DonatePerson(
                                donatePerson.getInt("donatePersonId"),
                                donatePerson.getInt("donateId"),
                                donatePerson.getInt("userId"),
                                donatePerson.getString("userEmail"),
                                donatePerson.getString("userName"),
                                donatePerson.getString("userImage"),
                                donatePerson.getDouble("userTotalDonate"),
                                donatePerson.getString("paymentMethod"),
                                donatePerson.getString("createDate"),
                            )
                            donatePersonList.add(data)

                            CoroutineScope(Dispatchers.IO).launch{
                                donateDB.donateDatabaseDao().insertDonatePerson(data)
                            }
                        }
                    }
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
            }) {
        }
        val requestQueue = Volley.newRequestQueue(context?.applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun donateGetAll(){
        val url = "http://10.0.2.2/Assignment(Mobile)/donateGetAll.php"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("donate", response)
                try {
                    if (response != null) {
                        val array = JSONArray(response)

                        for (i in 0 until array.length()) {
                            val donate = array.getJSONObject(i)
                            val data = Donate(
                                donate.getInt("donateId"),
                                donate.getInt("adminId"),
                                donate.getString("donateName"),
                                donate.getString("donateImage"),
                                donate.getString("donateCategory"),
                                donate.getString("donateOrgname"),
                                donate.getString("donateStartTime"),
                                donate.getString("donateEndTime"),
                                donate.getDouble("totalDonation"),
                                donate.getString("donateDescription"),
                                donate.getInt("isDeleted"),
                            )
                            donateList.add(data)

                            CoroutineScope(Dispatchers.IO).launch{
                                donateDB.donateDatabaseDao().insertDonate(data)
                            }

                        }
                        adapter = DonateAdapter(donateList)
                        binding.donateRecycleView.adapter  = adapter

                        binding.progressBar.visibility = View.GONE
                        binding.loading.visibility = View.GONE

                    }
                }catch (e:JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
            }) {
        }
        val requestQueue = Volley.newRequestQueue(context?.applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun searchByCategory(category:String): MutableList<Donate>{
        var donates = mutableListOf<Donate>()
        for (donate in donateList){
            if(donate.donateCategory == category){
                donates.add(donate)
            }
        }
        return donates
    }

}