package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_home)
        //setContentView(R.layout.user_home)

//        val drawerLayout : DrawerLayout = findViewById(R.id.admin_home)
//        val navView : NavigationView = findViewById(R.id.nav_view)
//
//        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        navView.setNavigationItemSelectedListener {
//
//            when(it.itemId){
//                R.id.admin_nav_dashboard -> Toast.makeText(applicationContext,"Clicked", Toast.LENGTH_LONG).show()
//            }
//
//            true
//
//        }

//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        if(toggle.onOptionsItemSelected(item)){
//            return true
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
    }
}