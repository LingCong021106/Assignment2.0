package com.example.assignment

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast

class UserProfile : AppCompatActivity() {

    lateinit var btnEdit : Button
    lateinit var radioGroupService : RadioGroup
    lateinit var btnContact : Button
    lateinit var btnLogOut : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        btnEdit = findViewById(R.id.btnEdit)
        radioGroupService = findViewById(R.id.serviceGroup)
        btnContact = findViewById(R.id.btnContact)
        btnLogOut = findViewById(R.id.btnLogout)

        radioGroupService.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.eventJoinedRecord -> {
                    Toast.makeText(
                        this@UserProfile,
                        "Joined Event Screen",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                R.id.volunteerRecord -> {
                    Toast.makeText(
                        this@UserProfile,
                        "Volunteer Events Screen",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                R.id.donationRecord -> {
                    Toast.makeText(
                        this@UserProfile,
                        "Donation Records Screen",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

        }

        btnEdit.setOnClickListener {
            Toast.makeText(
                this@UserProfile,
                "User Edit Profile Screen",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:0123456789"))
            startActivity(intent)
        }

        btnLogOut.setOnClickListener {
            Toast.makeText(
                this@UserProfile,
                "Log Out",
                Toast.LENGTH_SHORT
            ).show()
        }


    }
}