package com.example.assignment.admin.event

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.user.event.Event
import com.example.assignment.user.event.eventJoinedList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AdminEventAdapter(var eventList: List<Event>):RecyclerView.Adapter<AdminEventAdapter.AdminEventViewHolder>() {
    private var context: Context? = null
    private var imageString: String? = null
    lateinit var builder : AlertDialog.Builder

    inner class AdminEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val eventImage : ImageView = itemView.findViewById(R.id.admin_event_image) as ImageView
        val eventName: TextView = itemView.findViewById(R.id.admin_event_name) as TextView
        val eventPersonJoined: TextView = itemView.findViewById(R.id.admin_event_person_joined) as TextView
        val editBtn: ImageButton = itemView.findViewById(R.id.admin_event_editBtn) as ImageButton
        val deleteBtn: ImageButton = itemView.findViewById(R.id.admin_event_deleteBtn) as ImageButton
        val eventCardView : CardView = itemView.findViewById(R.id.admin_event_card) as CardView
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminEventAdapter.AdminEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_event_card,parent, false)
        val vh = AdminEventViewHolder(view)
        context = parent.context
        builder = AlertDialog.Builder(context!!)
        return vh
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: AdminEventAdapter.AdminEventViewHolder, position: Int) {
        val currentEvent = eventList[position]
        var totalPerson = personJoined(currentEvent.eventId)
        var maxPerson = currentEvent.eventMaxPerson

        imageString = currentEvent.eventImage
        var bitmap = BitmapConverter.convertStringToBitmap(imageString)
        holder.eventImage.setImageBitmap(bitmap)
        holder.eventName.text = currentEvent.eventName
        holder.eventPersonJoined.text = "$totalPerson/$maxPerson person joined"
        holder.deleteBtn.setOnClickListener{
            builder.setMessage("Are you sure want to delete this event?")
                .setCancelable(true)
                .setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss() // Dismiss the first dialog
                    deleteEvent(currentEvent.eventId)
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })
                .show()
        }

        holder.editBtn.setOnClickListener{
            val bundle = Bundle()
            val fragment = AdminEventUpdateFragment()
            bundle.putInt("eventId", currentEvent.eventId)
            fragment.arguments = bundle
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun personJoined(eventId:Int):Int{
        var personJoined = 0
        for(eventJoined in eventJoinedList){
            if(eventJoined.eventId == eventId){
                personJoined++
            }
        }
        return personJoined
    }

    private fun deleteEvent(eventId:Int){

        val url : String = "http://10.0.2.2/Assignment(Mobile)/deleteEvent.php"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("Register",response)
                if (response == "success") {
                    val fragmentManager = (context as FragmentActivity).supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container,AdminEventFragment())
                    transaction.commit()
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
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                val formattedDateTime = currentDateTime.format(formatter)

                data["eventId"] = eventId.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }
}