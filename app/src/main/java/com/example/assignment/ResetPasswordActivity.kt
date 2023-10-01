package com.example.assignment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.database.AppDatabase
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var etPassword :EditText
    lateinit var etconPassword:EditText
    lateinit var submitButton: Button
    lateinit var etPasswordLayout : TextInputLayout
    lateinit var etconPasswordLayout: TextInputLayout
    private var email: String? = null
    private val resetPasswordURL:String="http://192.168.0.4/Assignment(Mobile)/reset_password.php"
    private lateinit var appDb: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_reset_password)
        submitButton  = findViewById(R.id.submitButton)
        Log.d("ResetPasswordActivity", "ResetPasswordActivity started")
        etPasswordLayout = findViewById(R.id.etPasswordLayout)
        etconPasswordLayout = findViewById(R.id.etconPasswordLayout)
        appDb = AppDatabase.getInstance(this)

         email = intent.getStringExtra("userEmail")
        Log.d("EmailDebug", "Email sending initiated for userEmail: $email")

        if (email != null) {
            etPassword = findViewById(R.id.etPassword)
            etconPassword = findViewById(R.id.etconPassword)


        }else{

            Toast.makeText(applicationContext,"There is something wrong", Toast.LENGTH_LONG).show()

        }

        submitButton.setOnClickListener(){
            // Show a confirmation dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to change your password?")

            // Positive button (OK)
            builder.setPositiveButton("Change") { dialog, which ->
                // User clicked OK, proceed with the profile update
                checkPassword()
            }

            // Negative button (Cancel)
            builder.setNegativeButton("Cancel") { dialog, which ->
                // User clicked Cancel, do nothing or handle as needed
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun checkPassword(){
        val password = etPassword.text.toString().trim()
        val conpassword = etconPassword.text.toString().trim()


        if(password.isNotEmpty() && conpassword.isNotEmpty()){
            var hasError = false

            if (password.isEmpty()) {
                etPasswordLayout.setError("Password cannot be empty")
                etPasswordLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                hasError = true
            } else {
                etPasswordLayout.error = null
                etPasswordLayout.isErrorEnabled = false
                etPasswordLayout.setEndIconDrawable(R.drawable.baseline_check_24)
            }

            if (conpassword.isEmpty()) {
                etconPasswordLayout.setError("Password cannot be empty")
                etconPasswordLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                hasError = true
            } else {
                etconPasswordLayout.error = null
                etconPasswordLayout.isErrorEnabled = false
                etconPasswordLayout.setEndIconDrawable(R.drawable.baseline_check_24)
            }

            if (password != conpassword) {
                etPasswordLayout.setError("Passwords do not match")
                etPasswordLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                etconPasswordLayout.setError("Passwords do not match")
                etconPasswordLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                hasError = true
            } else {
                etPasswordLayout.error = null
                etPasswordLayout.isErrorEnabled = false
                etconPasswordLayout.error = null
                etconPasswordLayout.isErrorEnabled = false
                etPasswordLayout.setEndIconDrawable(R.drawable.baseline_check_24)
                etconPasswordLayout.setEndIconDrawable(R.drawable.baseline_check_24)

                // Password Format
                if (!isValidPassword(password)) {
                    etPasswordLayout.setError(
                        "Password must have at least 6 characters, including at least one uppercase letter, one lowercase letter, and one special character"
                    )
                    etPasswordLayout.setEndIconDrawable(R.drawable.baseline_error_24)
                    etconPasswordLayout.setError(
                        "Password must have at least 6 characters, including at least one uppercase letter, one lowercase letter, and one special character"
                    )
                    etconPasswordLayout.setEndIconDrawable(R.drawable.baseline_error_24)
                    hasError = true
                } else {
                    etPasswordLayout.error = null
                    etPasswordLayout.isErrorEnabled = false
                    etconPasswordLayout.error = null
                    etconPasswordLayout.isErrorEnabled = false
                    etPasswordLayout.setEndIconDrawable(R.drawable.baseline_check_24)
                    etconPasswordLayout.setEndIconDrawable(R.drawable.baseline_check_24)
                }
            }

            if(!hasError){
                storeNewPassword(password)
            }

        }else{

            Toast.makeText(applicationContext,"Password and Confirm Password cannot be empty", Toast.LENGTH_LONG).show()
        }
    }

    fun storeNewPassword(password: String){
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
            val stringRequestRemote = object : StringRequest(
                Request.Method.POST, resetPasswordURL,
                com.android.volley.Response.Listener { response ->
                    try {
                        Log.d("ResponseData", "Server Response: $response")
                        val jsonResponse = JSONObject(response)
                        val success = jsonResponse.getInt("success")
                        val role = jsonResponse.getInt("role")
                        val roleAdmin = jsonResponse.getInt("roleAdmin")
                        val encryptedPassword = md5(password)
                        if (success == 1) {

                            CoroutineScope(Dispatchers.IO).launch {
                                if (role == 1) {
                                    val user = appDb.userDao().getUserByEmail(email ?: "")
                                    appDb.userDao().updateUserPassword(encryptedPassword, email ?: "")
                                } else if (roleAdmin == 1) {
                                    val admin = appDb.adminDao().getAdminByEmail(email ?: "")
                                    appDb.adminDao().updateAdminPassword(encryptedPassword, email ?: "")
                                }
//                            val user = appDb.userDao().getUserByEmail(userEmail ?: "")
//                            appDb.userDao().updateUserInfo(originalEmail, newName, newEmail, newPhone, profileImageUrl)
                            }

                            val intent = Intent(this, MainActivity::class.java) // 返回到登录活动
                            startActivity(intent)
                            finish()
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    "Password reset successful. Please log in again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {

                            Toast.makeText(
                                this@ResetPasswordActivity,
                                "Password updated Failed....",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("ResponseData", "JSON parsing error: ${e.message}")
                    }
                },
                com.android.volley.Response.ErrorListener { error ->
                    error.printStackTrace()
                    Log.e("ResponseData", "Network error: ${error.message}")
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val data: MutableMap<String, String> = HashMap()
                    data["email"] = email ?: ""
                    data["password"] = password
                    return data
                }
            }
            val requestQueue = Volley.newRequestQueue(this@ResetPasswordActivity)
            requestQueue.add(stringRequestRemote)
        }
    }



    fun login(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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

    private fun isValidPassword(password: String): Boolean {
        //Requirement: One Uppercase , One Lowercase , One Symbol , At Least 6 Characters
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$"
        return password.matches(passwordPattern.toRegex())
    }
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}
