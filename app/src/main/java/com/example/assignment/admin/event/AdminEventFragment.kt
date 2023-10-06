package com.example.assignment.admin.event

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.databinding.FragmentAdminEventBinding
import com.example.assignment.user.event.Event
import com.example.assignment.user.event.EventJoined
import com.example.assignment.user.event.eventJoinedList
import com.example.assignment.user.event.eventList
import com.example.assignment.user.event.searchList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.util.Locale

class AdminEventFragment : Fragment() {

    private var _binding: FragmentAdminEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var mToolbar : Toolbar
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: AdminEventAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminEventBinding.inflate(inflater, container, false)
        val rootView : View = binding.root

        //share preference
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userRole = sharedPreferences.getString("userRole", "users")
        val adminId = sharedPreferences.getInt("userId",-1)

        //change toolbar title
        (activity as AppCompatActivity).supportActionBar?.title = "Event"

        binding.moveAddEventFragment.setOnClickListener{
            loadFragment(AdminEventAddFragment())
        }

        if(isLoggedIn && userRole == "organization"){
            eventList.clear()
            eventJoinedList.clear()
            recyclerView = binding.adminEventRecycleView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)
            eventGetAllByAdminId(adminId)
        }
        else {
            eventList.clear()
            eventJoinedList.clear()
            recyclerView = binding.adminEventRecycleView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)
            getEventJoined()
            eventGetAll()
        }

        searchBySearchView(binding.adminEventSearch)
        return rootView
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun eventGetAll(){
        val url = "http://10.0.2.2/Assignment(Mobile)/eventGetAll.php"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("Event", response)
                try {
                    if (response != null) {
                        val array = JSONArray(response)

                        for (i in 0 until array.length()) {
                            val event = array.getJSONObject(i)
                            val data = Event(
                                event.getInt("eventId"),
                                event.getInt("adminId"),
                                event.getString("eventName"),
                                event.getString("eventCategory"),
                                event.getString("eventImage"),
                                event.getString("eventDescription"),
                                event.getString("eventRegEndTime"),
                                event.getString("eventOrgName"),
                                event.getString("eventContactNumber"),
                                event.getString("eventContactPerson"),
                                event.getInt("eventMaxPerson"),
                                event.getString("eventDate"),
                                event.getString("eventLocation"),
                                event.getInt("isDeleted"),
                                )
                            eventList.add(data)

                            CoroutineScope(Dispatchers.IO).launch{
//                                eventDB.eventDatabaseDao().insertEvent(data)
                            }
                        }
                        adapter = AdminEventAdapter(eventList)
                        binding.adminEventRecycleView.adapter  = adapter

                        binding.eventBar.visibility = View.GONE
                        binding.loadingText.visibility = View.GONE

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

    private fun getEventJoined(){
        val url = "http://10.0.2.2/Assignment(Mobile)/eventJoinedGetAll.php"
        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.POST, url,
                Response.Listener { response ->
                    Log.d("EventJoined", response)
                    try {
                        if (response != null) {
                            val array = JSONArray(response)

                            for (i in 0 until array.length()) {
                                val eventJoined = array.getJSONObject(i)
                                val data = EventJoined(
                                    eventJoined.getInt("eventJoinedId"),
                                    eventJoined.getInt("eventId"),
                                    eventJoined.getInt("userId"),
                                    eventJoined.getString("userEmail"),
                                    eventJoined.getString("userImage"),
                                    eventJoined.getString("userName"),
                                    eventJoined.getString("createDate"),
                                )
                                eventJoinedList.add(data)
                                CoroutineScope(Dispatchers.IO).launch{
//                                    eventDB.eventDatabaseDao().insertEventJoined(data)
                                }

                            }
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        context,
                        "Error Connection, Please Try Later",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {

            }
        val requestQueue = Volley.newRequestQueue(context?.applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun eventGetAllByAdminId(adminId : Int){
        val url = "http://10.0.2.2/Assignment(Mobile)/getAllEventById.php"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("Event", response)
                try {
                    if (response != null) {
                        val array = JSONArray(response)

                        for (i in 0 until array.length()) {
                            val event = array.getJSONObject(i)
                            val data = Event(
                                event.getInt("eventId"),
                                event.getInt("adminId"),
                                event.getString("eventName"),
                                event.getString("eventCategory"),
                                event.getString("eventImage"),
                                event.getString("eventDescription"),
                                event.getString("eventRegEndTime"),
                                event.getString("eventOrgName"),
                                event.getString("eventContactNumber"),
                                event.getString("eventContactPerson"),
                                event.getInt("eventMaxPerson"),
                                event.getString("eventDate"),
                                event.getString("eventLocation"),
                                event.getInt("isDeleted"),
                                )
                            eventList.add(data)

                            CoroutineScope(Dispatchers.IO).launch{
//                                eventDB.eventDatabaseDao().insertEvent(data)
                            }
                        }
                        adapter = AdminEventAdapter(eventList)
                        binding.adminEventRecycleView.adapter  = adapter

                        binding.eventBar.visibility = View.GONE
                        binding.loadingText.visibility = View.GONE

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
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                data["adminId"] = adminId.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(context?.applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun searchBySearchView(searchView: android.widget.SearchView){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if(newText!!.isNotEmpty()){
                    searchList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    eventList.forEach {
                        if(it.eventName.lowercase(Locale.getDefault()).contains(search)){
                            searchList.add(it)
                        }
                    }
                    adapter = AdminEventAdapter(searchList)
                    binding.adminEventRecycleView.adapter  = adapter
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                else{
                    searchList.clear()
                    searchList.addAll(eventList)
                    adapter = AdminEventAdapter(searchList)
                    binding.adminEventRecycleView.adapter  = adapter
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })
    }

}