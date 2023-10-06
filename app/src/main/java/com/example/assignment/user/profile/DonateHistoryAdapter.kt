package com.example.assignment.user.profile

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.user.donate.DonateDetails
import com.example.assignment.user.donate.donatePersonList
import com.example.assignment.user.event.EventDetails

class DonateHistoryAdapter(val context : Context, val donateHistoryList: List<History>): RecyclerView.Adapter<DonateHistoryAdapter.DonateHistoryViewHolder>(){
    private var imageString: String? = null

    inner class DonateHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cardView : CardView = itemView.findViewById(R.id.card_view) as CardView
        val image : ImageView = itemView.findViewById(R.id.record_image) as ImageView
        val name : TextView = itemView.findViewById(R.id.record_name) as TextView
        val status : TextView = itemView.findViewById(R.id.record_status) as TextView
        val time : TextView = itemView.findViewById(R.id.record_time) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonateHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.profile_card,parent, false)

        return DonateHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return donateHistoryList.size
    }

    override fun onBindViewHolder(holder: DonateHistoryViewHolder, position: Int) {
        var currentDonateHistory = donateHistoryList[position]
        var totalRaised = getCurrentDonate(currentDonateHistory.id)
        imageString = currentDonateHistory.image
        var bitmap = BitmapConverter.convertStringToBitmap(imageString)
        holder.image.setImageBitmap(bitmap)
        holder.name.text = currentDonateHistory.title
        holder.status.text = currentDonateHistory.status
        holder.time.text = currentDonateHistory.createTime
        holder.cardView.setOnClickListener{
            val intent = Intent(context, DonateDetails::class.java)
            intent.putExtra("donateId", currentDonateHistory.id)
            intent.putExtra("currentDonate", totalRaised)
            context?.startActivity(intent)
        }
    }

    private fun getCurrentDonate(donateId:Int):Double{
        var totalRaised = 0.0
        for(donatePerson in donatePersonList){
            if(donatePerson.donateId == donateId){
                totalRaised += donatePerson.userTotalDonate
            }
        }
        return totalRaised
    }
}