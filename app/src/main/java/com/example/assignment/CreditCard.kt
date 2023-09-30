package com.example.assignment

import android.content.DialogInterface
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

class CreditCard : AppCompatActivity() {

    lateinit var inputCard : EditText
    lateinit var inputMonth : EditText
    lateinit var inputYear : EditText
    lateinit var inputCvv : EditText
    lateinit var btnPayCard : Button
    lateinit var errorMsgCard : TextView
    lateinit var builder : AlertDialog.Builder
    lateinit var amountTxt : TextView
    private var eventID : String =""
    private var username : String =""
    private var amount : String = ""
    private var methodPay : String = "card"
    private val URLinsertDonate :String = "http://192.168.0.21:8081/mobile/insertDonateRecord.php"


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

        eventID = intent.getStringExtra("eventID").toString()
        username = intent.getStringExtra("username").toString()
        amount = intent.getStringExtra("amount").toString()

        btnPayCard.setOnClickListener { validationInput() }

    }

    private fun validationInput() {

        if(inputCard.text.toString().isEmpty() || inputCvv.text.toString().isEmpty()){
            errorMsgCard.text="Can't empty, please fill in"
        }else{
            if(inputCard.length() !=16){
                errorMsgCard.text="Invalid card number"
            }
            else{
                if(inputCvv.length() !=3){
                    errorMsgCard.text="Invalid CVV"
                }
                else{
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
                            finish()
                        })
                        .setNegativeButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                        .show()
                } else if (response == "failure") {
                    builder.setMessage("So sorry, this is full already, please try to join other event")
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
                data["id"] = eventID
                data["name"] = username
                data["amount"] = amount
                data["method"] = methodPay
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)

    }
}