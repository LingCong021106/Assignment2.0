package com.example.assignment.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.assignment.MainActivity
import com.example.assignment.R
import com.example.assignment.admin.dashboard.DashboardFragment
import com.example.assignment.admin.donate.AdminDonateFragment
import com.example.assignment.admin.news.AdminNewsFragment
import com.example.assignment.admin.report.AdminReportFragment
import com.example.assignment.admin.volunteer.AdminVolunteerFragment
import com.google.android.material.navigation.NavigationView

private lateinit var drawerLayout: DrawerLayout


class AdminHome: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_home)


        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment()).commit()
            navigationView.setCheckedItem(R.id.admin_nav_dashboard)
        }

        toolbar.setNavigationOnClickListener{
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)

            } else {
                var currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                when(currentFragment){
//                    is AdminUserFragment -> navigationView.setCheckedItem(R.id.admin_nav_user)
                    is AdminReportFragment -> navigationView.setCheckedItem(R.id.admin_nav_report)
                    is AdminNewsFragment -> navigationView.setCheckedItem(R.id.admin_nav_news)
                    is AdminDonateFragment -> navigationView.setCheckedItem(R.id.admin_nav_donate)
                    is AdminVolunteerFragment -> navigationView.setCheckedItem(R.id.admin_nav_volunteer)
                    else -> navigationView.setCheckedItem(R.id.admin_nav_dashboard)
                }
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.admin_nav_dashboard -> loadFragment(DashboardFragment())
            R.id.admin_nav_donate -> loadFragment(AdminDonateFragment())
            R.id.admin_nav_news -> loadFragment(AdminNewsFragment())
            R.id.admin_nav_report -> loadFragment(AdminReportFragment())
            R.id.admin_nav_volunteer -> loadFragment(AdminVolunteerFragment())
//            R.id.admin_nav_user -> loadFragment(AdminUserFragment())
            R.id.admin_nav_logout -> logout()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()
    }

    fun logout(){
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        editor.remove("isLoggedIn")
        editor.remove("userRole")
        editor.remove("userEmail")
        editor.remove("userId")

        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}