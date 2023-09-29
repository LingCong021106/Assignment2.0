package com.example.assignment.admin.donate

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import com.example.assignment.R
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminDonateAddFragment : Fragment() {

    private lateinit var startBtn : Button
    private lateinit var endBtn : Button
    private lateinit var addBtn : Button
    private lateinit var target : TextInputEditText


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
        val view =  inflater.inflate(R.layout.fragment_admin_donate_add, container, false)

        target = view.findViewById(R.id.donateTargetValue)
        startBtn = view.findViewById(R.id.addDonateStartBtn)
        endBtn = view.findViewById(R.id.addDonateEndBtn)
        addBtn = view.findViewById(R.id.addDonateButton)

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
            val inputText = target.text.toString().trim()

            if (inputText.isEmpty()) {
                Toast.makeText(requireContext(),"Field cannot be empty", Toast.LENGTH_LONG).show()
            } else if (!inputText.matches("-?\\d+".toRegex())) {
                Toast.makeText(requireContext(),"Please enter a valid integer", Toast.LENGTH_LONG).show()
            } else {
                // Input is a valid integer
                val integerValue = inputText.toInt()
                Toast.makeText(requireContext(),"\"You entered: $integerValue\"", Toast.LENGTH_LONG).show()
                // You can use the integerValue in your code
            }
        }


        return view
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if(startChange == 1){
            if(validateDates() == true){
                startBtn!!.text = sdf.format(startDateCal.getTime())
            }else{
                startBtn!!.text = "Start Date"
            }

            startChange = 0
        }

        if(endChange == 1){
            if(validateDates() == true){
                endBtn!!.text = sdf.format(endDateCal.getTime())
            }else{
                endBtn.text = "End Date"
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


}