package com.example.assignment.user.donate

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import com.example.assignment.user.event.EventFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class Paypal : AppCompatActivity() {

    lateinit var inputEmail : EditText
    lateinit var inputPassword : EditText
    lateinit var buttonPay : Button
    lateinit var errorMsg : TextView
    lateinit var builder : AlertDialog.Builder
    lateinit var amountText : TextView
    private var donateID by Delegates.notNull<Int>()
    private lateinit var amount : String
    private lateinit var userName : String
    private lateinit var userImage : String
    private lateinit var userEmail : String
    private var methodPay : String = "paypal"
    private var userId by Delegates.notNull<Int>()
    private lateinit var appDB : AppDatabase
    private val URLinsertDonate :String = "http://10.0.2.2/Assignment(Mobile)/insertDonatePayment.php"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.donate_paypal_display)

        inputEmail = findViewById(R.id.email_paypal)
        inputPassword = findViewById(R.id.password_paypal)
        buttonPay = findViewById(R.id.paypal_btn)
        errorMsg = findViewById(R.id.txterrorMsg)
        builder = AlertDialog.Builder(this)
        amountText = findViewById(R.id.donateAmount_txt_paypal)

        amountText.text = intent.getStringExtra("amount").toString()

        donateID = intent.getIntExtra("donateId", -1)
        userId = intent.getIntExtra("userId", -1)

        //get user data from room
        CoroutineScope(Dispatchers.IO).launch {
            appDB = AppDatabase.getInstance(this@Paypal)
            val user: User? = appDB.userDao().getUserById(userId)
            if (user != null) {
                userName = user.userName.toString()
                userImage = user.photo.toString()
                userEmail = user.userEmail.toString()
            }
        }

        buttonPay.setOnClickListener { validateInput() }

    }

    private fun validateInput() {

        if(inputEmail.text.toString().isEmpty() || inputPassword.text.toString().isEmpty()){
            errorMsg.text = "can't empty, please fill in"
            builder.setMessage("can't empty, please fill in")
                .setCancelable(true)
                .setPositiveButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .setNegativeButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })
                .show()
        }
        else{
            if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.text.toString()).matches()){
                errorMsg.text = "Invalid Email"
                builder.setMessage("Invalid Email")
                    .setCancelable(true)
                    .setPositiveButton("", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                    .setNegativeButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }
            else{
                errorMsg.text=""
                builder.setMessage("Confirm to donate?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        storeDonateRecordPaypal()
                    })
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }
        }
    }

    private fun storeDonateRecordPaypal() {
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, URLinsertDonate,
            Response.Listener { response ->
                Log.d("Register",response)
                if (response == "success") {
                    builder.setMessage("Thank for you donating this project")
                        .setCancelable(true)
                        .setPositiveButton("back to home", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss() // Dismiss the second dialog
                            val intent = Intent(this@Paypal, UserHome::class.java)
                            startActivity(intent)
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
                            val intent = Intent(this@Paypal, UserHome::class.java)
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
                data["paymentMethod"] = "Paypal"
                data["totalDonate"] = amountText.text.toString()
                data["createDate"] = formattedDateTime.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
        }

        override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
        }
}