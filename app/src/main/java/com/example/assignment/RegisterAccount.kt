package com.example.assignment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import android.widget.Toast
import android.util.Log
import org.w3c.dom.Text

class RegisterAccount : AppCompatActivity() {
    lateinit var registerText:TextView
    lateinit var nameLayout: TextInputLayout
    lateinit var etEmailLayout: TextInputLayout
    lateinit var passwordLayout: TextInputLayout
    lateinit var conpasswordLayout: TextInputLayout
    lateinit var etname:EditText
    lateinit var etemail: EditText
    lateinit var etpassword:EditText
    lateinit var etconpassword:EditText
    lateinit var signupBtn:Button
    var name : String? = ""
    var email : String? = ""
    var password : String? = ""
    var conpassword : String? = ""
    //device ipv4 address
    private val URL:String="http://192.168.151.84/Assignment(Mobile)/register.php"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_register_account)

        nameLayout = findViewById(R.id.nameLayout)
        etEmailLayout = findViewById(R.id.etEmailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        conpasswordLayout = findViewById(R.id.conpasswordLayout)


        etname = findViewById(R.id.nameEditText)
        etemail = findViewById(R.id.etEmailEditText)
        etpassword = findViewById(R.id.passwordEditText)
        etconpassword = findViewById(R.id.conpasswordEditText)
        signupBtn=findViewById(R.id.signupBtn)

        nameLayout.helperText = null
        etEmailLayout.helperText = null
        passwordLayout.helperText = null
        conpasswordLayout.helperText = null


        signupBtn.setOnClickListener{
             validationInput()
        }


    }

    fun validationInput(){

    }
//    fun save(view: View?) {
//        name = etname.text.toString().trim { it <= ' ' }
//        email = etemail.text.toString().trim { it <= ' ' }
//        password = etpassword.text.toString().trim { it <= ' ' }
//        conpassword = etconpassword.text.toString().trim { it <= ' ' }
//
//        if (password != conpassword) {
//            Toast.makeText(this, "Password Mismatch", Toast.LENGTH_SHORT).show()
//        } else if (name != "" && email != "" && password != "") {
//            val stringRequest: StringRequest = object : StringRequest(
//                Request.Method.POST, URL,
//                Response.Listener { response ->
//                    tvStatus.text = response.toString()
//                    Log.d("Register",response)
//                    if (response == "success") {
//                        tvStatus.text = "Successfully registered."
//                        btnRegister.isClickable = false
//                    } else if (response == "failure") {
//                        tvStatus.text = "Something went wrong!"
//                    }
//                },
//                Response.ErrorListener { error ->
//                    Toast.makeText(
//                        applicationContext,
//                        error.toString().trim { it <= ' ' },
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }) {
//                @Throws(AuthFailureError::class)
//                override fun getParams(): Map<String, String>? {
//                    val data: MutableMap<String, String> = HashMap()
//                    data["name"] = name!!
//                    data["email"] = email!!
//                    data["password"] = password!!
//                    return data
//                }
//            }
//            val requestQueue = Volley.newRequestQueue(applicationContext)
//            requestQueue.add(stringRequest)
//        }
//    }

    fun login(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}