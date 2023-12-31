package com.example.assignment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.assignment.admin.AdminHome
import com.example.assignment.database.Admin
import com.example.assignment.database.AppDatabase
import com.example.assignment.databinding.ActivityMainBinding
import com.example.assignment.user.UserHome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddOrganization : AppCompatActivity() {

    lateinit var userImageView: ImageView
    lateinit var etName : EditText
    lateinit var passwordEditText: EditText
    lateinit var emailEditText: EditText
    lateinit var phoneEditText: EditText
    lateinit var saveButton: Button
    private var imageUrl: String? = null
    private var isImageChanged = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var appDb: AppDatabase
    private val check_emailURL:String="http://10.0.2.2/Assignment(Mobile)/check_email.php"
    private val check_phoneURL:String="http://10.0.2.2/Assignment(Mobile)/check_phone.php"
    private val addNewOrganizationURL:String="http://10.0.2.2/Assignment(Mobile)/addNewOrganization.php"

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_organization)


        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userRole = sharedPreferences.getString("userRole", "users")
        val userEmail = sharedPreferences.getString("userEmail", "")
        val userId = sharedPreferences.getInt("userId", -1)
        appDb = AppDatabase.getInstance(this)
        saveButton = findViewById(R.id.saveButton)
        userImageView = findViewById(R.id.userImageView)

        if (isLoggedIn) {
            CoroutineScope(Dispatchers.IO).launch {

                withContext(Dispatchers.Main) {
                    etName = findViewById(R.id.etName)
                    passwordEditText = findViewById(R.id.passwordEditText)
                    emailEditText = findViewById(R.id.emailEditText)
                    phoneEditText = findViewById(R.id.phoneEditText)

                    Glide.with(this@AddOrganization)
                        .load(R.drawable.upload_image)
                        .apply(com.bumptech.glide.request.RequestOptions.bitmapTransform(CircleCrop()))
                        .into(userImageView)
                }
            }
        }else{
            Toast.makeText(
                this@AddOrganization,
                "You are not loggin...",
                Toast.LENGTH_SHORT
            ).show()
        }

        saveButton.setOnClickListener {
            // Show a confirmation dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to add new organization?")

            // Positive button (OK)
            builder.setPositiveButton("Add") { dialog, which ->
                // User clicked OK, proceed with the profile update
                checkValidation()
            }

            // Negative button (Cancel)
            builder.setNegativeButton("Cancel") { dialog, which ->
                // User clicked Cancel, do nothing or handle as needed
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
        userImageView.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"

            // 启动图像选择器
            startActivityForResult(intent, Companion.PICK_IMAGE_REQUEST)
        }
    }

    fun checkValidation(){
        clearFields()
        val name = etName.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()

        if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && phone.isNotEmpty()){
            var hasError = false

            if (name.isEmpty()) {
                Toast.makeText(this, "Name Empty", Toast.LENGTH_SHORT).show()
                hasError = true
                etName.error = "Name Cannot Be Empty."
                val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                etName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
            } else {
                // no error
            }

            if (email.isEmpty()) {
                runOnUiThread {
                    hasError = true
                    emailEditText.error = "Email Cannot Be Empty."
                    val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                    emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                }
            } else {
                // Check Email Format
                if (!isValidEmail(email)) {
                    runOnUiThread {
                        hasError = true
                        emailEditText.error = "Invalid Email"
                        val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                        emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                    }
                } else {
                    // Check Exist Email if originalEmail != newEmail

                        checkEmailExistence(email) { emailExists ->
                            if (emailExists) {
                                runOnUiThread {
                                    hasError = true
                                    emailEditText.error = "Email Exists"
                                    val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                                    emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                                }
                            } else {
                                // No error
                            }
                        }
                }
            }
            if (phone.isEmpty()) {
                hasError = true
                phoneEditText.error = "Phone Cannot Be Empty."
                val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                phoneEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
            } else {
                // Check Phone Format
                if (!isValidPhone(phone)) {
                    phoneEditText.error = "Invalid Phone(Eg:01x-xxxxxxx)"
                    val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                    phoneEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                    hasError = true
                } else {
                    // Check Exist Phone if originalPhone != newPhone
                        checkPhoneExistence(phone) { phoneExists ->
                            if (phoneExists) {
                                runOnUiThread {
                                    phoneEditText.error = "Phone Exists"
                                    val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                                    phoneEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                                }
                                hasError = true
                            } else {
                                // No error
                            }
                    }
                }
            }

            if(password.isEmpty()){
                passwordEditText.error = "Empty Password"
                val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                hasError = true
            }else{

                if (!isValidPassword(password)) {
                    passwordEditText.error = "\"Password must have at least 6 characters, including at least one uppercase letter, one lowercase letter, and one special character\""
                    val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                    passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                    hasError = true
                }
            }
            if(!hasError){
                addNewOrganization(name,email,password,phone,imageUrl ?: "")
            }

        }else{
            Toast.makeText(
                this@AddOrganization,
                "Please Make Sure All Field is entered....",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addNewOrganization(name:String, email: String,password: String,phone: String, profileImageUrl:String) {
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
                Request.Method.POST, addNewOrganizationURL,
                com.android.volley.Response.Listener { response ->
                    try {
                        Log.d("ResponseData", "Server Response: $response")
                        val jsonResponse = JSONObject(response)
                        val success = jsonResponse.getInt("success")
                        val emailExists = jsonResponse.getInt("emailExists")
                        val phoneExists = jsonResponse.getInt("phoneExists")
                        val aId = jsonResponse.getInt("id")


                        if (success == 1) {
                            val currentDateTime = Date()
                            val dateFormat =
                                SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault())
                            val formattedDateTime = dateFormat.format(currentDateTime)

                            val admin = Admin(
                                aId = aId,
                                aName = name,
                                aPassword = md5(password),
                                aEmail = email,
                                aPhone = phone,
                                photo = profileImageUrl,
                                role = "organization",
                                joinedDate = formattedDateTime
                            )
                            CoroutineScope(Dispatchers.IO).launch {

                                appDb.adminDao().insert(admin)
                            }

                            isImageChanged = false
                            clearFields()
                            Toast.makeText(
                                this@AddOrganization,
                                "Organization added successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, AdminHome::class.java)
                            intent.putExtra("fragment", "user")
                            startActivity(intent)
                        } else {
                            emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                null,
                                null,
                                null,
                                null
                            )
                            phoneEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                null,
                                null,
                                null,
                                null
                            )

                            if (emailExists == 1) {
                                emailEditText.error = "Email Exists"
                                val errorIcon =
                                    ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                                emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    null,
                                    null,
                                    errorIcon,
                                    null
                                )
                            }
                            if (phoneExists == 1) {
                                phoneEditText.error = "Phone Exists"
                                val errorIcon =
                                    ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                                phoneEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    null,
                                    null,
                                    errorIcon,
                                    null
                                )
                            }
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
                    data["name"] = name
                    data["email"] = email
                    data["password"] = password
                    data["phone"] = phone
                    data["profileImageUrl"] = profileImageUrl
                    data["role"] = "organization"
                    return data
                }
            }
            val requestQueue = Volley.newRequestQueue(this@AddOrganization)
            requestQueue.add(stringRequestRemote)
        }
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

    private fun isValidPassword(password: String): Boolean {
        //Requirement: One Uppercase , One Lowercase , One Symbol , At Least 6 Characters
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$"
        return password.matches(passwordPattern.toRegex())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AddOrganization.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data


            val inputStream: InputStream? = selectedImageUri?.let { contentResolver.openInputStream(it) }

            if (inputStream != null) {

                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)


                imageUrl = BitmapConverter.convertBitmapToString(bitmap)


                Glide.with(this)
                    .load(selectedImageUri)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(userImageView)


                isImageChanged = true
            } else {

                val encodedImage = "/9j/4AAQSkZJRgABAQAAAQABAAD/4gIoSUNDX1BST0ZJTEUAAQEAAAIYAAAAAAQwAABtbnRyUkdC"
                val bitmap2 = BitmapConverter.convertStringToBitmap(encodedImage)

                Glide.with(this)
                    .load(bitmap2)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(userImageView)
                isImageChanged = false
            }
        }
    }






    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"

        // open image chooser
        startActivityForResult(intent, Companion.PICK_IMAGE_REQUEST)
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
        val requestQueue = Volley.newRequestQueue(this@AddOrganization)
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
        val requestQueue = Volley.newRequestQueue(this@AddOrganization)
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

    private fun clearFields(){
        passwordEditText.error = null
        passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        phoneEditText.error = null
        phoneEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        etName.error = null
        etName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        emailEditText.error = null
        emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}