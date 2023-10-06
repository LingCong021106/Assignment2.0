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
import android.view.View
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
import com.example.assignment.MainActivity
import com.example.assignment.R
import com.example.assignment.database.AppDatabase
import com.example.assignment.database.User
import com.example.assignment.databinding.EventDetailsBinding
import com.example.assignment.user.UserHome
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
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
    private var connection : Boolean = false
    private var userId : Int = 0
    private var userName : String = ""
    private var userImage : String = ""
    private var userEmail : String = ""
    private var eventId : Int = 0
    private lateinit var appDB : AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //share preference
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId",-1)
        CoroutineScope(Dispatchers.IO).launch {
            appDB = AppDatabase.getInstance(this@EventDetails)
            val user: User? = appDB.userDao().getUserById(userId)
            if (user != null) {
                userName = user.userName.toString()
                userImage = user.photo.toString()
                userEmail = user.userEmail.toString()
            }
        }

        //check connection
        if(CheckConnection.checkForInternet(this)){
            connection = true
        }

        eventId  = intent.getIntExtra("eventId", -1)
        val dateFormated = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val personJoined = intent.getIntExtra("totalPerson", -1)
        val event = getEvent(eventId)
        if(event!= null){
            val contactNumber = event.eventContactNumber
            val eventMaxJoined = event.eventMaxPerson.toString()
            imageString = event.eventImage
            var bitmap = BitmapConverter.convertStringToBitmap(imageString)
            binding.eventImg.setImageBitmap(bitmap)
            binding.eventLocationEdit.text = event.eventLocation
            binding.eventTitle.text = event.eventName
            binding.eventNumberpoeple.text = "$personJoined/$eventMaxJoined"
            binding.eventTimeEdit.text = event.eventDate.format(dateFormated)
            binding.eventLocationEdit.text = event.eventLocation
            binding.eventOrganizationEdit.text = event.eventOrgName
            binding.eventContactnumberEdit.text = contactNumber
            binding.eventContactnumberEdit.setPaintFlags(binding.eventContactnumberEdit.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

            binding.eventContactnumberEdit.setOnClickListener{
                intent = Intent(Intent.ACTION_DIAL)
                intent.setData(Uri.parse("tel:$contactNumber"))
                startActivity(intent)
            }
            binding.eventContactpersonEdit.text = event.eventContactPerson
            binding.eventContentEdit.text = event.eventDescription

            if(personJoined >= event.eventMaxPerson){
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

        if(!connection){
            binding.btnJoin.setOnClickListener{
                Snackbar.make(findViewById(android.R.id.content), "No connection now!", Snackbar.LENGTH_SHORT)
                    .setAction("Retry", View.OnClickListener {
                        finish();
                        startActivity(intent);
                    })
                    .show()
            }
        }
        else{
            builder = AlertDialog.Builder(this)
            if(!checkAvailability()){
                binding.btnJoin.isEnabled = false
                binding.btnJoin.setBackgroundResource(R.drawable.event_joined_btn)
                binding.btnJoin.text = "Your Are Joined"
            }

            binding.btnJoin.setOnClickListener{
                confirmation()
            }
        }


    }

    private fun checkAvailability(): Boolean{
        for (eventJoined in eventJoinedList){
            if(eventJoined.userId == userId && eventId == eventJoined.eventId){
                return false
            }
        }
        return true
    }

    private fun getEvent(eventId: Int): Event?{
        for(event in eventList){
            if(event.eventId == eventId){
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
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val formattedDateTime = currentDateTime.format(formatter)

                data["eventId"] = eventId.toString()
                data["userId"] = userId.toString()
                data["userEmail"] = userEmail
                data["userImage"] = userImage
                data["userName"] = userName
                data["createDate"] = formattedDateTime
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }
}