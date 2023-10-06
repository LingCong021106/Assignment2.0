package com.example.assignment.user.donate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.example.assignment.R
import kotlin.properties.Delegates

class Payment: AppCompatActivity() {
    lateinit var editTextAmount : EditText
    lateinit var radioGroupAmount: RadioGroup
    lateinit var radioGroupMethod : RadioGroup
    lateinit var buttonDonate : Button
    private lateinit var method: String
    private lateinit var amount: String
    private var donateId by Delegates.notNull<Int>()
    private var userId by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fundraising_details_donate_payment)


        editTextAmount = findViewById(R.id.donate_amount_edit)
        radioGroupAmount = findViewById(R.id.donate_amount)
        buttonDonate = findViewById(R.id.donate_payment_btn)
        radioGroupMethod = findViewById(R.id.donate_payment_method)

        donateId = intent.getIntExtra("donateId", -1)
        userId = intent.getIntExtra("userId", -1)

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
        buttonDonate.setOnClickListener { validationDonate(it) }
    }

    private fun validationDonate(view: View) {
        val inputAmount = editTextAmount.text.toString().trim()

        if (inputAmount.isEmpty() && amount.isEmpty()) {
            // Neither the radio buttons nor the input field have a value
            Toast.makeText(
                this@Payment,
                "Please fill in the donate amount",
                Toast.LENGTH_SHORT
            ).show()
            return
        }else if(inputAmount =="0"){
            Toast.makeText(
                this@Payment,
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
                    val intent = Intent(this@Payment, CreditCard::class.java)
                    intent.putExtra("donateId", donateId)
                    intent.putExtra("userId",userId)
                    intent.putExtra("amount", amount)
                    startActivity(intent)
                }

                "paypal" -> {
                    val intent = Intent(this@Payment, Paypal::class.java)
                    intent.putExtra("donateId", donateId)
                    intent.putExtra("userId",userId)
                    intent.putExtra("amount", amount)
                    startActivity(intent)
                }

                "" -> {
                    Toast.makeText(
                        this@Payment,
                        "Please select a donate method",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            }

        }


    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

}