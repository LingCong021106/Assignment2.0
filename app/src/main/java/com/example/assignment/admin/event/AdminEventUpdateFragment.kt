package com.example.assignment.admin.event

import android.R.*
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.example.assignment.databinding.FragmentAdminEventUpdateBinding
import com.example.assignment.user.event.Event
import com.example.assignment.user.event.eventList
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [AdminEventUpdateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminEventUpdateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentAdminEventUpdateBinding? = null
    private val binding get() = _binding!!
    private var imageString: String? = null
    private var event: Event = Event()
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
    private lateinit var eventCategory : String
    lateinit var builder : AlertDialog.Builder

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
        _binding = FragmentAdminEventUpdateBinding.inflate(inflater, container, false)
        val rootView : View = binding.root

        binding.eventBack.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,AdminEventFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        //initial
        insertImage = ""
        builder = AlertDialog.Builder(requireContext())


        val data = arguments
        val eventId = data!!.getInt("eventId")

        event = findEvent(eventId)!!

        imageString = event.eventImage.toString()
        var bitmap = BitmapConverter.convertStringToBitmap(imageString)
        binding.addEventImage.setImageBitmap(bitmap)
        insertImage = event.eventImage.toString()

        binding.addEventTitle.setText(event.eventName)

        binding.eventOrganization.setText(event.eventOrgName)
        binding.eventContactnumber.setText(event.eventContactNumber)
        binding.eventContactperson.setText(event.eventContactPerson)
        binding.eventLocation.setText(event.eventLocation)
        binding.eventMaxperson.setText(event.eventMaxPerson.toString())

        //parse date and time
        val f: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
        val d: Date? = f.parse(event.eventDate)

        // Format date and time components separately
        val date: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        val time: DateFormat = SimpleDateFormat("hh:mm a")

        binding.eventStartDateBtn.text = date.format(d)
        binding.eventStartTimeBtn.text = time.format(d)

        binding.eventRegEndDate.text = event.eventRegEndDate
        binding.addDonateDescription.setText(event.eventDescription)

        binding.addEventImage.setOnClickListener{

        }

        //insert category name into spinner
        val compateValue = event.eventCategory
        val types = resources.getStringArray(R.array.event_category)
        val spinner = binding.eventCategorySelection

        spinner?.adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, types) as SpinnerAdapter
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                eventCategory = parent?.getItemAtPosition(position).toString()

            }
        }
        val position = (spinner.adapter as ArrayAdapter<String>).getPosition(compateValue)
        spinner.setSelection(position)

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
            val dateFormat = android.icu.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
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

        binding.updateEventButton.setOnClickListener{
            if(checkValidation()){
                builder.setMessage("Confirm to update event?")
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminEventUpdateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminEventUpdateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun findEvent(eventId:Int): Event? {
        for(event in eventList){
            if(event.eventId == eventId){
                return event
            }
        }
        return null
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
        val url : String = "http://10.0.2.2/Assignment(Mobile)/updateEvent.php"
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("updateEvent",response)
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
                data["eventId"] = event.eventId.toString()
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

    fun subtractDaysFromMillis(millis: Long, daysToSubtract: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract)
        return calendar.timeInMillis
    }

    fun stringToMillis(dateString: String, dateFormat: String): Long {
        val formatter = android.icu.text.SimpleDateFormat(dateFormat, Locale.getDefault())
        val date = formatter.parse(dateString)
        return date?.time ?: -1
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

    fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }
}