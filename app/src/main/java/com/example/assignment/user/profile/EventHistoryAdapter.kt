package com.example.assignment.user.profile

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.user.event.EventDetails
import com.example.assignment.user.event.eventJoinedList


class EventHistoryAdapter (    private val context: Context,
                               var eventHistoryList : List<History>) :
    RecyclerView.Adapter<EventHistoryAdapter.EventHistoryViewHolder>() {

    private var imageString: String? = null

    inner class EventHistoryViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val cardView : CardView = itemView.findViewById(R.id.card_view) as CardView
        val image : ImageView = itemView.findViewById(R.id.record_image) as ImageView
        val name : TextView = itemView.findViewById(R.id.record_name) as TextView
        val status : TextView = itemView.findViewById(R.id.record_status) as TextView
        val time : TextView = itemView.findViewById(R.id.record_time) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.profile_card,parent, false)

        return EventHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return eventHistoryList.size
    }

    override fun onBindViewHolder(holder: EventHistoryViewHolder, position: Int) {
        var currentEventHistory = eventHistoryList[position]
        imageString = currentEventHistory.image
        val totalJoined = personJoined(currentEventHistory.id)
        var bitmap = BitmapConverter.convertStringToBitmap(imageString)
        holder.image.setImageBitmap(bitmap)
        holder.name.text = currentEventHistory.title
        holder.status.text = currentEventHistory.status
        holder.time.text = currentEventHistory.createTime
        holder.cardView.setOnClickListener{
            val intent = Intent(context, EventDetails::class.java)
            intent.putExtra("eventId", currentEventHistory.id)
            intent.putExtra("totalPerson", totalJoined)
            context?.startActivity(intent)
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
}