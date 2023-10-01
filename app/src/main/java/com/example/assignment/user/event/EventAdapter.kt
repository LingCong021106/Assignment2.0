package com.example.assignment.user.event

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.admin.AdminHome
import com.google.android.material.internal.ContextUtils.getActivity
import org.json.JSONArray
import org.json.JSONException
import java.io.Serializable


class EventAdapter(var eventList : List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var context: Context? = null
    private var imageString: String? = null
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val eventImage : ImageView = itemView.findViewById(R.id.event_image) as ImageView
        val eventName: TextView = itemView.findViewById(R.id.event_name) as TextView
        val eventRegEndTime: TextView = itemView.findViewById(R.id.event_registration_end_time) as TextView
        val eventMaxPerson: TextView = itemView.findViewById(R.id.event_max_person) as TextView
        val eventSelectedButton: Button = itemView.findViewById(R.id.event_selected_btn) as Button
        val userCardView : CardView = itemView.findViewById(R.id.user_card_view) as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_card,parent, false)
        val vh = EventViewHolder(view)
        context = parent.context
        return vh
    //        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventAdapter.EventViewHolder, position: Int) {
        val currentEvent = eventList[position]
        var totalPerson = personJoined(currentEvent.id)
        var maxPerson = currentEvent.maxPerson
        holder.eventName.text = currentEvent.name
        holder.eventRegEndTime.text = currentEvent.registrationEndDate
        imageString = currentEvent.image
        var bitmap = BitmapConverter.convertStringToBitmap(imageString)
        holder.eventImage.setImageBitmap(bitmap)
        holder.eventMaxPerson.text = "$totalPerson/$maxPerson person joined"
        if(totalPerson>= maxPerson){
            holder.eventMaxPerson.setTextColor(Color.parseColor("#F30F0F"))

        }
        else{
            holder.eventMaxPerson.setTextColor(Color.parseColor("#14EC18"))
        }
        holder.userCardView.setOnClickListener {
            val intent = Intent(context, EventDetails::class.java)
            intent.putExtra("eventId", currentEvent.id)
            intent.putExtra("totalPerson", totalPerson)
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
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
}