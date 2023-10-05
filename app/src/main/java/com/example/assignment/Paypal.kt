package com.example.assignment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
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

class Paypal : AppCompatActivity() {

    lateinit var inputEmail : EditText
    lateinit var inputPassword : EditText
    lateinit var buttonPay : Button
    lateinit var errorMsg : TextView
    lateinit var builder : AlertDialog.Builder
    lateinit var amountText : TextView
    private var donateID : String =""
    private var username : String =""
    private var amount : String = ""
    private var methodPay : String = "paypal"
    private var userID : String = ""
    private val URLinsertDonate :String = "http://192.168.0.21:8081/Assignment(Mobile)/insertDonateRecord.php"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.donate_paypal_display)

//        inputEmail = findViewById(R.id.email_paypal)
//        inputPassword = findViewById(R.id.password_paypal)
//        buttonPay = findViewById(R.id.paypal_btn)
//        errorMsg = findViewById(R.id.txterrorMsg)
//        builder = AlertDialog.Builder(this)
//        amountText = findViewById(R.id.donateAmount_txt_paypal)
//
//        amountText.text = intent.getStringExtra("amount").toString()
//
//        donateID = intent.getStringExtra("donateID").toString()
//        username = intent.getStringExtra("username").toString()
//        amount = intent.getStringExtra("amount").toString()
//        userID = intent.getStringExtra("userID").toString()
//
//        buttonPay.setOnClickListener { validateInput() }

    }

//    private fun validateInput() {
//
//        if(inputEmail.text.toString().isEmpty() || inputPassword.text.toString().isEmpty()){
//            errorMsg.text = "can't empty, please fill in"
//            builder.setMessage("can't empty, please fill in")
//                .setCancelable(true)
//                .setPositiveButton("", DialogInterface.OnClickListener { dialogInterface, i ->
//                    dialogInterface.dismiss()
//                })
//                .setNegativeButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
//                    dialogInterface.cancel()
//                })
//                .show()
//        }
//        else{
//            if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.text.toString()).matches()){
//                errorMsg.text = "Invalid Email"
//                builder.setMessage("Invalid Email")
//                    .setCancelable(true)
//                    .setPositiveButton("", DialogInterface.OnClickListener { dialogInterface, i ->
//                        dialogInterface.dismiss()
//                    })
//                    .setNegativeButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
//                        dialogInterface.cancel()
//                    })
//                    .show()
//            }
//            else{
//                errorMsg.text=""
//                builder.setMessage("Confirm to donate?")
//                    .setCancelable(true)
//                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
//                        dialogInterface.dismiss()
//                        storeDonateRecordPaypal()
//                    })
//                    .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
//                        dialogInterface.cancel()
//                    })
//                    .show()
//            }
//        }
//    }

//    private fun storeDonateRecordPaypal() {
//        val stringRequest: StringRequest = object : StringRequest(
//            Request.Method.POST, URLinsertDonate,
//            Response.Listener { response ->
//                Log.d("Register",response)
//                if (response == "success") {
//                    builder.setMessage("Thank for you donating this project")
//                        .setCancelable(true)
//                        .setPositiveButton("back to home", DialogInterface.OnClickListener { dialogInterface, i ->
//                            dialogInterface.dismiss() // Dismiss the second dialog
//                            val intent = Intent(this@Paypal, FundraisingDetails::class.java)
//                            startActivity(intent)
//                        })
//                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
//                            dialogInterface.cancel()
//                        })
//                        .show()
//                } else if (response == "failure") {
//                    builder.setMessage("So sorry, this is full already, please try to join other event")
//                        .setCancelable(true)
//                        .setPositiveButton("back to home", DialogInterface.OnClickListener { dialogInterface, i ->
//                            dialogInterface.dismiss() // Dismiss the second dialog
//                            finish()
//                        })
//                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
//                            dialogInterface.cancel()
//                        })
//                        .show()
//                }
//            },
//            Response.ErrorListener { error ->
//                Toast.makeText(
//                    applicationContext,
//                    error.toString().trim { it <= ' ' },
//                    Toast.LENGTH_SHORT
//                ).show()
//            }) {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String>? {
//                val data: MutableMap<String, String> = HashMap()
//                //database
//                val currentDateTime = LocalDateTime.now()
//                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
//                val formattedDateTime = currentDateTime.format(formatter)
//
//                data["id"] = donateID
//                data["userID"] = userID
//                data["date"] = formattedDateTime
//                data["name"] = username
//                data["amount"] = amount
//                data["method"] = methodPay
//                return data
//            }
//        }
//        val requestQueue = Volley.newRequestQueue(applicationContext)
//        requestQueue.add(stringRequest)
//        }

}