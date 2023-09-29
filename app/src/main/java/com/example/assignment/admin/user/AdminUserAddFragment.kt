package com.example.assignment.admin.user

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.database.user.User
import com.example.assignment.database.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream


class AdminUserAddFragment : Fragment() {

    private lateinit var appDb : UserDatabase
    private lateinit var addName : EditText
    private lateinit var addRole : EditText
    private lateinit var addbtn : Button
    private lateinit var testbtn : Button
    private  lateinit var profile : ImageView
    private lateinit var bitmapString : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_user_add, container, false)

        appDb = UserDatabase.getDatabase(requireContext())
        addName = view.findViewById(R.id.addName)
        addRole = view.findViewById(R.id.addRole)
        addbtn = view.findViewById(R.id.addbtn)
        testbtn = view.findViewById(R.id.testupdate)
        profile = view.findViewById(R.id.profile_image)

        profile.setOnClickListener { selectImageFromGallery() }

        addbtn.setOnClickListener {
            addUser()
        }

        testbtn.setOnClickListener {

            updateUser()
        }

        return view
    }

    private fun addUser(){

        val name = addName.text.toString()
        val role = addRole.text.toString()
        val profile = bitmapString

        if(name.isNotEmpty() && role.isNotEmpty()) {
            val user = User(
                null, name, role, profile
            )
            GlobalScope.launch(Dispatchers.IO) {

                appDb.userDao().insert(user)
            }

            addName.text.clear()
            addRole.text.clear()

            Toast.makeText(requireContext(),"Successfully written", Toast.LENGTH_SHORT).show()
        }else Toast.makeText(requireContext(),"PLease Enter Data", Toast.LENGTH_SHORT).show()

    }

    fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    // Handle the result of image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            val inputStream: InputStream? =
                selectedImageUri?.let { requireContext().contentResolver.openInputStream(it) }
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            bitmapString = BitmapConverter.convertBitmapToString(bitmap)

            if (selectedImageUri != null) {
                profile.setImageURI(selectedImageUri)
            }
        }
    }


    private fun updateUser() {

//        val name = viewName.text.toString()
//        val role = viewRole.text.toString()
//        val profile = bitmapString

        val user = User(
            1, "test2", "Organization", "test2"
        )

        GlobalScope.launch(Dispatchers.IO) {
            appDb.userDao().update(user)
        }

    }


}