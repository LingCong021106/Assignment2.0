package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.assignment.admin.user.AdminUserFragment
import com.example.assignment.admin.user.AdminUserViewModel
import com.google.android.material.navigation.NavigationView
import com.example.assignment.databinding.AdminHomeBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    //cy
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: AdminHomeBinding
    private lateinit var dashboardUserBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.admin_home)
        //setContentView(R.layout.user_home)

        //cy
        binding = AdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.admin_nav_dashboard, R.id.admin_nav_donate, R.id.admin_nav_volunteer, R.id.admin_nav_user, R.id.admin_nav_news, R.id.admin_nav_report

            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        dashboardUserBtn = findViewById(R.id.dashboardUserBtn)
//        dashboardUserBtn.setOnClickListener {
//            val intent = Intent(this, AdminUserViewModel::class.java)
//            startActivity(intent)
//        }



   }


    //cy
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }


    //cy
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
