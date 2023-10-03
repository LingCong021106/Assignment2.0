package com.example.assignment

import android.annotation.SuppressLint
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
import com.example.assignment.database.AppDatabase
import com.example.assignment.database.User
import com.example.assignment.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import android.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.assignment.database.Admin
import kotlinx.coroutines.CoroutineScope
import org.json.JSONObject
import org.json.JSONException

//jiahon
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appDb: AppDatabase

    companion object {
        const val MY_SOCKET_TIMEOUT_MS = 30000
    }

    //TextView
    lateinit var loginText: TextView
    lateinit var signupText: TextView
    lateinit var etEmailLayout: TextInputLayout
    lateinit var passwordInputLayout: TextInputLayout

    //Button
    lateinit var individualButton: Button
    lateinit var organizationButton: Button
    lateinit var adminButton: Button
    lateinit var loginBtn: Button

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
    private val URL: String = "http://192.168.0.3/Assignment(Mobile)/login.php"
    private val findRemoteURL: String = "http://192.168.0.3/Assignment(Mobile)/findRemote.php"
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_login)


        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        appDb = AppDatabase.getInstance(this)

        if (isLoggedIn) {
            // Logged in directly navigate
            val userRole = sharedPreferences.getString("userRole", "users")
            when (userRole) {
                "admin" -> {
                    val intent = Intent(this@MainActivity, Admin_Organization_EditProfile::class.java)
                    startActivity(intent)
                    finish()
                }

                "organization" -> {
                    val intent = Intent(this@MainActivity, Admin_Organization_EditProfile::class.java)
                    startActivity(intent)
                    finish()
                }

                else -> {
                    val intent = Intent(this@MainActivity, UserEditProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        } else {

            signupText = findViewById(R.id.signupText)
            loginText = findViewById(R.id.loginText)
            individualButton = findViewById(R.id.individualButton)
            organizationButton = findViewById(R.id.organizationButton)
            adminButton = findViewById(R.id.adminButton)
            loginBtn = findViewById(R.id.loginBtn)

            iconDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_person_2_24)!!
            OiconDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_business_24)!!
            AiconDrawable = ContextCompat.getDrawable(this, R.drawable.admin_icon)!!

            val selectedIconColor = ContextCompat.getColor(this, R.color.selected_icon_color)
            individualButton.backgroundTintList = ColorStateList.valueOf(selectedIconColor)
            individualButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            iconDrawable.setTint(ContextCompat.getColor(this, android.R.color.white))
            individualButton.setCompoundDrawablesWithIntrinsicBounds(null, iconDrawable, null, null)

            etEmailLayout = findViewById<TextInputLayout>(R.id.etEmailLayout)
            passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)

            organizationButton.setOnClickListener {
                usersRole = "organization"
                updateOrganizationButtonLayout()
                signupText.visibility = View.VISIBLE
            }

            individualButton.setOnClickListener {
                usersRole = "users"
                updateIndividualButtonLayout()
                signupText.visibility = View.VISIBLE
            }

            adminButton.setOnClickListener {
                usersRole = "admin"
                updateAdminButtonLayout()
                signupText.visibility = View.GONE
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

            val builder = AlertDialog.Builder(this)
            builder.setTitle("No connection")
            builder.setMessage("Please check your internet connection and try again")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()

        } else {
//         Log.d("MyApp","Sending login request to URL: $URL")
            etEmailLayout.helperText = null
            passwordInputLayout.helperText = null

            var localEmail = etEmail.text.toString()
            val localPassword = etPassword.text.toString()
            val encryptedPassword = md5(localPassword)

            lifecycleScope.launch(Dispatchers.IO) {
                val user = appDb.userDao().checkUser(localEmail, encryptedPassword)
                val admin = appDb.adminDao().checkAdmin(localEmail, encryptedPassword)

                launch(Dispatchers.Main) {
                    if (user != null || admin != null) {
                        val LoggedInUserEmail = localEmail.toString()
                        val userIdString = LoggedInUserEmail

                        val sharedPreferences =
                            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()


                        editor.putString("userRole", usersRole)
                        editor.putBoolean("isLoggedIn", true)
                        editor.putString("userEmail", userIdString)
                        editor.apply()

                        if (admin != null && usersRole == "admin") {
                            val intent =
                                Intent(
                                    this@MainActivity,
                                    Admin_Organization_EditProfile::class.java
                                )
                            startActivity(intent)
                            finish()
                        } else if (admin != null && usersRole == "organization") {
                            val intent = Intent(
                                this@MainActivity,
                                Admin_Organization_EditProfile::class.java
                            )
                            startActivity(intent)
                            finish()
                        } else if (user != null && usersRole == "users") {

                            val intent =
                                Intent(this@MainActivity, UserEditProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Invalid Email Address/Password",
                                Toast.LENGTH_SHORT
                            ).show()
                            etEmail.text.clear()
                            etPassword.text.clear()
                            etEmailLayout.helperText = "*Invalid Email Address/Password"
                            etEmailLayout.setHelperTextColor(
                                ColorStateList.valueOf(
                                    Color.RED
                                )

                            )

                            passwordInputLayout.helperText =
                                "*Invalid Email Address/Password"
                            passwordInputLayout.setHelperTextColor(
                                ColorStateList.valueOf(
                                    Color.RED
                                )
                            )
                        }
                    }else {

                        var email = etEmail.getText().toString().trim()
                        var password = etPassword.getText().toString().trim()
                        if (email != "" && password != "") {

                            val stringRequest: StringRequest = @SuppressLint("SuspiciousIndentation")
                            object : StringRequest(
                                Request.Method.POST, URL,
                                Response.Listener { response ->
                                    Log.d("res", response)
                                    val jsonResponseLogin = JSONObject(response)
                                    val successLogin = jsonResponseLogin.getInt("loginSucess")


                                    if (successLogin == 1) {

                                        val LoggedInUserEmail = jsonResponseLogin.getString("userEmail")
                                        val userIdString = LoggedInUserEmail

                                        val LoggedInUserId = jsonResponseLogin.getInt("user_id")
                                        val user_id = LoggedInUserId.toString()

                                        val sharedPreferences =
                                            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        editor.putBoolean("isLoggedIn", true)
                                        editor.putString("userRole", usersRole)
                                        editor.putString("userEmail", userIdString)
                                        editor.putString("userId", user_id)
                                        editor.apply()

                                        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
                                        val userRole = sharedPreferences.getString("userRole", "users")
                                        val userEmail = sharedPreferences.getString("userEmail", "")
                                        val userId = sharedPreferences.getString("userId", "")
                                        Log.d("cMyApp", "isLoggedIn: $isLoggedIn")
                                        Log.d("cMyApp", "userId: $userId")
                                        Log.d("cMyApp", "userEmail: $userEmail")
                                        Log.d("cMyApp", "userEmail: $userRole")

                                        val stringRequestRemote = object : StringRequest(
                                            Request.Method.POST, findRemoteURL,
                                            Response.Listener { response ->
                                                try {
                                                    val jsonResponse = JSONObject(response)
                                                    val success = jsonResponse.getInt("success")

                                                    if (success == 1) {

                                                        val userId = jsonResponse.getInt("user_id")
                                                        val username =
                                                            jsonResponse.getString("username")
                                                        val phone = jsonResponse.getString("phone")
                                                        val photo = jsonResponse.getString("photo")
                                                        val registerDate =
                                                            jsonResponse.getString("registerDate")


                                                        if (usersRole == "users") {
                                                            val user = User(
                                                                userName = username,
                                                                password = encryptedPassword,
                                                                userEmail = email,
                                                                phone = phone,
                                                                photo = photo,
                                                                registerDate = registerDate
                                                            )
                                                            CoroutineScope(Dispatchers.IO).launch {
                                                                // insert local db
                                                                appDb.userDao().insert(user)
                                                            }
                                                        } else {
                                                            val admin = Admin(
                                                                aName = username,
                                                                aEmail = email,
                                                                aPassword = encryptedPassword,
                                                                aPhone = phone,
                                                                role = usersRole,
                                                                photo = photo,
                                                                joinedDate = registerDate
                                                            )
                                                            CoroutineScope(Dispatchers.IO).launch {
                                                                // insert local admin db
                                                                appDb.adminDao().insert(admin)
                                                            }
                                                        }
                                                        if (usersRole == "admin") {
                                                            val intent = Intent(this@MainActivity, Admin_Organization_EditProfile::class.java)
                                                            startActivity(intent)
                                                            finish()
                                                        } else if (usersRole == "organization") {
                                                            val intent = Intent(this@MainActivity, Admin_Organization_EditProfile::class.java)
                                                            startActivity(intent)
                                                            finish()
                                                        } else {
                                                            val intent =
                                                                Intent(this@MainActivity, UserEditProfileActivity::class.java)
                                                            startActivity(intent)
                                                            finish()
                                                        }
                                                        //
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "User information stored locally",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        // user not found show error
                                                        val message =
                                                            jsonResponse.getString("message")
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            message,
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        Log.e(
                                                            "JSONException",
                                                            "JSON parsing error. Response: $response"
                                                        )
                                                    }
                                                } catch (e: JSONException) {
                                                    e.printStackTrace()
                                                    // Logg
                                                    Log.e(
                                                        "JSONException",
                                                        "JSON parsing error: ${e.message}"
                                                    )
                                                    Log.e(
                                                        "JSONException",
                                                        "JSON parsing error. Response: $response"
                                                    )
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "JSON parsing error",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },
                                            Response.ErrorListener { error ->
                                                error.printStackTrace()
                                                // network request error
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Network error",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        ) {
                                            override fun getParams(): MutableMap<String, String> {
                                                val params = HashMap<String, String>()
                                                params["email"] = email
                                                params["password"] = password
                                                params["role"] = usersRole
                                                return params
                                            }
                                        }


                                        val requestQueue = Volley.newRequestQueue(this@MainActivity)
                                        requestQueue.add(stringRequestRemote)



                                    } else if (successLogin == 0) {
                                        Log.d("cMyApp", "userEmail: $email")
                                        Log.d("cMyApp", "password: $password")
                                        Log.d("cMyApp", "password: $usersRole")

                                        Toast.makeText(
                                            this@MainActivity,
                                            "Invalid Email Address/Password",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        etEmail.text.clear()
                                        etPassword.text.clear()
                                        etEmailLayout.helperText = "*Invalid Email Address/Password"
                                        etEmailLayout.setHelperTextColor(
                                            ColorStateList.valueOf(
                                                Color.RED
                                            )

                                        )

                                        passwordInputLayout.helperText =
                                            "*Invalid Email Address/Password"
                                        passwordInputLayout.setHelperTextColor(
                                            ColorStateList.valueOf(
                                                Color.RED
                                            )
                                        )
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


                            }
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Fields can not be empty!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    fun register(view: View?) {
        val intent = Intent(this, RegisterAccount::class.java)
        val userRole = usersRole
        intent.putExtra("user_role_key", userRole)

        startActivity(intent)
        finish()

    }

    fun ForgetPassword(view: View?) {
        val intent = Intent(this, ForgotPassword::class.java)
        val userRole = usersRole
        intent.putExtra("user_role_key", userRole)

        startActivity(intent)
        finish()

    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        val result = StringBuilder()

        for (byte in digest) {
            result.append(String.format("%02x", byte))
        }

        return result.toString()
    }

}

