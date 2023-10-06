package com.example.assignment.user.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.databinding.ProfileHistoryBinding
import com.example.assignment.user.donate.Donate
import com.example.assignment.user.donate.DonateAdapter
import com.example.assignment.user.donate.donateList
import com.example.assignment.user.donate.donatePersonList
import com.example.assignment.user.donate.donatePersonListById
import com.example.assignment.user.event.eventJoinedList
import com.example.assignment.user.event.eventJoinedListById
import com.example.assignment.user.event.eventList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class DonateHistory: AppCompatActivity() {

    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: DonateHistoryAdapter
    private lateinit var binding: ProfileHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_history)
        binding = ProfileHistoryBinding.inflate(layoutInflater)

        donateList.clear()
        donateHistoryList.clear()
        recycleView = binding.historyRecycleView
        recycleView.layoutManager = LinearLayoutManager(applicationContext)
        recycleView.setHasFixedSize(true)

        donateGetAll()

        setContentView(binding.root)

    }

    private fun donateGetAll(){
        val url = "http://10.0.2.2/Assignment(Mobile)/donateGetAll.php"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("donate", response)
                try {
                    if (response != null) {
                        val array = JSONArray(response)

                        for (i in 0 until array.length()) {
                            val donate = array.getJSONObject(i)
                            val data = Donate(
                                donate.getInt("donateId"),
                                donate.getInt("adminId"),
                                donate.getString("donateName"),
                                donate.getString("donateImage"),
                                donate.getString("donateCategory"),
                                donate.getString("donateOrgname"),
                                donate.getString("donateStartTime"),
                                donate.getString("donateEndTime"),
                                donate.getDouble("totalDonation"),
                                donate.getString("donateDescription"),
                                donate.getInt("isDeleted"),
                            )
                            donateList.add(data)


                        }

                        donateHistoryList = checkDonateRecord()

                        adapter = DonateHistoryAdapter(this, donateHistoryList)
                        binding.historyRecycleView.adapter  = adapter

                        binding.loadingBar.visibility = View.GONE
                        binding.loadingText.visibility = View.GONE

                    }
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
            }) {
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun checkDonateRecord():MutableList<History>{
        var donateHistory = mutableListOf<History>()
        if(donatePersonListById.size>0 && donateList.size>0){
            for(donatePerson in donatePersonList){
                for(donate in donateList){
                    if(donatePerson.donateId == donate.donateId){
                        var history = History(
                            id = donate.donateId,
                            title = donate.donateName,
                            image = donate.donateImage.toString(),
                            status = "Donation",
                            createTime = donatePerson.createDate
                        )
                        donateHistory.add(history)
                    }
                }
            }
        }
        return donateHistory
    }
}