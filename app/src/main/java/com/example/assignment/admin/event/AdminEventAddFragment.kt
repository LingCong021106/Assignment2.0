package com.example.assignment.admin.event

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.databinding.FragmentAdminEventAddBinding
import java.io.InputStream
import java.util.Calendar
import java.util.Locale


class AdminEventAddFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentAdminEventAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var insertImage : String
    private lateinit var eventTitle : String
    private lateinit var eventOrgName: String
    private lateinit var eventContactPerson :String
    private lateinit var eventContactNumber :String
    private lateinit var eventLocation : String
    private lateinit var eventMaxPerson: String
    private lateinit var eventStartDate: String
    private lateinit var eventRegEndDate : String
    private lateinit var eventStartTime: String
    private lateinit var eventDescription: String
    lateinit var builder : AlertDialog.Builder
    private var adminId : Int = 0
    private lateinit var eventCategory : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminEventAddBinding.inflate(inflater, container, false)
        val rootView : View = binding.root

        insertImage = ""
        builder = AlertDialog.Builder(requireContext())

        //share preference
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        adminId = sharedPreferences.getInt("userId",-1)


        //insert category name into spinner
        val types = resources.getStringArray(R.array.event_category)
        val spinner = binding.eventCategorySelection
        spinner?.adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, types) as SpinnerAdapter
        spinner?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                eventCategory = parent?.getItemAtPosition(position).toString()

            }

        }

        binding.eventRegEndDate.setOnClickListener{
            //date picker

            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val day = c[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    binding.eventRegEndDate.text  = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                },
                year, month, day
            )

            var millis :Long = 0L
            val dateFormat = "dd-MM-yyyy"
            if(binding.eventStartDateBtn.text.toString()!= "Event Start Date"){
                val dateString = binding.eventStartDateBtn.text.toString() // Format: dd/MM/yyyy

                millis = stringToMillis(dateString, dateFormat)
                datePickerDialog.datePicker.maxDate = subtractDaysFromMillis(millis, 1)

            }

            datePickerDialog.datePicker.minDate = c.timeInMillis
            datePickerDialog.show()
        }

        binding.eventStartTimeBtn.setOnClickListener{
            //time picker
            val mcurrentTime: Calendar = Calendar.getInstance()
            val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute: Int = mcurrentTime.get(Calendar.MINUTE)
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val mTimePicker: TimePickerDialog = TimePickerDialog(context,
                { timePicker, selectedHour, selectedMinute ->
                    mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                    mcurrentTime.set(Calendar.MINUTE, selectedMinute)
                    val formattedTime = dateFormat.format(mcurrentTime.time)
                    binding.eventStartTimeBtn.text = "$formattedTime"
                },
                hour,
                minute,
                false

            )
            mTimePicker.setTitle("Select Time")
            mTimePicker.show()

        }

        binding.eventStartDateBtn.setOnClickListener{
            //date picker
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val day = c[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    binding.eventStartDateBtn.text  = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                },
                year, month, day
            )
            datePickerDialog.datePicker.minDate = c.timeInMillis
            datePickerDialog.show()
        }

        binding.addEventImage.setOnClickListener{selectImageFromGallery()}
        binding.eventBack.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,AdminEventFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.addEventButton.setOnClickListener{
            if(checkValidation()){
                builder.setMessage("Confirm to add event?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        storeEvent()
                    })
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }
        }
        return rootView
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            AdminEventAddFragment().apply {

            }
    }

    fun subtractDaysFromMillis(millis: Long, daysToSubtract: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract)
        return calendar.timeInMillis
    }

    fun stringToMillis(dateString: String, dateFormat: String): Long {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val date = formatter.parse(dateString)
        return date?.time ?: -1
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        eventCategory = ""
        eventCategory = parent.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    private fun checkValidation(): Boolean{
        eventTitle = binding.addEventTitle.text.toString()
        eventOrgName = binding.eventOrganization.text.toString()
        eventContactPerson = binding.eventContactperson.text.toString()
        eventContactNumber = binding.eventContactnumber.text.toString()
        eventLocation = binding.eventLocation.text.toString()
        eventMaxPerson = binding.eventMaxperson.text.toString()
        eventDescription = binding.addDonateDescription.text.toString()
        eventStartDate = binding.eventStartDateBtn.text.toString()
        eventRegEndDate = binding.eventRegEndDate.text.toString()
        eventStartTime = binding.eventStartTimeBtn.text.toString()
        if(insertImage.isEmpty()||eventTitle.isEmpty()||eventCategory.isEmpty() || eventCategory == "--Select--"||eventOrgName.isEmpty()||eventContactPerson.isEmpty()||eventContactNumber.isEmpty()||eventLocation.isEmpty()|| eventMaxPerson.isEmpty() ||eventStartDate.isEmpty()||eventRegEndDate.isEmpty()||eventStartTime.isEmpty()||eventDescription.isEmpty()){
            Toast.makeText(context, "Please fill in all information", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
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
                binding.addEventImage.setImageURI(selectedImageUri)
            }
        }
    }

    private fun storeEvent(){
        val url : String = "http://10.0.2.2/Assignment(Mobile)/insertEvent.php"
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("insertEvent",response)
                if (response == "success") {
                            val transaction = parentFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container,AdminEventFragment())
                            transaction.commit()
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
                //database
                data["adminId"] = adminId.toString()
                data["eventName"] = eventTitle
                data["eventCategory"] = eventCategory
                data["eventImage"] = insertImage
                data["eventDescription"] = eventDescription
                data["eventRegEndDate"] = eventRegEndDate
                data["eventOrgName"] = eventOrgName
                data["eventContactNumber"] = eventContactNumber
                data["eventContactPerson"] = eventContactPerson
                data["eventMaxPerson"] = eventMaxPerson
                data["eventDate"] = "$eventStartDate $eventStartTime"
                data["eventLocation"] = eventLocation

                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

}