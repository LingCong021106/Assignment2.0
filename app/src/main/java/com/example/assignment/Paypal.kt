package com.example.assignment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Paypal : AppCompatActivity() {

    lateinit var inputEmail : EditText
    lateinit var inputPassword : EditText
    lateinit var buttonPay : Button
    lateinit var errorMsg : TextView
    lateinit var builder : AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.donate_paypal_display)

        inputEmail = findViewById(R.id.email_paypal)
        inputPassword = findViewById(R.id.password_paypal)
        buttonPay = findViewById(R.id.paypal_btn)
        errorMsg = findViewById(R.id.txterrorMsg)
        builder = AlertDialog.Builder(this)

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
                    .setPositiveButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        finish()
                    })
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }
        }
    }

}