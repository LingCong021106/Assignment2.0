package com.example.assignment.user.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.databinding.ProfileHistoryBinding
import com.example.assignment.user.event.Event
import com.example.assignment.user.event.eventJoinedList
import com.example.assignment.user.event.eventJoinedListById
import com.example.assignment.user.event.eventList
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventHistory : AppCompatActivity() {

    private lateinit var adapter: EventHistoryAdapter
    private lateinit var binding: ProfileHistoryBinding
    private lateinit var recycleView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventList.clear()
        eventHistoryList.clear()
        recycleView = binding.historyRecycleView
        recycleView.layoutManager = LinearLayoutManager(applicationContext)
        recycleView.setHasFixedSize(true)

        eventGetAll()

        setContentView(binding.root)

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

//                            CoroutineScope(Dispatchers.IO).launch{
//                                eventDB.eventDatabaseDao().insertEvent(data)
//                            }
                        }
                        eventHistoryList = checkEventRecord()

                            adapter = EventHistoryAdapter(this, eventHistoryList)
                            binding.historyRecycleView.adapter  = adapter

                            binding.loadingBar.visibility = View.GONE
                            binding.loadingText.visibility = View.GONE


                    }
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    this,
                    "Error Connection, Please Try Later",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun checkEventRecord():MutableList<History>{
        var eventHistory = mutableListOf<History>()
        if(eventJoinedListById.size>0 && eventList.size>0){
            for(eventJoined in eventJoinedList){
                for(event in eventList){
                    if(eventJoined.eventId == event.eventId){
                        val status = statusCheck(event.eventDate)
                        var history = History(
                            id = event.eventId,
                            title = event.eventName,
                            image = event.eventImage.toString(),
                            status = status,
                            createTime = eventJoined.createDate
                        )
                        eventHistory.add(history)
                    }
                }
            }
        }
        return eventHistory
    }

    private fun statusCheck(eventDate: String): String {
        val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        val eventDateTime = dateTimeFormat.parse(eventDate)
        val currentDateTime = Date()

        val compare = eventDateTime > currentDateTime
        if(compare){
            return "Joined"
        }
        return "Finish"
    }
}