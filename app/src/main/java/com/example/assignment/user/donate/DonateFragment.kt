package com.example.assignment.user.donate

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.databinding.FragmentUserDonateBinding
import com.example.assignment.databinding.FragmentUserEventBinding
import com.example.assignment.user.event.Event
import com.example.assignment.user.event.EventAdapter
import com.example.assignment.user.event.eventJoinedList
import com.example.assignment.user.event.eventList
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
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: DonateAdapter
    private lateinit var mToolbar : Toolbar
    private val binding get() = _binding!!
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
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if(newText!!.isNotEmpty()){
                        com.example.assignment.user.donate.searchList.clear()
                        val search = newText.lowercase(Locale.getDefault())
                        donateList.forEach {
                            if(it.name.lowercase(Locale.getDefault()).contains(search)){
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserDonateBinding.inflate(inflater, container, false)
        val rootView: View = binding.root

        setHasOptionsMenu(true)
        mToolbar = requireActivity().findViewById(R.id.toolbar)
        if (mToolbar != null) {
            (activity as AppCompatActivity?)?.setSupportActionBar(mToolbar)
        }
        mToolbar.setTitle(null)

        donateList.clear()
        donatePersonList.clear()
        recyclerView = binding.donateRecycleView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        getDonatePerson()
        donateGetAll()

        binding.imageButton6.setOnClickListener{
            donateList.clear()
            donateGetAll()
        }
        binding.imageButton7.setOnClickListener{
            val donate = searchByCategory("ocean")
            adapter = DonateAdapter(donate)
            binding.donateRecycleView.adapter  = adapter
        }
        binding.imageButton8.setOnClickListener{
            val donate = searchByCategory("animal")
            adapter = DonateAdapter(donate)
            binding.donateRecycleView.adapter  = adapter
        }
        binding.imageButton9.setOnClickListener{
            val donate = searchByCategory("education")
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
        val url = "http://10.0.2.2/Assignment(Mobile)/donatePerson.php"
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
                                donatePerson.getInt("donateId"),
                                donatePerson.getString("userEmail"),
                                donatePerson.getString("userName"),
                                donatePerson.getString("userImage"),
                                donatePerson.getDouble("userTotalDonate"),
                            )
                            donatePersonList.add(data)

                        }
                    }
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    activity,
                    "Error Connection, Please Try Later",
                    Toast.LENGTH_SHORT
                ).show()
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
                                donate.getString("donateName"),
                                donate.getString("donateImage"),
                                donate.getString("donateCategory"),
                                donate.getString("donateOrgname"),
                                donate.getString("donateStartTime"),
                                donate.getString("donateEndTime"),
                                donate.getDouble("totalDonation"),
                                donate.getString("donateDescription"),
                            )
                            donateList.add(data)

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
            Response.ErrorListener { error ->
                Toast.makeText(
                    activity,
                    "Error Connection, Please Try Later",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
        }
        val requestQueue = Volley.newRequestQueue(context?.applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun searchByCategory(category:String): MutableList<Donate>{
        var donates = mutableListOf<Donate>()
        for (donate in donateList){
            if(donate.category == category){
                donates.add(donate)
            }
        }
        return donates
    }
}