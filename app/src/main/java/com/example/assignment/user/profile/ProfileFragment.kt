package com.example.assignment.user.profile

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.assignment.BitmapConverter
import com.example.assignment.MainActivity
import com.example.assignment.R
import com.example.assignment.UserEditProfileActivity
import com.example.assignment.database.AppDatabase
import com.example.assignment.database.User
import com.example.assignment.databinding.FragmentUserHomeBinding
import com.example.assignment.databinding.FragmentUserProfileBinding
import com.example.assignment.user.donate.DonatePerson
import com.example.assignment.user.donate.donatePersonList
import com.example.assignment.user.donate.donatePersonListById
import com.example.assignment.user.event.Event
import com.example.assignment.user.event.EventAdapter
import com.example.assignment.user.event.EventJoined
import com.example.assignment.user.event.eventJoinedList
import com.example.assignment.user.event.eventJoinedListById
import com.example.assignment.user.event.eventList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mToolbar : Toolbar
    private var _binding: FragmentUserProfileBinding? = null
    private lateinit var appDB : AppDatabase
    private var userName : String = ""
    private var userImage : String = ""
    private var userEmail : String = ""
    private var userPhone : String = ""
    private var imageString : String = ""
    private var user: User = User()
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view: View = binding.root

        // Initialize SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId: Int = sharedPreferences.getInt("userId", -1)

        // Fetch user data from Room database
        CoroutineScope(Dispatchers.IO).launch {
            appDB = AppDatabase.getInstance(requireContext())
            user = appDB.userDao().getUserById(userId)!!
            getEventJoinedByUserId(userId)
            getDonatePersonByUserId(userId)
            getEventJoined()
            getDonatePerson()
            // Update UI with user data (on the main thread)
            withContext(Dispatchers.Main) {
                user?.let {
                    userName = it.userName.toString()
                    userImage = it.photo.toString()
                    userEmail = it.userEmail.toString()
                    userPhone = it.phone.toString()

                    // Update UI with the fetched user data
                    updateUI()
                }
            }
        }
        //toolbar
        setHasOptionsMenu(true)
        mToolbar = requireActivity().findViewById(R.id.toolbar)
        if (mToolbar != null) {
            (activity as AppCompatActivity?)?.setSupportActionBar(mToolbar)
        }
        mToolbar.title = "Profile"




        return view
    }
    private fun updateUI() {
        binding.profilContact.text = userPhone
        binding.profileEmail.text = userEmail
        binding.profileName.text = userName

        // Load user image
        var bitmap = BitmapConverter.convertStringToBitmap(userImage)
        Glide.with(this)
            .load(bitmap)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(binding.userImageView)
        binding.userImageView.setImageBitmap(bitmap)

        binding.btnLogout.setOnClickListener{
            logout(view)
        }

        binding.btnEdit.setOnClickListener{
            val intent = Intent(requireContext(), UserEditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnContact.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:012-23456789"))
            startActivity(intent)
        }

        //event
        if(eventJoinedList.size>0){
            binding.joinedEventTotal.text = eventJoinedListById.size.toString()
        }
        binding.eventJoinedRecord.setOnClickListener{
            val intent = Intent(context, EventHistory::class.java)
            startActivity(intent)
        }

        //donate
        var decimalFormat = DecimalFormat("#,##0.00")
        if(donatePersonListById.size>0){
            val totalDonate = totalValueDonate()
            binding.donateEventTotal.text = donatePersonList.size.toString()
            binding.donateValueTotal.text = decimalFormat.format(totalDonate)
        }
        binding.donationRecord.setOnClickListener{
            val intent = Intent(context, DonateHistory::class.java)
            startActivity(intent)
        }
    }

    fun logout(view: View?){
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        editor.remove("isLoggedIn")
        editor.remove("userRole")
        editor.remove("userEmail")
        editor.remove("userId")

        editor.apply()

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun getEventJoinedByUserId(userId: Int){
        eventJoinedListById.clear()
        val url = "http://10.0.2.2/Assignment(Mobile)/getEventJoinedByUserId.php"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("eventJoined", response)
                try {
                    if (response != null) {
                        val array = JSONArray(response)

                        for (i in 0 until array.length()) {
                            val eventJoined = array.getJSONObject(i)
                            val data = EventJoined(
                                eventJoined.getInt("eventJoinedId"),
                                eventJoined.getInt("eventId"),
                                eventJoined.getInt("userId"),
                                eventJoined.getString("userEmail"),
                                eventJoined.getString("userImage"),
                                eventJoined.getString("userName"),
                                eventJoined.getString("createDate"),
                            )
                            eventJoinedListById.add(data)

//                            CoroutineScope(Dispatchers.IO).launch{
//                                eventDB.eventDatabaseDao().insertEvent(data)
//                            }
                        }

                        updateUI()
                    }
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    activity,
                    "Error Connection, Please Try Later",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                //database
                data["userId"] = userId.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(context?.applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun getDonatePersonByUserId(userId: Int){
        donatePersonListById.clear()
        val url = "http://10.0.2.2/Assignment(Mobile)/getDonatePersonByUserId.php"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("donatePerson", response)
                try {
                    if (response != null) {
                        val array = JSONArray(response)

                        for (i in 0 until array.length()) {
                            val donatePerson = array.getJSONObject(i)
                            val data = DonatePerson(
                                donatePerson.getInt("donatePersonId"),
                                donatePerson.getInt("donateId"),
                                donatePerson.getInt("userId"),
                                donatePerson.getString("userEmail"),
                                donatePerson.getString("userName"),
                                donatePerson.getString("userImage"),
                                donatePerson.getDouble("userTotalDonate"),
                                donatePerson.getString("paymentMethod"),
                                donatePerson.getString("createDate"),
                                )
                            donatePersonListById.add(data)

//                            CoroutineScope(Dispatchers.IO).launch{
//                                eventDB.eventDatabaseDao().insertEvent(data)
//                            }
                        }
                        updateUI()
                    }
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    activity,
                    "Error Connection, Please Try Later",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                //database
                data["userId"] = userId.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(context?.applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun getEventJoined(){
        eventJoinedList.clear()
        val url = "http://10.0.2.2/Assignment(Mobile)/eventJoinedGetAll.php"
        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.POST, url,
                Response.Listener { response ->
                    Log.d("EventJoined", response)
                    try {
                        if (response != null) {
                            val array = JSONArray(response)

                            for (i in 0 until array.length()) {
                                val eventJoined = array.getJSONObject(i)
                                val data = EventJoined(
                                    eventJoined.getInt("eventJoinedId"),
                                    eventJoined.getInt("eventId"),
                                    eventJoined.getInt("userId"),
                                    eventJoined.getString("userEmail"),
                                    eventJoined.getString("userImage"),
                                    eventJoined.getString("userName"),
                                    eventJoined.getString("createDate"),
                                )
                                eventJoinedList.add(data)
//                                CoroutineScope(Dispatchers.IO).launch{
//                                    eventDB.eventDatabaseDao().insertEventJoined(data)
//                                }

                            }
                            updateUI()
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        context,
                        "Error Connection, Please Try Later",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {

            }
        val requestQueue = Volley.newRequestQueue(context?.applicationContext)
        requestQueue.add(stringRequest)

    }

    private fun getDonatePerson(){
        donatePersonList.clear()
        val url = "http://10.0.2.2/Assignment(Mobile)/donatePersonGetAll.php"
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("donatePerson", response)
                try {
                    if (response != null) {
                        val array = JSONArray(response)

                        for (i in 0 until array.length()) {
                            val donatePerson = array.getJSONObject(i)
                            val data = DonatePerson(
                                donatePerson.getInt("donatePersonId"),
                                donatePerson.getInt("donateId"),
                                donatePerson.getInt("userId"),
                                donatePerson.getString("userEmail"),
                                donatePerson.getString("userName"),
                                donatePerson.getString("userImage"),
                                donatePerson.getDouble("userTotalDonate"),
                                donatePerson.getString("paymentMethod"),
                                donatePerson.getString("createDate"),
                            )
                            donatePersonList.add(data)


                        }
                        updateUI()
                    }
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
            }) {
        }
        val requestQueue = Volley.newRequestQueue(context?.applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun totalValueDonate():Double{
        var totalDonate = 0.00
        if(donatePersonList.size>0){
            for(donatePerson in donatePersonList){
                totalDonate += donatePerson.userTotalDonate
            }
        }
        return totalDonate
    }
}