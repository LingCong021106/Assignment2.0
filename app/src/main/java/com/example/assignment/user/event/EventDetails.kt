package com.example.assignment.user.event

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.BitmapConverter
import com.example.assignment.CheckConnection
import com.example.assignment.ListDonate
import com.example.assignment.R
import com.example.assignment.databinding.EventDetailsBinding
import com.example.assignment.user.UserHome
import java.time.format.DateTimeFormatter


class EventDetails : AppCompatActivity(){

    private lateinit var binding: EventDetailsBinding
    lateinit var image1 : ImageView
    private lateinit var recycleView : RecyclerView
    private lateinit var adapter: EventJoinedAdapter
    private lateinit var newArrayList: ArrayList<ListDonate>
    lateinit var imageId : Array<Int>
    lateinit var name : Array<String>
    lateinit var btnJoin : Button
    lateinit var builder : AlertDialog.Builder
//    private val URL :String = "http://192.168.0.19:8081/mobile/eventjoin.php"
    private val URLinsert : String = "http://192.168.0.19:8081/mobile/insertEventPeople.php"
    private val URL : String = "http://192.168.0.135/Assignment(Mobile)/eventJoined.php"
    private val nameList: ArrayList<String> = ArrayList()
    private val imageIdList: ArrayList<Int> = ArrayList()
    private var imageString: String? = null

    lateinit var userName : String
    var eventId : Int = 0
    //user Id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_details)
        binding = EventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        eventId  = intent.getIntExtra("eventId", -1)
        val dateFormated = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val personJoined = intent.getIntExtra("totalPerson", -1)
        val event = getEvent(eventId)
        if(event!= null){
            val contactNumber = event.contactNumber
            val eventMaxJoined = event.maxPerson.toString()
            imageString = event.image
            var bitmap = BitmapConverter.convertStringToBitmap(imageString)
            binding.eventImg.setImageBitmap(bitmap)
            binding.eventLocationEdit.text = event.location
            binding.eventTitle.text = event.name
            binding.eventNumberpoeple.text = "$personJoined/$eventMaxJoined"
            binding.eventTimeEdit.text = event.eventDate.format(dateFormated)
            binding.eventLocationEdit.text = event.location
            binding.eventOrganizationEdit.text = event.orgatnizationName
            binding.eventContactnumberEdit.text = contactNumber
            binding.eventContactnumberEdit.setPaintFlags(binding.eventContactnumberEdit.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

            binding.eventContactnumberEdit.setOnClickListener{
                intent = Intent(Intent.ACTION_DIAL)
                intent.setData(Uri.parse("tel:$contactNumber"))
                startActivity(intent)
            }
            binding.eventContactpersonEdit.text = event.contactPerson
            binding.eventContentEdit.text = event.description

            if(personJoined >= event.maxPerson){
                binding.btnJoin.isEnabled = false
                binding.btnJoin.setBackgroundResource(R.drawable.event_joined_btn )
                binding.btnJoin.text = "This Event is Full"
            }

        }

        recycleView = binding.fundraisingPeopleList
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.setHasFixedSize(true)


        adapter = EventJoinedAdapter(getPersonJoined(eventId))
        binding.fundraisingPeopleList.adapter  = adapter

        builder = AlertDialog.Builder(this)
        if(!checkAvailability()){
            binding.btnJoin.isEnabled = false
            binding.btnJoin.setBackgroundResource(R.drawable.event_joined_btn)
            binding.btnJoin.text = "Your Are Joined"
        }

        binding.btnJoin.setOnClickListener{
            if (CheckConnection.checkForInternet(this)) {
                if(binding.btnJoin.isEnabled) {
                    confirmation()
                }
            } else {
                Toast.makeText(this, "Connection error, Please try later", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAvailability(): Boolean{
        val userEmail = "tester@gmail.com"
        for (eventJoined in eventJoinedList){
            if(eventJoined.userEmail == userEmail && eventId == eventJoined.eventId){
                return false
            }
        }
        return true
    }

    private fun getEvent(eventId: Int): Event?{
        for(event in eventList){
            if(event.id == eventId){
                return event
            }
        }
        return null
    }

    private fun getPersonJoined(eventId:Int): MutableList<EventJoined> {
        var personJoinedList = mutableListOf<EventJoined>()
        for (eventJoined in eventJoinedList){
            if(eventJoined.eventId == eventId){
                personJoinedList.add(eventJoined)
            }
        }
        return personJoinedList
    }

//    private fun getUserdata() {
//
//        val nameArray = nameList.toTypedArray()
//        val imageIdArray = imageIdList.toTypedArray()
//
//        for (i in imageIdArray.indices) {
//            val people = ListDonate(imageIdArray[i], nameArray[i])
//            newArrayList.add(people)
//        }
//
//        listPeopleRecycler.adapter = EventJoinedAdapter(newArrayList)
//    }

    private fun confirmation() {
        builder.setMessage("Are you sure want to join this event?")
            .setCancelable(true)
            .setPositiveButton("Join", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss() // Dismiss the first dialog
                storeJoinEventPeople()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.cancel()
            })
            .show()
    }

    private fun storeJoinEventPeople() {
        val url : String = "http://10.0.2.2/Assignment(Mobile)/insertEventPeople.php"

        userName = "Test name"
        var userEmail = "tester@gmail.com"
        var userImage = "image123"
        val userId = 1

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("Register",response)
                if (response == "success") {
                    builder.setMessage("Thank for you joining this event")
                        .setCancelable(true)
                        .setPositiveButton("back to home", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss() // Dismiss the second dialog
                            intent = Intent(this@EventDetails, UserHome::class.java)
                            startActivity(intent)
                        })
                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                } else if (response == "failure") {
                    builder.setMessage("So sorry, this is full already, please try to join other event")
                        .setCancelable(true)
                        .setPositiveButton("back to home", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss() // Dismiss the second dialog
                        })
                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    applicationContext,
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                data["eventId"] = eventId.toString()
                data["userEmail"] = userEmail
                data["userImage"] = userImage
                data["userName"] = userName
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }
}