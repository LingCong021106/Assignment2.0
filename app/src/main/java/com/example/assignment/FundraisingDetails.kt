package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class FundraisingDetails : AppCompatActivity() {

    lateinit var image1 : ImageView
    lateinit var image2 : ImageView
    private lateinit var listPeopleRecycler : RecyclerView
    private lateinit var newArrayList: ArrayList<ListDonate>
    lateinit var btnDonate : Button
    lateinit var builder : AlertDialog.Builder
    private val URL :String = "http://192.168.0.21:8081/Assignment(Mobile)/fundraisingdonate.php"
    lateinit var funId : String
    private val nameList: ArrayList<String> = ArrayList()
    private val imageIdList: ArrayList<Int> = ArrayList()
    private var amount : Int = 0
    private var donateID : String ="1"
    lateinit var progressDonate : ProgressBar
    lateinit var progressDonatePercentage : TextView
    lateinit var raisedAmount : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fundraising_details)

        val textViewTitle : TextView
        textViewTitle = findViewById(R.id.fundraising_title_edit)
        progressDonate = findViewById(R.id.progress_donate)
        progressDonatePercentage = findViewById(R.id.progress_donate_txt)
        raisedAmount = findViewById(R.id.fundraising_currentdonate_edit)

        listPeopleRecycler = findViewById(R.id.fundraising_people_list)
        listPeopleRecycler.layoutManager = LinearLayoutManager(this)
        listPeopleRecycler.setHasFixedSize(true)

        newArrayList = arrayListOf<ListDonate>()

        val stringRequest: StringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST, URL,
            Response.Listener { response ->
                Log.d("res", response)
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("status")
                    if (message == "success") {
                        val dataArray = jsonObject.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)
                            val nameListItem = dataObject.getString("userName").toString()
                            val donateAmount = dataObject.getString("amount").toInt()
                            val combinedString = "$nameListItem donate RM$donateAmount"
                            val imageIdListItem = R.drawable.dice_1
                            amount +=donateAmount
                            nameList.add(combinedString)
                            imageIdList.add(imageIdListItem)
                        }
                        //calculate progress donate
                        val target: Int = 80000
                        val progressTotal: Double = (amount.toDouble() / target.toDouble()) * 100.0

                        if(progressTotal >=100){
                            progressDonate.progress = 100
                            progressDonatePercentage.text = "100 %"
                        }else{
                            progressDonate.progress = progressTotal.toInt()
                            val formattedProgress = String.format("%.2f",progressTotal)
                            progressDonatePercentage.text = "$formattedProgress %"
                        }

                        raisedAmount.text = "RM $amount"

                        getUserdata()

                    } else if (message == "failure") {
                        Toast.makeText(
                            this@FundraisingDetails,
                            "No people join yet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        this@FundraisingDetails,
                        e.message.toString().trim { it <= ' ' },
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    this@FundraisingDetails,
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                data["id"] = donateID
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)

        btnDonate = findViewById(R.id.donate_btn)
        btnDonate.setOnClickListener { gotoDonate() }


    }

    private fun gotoDonate() {
        val intent = Intent(this@FundraisingDetails, Donate::class.java)
        intent.putExtra("donateID", donateID)
        startActivity(intent)
    }

    private fun getUserdata(){

        val nameArray = nameList.toTypedArray()
        val imageIdArray = imageIdList.toTypedArray()

        for (i in imageIdArray.indices) {
            val people = ListDonate(imageIdArray[i], nameArray[i])
            newArrayList.add(people)
        }

        listPeopleRecycler.adapter = MyAdapter(newArrayList)
    }


}