package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.example.assignment.databinding.AdminHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    //cy
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: AdminHomeBinding
//    private val userHomeFragment = UserHomeFragment()
//    private val eventFragment = EventFragment()
//    private val donateFragment = DonateFragment()
//    private val profileFragment = ProfileFragment()
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//lc
//        setContentView(R.layout.user_home)
//
//        loadFragment(UserHomeFragment())
//        bottomNav = findViewById(R.id.bottom_navigation) as BottomNavigationView
//        bottomNav.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.home -> {
//                    loadFragment(UserHomeFragment())
//                    true
//                }
//
//                R.id.donate -> {
//                    loadFragment(DonateFragment())
//                    true
//                }
//
//                R.id.profile -> {
//                    loadFragment(ProfileFragment())
//                    true
//                }
//
//                R.id.event -> {
//                    loadFragment(EventFragment())
//                    true
//                }
//
//                else -> {
//                    true
//                }
//            }
//
//
//        }

        //setContentView(R.layout.admin_home)
//        setContentView(R.layout.user_register_account2)

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
                R.id.admin_nav_dashboard,
                R.id.admin_nav_donate,
                R.id.admin_nav_volunteer,
                R.id.admin_nav_user,
                R.id.admin_nav_news,
                R.id.admin_nav_report
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


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


//
//        private
//        //sb
//        lateinit var image1: ImageView
//        lateinit var image2: ImageView
//        private lateinit var listPeopleRecycler: RecyclerView
//        private lateinit var newArrayList: ArrayList<ListDonate>
//        lateinit var imageId: Array<Int>
//        lateinit var name: Array<String>
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            setContentView(R.layout.user_home)
//            //setContentView(R.layout.fundraising_details)

//
//
//            //setContentView(R.layout.user_home)
//
////        val drawerLayout : DrawerLayout = findViewById(R.id.admin_home)
////        val navView : NavigationView = findViewById(R.id.nav_view)
////
////        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
////        drawerLayout.addDrawerListener(toggle)
////        toggle.syncState()
////
////        supportActionBar?.setDisplayHomeAsUpEnabled(true)
////
////        navView.setNavigationItemSelectedListener {
////
////            when(it.itemId){
////                R.id.admin_nav_dashboard -> Toast.makeText(applicationContext,"Clicked", Toast.LENGTH_LONG).show()
////            }
////
////            true
////
////        }
//
//
////        image1  = findViewById(R.id.event_img)
////        image1.setImageResource(R.drawable.dice_1)
//
//            image1 = findViewById(R.id.fundraising_img)
//            image1.setImageResource(R.drawable.dice_1)
//
//            image2 = findViewById(R.id.fundraising_orgazation_img)
//            image2.setImageResource(R.drawable.dice_1)
//
//            imageId = arrayOf(
//                R.drawable.dice_1,
//                R.drawable.dice_2,
//                R.drawable.dice_3,
//                R.drawable.dice_4,
//                R.drawable.dice_5,
//                R.drawable.dice_6
//            )
//
//            name = arrayOf(
//                "Ling Cong Cong",
//                "Yong SHun Bin",
//                "Yeam Chi Yong",
//                "Tan Jia Hon",
//                "Ng Ming Zhe",
//                "Khoo Jie Kee"
//            )
//
//            listPeopleRecycler = findViewById(R.id.fundraising_people_list)
//            listPeopleRecycler.layoutManager = LinearLayoutManager(this)
//            listPeopleRecycler.setHasFixedSize(true)
//
//            newArrayList = arrayListOf<ListDonate>()
//            getUserdata()
//
////    }
//
////    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////
////        if(toggle.onOptionsItemSelected(item)){
////            return true
////        }
////
////        return super.onOptionsItemSelected(item)
////    }
//
//
//        }
//
//        private fun getUserdata() {
//            for (i in imageId.indices) {
//                val people = ListDonate(imageId[i], name[i])
//                newArrayList.add(people)
//            }
//
//            listPeopleRecycler.adapter = MyAdapter(newArrayList)
//        }
//

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}


//    }
//}

