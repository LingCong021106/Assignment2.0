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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.example.assignment.database.donate.DonateDatabase
import com.example.assignment.database.donate.donateList
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminDonateAddFragment : Fragment(), AdapterView.OnItemSelectedListener {

//    val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
//    val userRole = sharedPreferences.getString("userRole", "users")
//    val userName= sharedPreferences.getString("userName", "")
//    val userEmail = sharedPreferences.getString("userEmail", "")
//    val userId = sharedPreferences.getString("userId","")

    private lateinit var donateImage : ImageView
    private lateinit var startBtn : Button
    private lateinit var endBtn : Button
    private lateinit var addBtn : Button
    private lateinit var organization : TextInputEditText
    private lateinit var title : TextInputEditText
    private lateinit var target : TextInputEditText
    private lateinit var description : TextInputEditText
    private lateinit var back : ImageView
    private lateinit var spinner: Spinner


    private var startChange = 0
    private var endChange = 0

    private val cal: Calendar = Calendar.getInstance()
    private val startDateCal: Calendar = Calendar.getInstance()
    private val endDateCal: Calendar = Calendar.getInstance()

    lateinit var builder : AlertDialog.Builder
    private val URL :String = "http://10.0.2.2/Assignment(Mobile)/insertDonations.php"

    private var insertImage : String = ""
    private lateinit var insertOrganization : String
    private lateinit var insertTitle : String
    private lateinit var insertTarget : String
    private var category : String = ""
    private lateinit var insertStartDate : String
    private lateinit var insertEndDate : String
    private lateinit var insertDescription : String
    var adminID = 0
    private lateinit var appDb : DonateDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_admin_donate_add, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userRole = sharedPreferences.getString("userRole", "users")
        val adminId = sharedPreferences.getInt("userId",-1)

        adminID = adminId

        appDb = DonateDatabase.getDatabase(requireContext())

        donateImage = view.findViewById(R.id.addDonateImage)
        organization = view.findViewById(R.id.addDonateOriganization)
        title = view.findViewById(R.id.addDonateTitle)
        target = view.findViewById(R.id.donateTargetValue)
        startBtn = view.findViewById(R.id.addDonateStartBtn)
        endBtn = view.findViewById(R.id.addDonateEndBtn)
        description = view.findViewById(R.id.addDonateDescription)
        addBtn = view.findViewById(R.id.addDonateButton)
        back = view.findViewById(R.id.donateBack)
        spinner = view.findViewById(R.id.spinnerDonateCategory)

        builder = AlertDialog.Builder(requireContext())

        donateImage.setOnClickListener { selectImageFromGallery() }
        back.setOnClickListener { loadFragment(AdminDonateFragment()) }
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

        startBtn!!.setOnClickListener(object : View.OnClickListener {
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

        endBtn!!.setOnClickListener(object : View.OnClickListener {
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

        addBtn.setOnClickListener {
            insertOrganization = organization.text.toString()
            insertTitle = title.text.toString()
            insertTarget = target.text.toString()
            insertDescription = description.text.toString()

            if(CheckConnection.checkForInternet(requireContext())){
                if (insertOrganization.isEmpty() ||insertImage.isEmpty() ||insertTitle.isEmpty() || insertTarget.isEmpty() ||
                    insertDescription.isEmpty() || insertStartDate.isEmpty() || insertEndDate.isEmpty() || category == "Select the Category") {
                    Toast.makeText(requireContext(),"Field cannot be empty", Toast.LENGTH_LONG).show()

                } else if (!insertTarget.matches("-?\\d+".toRegex())) {
                    Toast.makeText(requireContext(),"Please enter a valid integer", Toast.LENGTH_LONG).show()
                } else {
                    addNewRemoteDonation()
                }
            }else{
                builder.setMessage("Please change to an available network connection before adding new donation")
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


        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.donateCategory,
            android.R.layout.simple_spinner_item

        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this





        return view
    }

    fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            val inputStream: InputStream? =
                selectedImageUri?.let { requireContext().contentResolver.openInputStream(it) }
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            insertImage = BitmapConverter.convertBitmapToString(bitmap)

            if (selectedImageUri != null) {
                donateImage.setImageURI(selectedImageUri)
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if(startChange == 1){
            if(validateDates() == true){
                startBtn!!.text = sdf.format(startDateCal.getTime())
                insertStartDate = sdf.format(startDateCal.getTime()).toString()
            }else{
                startBtn!!.text = "Start Date"
                insertStartDate = ""
            }

            startChange = 0
        }

        if(endChange == 1){
            if(validateDates() == true){
                endBtn!!.text = sdf.format(endDateCal.getTime())
                insertEndDate = sdf.format(endDateCal.getTime()).toString()
            }else{
                endBtn.text = "End Date"
                insertEndDate = ""
            }

            endChange = 0
        }

    }

    private fun validateDates() : Boolean {

        if(startChange == 1 && endBtn.text == "End Date"){
            return true
        }else if (startDateCal.after(endDateCal)) {
            Toast.makeText(requireContext(),"Invalid Date, End Date Can't Before Start Date. Please Select Again", Toast.LENGTH_LONG).show()
            return false
        }else{
            return true
        }
    }

    private fun addNewRemoteDonation() {

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, URL,
            Response.Listener { response ->
                Log.d("Register",response)
                if (response == "success") {
                    builder.setMessage("Added New Donation")
                        .setCancelable(true)
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss() // Dismiss the second dialog
                            anyChange = 1
                            loadFragment(AdminDonateFragment())

                        })
                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        }).show()
                } else if (response == "failure") {
                    builder.setMessage("Fail Add New Donation")
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
                data["adminId"] = adminID.toString()
                data["donateName"] = insertTitle
                data["donateImage"] = insertImage
                data["donateCategory"] = category
                data["donateOrgname"] = insertOrganization
                data["donateStartTime"] = insertStartDate
                data["donateEndTime"] = insertEndDate
                data["totalDonation"] = insertTarget
                data["donateDescription"] = insertDescription
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)


    }


    override fun onItemSelected(p0: AdapterView<*>, p1: View?, p2: Int, p3: Long) {

        category = p0.getItemAtPosition(p2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>) {
        category == ""
    }


}