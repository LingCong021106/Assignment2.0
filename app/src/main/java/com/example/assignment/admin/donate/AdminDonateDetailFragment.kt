package com.example.assignment.admin.donate

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.database.donate.Donate
import com.example.assignment.database.donate.DonatePeople
import com.example.assignment.database.donate.donateList
import com.example.assignment.database.donate.donatePeopleList
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AdminDonateDetailFragment : Fragment() {

    val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
    val userRole = sharedPreferences.getString("userRole", "users")
    val userName= sharedPreferences.getString("userName", "")
    val userEmail = sharedPreferences.getString("userEmail", "")
    val userId = sharedPreferences.getString("userId","")

    private lateinit var back : ImageView
    private lateinit var donateImage : ImageView
    private lateinit var organizationEditText : TextInputEditText
    private lateinit var titleEditText : TextInputEditText
    private lateinit var targetEditText : TextInputEditText
    private lateinit var currentValue : TextInputEditText
    private lateinit var progressBar : ProgressBar
    private lateinit var percentages : TextView
    private lateinit var startDateEditText : Button
    private lateinit var endDateEditText : Button
    private lateinit var descriptionEditText : TextInputEditText
    private lateinit var updateBtn : Button
    private lateinit var recordBtn : Button


    private var imageString : String? = ""
    private  var organization : String? = ""
    private var title : String? = ""
    private var startDate : String? = ""
    private var endDate : String? = ""
    private var description : String? = ""


    private var donateID : Int = 0
    private var target : Int = 0
    private var userID : Int = 0
    private var current : Int = 0
    private var current1 : Int = 0

    private var targetTest : String = ""

    lateinit var builder : AlertDialog.Builder
    private val URLupdate :String = "http://192.168.0.192/Assignment(Mobile)/updateDonation.php"
    private val URLgetpeople :String = "http://192.168.0.192/Assignment(Mobile)/getDonatePeople.php"

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: RecordAdapter

    private var startChange = 0
    private var endChange = 0

    private val cal: Calendar = Calendar.getInstance()
    private val startDateCal: Calendar = Calendar.getInstance()
    private val endDateCal: Calendar = Calendar.getInstance()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_donate_detail, container, false)

        builder = AlertDialog.Builder(requireContext())
        val id = arguments?.getString("id")
        if (id != null) {
            donateID = id.toInt()
        }
        imageString = arguments?.getString("donateImage")
        val target1 = arguments?.getString("target")
        if (target1 != null) {
            target = target1.toInt()
        }

        back = view.findViewById(R.id.donateDetailBack)
        donateImage = view.findViewById(R.id.detailDonateImage)
        organizationEditText = view.findViewById(R.id.DetailDonateOriganization)
        titleEditText = view.findViewById(R.id.DetailDonateTitle)
        targetEditText = view.findViewById(R.id.donateTargetValue)
        progressBar = view.findViewById(R.id.detailProgressBar)
        percentages = view.findViewById(R.id.percentages)
        startDateEditText = view.findViewById(R.id.detailDonateStartBtn)
        endDateEditText = view.findViewById(R.id.detailDonateEndBtn)
        descriptionEditText = view.findViewById(R.id.detailDonateDescription)
        updateBtn = view.findViewById(R.id.updateDonateButton)
        currentValue = view.findViewById(R.id.donateCurrentValue)
        recyclerView = view.findViewById(R.id.recordRecycleView)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        back.setOnClickListener { loadFragment(AdminDonateFragment()) }
        val bitmap = BitmapConverter.convertStringToBitmap(imageString)
        donateImage.setImageBitmap(bitmap)
        organizationEditText.setText(arguments?.getString("organization"))
        titleEditText.setText(arguments?.getString("title"))
        targetEditText.setText(target.toString())
        startDateEditText.setText(arguments?.getString("startDate"))
        endDateEditText.setText(arguments?.getString("endDate"))
        descriptionEditText.setText(arguments?.getString("description"))

        targetTest = targetEditText.text.toString()

        arguments?.clear()

        //currentValue.setText(targetTest)

        getPeople()


        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {

                if(startChange == 1){
                    startDateCal.set(Calendar.YEAR, year)
                    startDateCal.set(Calendar.MONTH, monthOfYear)
                    startDateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }else if(endChange == 1){
                    endDateCal.set(Calendar.YEAR, year)
                    endDateCal.set(Calendar.MONTH, monthOfYear)
                    endDateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                updateDateInView()
            }
        }

        startDateEditText!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(requireContext(),
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
                startChange = 1
            }

        })

        endDateEditText!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(requireContext(),
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
                endChange = 1
            }

        })

//        addBtn.setOnClickListener {
//            insertTitle = title.text.toString()
//            insertTarget = target.text.toString().trim()
//            insertDescription = description.text.toString()
//
//
//            if (insertImage.isEmpty() ||insertTitle.isEmpty() || insertTarget.isEmpty() || insertDescription.isEmpty() || insertStartDate.isEmpty() || insertEndDate.isEmpty()) {
//                Toast.makeText(requireContext(),"Field cannot be empty", Toast.LENGTH_LONG).show()
//            } else if (!insertTarget.matches("-?\\d+".toRegex())) {
//                Toast.makeText(requireContext(),"Please enter a valid integer", Toast.LENGTH_LONG).show()
//            } else {
//                addNewRemoteDonation()
//            }
//
//
//        }


        updateBtn.setOnClickListener { update() }
        donateImage.setOnClickListener { selectImageFromGallery() }

        return view
    }

    fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    // Handle the result of image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            val inputStream: InputStream? =
                selectedImageUri?.let { requireContext().contentResolver.openInputStream(it) }
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            imageString = BitmapConverter.convertBitmapToString(bitmap)

            if (selectedImageUri != null) {
                donateImage.setImageURI(selectedImageUri)
            }
        }
    }


    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if(startChange == 1){
            if(validateDates() == true){
                startDateEditText!!.text = sdf.format(startDateCal.getTime()).toString()

            }else{
                startDateEditText!!.setText(arguments?.getString("startDate"))

            }

            startChange = 0
        }

        if(endChange == 1){
            if(validateDates() == true){
                endDateEditText!!.text = sdf.format(endDateCal.getTime()).toString()

            }else{
                endDateEditText.setText(arguments?.getString("endDate"))

            }

            endChange = 0
        }

    }

    private fun validateDates() : Boolean {
        if (startDateCal.after(endDateCal)) {
            Toast.makeText(requireContext(),"Invalid Date, End Date Can't Before Start Date", Toast.LENGTH_LONG).show()
            return false
        }else{
            return true
        }
    }




    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }



    fun update(){
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, URLupdate,
            Response.Listener { response ->
                Log.d("Register",response)
                if (response == "success") {
                    builder.setMessage("Updated Donation")
                        .setCancelable(true)
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss() // Dismiss the second dialog
                            loadFragment(AdminDonateFragment())
                        })
                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                } else if (response == "failure") {
                    builder.setMessage("Fail Update Donation")
                        .setCancelable(true)
                        .setPositiveButton("back to home", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss() // Dismiss the second dialog

                        })
                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    requireContext(),
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                data["id"] = donateID.toString()
                data["image"] = imageString.toString()
                data["organization"] = organizationEditText.text.toString()
                data["title"] = titleEditText.text.toString()
                data["target"] = targetEditText.text.toString()
                data["startDate"] = startDateEditText.text.toString()
                data["endDate"] = endDateEditText.text.toString()
                data["description"] = descriptionEditText.text.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    private fun getPeople(){

        val stringRequest: StringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST, URLgetpeople,
            Response.Listener { response ->
                Log.d("res", response)
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("status")
                    if (message == "success") {
                        val dataArray = jsonObject.getJSONArray("data")
                        donatePeopleList.clear()
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)

                            val donatePeople = DonatePeople(
                                dataObject.getInt("id"), dataObject.getInt("donateID"),
                                dataObject.getInt("userID"), dataObject.getString("userName").toString(),
                                dataObject.getInt("amount"), dataObject.getString("date").toString()

                            )
                            donatePeopleList.add(donatePeople)

                            current += dataObject.getInt("amount")

                        }

                        adapter = RecordAdapter(donatePeopleList)

                        recyclerView.adapter = adapter



                        currentValue.setText(current.toString())
                        val progressTest: Double = (current.toDouble() / targetTest.toDouble()) * 100.0
                        progressBar.progress = progressTest.toInt()
                        val formattedProgress = String.format("%.2f", progressTest)
                        percentages.text = "$formattedProgress %"


                    } else if (message == "failure") {
                        Toast.makeText(
                            requireContext(),
                            "No Donation",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Handle JSON parsing error here
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    requireContext(),
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                data["id"] = donateID.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }




}