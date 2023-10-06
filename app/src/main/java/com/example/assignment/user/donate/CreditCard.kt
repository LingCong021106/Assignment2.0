package com.example.assignment.user.donate

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.assignment.R
import com.example.assignment.database.AppDatabase
import com.example.assignment.database.User
import com.example.assignment.user.UserHome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class CreditCard : AppCompatActivity() {

    lateinit var inputCard : EditText
    lateinit var inputMonth : EditText
    lateinit var inputYear : EditText
    lateinit var inputCvv : EditText
    lateinit var btnPayCard : Button
    lateinit var errorMsgCard : TextView
    lateinit var builder : AlertDialog.Builder
    lateinit var amountTxt : TextView
    private var username : String =""
    private var methodPay : String = "card"
    private var donateID by Delegates.notNull<Int>()
    private lateinit var amount : String
    private lateinit var userName : String
    private lateinit var userImage : String
    private lateinit var userEmail : String
    private var userId by Delegates.notNull<Int>()
    private lateinit var appDB : AppDatabase
    private val URLinsertDonate :String = "http://10.0.2.2/Assignment(Mobile)/insertDonatePayment.php"


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

        donateID = intent.getIntExtra("donateId", -1)
        userId = intent.getIntExtra("userId", -1)

        //get user data from room
        CoroutineScope(Dispatchers.IO).launch {
            appDB = AppDatabase.getInstance(this@CreditCard)
            val user: User? = appDB.userDao().getUserById(userId)
            if (user != null) {
                userName = user.userName.toString()
                userImage = user.photo.toString()
                userEmail = user.userEmail.toString()
            }
        }

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
                            val intent = Intent(this@CreditCard, UserHome::class.java)
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
                            val intent = Intent(this@CreditCard, UserHome::class.java)
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
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val formattedDateTime = currentDateTime.format(formatter)

                data["donateId"] = donateID.toString()
                data["userId"] = userId.toString()
                data["userEmail"] = userEmail
                data["userImage"] = userImage
                data["userName"] = userName
                data["paymentMethod"] = "Credit Card"
                data["totalDonate"] = amountTxt.text.toString()
                data["createDate"] = formattedDateTime.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)

        }
}