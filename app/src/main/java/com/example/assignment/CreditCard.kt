package com.example.assignment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.textservice.TextInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreditCard : AppCompatActivity() {

    lateinit var inputCard : EditText
    lateinit var inputMonth : EditText
    lateinit var inputYear : EditText
    lateinit var inputCvv : EditText
    lateinit var btnPayCard : Button
    lateinit var errorMsgCard : TextView
    lateinit var builder : AlertDialog.Builder
    lateinit var amountTxt : TextView
    private var donateID : String =""
    private var username : String =""
    private var amount : String = ""
    private var methodPay : String = "card"
    private var userID : String = ""
    private val URLinsertDonate :String = "http://10.0.2.2/Assignment(Mobile)/insertDonateRecord.php"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.donate_creditcard_display)

        inputCard = findViewById(R.id.cardNumber)
        inputMonth = findViewById(R.id.cardmonth)
        inputYear = findViewById(R.id.cardyear)
        inputCvv = findViewById(R.id.cardcvv)
        btnPayCard = findViewById(R.id.payment_donate_btn)
        errorMsgCard = findViewById(R.id.errorCardtxt)
        builder = AlertDialog.Builder(this)
        amountTxt = findViewById(R.id.donateAmount_txt)

        amountTxt.text = intent.getStringExtra("amount").toString()

        donateID = intent.getStringExtra("donateID").toString()
        username = intent.getStringExtra("username").toString()
        amount = intent.getStringExtra("amount").toString()
        userID = intent.getStringExtra("userID").toString()

        btnPayCard.setOnClickListener { validationInput() }

    }

    private fun validationInput() {

        val card  =inputCard.text.toString().toIntOrNull()
        val month = inputMonth.text.toString().toIntOrNull()
        val year = inputYear.text.toString().toIntOrNull()
        val cvv = inputCvv.text.toString().toIntOrNull()
        val currentYearLast2Digits = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy")).toInt()
        val currentMonth = LocalDate.now().monthValue

        if(inputCard.text.toString().isEmpty() || inputMonth.text.toString().isEmpty() || inputYear.text.toString().isEmpty() || inputCvv.text.toString().isEmpty()){
            errorMsgCard.text="Can't empty, please fill in"
            builder.setMessage("can't empty, please fill in")
                .setCancelable(true)
                .setPositiveButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .setNegativeButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })
                .show()
        }else{
            if(inputCard.length() !=16){
                errorMsgCard.text="Invalid card number"
                builder.setMessage("Invalid card number")
                    .setCancelable(true)
                    .setPositiveButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                    .setNegativeButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }else if(inputYear.length()!=2){
                errorMsgCard.text="Invalid year"
                builder.setMessage("Invalid year")
                    .setCancelable(true)
                    .setPositiveButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                    .setNegativeButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }else if(inputMonth.length()!=2 || month !in 1..12){
                errorMsgCard.text="Invalid month"
                builder.setMessage("Invalid month")
                    .setCancelable(true)
                    .setPositiveButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                    .setNegativeButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }else if(year != null && (year < currentYearLast2Digits || (year == currentYearLast2Digits && month != null && month < currentMonth))){
                errorMsgCard.text = "Invalid expiration date"
                builder.setMessage("Invalid expiration date")
                    .setCancelable(true)
                    .setPositiveButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                    .setNegativeButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }else{
                if(inputCvv.length() != 3){
                    errorMsgCard.text="Invalid CVV"
                    builder.setMessage("Invalid CVV")
                        .setCancelable(true)
                        .setPositiveButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                        .setNegativeButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                }else{
                    builder.setMessage("Confirm to donate?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                            storeDonateRecordCard()
                        })
                        .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                }
            }

        }

    }

    private fun storeDonateRecordCard() {
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, URLinsertDonate,
            Response.Listener { response ->
                Log.d("Register",response)
                if (response == "success") {
                    builder.setMessage("Thank for you donating this project")
                        .setCancelable(true)
                        .setPositiveButton("back to home", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss() // Dismiss the second dialog
//                            val intent = Intent(this@CreditCard, FundraisingDetails::class.java)
                            startActivity(intent)
                        })
                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                } else if (response == "failure") {
                    builder.setMessage("")
                        .setCancelable(true)
                        .setPositiveButton("back to home", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss() // Dismiss the second dialog
                            finish()
                        })
                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    applicationContext,
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                //database
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                val formattedDateTime = currentDateTime.format(formatter)

                data["id"] = donateID
                data["userID"] = userID
                data["name"] = username
                data["amount"] = amount
                data["date"] = formattedDateTime
                data["method"] = methodPay
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)

        }
}