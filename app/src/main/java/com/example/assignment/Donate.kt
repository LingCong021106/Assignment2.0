package com.example.assignment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Donate: AppCompatActivity() {
    lateinit var editTextAmount : EditText
    lateinit var radioGroupAmount: RadioGroup
    lateinit var radioGroupMethod : RadioGroup
    lateinit var buttonDonate : Button
    private var method: String = ""
    private var amount: String = ""
    private var donateID : String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fundraising_details_donate_payment)


        editTextAmount = findViewById(R.id.donate_amount_edit)
        radioGroupAmount = findViewById(R.id.donate_amount)
        buttonDonate = findViewById(R.id.donate_payment_btn)
        radioGroupMethod = findViewById(R.id.donate_payment_method)

        donateID = intent.getStringExtra("donateID").toString()

        radioGroupAmount.setOnCheckedChangeListener { group, checkedId ->
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editTextAmount.windowToken, 0)

            when (checkedId) {
                R.id.donate_5 -> {
                    amount = "5"
                    editTextAmount.setText("5")
                }

                R.id.donate_10 -> {
                    amount = "10"
                    editTextAmount.setText("10")
                }

                R.id.donate_20 -> {
                    amount = "20"
                    editTextAmount.setText("20")
                }
            }

        }

        radioGroupMethod.setOnCheckedChangeListener { group, methodId ->
            when {
                methodId == R.id.donate_card ->
                    method = "card"
                methodId == R.id.donate_paypal ->
                    method = "paypal"
            }
        }
        buttonDonate.setOnClickListener { storeDonate(it) }
    }

    private fun storeDonate(view: View) {
        val inputAmount = editTextAmount.text.toString().trim()

        if (inputAmount.isEmpty() && amount.isEmpty()) {
            // Neither the radio buttons nor the input field have a value
            Toast.makeText(
                this@Donate,
                "Please fill in the donate amount",
                Toast.LENGTH_SHORT
            ).show()
            return
        }else if(inputAmount =="0"){
            Toast.makeText(
                this@Donate,
                "Donation amount cant 0",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        else if (inputAmount.isNotEmpty()) {
            // User entered a custom donation amount, use it
            amount = inputAmount
            when (method) {
                "card" -> {
                    val intent = Intent(this@Donate, CreditCard::class.java)
                    intent.putExtra("donateID", donateID)
                    intent.putExtra("userID","123")
                    intent.putExtra("username", "test name")
                    intent.putExtra("amount", amount)
                    startActivity(intent)
                }

                "paypal" -> {
                    val intent = Intent(this@Donate, Paypal::class.java)
                    intent.putExtra("donateID", donateID)
                    intent.putExtra("userID","123")
                    intent.putExtra("username", "test name")
                    intent.putExtra("amount", amount)
                    startActivity(intent)
                }

                "" -> {
                    Toast.makeText(
                        this@Donate,
                        "Please select a donate method",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            }

        }



}