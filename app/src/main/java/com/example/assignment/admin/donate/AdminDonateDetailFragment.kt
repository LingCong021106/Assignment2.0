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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
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
import com.example.assignment.CheckConnection
import com.example.assignment.GlobalVariables.anyChange
import com.example.assignment.R
import com.example.assignment.database.donate.Donate
import com.example.assignment.database.donate.DonatePeople
import com.example.assignment.database.donate.DonatePeopleDatabase
import com.example.assignment.database.donate.donateList
import com.example.assignment.database.donate.donatePeopleList
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AdminDonateDetailFragment : Fragment(), AdapterView.OnItemSelectedListener {

//    val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
//    val userRole = sharedPreferences.getString("userRole", "users")
//    val userName= sharedPreferences.getString("userName", "")
//    val userEmail = sharedPreferences.getString("userEmail", "")
//    val userId = sharedPreferences.getString("userId","")

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
    private lateinit var spinner: Spinner


    private var imageString : String? = ""
//    private  var organization : String? = ""
//    private var title : String? = ""
//    private var startDate : String? = ""
//    private var endDate : String? = ""
//    private var description : String? = ""


    private var donateID : Int = 0
    private var target : Int = 0
    private var percentagesValue : Int = 0
    private var userID : Int = 0
    private var current : Int = 0
    private var current1 : Int = 0

    private var targetTest : String = ""

    lateinit var builder : AlertDialog.Builder
    private val URLupdate :String = "http://192.168.0.192/Assignment(Mobile)/updateDonation.php"
    private val URLgetpeople :String = "http://192.168.0.192/Assignment(Mobile)/getDonatePeople.php"

    private lateinit var appDb : DonatePeopleDatabase

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: RecordAdapter

    private var startChange = 0
    private var endChange = 0
    private lateinit var start : String
    private lateinit var end : String
    lateinit var startDate : Date
    lateinit var endDate : Date
    lateinit var spinnerDefaultValue : String
    lateinit var category : String


    private val cal: Calendar = Calendar.getInstance()
    private val startDateCal: Calendar = Calendar.getInstance()
    private val endDateCal: Calendar = Calendar.getInstance()
    val myFormat = "dd/MM/yyyy" // mention the format you need
    val sdf = SimpleDateFormat(myFormat, Locale.US)



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_donate_detail, container, false)

        builder = AlertDialog.Builder(requireContext())
        appDb = DonatePeopleDatabase.getDatabase(requireContext())

        val id = arguments?.getString("id")
        if (id != null) {
            donateID = id.toInt()
        }
        imageString = arguments?.getString("donateImage")
        val target1 = arguments?.getString("totalDonation")
        if (target1 != null) {
            target = target1.toInt()
        }
        val percentages1 = arguments?.getString("percentages")
        if(percentages1 != null){
            percentagesValue = percentages1.toInt()
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
        spinner = view.findViewById(R.id.spinnerDonateDetailCategory)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        back.setOnClickListener { loadFragment(AdminDonateFragment()) }
        val bitmap = BitmapConverter.convertStringToBitmap(imageString)
        donateImage.setImageBitmap(bitmap)
        organizationEditText.setText(arguments?.getString("organization"))
        titleEditText.setText(arguments?.getString("donateName"))
        targetEditText.setText(target.toString())
        progressBar.progress = percentagesValue
        percentages.text = "" + percentagesValue.toString() + "%"
        startDateEditText.text = arguments?.getString("startDate")
        endDateEditText.text = arguments?.getString("endDate")
        descriptionEditText.setText(arguments?.getString("description"))

        targetTest = targetEditText.text.toString()
        start = arguments?.getString("startDate").toString()
        end = arguments?.getString("endDate").toString()
        startDate = sdf.parse(start)
        endDate =  sdf.parse(end)
        spinnerDefaultValue = arguments?.getString("category").toString()


        arguments?.clear()


        if(CheckConnection.checkForInternet(requireContext())){
            getPeople()
        }else{
            getLocalDonationPeople()
        }




        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {

            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {


                if(startChange == 1){
                    startDateCal.set(Calendar.YEAR, year)
                    startDateCal.set(Calendar.MONTH, monthOfYear)
                    startDateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    if(validateDates() == true){
                        startDateEditText.text = sdf.format(startDateCal.getTime()).toString()

                    }else{
                        //startDateEditText.text = start
                        startDateCal.time = startDate
                        startDateCal.get(Calendar.YEAR)
                        startDateCal.get(Calendar.MONTH)
                        startDateCal.get(Calendar.DAY_OF_MONTH)
                        startDateEditText.text = sdf.format(startDateCal.getTime()).toString()
                    }

                    startChange = 0

                }else if(endChange == 1){
                    endDateCal.set(Calendar.YEAR, year)
                    endDateCal.set(Calendar.MONTH, monthOfYear)
                    endDateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    if(validateDates() == true){
                        endDateEditText.text = sdf.format(endDateCal.getTime()).toString()

                    }else{
                        //endDateEditText.text = end
                        endDateCal.time = endDate
                        endDateCal.get(Calendar.YEAR)
                        endDateCal.get(Calendar.MONTH)
                        endDateCal.get(Calendar.DAY_OF_MONTH)
                        endDateEditText.text = sdf.format(endDateCal.getTime()).toString()

                    }

                    endChange = 0
                }
                //updateDateInView()
            }
        }



        startDateEditText.setOnClickListener(object : View.OnClickListener {


            override fun onClick(view: View) {
                startDateCal.time = startDate
                DatePickerDialog(requireContext(),
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    startDateCal.get(Calendar.YEAR),
                    startDateCal.get(Calendar.MONTH),
                    startDateCal.get(Calendar.DAY_OF_MONTH)).show()
                startChange = 1

            }

        })

        endDateEditText.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View) {
                endDateCal.time = endDate

                DatePickerDialog(requireContext(),
                    dateSetListener,
                    endDateCal.get(Calendar.YEAR),
                    endDateCal.get(Calendar.MONTH),
                    endDateCal.get(Calendar.DAY_OF_MONTH)).show()

                endChange = 1

            }

        })


        updateBtn.setOnClickListener {

            if(CheckConnection.checkForInternet(requireContext())){
                if (imageString == ""  || titleEditText.text.isNullOrBlank() || targetEditText.text.isNullOrBlank() ||
                    startDateEditText.text.isNullOrBlank() || endDateEditText.text.isNullOrBlank() ||
                    descriptionEditText.text.isNullOrBlank() || category == "Select the Category") {
                    Toast.makeText(requireContext(),"Field cannot be empty", Toast.LENGTH_LONG).show()

                } else if (!targetEditText.text!!.matches("-?\\d+".toRegex())) {
                    Toast.makeText(requireContext(),"Please enter a valid integer", Toast.LENGTH_LONG).show()
                } else {
                    update()

                }
            }else{
                builder.setMessage("Please change to an available network connection before updating donation")
                    .setCancelable(true)
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss() // Dismiss the second dialog
                    })
                    .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }



        }

        donateImage.setOnClickListener { selectImageFromGallery() }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.donateCategory,
            android.R.layout.simple_spinner_item

        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this

        val position = (spinner.adapter as ArrayAdapter<String>).getPosition(spinnerDefaultValue)
        spinner.setSelection(position)

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




    private fun validateDates() : Boolean {
        if (startDateCal.after(endDateCal)) {
            Toast.makeText(requireContext(),"Invalid Date, End Date Can't Before Start Date. Please Select Again.", Toast.LENGTH_LONG).show()
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

        builder.setMessage("Confirm Update the Donation")
            .setCancelable(true)
            .setPositiveButton("Confirm", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss() // Dismiss the second dialog
                val stringRequest: StringRequest = object : StringRequest(
                    Request.Method.POST, URLupdate,
                    Response.Listener { response ->
                        Log.d("Register",response)
                        if (response == "success") {
                            builder.setMessage("Updated Donation")
                                .setCancelable(true)
                                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                                    dialogInterface.dismiss() // Dismiss the second dialog
                                    anyChange = 1

                                })
                                .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                                    dialogInterface.cancel()
                                })
                                .show()
                        } else if (response == "failure") {
                            builder.setMessage("Fail Update Donation")
                                .setCancelable(true)
                                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
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
                        data["category"] = category
                        data["startDate"] = startDateEditText.text.toString()
                        data["endDate"] = endDateEditText.text.toString()
                        data["description"] = descriptionEditText.text.toString()
                        return data
                    }
                }
                val requestQueue = Volley.newRequestQueue(requireContext())
                requestQueue.add(stringRequest)
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.cancel()
            })
            .show()

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
                                dataObject.getInt("donatePersonId"), dataObject.getInt("donateId"),
                                dataObject.getInt("userId"), dataObject.getString("userEmail").toString(),
                                dataObject.getString("userName").toString(), dataObject.getString("userImage").toString(),
                                dataObject.getDouble("userTotalDonate"), dataObject.getString("paymentMethod").toString(),
                                dataObject.getString("createDate").toString()
                            )
                            donatePeopleList.add(donatePeople)

                            current += dataObject.getInt("userTotalDonate")

                        }

                        adapter = RecordAdapter(donatePeopleList)

                        recyclerView.adapter = adapter



                        currentValue.setText(current.toString())
                        val progressTest: Double = (current.toDouble() / targetTest.toDouble()) * 100.0
                        progressBar.progress = progressTest.toInt()
                        val formattedProgress = String.format("%.2f", progressTest)
                        percentages.text = "$formattedProgress %"


                    } else if (message == "failure") {
                        currentValue.setText("0")
                        percentages.text = "0%"

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

    private fun getLocalDonationPeople(){


        GlobalScope.launch(Dispatchers.IO) {
            var donatePeople= appDb.donatePeopleDao().getDonatePeople(donateID)

            withContext(Dispatchers.Main) {
                adapter = RecordAdapter(donatePeople)

                recyclerView.adapter = adapter

                for(i in donatePeople){
                    if(i.userTotalDonate != null){
                        current += i.userTotalDonate.toInt()
                    }

                }
                currentValue.setText(current.toString())

            }
        }

    }

    override fun onItemSelected(p0: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
        category = p0.getItemAtPosition(p2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        category = ""
    }


}