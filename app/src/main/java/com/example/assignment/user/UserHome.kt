package com.example.assignment.user

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.assignment.R
import com.example.assignment.user.donate.DonateFragment
import com.example.assignment.user.event.EventFragment
import com.example.assignment.user.home.SlideAdapter
import com.example.assignment.user.home.SlideItem
import com.example.assignment.user.home.UserHomeFragment
import com.example.assignment.user.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserHome: AppCompatActivity(){
    lateinit var bottomNav : BottomNavigationView

    private var _binding: UserHome? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_home)

        val getFragment = intent.getStringExtra("fragment")
        if(getFragment == "profile"){
            loadFragment(ProfileFragment())
        }
        else{
            loadFragment(UserHomeFragment())
        }

        bottomNav = findViewById(R.id.bottom_navigation) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(UserHomeFragment())
                    true
                }

                R.id.donate -> {
                    loadFragment(DonateFragment())
                    true
                }

                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }


                R.id.event -> {
                    loadFragment(EventFragment())
                    true
                }

                else -> {
                    true
                }
            }
        }

    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }



}