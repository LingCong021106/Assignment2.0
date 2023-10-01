package com.example.assignment.user.donate

import android.content.Intent
import android.graphics.Paint
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.BitmapConverter
import com.example.assignment.CheckConnection
import com.example.assignment.Payment
import com.example.assignment.R
import com.example.assignment.databinding.DonateDetailsBinding
import com.example.assignment.databinding.EventDetailsBinding
import com.example.assignment.user.event.Event
import com.example.assignment.user.event.EventJoined
import com.example.assignment.user.event.EventJoinedAdapter
import com.example.assignment.user.event.eventJoinedList
import com.example.assignment.user.event.eventList
import java.time.format.DateTimeFormatter

class DonateDetails : AppCompatActivity(){

    private lateinit var binding: DonateDetailsBinding
    private lateinit var recycleView : RecyclerView
    private lateinit var adapter: DonatePeopleAdapter
    var donateId : Int = 0
    private var imageString: String? = null
    lateinit var builder : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.donate_details)
        binding = DonateDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        donateId = intent.getIntExtra("donateId", -1)
        val currentDonate = intent.getDoubleExtra("currentDonate", 0.0)

        val dateFormated = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val donate = getDonate(donateId)
        if(donate!= null){
            imageString = donate.image
            var bitmap = BitmapConverter.convertStringToBitmap(imageString)
            binding.fundraisingImg.setImageBitmap(bitmap)
            binding.fundraisingOrganizationEdit.setText(donate.organizationName)
            binding.fundraisingTitleEdit.setText(donate.name)
            binding.fundraisingStarttimeEdit.setText(donate.startTime)
            binding.fundraisingEndtimeEdit.setText(donate.endTime)
            var decimalFormat = DecimalFormat("#,##0.00")
            binding.fundraisingTotaldonateEdit.setText("RM" + decimalFormat.format(donate.totalDonation))
            binding.fundraisingCurrentdonateEdit.setText("RM" + decimalFormat.format(currentDonate))
            binding.eventContentEdit.setText(donate.description)
            var donatePercent = (currentDonate/donate.totalDonation)*100
            if(donatePercent>=100){
                donatePercent = 100.00
            }
            binding.progressDonateTxt.setText(decimalFormat.format(donatePercent) + "%")
            binding.progressDonate.progress = donatePercent.toInt()
        }

        recycleView = binding.fundraisingPeopleList
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.setHasFixedSize(true)


        adapter = DonatePeopleAdapter(getDonatePerson(donateId))
        binding.fundraisingPeopleList.adapter  = adapter

        binding.donateBtn.setOnClickListener{
            intent = Intent(this,Payment::class.java)
            intent.putExtra("donateID",donateId)
            startActivity(intent)
        }

//        builder = AlertDialog.Builder(this)
//        if(!checkAvailability()){
//            binding.btnJoin.isEnabled = false
//            binding.btnJoin.setBackgroundResource(R.drawable.event_joined_btn)
//            binding.btnJoin.text = "Your Are Joined"
//        }
//
//        binding.btnJoin.setOnClickListener{
//            if (CheckConnection.checkForInternet(this)) {
//                if(binding.btnJoin.isEnabled) {
//                    confirmation()
//                }
//            } else {
//                Toast.makeText(this, "Connection error, Please try later", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun getDonate(donateId: Int): Donate?{
        for(donate in donateList){
            if(donate.id == donateId){
                return donate
            }
        }
        return null
    }

    private fun getDonatePerson(donateId:Int): MutableList<DonatePerson> {
        var donatePeopleList = mutableListOf<DonatePerson>()
        for (donatePerson in donatePersonList){
            if(donatePerson.donateId == donateId){
                donatePeopleList.add(donatePerson)
            }
        }
        return donatePeopleList
    }

    private fun checkAvailability(): Boolean{
        val userEmail = "tester@gmail.com"
        for (donatePerson in donatePersonList){
            if(donatePerson.userEmail == userEmail && donateId == donatePerson.donateId){
                return false
            }
        }
        return true
    }
}