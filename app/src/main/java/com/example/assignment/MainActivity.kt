package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.ButtonBarLayout
import androidx.fragment.app.Fragment
import com.example.assignment.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    //lc
    private val userHomeFragment = UserHomeFragment()
    private val eventFragment = EventFragment()
    private val donateFragment = DonateFragment()
    private val profileFragment = ProfileFragment()
    private
    lateinit var bottomNav : BottomNavigationView
    //sb
    lateinit var image1 : ImageView
    lateinit var image2 : ImageView
    private lateinit var listPeopleRecycler : RecyclerView
    private lateinit var newArrayList: ArrayList<ListDonate>
    lateinit var imageId : Array<Int>
    lateinit var name : Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_home)
        //setContentView(R.layout.fundraising_details)
        loadFragment(UserHomeFragment())
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
                R.id.event -> {
                    loadFragment(EventFragment())
                    true
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> {true}
            }
        }




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


//        image1  = findViewById(R.id.event_img)
//        image1.setImageResource(R.drawable.dice_1)

        image1 = findViewById(R.id.fundraising_img)
        image1.setImageResource(R.drawable.dice_1)

        image2 = findViewById(R.id.fundraising_orgazation_img)
        image2.setImageResource(R.drawable.dice_1)

        imageId = arrayOf(
            R.drawable.dice_1,
            R.drawable.dice_2,
            R.drawable.dice_3,
            R.drawable.dice_4,
            R.drawable.dice_5,
            R.drawable.dice_6
        )

        name = arrayOf(
            "Ling Cong Cong",
            "Yong SHun Bin",
            "Yeam Chi Yong",
            "Tan Jia Hon",
            "Ng Ming Zhe",
            "Khoo Jie Kee"
        )

        listPeopleRecycler = findViewById(R.id.fundraising_people_list)
        listPeopleRecycler.layoutManager = LinearLayoutManager(this)
        listPeopleRecycler.setHasFixedSize(true)

        newArrayList = arrayListOf<ListDonate>()
        getUserdata()

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

    private fun getUserdata() {
        for(i in imageId.indices){
            val people = ListDonate(imageId[i],name[i])
            newArrayList.add(people)
        }

        listPeopleRecycler.adapter = MyAdapter(newArrayList)
    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()
    }
}