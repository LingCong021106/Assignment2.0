package com.example.assignment

import android.content.Context
import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Donate: AppCompatActivity() {
    lateinit var textViewAmount : TextView
    lateinit var editTextAmount : EditText
    lateinit var radioGroupAmount: RadioGroup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fundraising_details_donate_payment)

        textViewAmount = findViewById(R.id.donate_amount_txt)
        editTextAmount = findViewById(R.id.donate_amount_edit)
        radioGroupAmount = findViewById(R.id.donate_amount)
        radioGroupAmount.setOnCheckedChangeListener { group, checkedId ->
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editTextAmount.windowToken, 0)

            when{
                checkedId==R.id.donate_5->
                    textViewAmount.text = "5"
                checkedId==R.id.donate_10->
                    textViewAmount.text = "10"
                checkedId==R.id.donate_20->
                    textViewAmount.text = "20"
                checkedId==R.id.donate_50->
                    textViewAmount.text = "50"
                checkedId==R.id.donate_100->
                    textViewAmount.text = "100"
            }

            editTextAmount.visibility=View.GONE
            textViewAmount.visibility=View.VISIBLE
        }

        textViewAmount.setOnClickListener { openEditText(it) }
    }

        private fun openEditText(view:View) {
        radioGroupAmount.clearCheck()
        editTextAmount.visibility=View.VISIBLE
        textViewAmount.visibility=View.GONE
    }

}