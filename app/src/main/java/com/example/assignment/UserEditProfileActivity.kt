package com.example.assignment

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.database.AppDatabase
import com.example.assignment.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.assignment.database.User
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import com.example.assignment.admin.event.AdminEventFragment
import com.example.assignment.databinding.DonateDetailsBinding
import com.example.assignment.databinding.FragmentAdminEventBinding
import com.example.assignment.databinding.UserEditProfileBinding
import com.example.assignment.databinding.UserProfileBinding
import com.example.assignment.user.UserHome
import java.io.InputStream

class UserEditProfileActivity : AppCompatActivity() {
    lateinit var nameText: TextView
    private lateinit var binding: UserEditProfileBinding
    private lateinit var appDb: AppDatabase
    private lateinit var etName : EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneeditText: EditText
    private lateinit var saveButton : Button
    private lateinit var userImageView: ImageView
    private var imageUrl: String? = null
    private var isImageChanged = false
    private var originalProfileImageUrl: String? = null
    private val check_emailURL:String="http://10.0.2.2/Assignment(Mobile)/check_email.php"
    private val check_phoneURL:String="http://10.0.2.2/Assignment(Mobile)/check_phone.php"
    private val updateProfileURL:String="http://10.0.2.2/Assignment(Mobile)/update_profile.php"

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_edit_profile)
        binding = UserEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
//testing
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userRole = sharedPreferences.getString("userRole", "users")
        val userEmail = sharedPreferences.getString("userEmail", "")
        val userId = sharedPreferences.getInt("userId", 0)

        Log.d("MyApp", "isLoggedIn: $isLoggedIn")
        Log.d("MyApp", "userId: $userId")
        Log.d("MyApp", "userEmail: $userEmail")

        appDb = AppDatabase.getInstance(this)
        userImageView = findViewById(R.id.userImageView)

        if (isLoggedIn) {
            CoroutineScope(Dispatchers.IO).launch {
                val user = userId?.let { appDb.userDao().getUserById(it) }

                withContext(Dispatchers.Main) {
                    if (user != null) {
                        val username = user.userName
                        etName = findViewById(R.id.etName)
                        emailEditText = findViewById(R.id.emailEditText)
                        phoneeditText = findViewById(R.id.phoneEditText)


                        etName.hint = user.userName
                        emailEditText.hint = user.userEmail
                        phoneeditText.hint = user.phone

                        etName.setText(user.userName)
                        emailEditText.setText(user.userEmail)
                        phoneeditText.setText(user.phone)
                        setUserData(user)
                        originalProfileImageUrl = user.photo

                        var bitmap = BitmapConverter.convertStringToBitmap(originalProfileImageUrl)

                        Glide.with(this@UserEditProfileActivity)
                            .load(bitmap)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(userImageView)

                    }else{
                        Toast.makeText(
                            this@UserEditProfileActivity,
                            "This User not Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
                saveButton = findViewById(R.id.saveButton)
                saveButton.setOnClickListener {
                    // Show a confirmation dialog
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Confirmation")
                    builder.setMessage("Are you sure you want to update your profile?")

                    // Positive button (OK)
                    builder.setPositiveButton("Update") { dialog, which ->
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

            // open image choose
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.upBtn.setOnClickListener{
            onBackPressed()
        }
    }

    override fun onBackPressed() {

            super.onBackPressed()
        finish()
    }
    fun checkValidation() {
        val newName = etName.text.toString().trim()
        val newEmail = emailEditText.text.toString().trim()
        val newPhone = phoneeditText.text.toString().trim()

        val originalName = etName.hint.toString()
        val originalEmail = emailEditText.hint.toString()
        val originalPhone = phoneeditText.hint.toString()

        val fieldsChanged = (newName != originalName || newEmail != originalEmail || newPhone != originalPhone || isImageChanged)


        if (fieldsChanged) {
            var hasError = false

            if (newName != originalName) {
                // check empty name
                if (newName.isEmpty()) {
                    Toast.makeText(this, "Name Empty", Toast.LENGTH_SHORT).show()
                    hasError = true
                    etName.error = "Name Cannot Be Empty."
                    val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                    etName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                } else {
                    // No error
                }
            }

            if (newEmail != originalEmail) {
                // check email empty
                if (newEmail.isEmpty()) {
                    runOnUiThread {
                        hasError = true
                        emailEditText.error = "Email Cannot Be Empty."
                        val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                        emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                    }
                } else {


                    // Check Email Format
                    if (!isValidEmail(newEmail)) {
                        runOnUiThread {
                            hasError = true
                            emailEditText.error = "Invalid Email"
                            val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                            emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                        }
                    } else {
                        // Check Exist Email if originalEmail != newEmail
                        if (originalEmail != newEmail) {
                            checkEmailExistence(newEmail) { emailExists ->
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
                }
            }


            if (newPhone != originalPhone) {
                // check phone empty
                if (newPhone.isEmpty()) {
                    hasError = true
                    phoneeditText.error = "Phone Cannot Be Empty."
                    val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                    phoneeditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                } else {
                    // Check Phone Format
                    if (!isValidPhone(newPhone)) {
                        phoneeditText.error = "Invalid Phone(Eg:01x-xxxxxxx)"
                        val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                        phoneeditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                        hasError = true
                    } else {
                        // Check Exist Phone if originalPhone != newPhone
                        if (originalPhone != newPhone) {
                            checkPhoneExistence(newPhone) { phoneExists ->
                                if (phoneExists) {
                                    runOnUiThread {
                                        phoneeditText.error = "Phone Exists"
                                        val errorIcon = ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                                        phoneeditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, errorIcon, null)
                                    }
                                    hasError = true
                                } else {
                                    // No error
                                }
                            }
                        }
                    }
                }
            }

            if (!hasError) {
                // update local db
                updateProfile(
                    originalName,
                    newName,
                    originalEmail,
                    newEmail,
                    originalPhone,
                    newPhone,
                    originalProfileImageUrl ?: "",
                    imageUrl ?: ""
                )
            } else {
                Toast.makeText(this, "No Change", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserHome::class.java)
                intent.putExtra("fragment", "profile")
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "No Change", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, UserHome::class.java)
            intent.putExtra("fragment", "profile")
            startActivity(intent)
        }
    }



    private fun updateProfile(originalName:String,newName: String, originalEmail: String, newEmail: String,originalPhone:String, newPhone: String, originalProfileImageUrl:String, profileImageUrl: String) {
        var imageStore = ""
        if(profileImageUrl == null|| profileImageUrl == ""){
            imageStore = originalProfileImageUrl
        }
        else{
            imageStore = profileImageUrl
        }

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
            if (newName == originalName && newEmail == originalEmail && newPhone == originalPhone && profileImageUrl == originalProfileImageUrl) {
                // no change
                Toast.makeText(
                    this@UserEditProfileActivity,
                    "No changes to update",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            val stringRequestRemote = object : StringRequest(
                Request.Method.POST, updateProfileURL,
                com.android.volley.Response.Listener { response ->
                    try {
                        Log.d("ResponseData", "Server Response: $response")
                        val jsonResponse = JSONObject(response)
                        val success = jsonResponse.getInt("success")
                        val emailExists = jsonResponse.getInt("emailExists")
                        val phoneExists = jsonResponse.getInt("phoneExists")


                        if (success == 1) {

                            CoroutineScope(Dispatchers.IO).launch {
                                val user = appDb.userDao().getUserByEmail(originalEmail)

                                val userId = user!!.userId
                                val sharedPreferences =
                                    getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("userRole", "users")
                                editor.putString("userEmail", newEmail)
                                editor.putInt("userId", userId)
                                editor.apply()

                                appDb.userDao().updateUserInfo(
                                    originalEmail,
                                    newName,
                                    newEmail,
                                    newPhone,
                                    imageStore
                                )
                            }

                            //set hint for update
                            etName.hint = newName
                            emailEditText.hint = newEmail
                            phoneeditText.hint = newPhone
                            isImageChanged = false
                            clearFields()
                            Toast.makeText(
                                this@UserEditProfileActivity,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, UserHome::class.java)
                            intent.putExtra("fragment", "profile")
                            startActivity(intent)
                        } else {
                            emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                null,
                                null,
                                null,
                                null
                            )
                            phoneeditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                null,
                                null,
                                null,
                                null
                            )

                            if (emailExists == 1) {
                                emailEditText.error = "Email Exists."
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
                                phoneeditText.error = "Phone Exists."
                                val errorIcon =
                                    ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
                                phoneeditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
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
                    data["originalEmail"] = originalEmail
                    data["originalPhone"] = originalPhone
                    data["newName"] = newName
                    data["newEmail"] = newEmail
                    data["newPhone"] = newPhone
                    data["profileImageUrl"] = imageStore
                    return data
                }
            }
            val requestQueue = Volley.newRequestQueue(this@UserEditProfileActivity)
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
        val requestQueue = Volley.newRequestQueue(this@UserEditProfileActivity)
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
        val requestQueue = Volley.newRequestQueue(this@UserEditProfileActivity)
        requestQueue.add(stringRequestRemote)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
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
                isImageChanged = false
            }
        }
    }


    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"

        // 启动图像选择器
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun clearFields(){
        phoneeditText.error = null
        phoneeditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        etName.error = null
        etName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        emailEditText.error = null
        emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)


    }

    private fun setUserData(user: User) {
        etName.hint = user.userName
        emailEditText.hint = user.userEmail
        phoneeditText.hint = user.phone
    }


    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun ChangePassword(view: View?) {
        val intent = Intent(this, ProfileChangePassword::class.java)
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "")
        intent.putExtra("userEmail", userEmail)
        intent.putExtra("callingActivityName", "UserEditProfileActivity")
        startActivity(intent)
        finish()
    }
}
