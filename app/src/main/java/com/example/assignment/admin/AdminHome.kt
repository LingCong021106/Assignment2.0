package com.example.assignment.admin

import android.R.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.assignment.Admin_Organization_EditProfile
import com.example.assignment.BitmapConverter
import com.example.assignment.MainActivity
import com.example.assignment.R
import com.example.assignment.admin.dashboard.DashboardFragment
import com.example.assignment.admin.donate.AdminDonateFragment
import com.example.assignment.admin.event.AdminEventFragment
import com.example.assignment.admin.news.AdminNewsFragment
import com.example.assignment.admin.report.AdminReportFragment
import com.example.assignment.admin.user.AdminUserFragment
import com.example.assignment.database.Admin
import com.example.assignment.database.AppDatabase
import com.example.assignment.database.User
import com.example.assignment.databinding.AdminHomeBinding
import com.example.assignment.databinding.ProfileHistoryBinding
import com.example.assignment.user.home.UserHomeFragment
import com.example.assignment.user.profile.ProfileFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext




class AdminHome: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appDB : AppDatabase
    private var admin: Admin = Admin()
    private var adminImage : String = ""
    private var adminEmail : String = ""
    private var adminName : String = ""
    private lateinit var binding: AdminHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminHomeBinding.inflate(layoutInflater)

        setContentView(R.layout.admin_home)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userRole = sharedPreferences.getString("userRole", "users")
        var adminId = sharedPreferences.getInt("userId", -1)


        //get data from room
        CoroutineScope(Dispatchers.IO).launch {
            appDB = AppDatabase.getInstance(this@AdminHome)
            admin = appDB.adminDao().getAdminById(adminId)!!
            withContext(Dispatchers.Main) {
                admin?.let {
                    adminName = it.aName.toString()
                    adminImage = it.photo.toString()
                    adminEmail = it.aEmail.toString()
                    updateUI()
                }
            }
        }

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)


        navigationView.setNavigationItemSelectedListener(this)

        val menuNav: Menu = navigationView.getMenu()

        if(userRole == "organization" && isLoggedIn){
            val item : MenuItem = menuNav.findItem(R.id.admin_nav_user).setVisible(false)
            this.invalidateOptionsMenu()

            val item2 : MenuItem = menuNav.findItem(R.id.admin_nav_news).setVisible(false)
            this.invalidateOptionsMenu()
        }

        val item2 : MenuItem = menuNav.findItem(R.id.admin_nav_report).setVisible(false)
        this.invalidateOptionsMenu()

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if(savedInstanceState == null){
            val getFragment = intent.getStringExtra("fragment")
            if(getFragment == "user"){

                loadFragment(AdminUserFragment())
            }
            else{
                loadFragment(DashboardFragment())
            }
        }

        toolbar.setNavigationOnClickListener{
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)

            } else {
                var currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                when(currentFragment){
                    is AdminUserFragment -> navigationView.setCheckedItem(R.id.admin_nav_user)
                    is AdminReportFragment -> navigationView.setCheckedItem(R.id.admin_nav_report)
                    is AdminNewsFragment -> navigationView.setCheckedItem(R.id.admin_nav_news)
                    is AdminDonateFragment -> navigationView.setCheckedItem(R.id.admin_nav_donate)
                    is AdminEventFragment -> navigationView.setCheckedItem(R.id.admin_nav_volunteer)
                    else -> navigationView.setCheckedItem(R.id.admin_nav_dashboard)
                }
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

    }

    private fun updateUI(){
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val header : View = navigationView.getHeaderView(0)
        val headerName = header.findViewById<TextView>(R.id.user_name)
        val headerEmail = header.findViewById<TextView>(R.id.user_gmail)
        val headerImage = header.findViewById<ImageView>(R.id.imageView)
        val headerView = header.findViewById<View>(R.id.headerView)

        headerName.text = adminName
        headerEmail.text = adminEmail
        var bitmap = BitmapConverter.convertStringToBitmap(adminImage)
        Glide.with(this)
            .load(bitmap)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(headerImage)
        headerImage.setImageBitmap(bitmap)
        headerView.setOnClickListener{
            val intent = Intent(this, Admin_Organization_EditProfile::class.java)
            startActivity(intent)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.admin_nav_dashboard -> loadFragment(DashboardFragment())
            R.id.admin_nav_donate -> loadFragment(AdminDonateFragment())
            R.id.admin_nav_news -> loadFragment(AdminNewsFragment())
            R.id.admin_nav_report -> loadFragment(AdminReportFragment())
            R.id.admin_nav_volunteer -> loadFragment(AdminEventFragment())
            R.id.admin_nav_user -> loadFragment(AdminUserFragment())
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
        transaction.addToBackStack(null)
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