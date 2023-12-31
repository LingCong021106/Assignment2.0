package com.example.assignment.user.donate

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.BitmapConverter
import com.example.assignment.R

class DonateAdapter(var donaeList : List<Donate>): RecyclerView.Adapter<DonateAdapter.DonateViewHolder>() {
    private var context: Context? = null
    private var imageString: String? = null

    inner class DonateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val donateImage : ImageView = itemView.findViewById(R.id.donate_image) as ImageView
        val donateName: TextView = itemView.findViewById(R.id.donate_name) as TextView
        val donateRegTime: TextView = itemView.findViewById(R.id.donate_registration_end_time) as TextView
        val donateTotalNeed: TextView = itemView.findViewById(R.id.donate_total) as TextView
        val donateSelectedBtn: Button = itemView.findViewById(R.id.donate_selected_btn) as Button
        val donateCardView : CardView = itemView.findViewById(R.id.card_view) as CardView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.donate_card,parent, false)
        val vh = DonateViewHolder(view)
        context = parent.context
        return vh
    }

    override fun getItemCount(): Int {
        return donaeList.size
    }

    override fun onBindViewHolder(holder: DonateViewHolder, position: Int) {
        var currentDonate = donaeList[position]
        var startDate = currentDonate.donateStartTime
        var endDate = currentDonate.donateEndTime
        var totalNeed = currentDonate.totalDonation
        var decimalFormat = DecimalFormat("#,##0.00")
        var formattedValue: String = decimalFormat.format(totalNeed)
        imageString = currentDonate.donateImage.toString()
        var bitmap = BitmapConverter.convertStringToBitmap(imageString)
        var totalRaised = getCurrentDonate(currentDonate.donateId)
        holder.donateImage.setImageBitmap(bitmap)
        holder.donateName.text = currentDonate.donateName
        holder.donateRegTime.text = "Available Time:\n$startDate until $endDate"
        holder.donateTotalNeed.text = "RM $formattedValue"
        holder.donateCardView.setOnClickListener{
            val intent = Intent(context, DonateDetails::class.java)
//            intent.putExtra("eventId", currentEvent.id)
//            intent.putExtra("totalPerson", totalPerson)
            context?.startActivity(intent)
        }
        holder.donateCardView.setOnClickListener{
            val intent = Intent(context, DonateDetails::class.java)
            intent.putExtra("donateId", currentDonate.donateId)
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