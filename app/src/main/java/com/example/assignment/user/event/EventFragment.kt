package com.example.assignment.user.event

import android.R.*
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.CheckConnection
import com.example.assignment.R
import com.example.assignment.databinding.FragmentUserEventBinding
import com.example.assignment.user.donate.DonateAdapter
import com.example.assignment.user.donate.DonateDatabase
import com.example.assignment.user.donate.DonateFragment
import com.example.assignment.user.donate.donateList
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
 * Use the [EventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventFragment : Fragment(){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentUserEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var appDb : EventDatabase
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var searchView: SearchView
    private lateinit var menuView : Menu
    private lateinit var mToolbar : Toolbar
    private lateinit var eventDB : EventDatabase
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
                        searchList.clear()
                        val search = newText.lowercase(Locale.getDefault())
                        eventList.forEach {
                            if(it.eventName.lowercase(Locale.getDefault()).contains(search)){
                                searchList.add(it)
                            }
                        }
                        adapter = EventAdapter(searchList)
                        binding.eventRecycleView.adapter  = adapter
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                    else{
                        searchList.clear()
                        searchList.addAll(eventList)
                        adapter = EventAdapter(searchList)
                        binding.eventRecycleView.adapter  = adapter
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserEventBinding.inflate(inflater, container, false)
        val rootView : View = binding.root

        //check connection
        if(CheckConnection.checkForInternet(requireContext())){
            connection = true
        }

        //refresh button
        binding.refreshbtn.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, EventFragment()).commit()
        }

        //toolbar
        setHasOptionsMenu(true)
        mToolbar = requireActivity().findViewById(R.id.toolbar)
        if (mToolbar != null) {
            (activity as AppCompatActivity?)?.setSupportActionBar(mToolbar)
        }
        mToolbar.title = "Event"

        eventList.clear()
        eventJoinedList.clear()
        recyclerView = binding.eventRecycleView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        //initial donate room database
        eventDB = EventDatabase.getInstance(requireContext())

        //check connection, if yes initial donate room database
        if(connection) {
            CoroutineScope(Dispatchers.IO).launch {
                if (eventDB.eventDatabaseDao().getAllEvent() != null) {
                    eventDB.eventDatabaseDao().deleteAllEvent()
                }
                if (eventDB.eventDatabaseDao().getAllEventJoined() != null) {
                    eventDB.eventDatabaseDao().deleteEventJoined()
                }
            }
            getEventJoined()
            eventGetAll()
            binding.progressBar.visibility = View.GONE
            binding.loading.visibility = View.GONE
        }
        else{
            //get data from room
            CoroutineScope(Dispatchers.IO).launch {
                eventList = eventDB.eventDatabaseDao().getAllEvent()
                eventJoinedList = eventDB.eventDatabaseDao().getAllEventJoined()
                if(eventList.size>0){
                    adapter = EventAdapter(eventList)
                    binding.eventRecycleView.adapter  = adapter

                    binding.progressBar.visibility = View.GONE
                    binding.loading.visibility = View.GONE

                }
                else{
                    binding.progressBar.visibility = View.GONE
                    binding.loading.visibility = View.GONE
                    binding.refreshbtn.visibility = View.VISIBLE
                }
                Snackbar.make(rootView, "No connection now!", Snackbar.LENGTH_SHORT).show()
            }
        }


        binding.imageButton6.setOnClickListener{
            adapter = EventAdapter(eventList)
            binding.eventRecycleView.adapter  = adapter
        }
        binding.imageButton7.setOnClickListener{
            val event = searchByCategory("category1")
            adapter = EventAdapter(event)
            binding.eventRecycleView.adapter  = adapter
        }
        binding.imageButton8.setOnClickListener{
            val event = searchByCategory("category2")
            adapter = EventAdapter(event)
            binding.eventRecycleView.adapter  = adapter
        }
        binding.imageButton9.setOnClickListener{
            val event = searchByCategory("category3")
            adapter = EventAdapter(event)
            binding.eventRecycleView.adapter  = adapter
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
         * @return A new instance of fragment EventFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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
                            )
                            eventList.add(data)

                            CoroutineScope(Dispatchers.IO).launch{
                                eventDB.eventDatabaseDao().insertEvent(data)
                            }
                        }
                        adapter = EventAdapter(eventList)
                        binding.eventRecycleView.adapter  = adapter

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

    private fun getEventJoined(){
        val url = "http://10.0.2.2/Assignment(Mobile)/eventJoined.php"
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
                                        eventJoined.getString("userEmail"),
                                        eventJoined.getString("userImage"),
                                        eventJoined.getString("userName"),
                                    )
                                eventJoinedList.add(data)
                                CoroutineScope(Dispatchers.IO).launch{
                                    eventDB.eventDatabaseDao().insertEventJoined(data)
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

    private fun searchByCategory(category:String): MutableList<Event>{
        var events = mutableListOf<Event>()
        for (event in eventList){
            if(event.eventCategory == category){
                events.add(event)
            }
        }
        return events
    }
    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

}