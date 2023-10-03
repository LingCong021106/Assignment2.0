//package com.example.assignment
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.android.volley.AuthFailureError
//import com.android.volley.Response
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
//import com.example.assignment.user.event.EventJoinedAdapter
//import org.json.JSONException
//import org.json.JSONObject
//
//class FundraisingDetails : AppCompatActivity() {
//
//    lateinit var image1 : ImageView
//    lateinit var image2 : ImageView
//    private lateinit var listPeopleRecycler : RecyclerView
//    private lateinit var newArrayList: ArrayList<ListDonate>
//    lateinit var imageId : Array<Int>
//    lateinit var name : Array<String>
//    lateinit var btnDonate : Button
//    lateinit var builder : AlertDialog.Builder
//    private val URL :String = "http://192.168.0.19:8081/mobile/fundraisingdonate.php"
//    lateinit var funId : String
//    private val nameList: ArrayList<String> = ArrayList()
//    private val imageIdList: ArrayList<Int> = ArrayList()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fundraising_details)
//
//        image1 = findViewById(R.id.fundraising_img)
//        image1.setImageResource(R.drawable.dice_1)
//
//        image2 = findViewById(R.id.fundraising_orgazation_img)
//        image2.setImageResource(R.drawable.dice_1)
//
//        val textViewTitle : TextView
//        funId = intent.getStringExtra("id").toString().trim()
//        textViewTitle = findViewById(R.id.fundraising_title_edit)
//        textViewTitle.text = intent.getStringExtra("name").toString()
//
//        name = emptyArray()
//        imageId= emptyArray()
//
//        listPeopleRecycler = findViewById(R.id.fundraising_people_list)
//        listPeopleRecycler.layoutManager = LinearLayoutManager(this)
//        listPeopleRecycler.setHasFixedSize(true)
//
//        newArrayList = arrayListOf<ListDonate>()
//
//        val stringRequest: StringRequest = object : StringRequest(
//            com.android.volley.Request.Method.POST, URL,
//            Response.Listener { response ->
//                Log.d("res", response)
//                try {
//                    val jsonObject = JSONObject(response)
//                    val message = jsonObject.getString("status")
//                    if (message == "success") {
//                        val dataArray = jsonObject.getJSONArray("data")
//                        for (i in 0 until dataArray.length()) {
//                            val dataObject = dataArray.getJSONObject(i)
//                            val nameListItem = dataObject.getString("name").toString()
//                            val donateAmount = dataObject.getInt("amount")
//                            val combinedString = "$nameListItem donate RM$donateAmount"
//                            val imageIdListItem = R.drawable.dice_1
//                            nameList.add(combinedString)
//                            imageIdList.add(imageIdListItem)
//                        }
//                        getUserdata()
//                    } else if (message == "failure") {
//                        Toast.makeText(
//                            this@FundraisingDetails,
//                            "Invalid Id",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                    // Handle JSON parsing error here
//                }
//            },
//            Response.ErrorListener { error ->
//                Toast.makeText(
//                    this@FundraisingDetails,
//                    error.toString().trim { it <= ' ' },
//                    Toast.LENGTH_SHORT
//                ).show()
//            }) {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String>? {
//                val data: MutableMap<String, String> = HashMap()
//                data["id"] = funId
//                return data
//            }
//        }
//        val requestQueue = Volley.newRequestQueue(applicationContext)
//        requestQueue.add(stringRequest)
//
//        btnDonate = findViewById(R.id.donate_btn)
//        btnDonate.setOnClickListener { gotoDonate() }
//
//
//    }
//
//    private fun gotoDonate() {
//        val intent = Intent(this@FundraisingDetails, Donate::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    private fun getUserdata(){
//
//        val nameArray = nameList.toTypedArray()
//        val imageIdArray = imageIdList.toTypedArray()
//
//        for (i in imageIdArray.indices) {
//            val people = ListDonate(imageIdArray[i], nameArray[i])
//            newArrayList.add(people)
//        }
//
//        listPeopleRecycler.adapter = EventJoinedAdapter(newArrayList)
//    }
//
//
//}