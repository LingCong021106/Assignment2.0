package com.example.assignment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import androidx.room.Room
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import com.example.assignment.database.Admin
import com.example.assignment.database.AppDatabase
import com.example.assignment.database.User
import com.example.assignment.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale



class RegisterAccount : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appDb: AppDatabase
    private var userRole: String? = null
    lateinit var roleLayout: TextView
    lateinit var registerText:TextView
    lateinit var nameLayout: TextInputLayout
    lateinit var etEmailLayout: TextInputLayout
    lateinit var passwordLayout: TextInputLayout
    lateinit var conpasswordLayout: TextInputLayout
    lateinit var etphoneLayout:TextInputLayout
    lateinit var etphoneEditText:EditText
    lateinit var etname:EditText
    lateinit var etemail: EditText
    lateinit var etpassword:EditText
    lateinit var etconpassword:EditText
    lateinit var signupBtn:Button
    var name : String? = ""
    var email : String? = ""
    var password : String? = ""
    var phone : String? = ""

    //device ipv4 address
    private val URL:String="http://192.168.0.4/Assignment(Mobile)/register.php"
    private val check_emailURL:String="http://192.168.0.4/Assignment(Mobile)/check_email.php"
    private val check_phoneURL:String="http://192.168.0.4/Assignment(Mobile)/check_phone.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_register_account)

        registerText=findViewById(R.id.registerText)
        roleLayout = findViewById(R.id.roleLayout)

        userRole = intent.getStringExtra("user_role_key") ?: "User"
        if (userRole != null) {
            if (userRole == "organization") {

                registerText.text = "Register As Organization"
                roleLayout.text = "Register As User"
            } else {

                registerText.text = "Register As User"
                roleLayout.text = "Register As Organization"
            }
        } else {
            // 如果没有传递用户角色数据，默认按钮文本
            registerText.text = "Register As User"
            roleLayout.text = "Register As Organization"
        }


        nameLayout = findViewById(R.id.nameLayout)
        etEmailLayout = findViewById(R.id.etEmailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        conpasswordLayout = findViewById(R.id.conpasswordLayout)
        etphoneLayout=findViewById(R.id.etPhoneLayout)
        appDb = AppDatabase.getInstance(this)

        etname = findViewById(R.id.nameEditText)
        etemail = findViewById(R.id.etEmailEditText)
        etpassword = findViewById(R.id.passwordEditText)
        etconpassword = findViewById(R.id.conpasswordEditText)
        etphoneEditText=findViewById(R.id.etPhoneEditText)
        signupBtn = findViewById(R.id.signupBtn)



        appDb = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "assignment"
        )
            .fallbackToDestructiveMigration()
            .build()

        signupBtn.setOnClickListener{
             validationInput()
        }
        roleLayout.setOnClickListener {
            if (userRole == "organization") {
                userRole = "users"
                registerText.text = "Register As Users"
                roleLayout.text = "Register As Organization"
            } else if (userRole == "users") {
                userRole = "organization"
                registerText.text = "Register As Organization"
                roleLayout.text = "Register As Users"
            }
        }


    }

    fun validationInput() {
        nameLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED))
        etEmailLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED))
        etphoneLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED))
        passwordLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED))
        conpasswordLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED))
        nameLayout.setErrorIconDrawable(null)
        etEmailLayout.setErrorIconDrawable(null)
        etphoneLayout.setErrorIconDrawable(null)
        passwordLayout.setErrorIconDrawable(null)
        conpasswordLayout.setErrorIconDrawable(null)

        val userName = etname.text.toString().trim()
        val aEmail = etemail.text.toString().trim()
        val phone = etphoneEditText.text.toString().trim()
        val password = etpassword.text.toString().trim()
        val conpassword = etconpassword.text.toString().trim()

        if (userName.isNotEmpty() && aEmail.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty() && conpassword.isNotEmpty()) {
            var hasError = false

// Check Name is Empty
            if (name!!.isEmpty()) {
                nameLayout.error = null
                nameLayout.isErrorEnabled = false
                nameLayout.setEndIconDrawable(R.drawable.baseline_check_24)
            } else {
                nameLayout.setError("User Name cannot be empty")
                nameLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                hasError = true
            }


// Check Password is Empty
            if (password.isEmpty()) {
                passwordLayout.setError("Password cannot be empty")
                passwordLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                hasError = true
            } else {
                passwordLayout.error = null
                passwordLayout.isErrorEnabled = false
                passwordLayout.setEndIconDrawable(R.drawable.baseline_check_24)
            }

            if (conpassword.isEmpty()) {
                conpasswordLayout.setError("Password cannot be empty")
                conpasswordLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                hasError = true
            } else {
                conpasswordLayout.error = null
                conpasswordLayout.isErrorEnabled = false
                etphoneLayout.setEndIconDrawable(R.drawable.baseline_check_24)
            }


            // Check password is equal conform password
            if (password != conpassword) {
                passwordLayout.setError("Passwords do not match")
                passwordLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                conpasswordLayout.setError("Passwords do not match")
                conpasswordLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                hasError = true
            } else {
                passwordLayout.error = null
                passwordLayout.isErrorEnabled = false
                conpasswordLayout.error = null
                conpasswordLayout.isErrorEnabled = false
                passwordLayout.setEndIconDrawable(R.drawable.baseline_check_24)
                conpasswordLayout.setEndIconDrawable(R.drawable.baseline_check_24)

                // Password Format
                if (!isValidPassword(password)) {
                    passwordLayout.setError(
                        "Password must have at least 6 characters, including at least one uppercase letter, one lowercase letter, and one special character"
                    )
                    passwordLayout.setEndIconDrawable(R.drawable.baseline_error_24)
                    conpasswordLayout.setEndIconDrawable(R.drawable.baseline_error_24)
                    hasError = true
                } else {
                    passwordLayout.error = null
                    passwordLayout.isErrorEnabled = false
                    conpasswordLayout.error = null
                    conpasswordLayout.isErrorEnabled = false
                    passwordLayout.setEndIconDrawable(R.drawable.baseline_check_24)
                    conpasswordLayout.setEndIconDrawable(R.drawable.baseline_check_24)
                }
            }


// Check Email is Empty
            if (aEmail.isEmpty()) {
                etEmailLayout.setError("Email Address cannot be empty")
                etEmailLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                hasError = true


            } else {
                etEmailLayout.error = null
                etEmailLayout.isErrorEnabled = false
                etEmailLayout.setEndIconDrawable(R.drawable.baseline_check_24)
                // Check Email Format
                if (!isValidEmail(aEmail)) {
                    etEmailLayout.setError("Invalid email format")
                    etEmailLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                    hasError = true
                } else {
                    etEmailLayout.error = null
                    etEmailLayout.isErrorEnabled = false
                    etEmailLayout.setEndIconDrawable(R.drawable.baseline_check_24)
                    //Check Exist Email
//                    checkEmailExistence(aEmail) { exists ->
//                        if (exists) {
//                            etEmailLayout.setError("Email already been used")
//                            etEmailLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
//                            hasError = true
////                            Toast.makeText(this, exists.toString(), Toast.LENGTH_SHORT).show()
//                        } else {
//                            etEmailLayout.error = null
//                            etEmailLayout.isErrorEnabled = false
//                            etEmailLayout.setEndIconDrawable(R.drawable.baseline_check_24)
////                            Toast.makeText(this, exists.toString(), Toast.LENGTH_SHORT).show()
//                        }
//                    }

                }
            }

// Check Phone Number is Empty
        if (phone.isEmpty()) {
            etphoneLayout.setError("Phone cannot be empty")
            etphoneLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
            hasError = true
        } else {
            etphoneLayout.error = null
            etphoneLayout.isErrorEnabled = false
            etphoneLayout.setEndIconDrawable(R.drawable.baseline_check_24)

            // Check Phone Format
            if (!isValidPhone(phone)) {
                etphoneLayout.setError("Invalid phone format")
                etphoneLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                hasError = true
            } else {
                etphoneLayout.error = null
                etphoneLayout.isErrorEnabled = false
                etphoneLayout.setEndIconDrawable(R.drawable.baseline_check_24)

//                checkPhoneExistence(phone) { exists ->
//                    if (exists) {
//                        etphoneLayout.setError("Phone already been used")
//                        etphoneLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
//                        hasError = true
//
//                    } else {
//                        etphoneLayout.error = null
//                        etphoneLayout.isErrorEnabled = false
//                        etphoneLayout.setEndIconDrawable(R.drawable.baseline_check_24)
//
//                    }
//                }
            }
        }


            var emailExist = false
            var phoneExist = false

            runBlocking {
                val jobEmail = async {
                    // check email exists
                    checkEmailExistence(aEmail) { emailExists ->
                        if (emailExists) {
                            emailExist = true
                            etEmailLayout.post {
                                etEmailLayout.error = "Email already in use"
                                etEmailLayout.isErrorEnabled = true
                                etEmailLayout.errorIconDrawable =
                                    ContextCompat.getDrawable(this@RegisterAccount, R.drawable.baseline_error_24)
                            }
                        }
                    }
                }

                val jobPhone = async {
                    // 检查电话号码是否存在
                    checkPhoneExistence(phone) { phoneExists ->
                        if (phoneExists) {
                            phoneExist = true
                            etphoneLayout.post {
                                etphoneLayout.error = "Phone already in use"
                                etphoneLayout.isErrorEnabled = true
                                etphoneLayout.errorIconDrawable =
                                    ContextCompat.getDrawable(this@RegisterAccount, R.drawable.baseline_error_24)
                            }
                        }
                    }
                }

                // 等待两个异步操作完成
                jobEmail.await()
                jobPhone.await()

                if (!hasError && !emailExist && !phoneExist) {
                    // 没有错误并且电子邮件和电话号码都不存在，可以保存数据
                    save(userName, aEmail, password, phone)
                }
            }




        }else{
            Toast.makeText(
                applicationContext,
                "Please Ensure All Fields is entered...",
                Toast.LENGTH_SHORT
            ).show()
        }

        }




    fun save(userName: String, aEmail: String, password: String, phone: String) {

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
            val currentDateTime = Date()
            val dateFormat = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault())
            val formattedDateTime = dateFormat.format(currentDateTime)
            var lastRole = userRole.toString()

            val stringRequest: StringRequest = object : StringRequest(
                Request.Method.POST, URL,
                Response.Listener { response ->
                    Log.d("Register", response)
                    if (response == "success") {
                        Toast.makeText(
                            applicationContext,
                            "Good",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (userRole == "users") {
                            val user = User(
                                userName = userName,
                                password = md5(password),
                                userEmail = aEmail,
                                phone = phone,
                                photo = null,
                                registerDate = formattedDateTime
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                // insert user local db
                                appDb.userDao().insert(user)
                            }
                        } else {
                            val admin = Admin(
                                aName = userName,
                                aPassword = md5(password),
                                aEmail = aEmail,
                                aPhone = phone,
                                role = userRole,
                                photo = null,
                                joinedDate = formattedDateTime
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                // insert admin local db
                                appDb.adminDao().insert(admin)
                            }
                        }

                        // 在成功插入数据后清除字段
                        clearFields()
                        // 执行验证

                    } else if (response == "failure") {
                        Toast.makeText(
                            applicationContext,
                            "something wrong...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener { error ->
                    // 网络错误或其他错误
                    Toast.makeText(
                        applicationContext,
                        error.toString().trim { it <= ' ' },
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val data: MutableMap<String, String> = HashMap()
                    data["name"] = userName
                    data["email"] = aEmail
                    data["password"] = password
                    data["phone"] = phone
                    data["role"] = lastRole
                    return data
                }
            }
            val requestQueue = Volley.newRequestQueue(applicationContext)
            requestQueue.add(stringRequest)

        }
    }
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
        fun login(view: View?) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        private fun clearFields() {
            etname.text.clear()
            etemail.text.clear()
            etphoneEditText.text.clear()
            etpassword.text.clear()
            etconpassword.text.clear()
            nameLayout.error = null
            etEmailLayout.error = null
            etphoneLayout.error = null
            passwordLayout.error = null
            conpasswordLayout.error = null
        }

        private fun isValidEmail(email: String): Boolean {
            // Format @ , .  , com
            val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"
            return email.matches(emailPattern.toRegex())
        }

        // Phone Validation
        private fun isValidPhone(phone: String): Boolean {
            // Format = 01x-xxxxxxx(7/8)
            val phonePattern = "01[0-9]-[0-9]{7,8}"
            return phone.matches(phonePattern.toRegex())
        }

        // Password validation
        private fun isValidPassword(password: String): Boolean {
            //Requirement: One Uppercase , One Lowercase , One Symbol , At Least 6 Characters
            val passwordPattern =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$"
            return password.matches(passwordPattern.toRegex())
        }

        fun checkEmailExistence(email: String, callback: (Boolean) -> Unit) {
            val stringRequestRemote = object : StringRequest(
                Request.Method.POST, check_emailURL,
                com.android.volley.Response.Listener { response ->
                    try {
                        Log.d("ResponseData", "Server Response: $response")
                        val jsonResponse = JSONObject(response)
                        val success = jsonResponse.getInt("success")

                        if (success == 1) {
                            callback(true)
                        } else {
                            callback(false)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("ResponseData", "JSON parsing error: ${e.message}")
                        callback(false)
                    }
                },
                com.android.volley.Response.ErrorListener { error ->
                    error.printStackTrace()
                    Log.e("ResponseData", "Network error: ${error.message}")
                    callback(false)
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(this@RegisterAccount)
            requestQueue.add(stringRequestRemote)
        }





    fun checkPhoneExistence(phone: String, callback: (Boolean) -> Unit) {
        val stringRequestRemote = object : StringRequest(
            Request.Method.POST, check_phoneURL,
            com.android.volley.Response.Listener { response ->
                try {
                    Log.d("ResponseData", "Server Response: $response")
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getInt("success")

                    if (success == 1) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("ResponseData", "JSON parsing error: ${e.message}")
                    callback(false)
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                error.printStackTrace()
                Log.e("ResponseData", "Network error: ${error.message}")
                callback(false)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["phone"] = phone
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this@RegisterAccount)
        requestQueue.add(stringRequestRemote)
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






//    private fun addToLocal(){

//    }
