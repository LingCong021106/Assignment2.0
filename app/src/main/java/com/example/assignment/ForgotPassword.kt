package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.util.Properties
import java.util.UUID

import java.util.*

lateinit var submitButton: Button
lateinit var etEmail: EditText
lateinit var etEmailLayout : TextInputLayout

private val check_emailURL:String="http://192.168.0.4/Assignment(Mobile)/check_email.php"

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_forget_password)

        submitButton = findViewById(R.id.submitButton)
        etEmail = findViewById(R.id.etEmail)
        etEmailLayout = findViewById(R.id.etEmailLayout)

        submitButton.setOnClickListener{
            validateEmail()
        }

    }

    fun validateEmail(){
        val email = etEmail.text.toString().trim()

        if(email.isNotEmpty()) {
            checkEmailExistence(email) { emailExists ->
                if (emailExists) {
                    runOnUiThread {
//                        val intent = Intent(this, ResetPasswordActivity::class.java)
//                        val userEmail = email
                        resetPassword(email)
//                        startActivity(intent)
//                        finish()

                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "The Email Address is not exists",
                        Toast.LENGTH_SHORT
                    ).show()

                    etEmailLayout.setError("Email Address is not exists")
                    etEmailLayout.setErrorIconDrawable(R.drawable.baseline_error_24)
                }
            }
        }else{
            Toast.makeText(
                applicationContext,
                "Email Address is Empty....",
                Toast.LENGTH_SHORT
            ).show()

        }
    }
    private fun sendResetEmail(email: String) {
        // email setting
        val properties = Properties()
        properties["mail.smtp.host"] = "smtp.gmail.com"
        properties["mail.smtp.port"] = "587"
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.starttls.enable"] = "true"

        val username = "jhtan-wm20@student.tarc.edu.my"
        val password = "020912100273"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, password)
                    }
                })

                val message = MimeMessage(session)
                message.setFrom(InternetAddress(username))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(email))
                message.subject = "Password Reset Request"


                // generate token
                val resetToken = generateResetToken()
                val resetLink = "https://azurefuture.com/reset_password?token=$resetToken"


                // content click(user)
                message.setText("Click the link below to reset your password:\n$resetLink")

                Transport.send(message)

                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Password reset email sent successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("EmailError", "Error sending reset email: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Error sending reset email: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }




fun resetPassword(email: String){
    val intent = Intent(this, ResetPasswordActivity::class.java)
    val userEmail = email
    intent.putExtra("userEmail", userEmail)
    startActivity(intent)
    finish()
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
        val requestQueue = Volley.newRequestQueue(this@ForgotPassword)
        requestQueue.add(stringRequestRemote)
    }

    fun login(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun generateResetToken(): String {
        return UUID.randomUUID().toString()
    }

}