package com.example.assignment.user.event

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.example.assignment.admin.AdminHome
import org.w3c.dom.Text

class EventAdapter(var eventList : List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private val itemNames = arrayOf("name1", "name2")
    private val itemImage = intArrayOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
    )
    private val itemDescription = arrayOf("desc1, desc2")
    private val itemRegEndDate = arrayOf("20/12/2022", "20/11/2022")
    private val itemOrgName = arrayOf("organization name1", "organization name2")
    private val itemContactNumber = arrayOf("contact number1", "contact number 2")
    private val itemContactPerson = arrayOf("contact person1", "contact person2")
    private val itemMaxPerson = arrayOf(10, 20)
    private val itemEventDate = arrayOf("20/12/2022", "20/11/2022")
    private val itemLocation = arrayOf("location1", "location2")


    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val eventImage : ImageView = itemView.findViewById(R.id.event_image)
        val eventName: TextView = itemView.findViewById(R.id.event_name)
        val eventRegEndTime: TextView = itemView.findViewById(R.id.event_registration_end_time)
        val eventMaxPerson: TextView = itemView.findViewById(R.id.event_max_person)
        val eventSelectedButton: Button = itemView.findViewById(R.id.event_selected_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_card,parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventAdapter.EventViewHolder, position: Int) {
        val currentEvent = eventList[position]
        holder.eventName.text = currentEvent.name
        holder.eventRegEndTime.text = currentEvent.registrationEndDate
//        holder.eventImage.setImageResource(currentEvent.image)
        holder.eventMaxPerson.text = currentEvent.maxPerson.toString()
//        holder.eventSelectedButton.setOnClickListener{
//
//        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

}