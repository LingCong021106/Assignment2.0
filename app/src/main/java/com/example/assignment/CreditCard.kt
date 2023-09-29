package com.example.assignment

import android.os.Bundle
import android.view.textservice.TextInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CreditCard : AppCompatActivity() {

    lateinit var inputCard : EditText
    lateinit var inputMonth : EditText
    lateinit var inputYear : EditText
    lateinit var inputCvv : EditText
    lateinit var btnPayCard : Button
    lateinit var errorMsgCard : TextView
    lateinit var builder : AlertDialog.Builder
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
                    errorMsgCard.text="Pass"
                }
            }
        }

    }
}