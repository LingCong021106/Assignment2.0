package com.example.assignment

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout

//jiahon
class MainActivity : AppCompatActivity() {

    companion object {
        const val MY_SOCKET_TIMEOUT_MS = 30000
    }

    //TextView
    lateinit var loginText: TextView
    lateinit var etEmailLayout: TextInputLayout
    lateinit var passwordInputLayout:TextInputLayout

    //Button
    lateinit var individualButton: Button
    lateinit var organizationButton: Button
    lateinit var adminButton: Button
    lateinit var loginBtn:Button
    //Drawable
    lateinit var iconDrawable: Drawable
    lateinit var OiconDrawable: Drawable
    lateinit var AiconDrawable: Drawable
    //EditText
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    //others
    private var email: String? = null
    private var password: String? = null
    private var usersRole = "users"
    private val URL: String ="http://192.168.151.84/Assignment(Mobile)/login.php"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_login)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // 用户已登录，根据用户角色跳转到相应页面
            val userRole = sharedPreferences.getString("userRole", "users")
            when (userRole) {
                "admin" -> {
//                    val intent = Intent(this, AdminActivity::class.java)
//                    startActivity(intent)
//                    finish()
                }
                "organization" -> {
//                    val intent = Intent(this, OrganizationActivity::class.java)
//                   startActivity(intent)
//                    finish()
                }
                else -> {
                    val intent = Intent(this, user_home::class.java)
                    startActivity(intent)
                   finish()
                }
            }
        }else{


            etEmailLayout=findViewById(R.id.etEmailLayout)
            passwordInputLayout=findViewById(R.id.passwordInputLayout)
            loginText = findViewById(R.id.loginText)
            individualButton = findViewById(R.id.individualButton)
            organizationButton = findViewById(R.id.organizationButton)
            adminButton=findViewById(R.id.adminButton)
            loginBtn=findViewById(R.id.loginBtn)

            iconDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_person_2_24)!!
            OiconDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_business_24)!!
            AiconDrawable = ContextCompat.getDrawable(this, R.drawable.admin_icon)!!

            val selectedIconColor = ContextCompat.getColor(this, R.color.selected_icon_color)
            individualButton.backgroundTintList = ColorStateList.valueOf(selectedIconColor)
            individualButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            iconDrawable.setTint(ContextCompat.getColor(this, android.R.color.white))
            individualButton.setCompoundDrawablesWithIntrinsicBounds(null, iconDrawable, null, null)


            organizationButton.setOnClickListener {
                usersRole = "organization"
                updateOrganizationButtonLayout()
            }

            individualButton.setOnClickListener {
                usersRole = "users"
                updateIndividualButtonLayout()
            }

            adminButton.setOnClickListener {
                usersRole = "admin"
                updateAdminButtonLayout()
            }
            loginBtn.setOnClickListener {
                userlogin()
            }


        }


    }
    private fun updateOrganizationButtonLayout() {

        etEmailLayout.helperText = null
        etEmailLayout.setHelperTextColor(ColorStateList.valueOf(Color.GRAY))
        passwordInputLayout.helperText = null
        passwordInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.GRAY))

        //set organizationButton
        val selectedIconColor = ContextCompat.getColor(this, R.color.selected_icon_color)
        organizationButton.backgroundTintList = ColorStateList.valueOf(selectedIconColor)
        organizationButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        OiconDrawable.setTint(ContextCompat.getColor(this, android.R.color.white))
        organizationButton.setCompoundDrawablesWithIntrinsicBounds(null, OiconDrawable, null, null)

        //set login text
        loginText.text = "Organization Login"

        //set individualButton(deselected)
        val deselectedIconColor1 = ContextCompat.getColor(this, R.color.white)
        individualButton.backgroundTintList = ColorStateList.valueOf(deselectedIconColor1)
        individualButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        iconDrawable.setTint(ContextCompat.getColor(this, android.R.color.black))
        individualButton.setCompoundDrawablesWithIntrinsicBounds(null, iconDrawable, null, null)

        //set individualButton(deselected)
        val deselectedIconColor2 = ContextCompat.getColor(this, R.color.white)
        adminButton.backgroundTintList = ColorStateList.valueOf(deselectedIconColor2)
        adminButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        AiconDrawable.setTint(ContextCompat.getColor(this, android.R.color.black))
        adminButton.setCompoundDrawablesWithIntrinsicBounds(null, AiconDrawable, null, null)
    }

    private fun updateIndividualButtonLayout() {

        etEmailLayout.helperText = null
        etEmailLayout.setHelperTextColor(ColorStateList.valueOf(Color.GRAY))
        passwordInputLayout.helperText = null
        passwordInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.GRAY))

        //set individualButton
        val selectedIconColor = ContextCompat.getColor(this, R.color.selected_icon_color)
        individualButton.backgroundTintList = ColorStateList.valueOf(selectedIconColor)
        individualButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        iconDrawable.setTint(ContextCompat.getColor(this, android.R.color.white))
        individualButton.setCompoundDrawablesWithIntrinsicBounds(null, iconDrawable, null, null)

        //set login text
        loginText.text = "User Login"

        //set individualButton
        val deselectedIconColor1 = ContextCompat.getColor(this, R.color.white)
        organizationButton.backgroundTintList = ColorStateList.valueOf(deselectedIconColor1)
        organizationButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        OiconDrawable.setTint(ContextCompat.getColor(this, android.R.color.black))
        organizationButton.setCompoundDrawablesWithIntrinsicBounds(null, OiconDrawable, null, null)

        //set individualButton(deselected)
        val deselectedIconColor2 = ContextCompat.getColor(this, R.color.white)
        adminButton.backgroundTintList = ColorStateList.valueOf(deselectedIconColor2)
        adminButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        AiconDrawable.setTint(ContextCompat.getColor(this, android.R.color.black))
        adminButton.setCompoundDrawablesWithIntrinsicBounds(null, AiconDrawable, null, null)

    }

    private fun updateAdminButtonLayout() {

        etEmailLayout.helperText = null
        etEmailLayout.setHelperTextColor(ColorStateList.valueOf(Color.GRAY))
        passwordInputLayout.helperText = null
        passwordInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.GRAY))

        //set individualButton
        val selectedIconColor = ContextCompat.getColor(this, R.color.selected_icon_color)
        adminButton.backgroundTintList = ColorStateList.valueOf(selectedIconColor)
        adminButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        AiconDrawable.setTint(ContextCompat.getColor(this, android.R.color.white))
        adminButton.setCompoundDrawablesWithIntrinsicBounds(null, AiconDrawable, null, null)

        //set login text
        loginText.text = "Admin Login"

        //set organizationButton
        val deselectedIconColor1 = ContextCompat.getColor(this, R.color.white)
        organizationButton.backgroundTintList = ColorStateList.valueOf(deselectedIconColor1)
        organizationButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        OiconDrawable.setTint(ContextCompat.getColor(this, android.R.color.black))
        organizationButton.setCompoundDrawablesWithIntrinsicBounds(null, OiconDrawable, null, null)

        //set individualButton(deselected)
        val deselectedIconColor2 = ContextCompat.getColor(this, R.color.white)
        individualButton.backgroundTintList = ColorStateList.valueOf(deselectedIconColor2)
        individualButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        iconDrawable.setTint(ContextCompat.getColor(this, android.R.color.black))
        individualButton.setCompoundDrawablesWithIntrinsicBounds(null, iconDrawable, null, null)

    }

    fun userlogin() {

        etEmail = findViewById(R.id.etEmailEditText)
        etPassword = findViewById(R.id.passwordEditText)

        if (!isNetworkConnected()) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
            return
        } else {
        Log.d("MyApp", "Sending login request to URL: $URL")

            etEmailLayout = findViewById<TextInputLayout>(R.id.etEmailLayout)
            passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)

            etEmailLayout.helperText = null
            etEmailLayout.setHelperTextColor(ColorStateList.valueOf(Color.GRAY))
            passwordInputLayout.helperText = null
            passwordInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.GRAY))

            var email = etEmail.getText().toString().trim()
            var password = etPassword.getText().toString().trim()
            if (email != "" && password != "") {

                val stringRequest: StringRequest = object : StringRequest(
                    Request.Method.POST, URL,
                    Response.Listener { response ->
                        Log.d("res", response)
                        if (response == "success") {

                            val sharedPreferences =
                                getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isLoggedIn", true)
                            editor.putString("userRole", usersRole)
                            editor.apply()

                            if (usersRole == "admin") {
                                //go admin page
                            } else if (usersRole == "organization") {
                                //go organization page
                            } else {
                                val intent = Intent(this@MainActivity, user_home::class.java)
                                startActivity(intent)
                                finish()
                            }

                        } else if (response == "failure") {
                            Toast.makeText(
                                this@MainActivity,
                                "Invalid Email Address/Password",
                                Toast.LENGTH_SHORT
                            ).show()
                            etEmailLayout.helperText = "*Invalid Email Address/Password"
                            etEmailLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                            passwordInputLayout.helperText = "*Invalid Email Address/Password"
                            passwordInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(
                            this@MainActivity,
                            error.toString().trim { it <= ' ' },
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String>? {
                        val data: MutableMap<String, String> = HashMap()
                        data["email"] = email
                        data["password"] = password
                        data["role"] = usersRole
                        return data
                    }
                }


                stringRequest.setShouldCache(false)
                stringRequest.retryPolicy = DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )

                val requestQueue = Volley.newRequestQueue(applicationContext)
                requestQueue.add(stringRequest)

            } else {
                if (email.isEmpty()) {
                    etEmailLayout.helperText = "*Email cannot be empty"
                    etEmailLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                }

                if (password.isEmpty()) {
                    passwordInputLayout.helperText = "*Password cannot be empty"
                    passwordInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED))

                    Toast.makeText(this, "Fields can not be empty!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun register(view: View?) {
        val intent = Intent(this, RegisterAccount::class.java)
        startActivity(intent)
        finish()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}



////lingcong
//class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
//
//    //cy
//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var binding: AdminHomeBinding
//
//    //    private val userHomeFragment = UserHomeFragment()
////    private val eventFragment = EventFragment()
////    private val donateFragment = DonateFragment()
////    private val profileFragment = ProfileFragment()
//    lateinit var bottomNav: BottomNavigationView
//    lateinit var navigationView: NavigationView
//
//    //lc
////        setContentView(R.layout.user_home)
////
//    private lateinit var dashboardUserBtn: Button
//    private lateinit var navController: NavController
//
//        private lateinit var drawerLayout: DrawerLayout
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.admin_home)
//
//        drawerLayout = findViewById<DrawerLayout>(R.id.testDrawer_layout)
//
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        val navigationView = findViewById<NavigationView>(R.id.nav_view)
//        navigationView.setNavigationItemSelectedListener(this)
//
//        val toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        drawerLayout.addDrawerListener(toogle)
//        toogle.syncState()
//
//        if(savedInstanceState == null){
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, DashboardFragment()).commit()
//            navigationView.setCheckedItem(R.id.admin_nav_dashboard)
//        }
//
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.admin_nav_dashboard -> loadFragment(DashboardFragment())
//            R.id.admin_nav_donate -> loadFragment(AdminDonateFragment())
//            R.id.admin_nav_news -> loadFragment(AdminNewsFragment())
//            R.id.admin_nav_report -> loadFragment(AdminReportFragment())
//            R.id.admin_nav_volunteer -> loadFragment(AdminVolunteerFragment())
//            R.id.admin_nav_user -> loadFragment(AdminUserFragment())
//        }
//        drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }
//
//    override fun onBackPressed() {
//        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
//            drawerLayout.closeDrawer(GravityCompat.START)
//        }
//        else{
//            onBackPressedDispatcher.onBackPressed()
//        }
////    }
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
//////        setContentView(R.layout.admin_home)
//////        setContentView(R.layout.user_home)
////
//////        cy
////        binding = AdminHomeBinding.inflate(layoutInflater)
////        setContentView(binding.root)
////
////        setSupportActionBar(binding.appBarMain.toolbar)
////
////        binding.appBarMain.fab.setOnClickListener { view ->
////            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                .setAction("Action", null).show()
////        }
////        val drawerLayout: DrawerLayout = binding.drawerLayout
////        val navView: NavigationView = binding.navView
////        val navController = findNavController(R.id.nav_host_fragment_content_main)
////        // Passing each menu ID as a set of Ids because each
////        // menu should be considered as top level destinations.
////        appBarConfiguration = AppBarConfiguration(
////            setOf(
////                R.id.admin_nav_dashboard,
////                R.id.admin_nav_donate,
////                R.id.admin_nav_volunteer,
////                R.id.admin_nav_user,
////                R.id.admin_nav_news,
////                R.id.admin_nav_report
////
////            ), drawerLayout
////        )
////        setupActionBarWithNavController(navController, appBarConfiguration)
////        navView.setupWithNavController(navController)
//
////        navigationView = findViewById(R.id.nav_view) as NavigationView
////        navigationView.setNavigationItemSelectedListener() {
////            when (it.itemId) {
////                R.id.admin_nav_dashboard -> {
////                    loadFragment(DashboardFragment())
////                    true
////                }
////                R.id.admin_nav_donate -> {
////                    loadFragment(AdminDonateFragment())
////                    true
////                }
////                R.id.admin_nav_volunteer -> {
////                    loadFragment(AdminVolunteerFragment())
////                    true
////                }
////                R.id.admin_nav_user -> {
////                    loadFragment(AdminUserFragment())
////                    true
////                }
////                R.id.admin_nav_news -> {
////                    loadFragment(AdminNewsFragment())
////                    true
////                }
////                R.id.admin_nav_report -> {
////                    loadFragment(AdminNewsFragment())
////                    true
////                }
////                else -> {
////                    true
////                }
////            }
////            }
//
////        loadFragment(UserHomeFragment())
////        bottomNav = findViewById(R.id.bottom_navigation) as BottomNavigationView
////        bottomNav.setOnItemSelectedListener {
////            when (it.itemId) {
////                R.id.home -> {
////                    loadFragment(UserHomeFragment())
////                    true
////                }
////
////                R.id.donate -> {
////                    loadFragment(DonateFragment())
////                    true
////                }
////
////                R.id.profile -> {
////                    loadFragment(ProfileFragment())
////                    true
////                }
////
////
////                R.id.event -> {
////                    loadFragment(EventFragment())
////                    true
////                }
////
////                else -> {
////                    true
////                }
////            }
////        }
//    }
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }
//
//    private  fun loadFragment(fragment: Fragment){
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.fragment_container,fragment)
//        transaction.commit()
//    }
//
//}
//
//
//
////        dashboardUserBtn = findViewById(R.id.dashboardUserBtn)
////        dashboardUserBtn.setOnClickListener {
////            val intent = Intent(this, AdminUserViewModel::class.java)
////            startActivity(intent)
////        }
//
//
//
////    override fun onCreateOptionsMenu(menu: Menu): Boolean {
////        // Inflate the menu; this adds items to the action bar if it is present.
////        menuInflater.inflate(R.menu.main, menu)
////        return true
////    }
//
//
//    //cy
//
//
//    //lc
////    private val userHomeFragment = UserHomeFragment()
////    private val eventFragment = EventFragment()
////    private val donateFragment = DonateFragment()
////    private val profileFragment = ProfileFragment()
////    lateinit var bottomNav : BottomNavigationView
//    //sb
////    lateinit var image1 : ImageView
////    lateinit var image2 : ImageView
////    private lateinit var listPeopleRecycler : RecyclerView
////    private lateinit var newArrayList: ArrayList<ListDonate>
////    lateinit var imageId : Array<Int>
////    lateinit var name : Array<String>
////    lateinit var btnJoin : Button
////    lateinit var builder : AlertDialog.Builder
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.user_home)
//
//
//
//
//    //cy
////    private lateinit var appBarConfiguration: AppBarConfiguration
////    private lateinit var binding: AdminHomeBinding
//
//
//        //setContentView(R.layout.admin_home)
////        setContentView(R.layout.user_register_account2)
//
//        //cy
////        binding = AdminHomeBinding.inflate(layoutInflater)
////        setContentView(binding.root)
////
////
////        }
//
//        //setContentView(R.layout.admin_home)
////        setContentView(R.layout.user_register_account2)
//
//        //cy
//
//
//
//
//
//
//    //cy
////    override fun onCreateOptionsMenu(menu: Menu): Boolean {
////        // Inflate the menu; this adds items to the action bar if it is present.
////        menuInflater.inflate(R.menu.main, menu)
////        return true
////    }
//
//
//    //cy
//
//
//
////
////        private
////        //sb
////        lateinit var image1: ImageView
////        lateinit var image2: ImageView
////        private lateinit var listPeopleRecycler: RecyclerView
////        private lateinit var newArrayList: ArrayList<ListDonate>
////        lateinit var imageId: Array<Int>
////        lateinit var name: Array<String>
////        override fun onCreate(savedInstanceState: Bundle?) {
////            super.onCreate(savedInstanceState)
////            setContentView(R.layout.user_home)
////            //setContentView(R.layout.fundraising_details)
//
////
////
////            //setContentView(R.layout.user_home)
////
//////        val drawerLayout : DrawerLayout = findViewById(R.id.admin_home)
//////        val navView : NavigationView = findViewById(R.id.nav_view)
//////
//////        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
//////        drawerLayout.addDrawerListener(toggle)
//////        toggle.syncState()
//////
//////        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//////
//////        navView.setNavigationItemSelectedListener {
//////
//////            when(it.itemId){
//////                R.id.admin_nav_dashboard -> Toast.makeText(applicationContext,"Clicked", Toast.LENGTH_LONG).show()
//////            }
//////
//////            true
//////
//////        }
////
////
//////        image1  = findViewById(R.id.event_img)
//////        image1.setImageResource(R.drawable.dice_1)
////
////            image1 = findViewById(R.id.fundraising_img)
////            image1.setImageResource(R.drawable.dice_1)
////
////            image2 = findViewById(R.id.fundraising_orgazation_img)
////            image2.setImageResource(R.drawable.dice_1)
////
////            imageId = arrayOf(
////                R.drawable.dice_1,
////                R.drawable.dice_2,
////                R.drawable.dice_3,
////                R.drawable.dice_4,
////                R.drawable.dice_5,
////                R.drawable.dice_6
////            )
////
////            name = arrayOf(
////                "Ling Cong Cong",
////                "Yong SHun Bin",
////                "Yeam Chi Yong",
////                "Tan Jia Hon",
////                "Ng Ming Zhe",
////                "Khoo Jie Kee"
////            )
////
////            listPeopleRecycler = findViewById(R.id.fundraising_people_list)
////            listPeopleRecycler.layoutManager = LinearLayoutManager(this)
////            listPeopleRecycler.setHasFixedSize(true)
////
////            newArrayList = arrayListOf<ListDonate>()
////            getUserdata()
////
//////    }
////
//////    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//////
//////        if(toggle.onOptionsItemSelected(item)){
//////            return true
//////        }
//////
//////        return super.onOptionsItemSelected(item)
//////    }
////
////
////        }
////        setContentView(R.layout.fundraising_details)
////        setContentView(R.layout.event_details_display)
////        setContentView(R.layout.event_join_confirmation)
////        image1  = findViewById(R.id.event_img)
////        image1.setImageResource(R.drawable.dice_1)
//
//
//
////        image1 = findViewById(R.id.fundraising_img)
////        image1.setImageResource(R.drawable.dice_1)
////
////        image2 = findViewById(R.id.fundraising_orgazation_img)
////        image2.setImageResource(R.drawable.dice_1)
//
////        btnJoin = findViewById(R.id.btn_join)
////        imageId = arrayOf(
////            R.drawable.dice_1,
////            R.drawable.dice_2,
////            R.drawable.dice_3,
////            R.drawable.dice_4,
////            R.drawable.dice_5,
////            R.drawable.dice_6
////        )
////
////        name = arrayOf(
////            "Ling Cong Cong",
////            "Yong SHun Bin",
////            "Yeam Chi Yong",
////            "Tan Jia Hon",
////            "Ng Ming Zhe",
////            "Khoo Jie Kee"
////        )
////
////        listPeopleRecycler = findViewById(R.id.fundraising_people_list)
////        listPeopleRecycler.layoutManager = LinearLayoutManager(this)
////        listPeopleRecycler.setHasFixedSize(true)
////
////        newArrayList = arrayListOf<ListDonate>()
////        getUserdata()
////
////        builder = AlertDialog.Builder(this)
////
////        btnJoin.setOnClickListener { confirmation(it) }
//
//
//
//
////    }
//
////    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
////
////        private fun getUserdata() {
////            for (i in imageId.indices) {
////                val people = ListDonate(imageId[i], name[i])
////                newArrayList.add(people)
////            }
////
////            listPeopleRecycler.adapter = MyAdapter(newArrayList)
////        }
////
//
////    private fun loadFragment(fragment: Fragment) {
////        val transaction = supportFragmentManager.beginTransaction()
////        transaction.replace(R.id.fragment_container, fragment)
////        transaction.commit()
////    }
//
////        return super.onOptionsItemSelected(item)
////    }
//
//
//
//
////    private fun confirmation(view:View) {
////        builder.setTitle("Confirmation")
////            .setMessage("Are you sure want to join this event?")
////            .setCancelable(true)
////            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i -> storeEventPeople() })
////            .setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.cancel() })
////            .show()
////    }
//
////    private fun storeEventPeople(){
////        builder.setTitle("Event")
////            .setMessage("Thank for your joining")
////            .setCancelable(true)
////            .setPositiveButton("Back to home", DialogInterface.OnClickListener { dialogInterface, i -> finish() })
////            .setNegativeButton("",DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.cancel() })
////            .show()
////    }
//
//
//
////    private fun getUserdata() {
////        for (i in imageId.indices) {
////            val people = ListDonate(imageId[i], name[i])
////            newArrayList.add(people)
////        }
////    }
//
//
//
//
////}
//

