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
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.database.user.User
import com.example.assignment.database.user.UserDatabase
import com.example.assignment.user.donate.DonateFragment
import com.example.assignment.user.event.EventFragment
import com.example.assignment.user.home.UserHomeFragment
import com.example.assignment.user.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream

class AdminUserDetailFragment: Fragment(){

    private lateinit var appDb : UserDatabase
    private lateinit var viewProfileImage : ImageView
    private lateinit var viewName : EditText
    private lateinit var viewRole : EditText
    private lateinit var updateBtn : Button
    var userid : Int = 0
    var imageString : String? = ""
    private lateinit var bitmapString : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_user_detail, container, false)

        appDb = UserDatabase.getDatabase(requireContext())
        viewProfileImage = view.findViewById(R.id.view_profile_image)
        viewName = view.findViewById(R.id.viewName)
        viewRole = view.findViewById(R.id.viewRole)
        updateBtn = view.findViewById(R.id.updatebtn)


        val id = arguments?.getString("id")
        if (id != null) {
            userid = id.toInt()
        }
        viewName.setText(arguments?.getString("name"))
        viewRole.setText(userid.toString())
        imageString = arguments?.getString("profile")

        val bitmap = BitmapConverter.convertStringToBitmap(imageString)
        viewProfileImage.setImageBitmap(bitmap)

        viewProfileImage.setOnClickListener { selectImageFromGallery() }

        updateBtn.setOnClickListener { updateUser() }

        return view
    }




    private fun updateUser(){

        val name = viewName.text.toString()
        val role = viewRole.text.toString()
        val profile = imageString

        if(name.isNotEmpty() && role.isNotEmpty()) {
            val user = User(
                userid, name, role, profile
            )
            GlobalScope.launch(Dispatchers.IO) {
                appDb.userDao().update(user)
            }

            Toast.makeText(requireContext(),"Success", Toast.LENGTH_SHORT).show()

        }else Toast.makeText(requireContext(),"PLease Enter Data", Toast.LENGTH_SHORT).show()

    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            val inputStream: InputStream? =
                selectedImageUri?.let { requireContext().contentResolver.openInputStream(it) }
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            imageString = BitmapConverter.convertBitmapToString(bitmap)

            if (selectedImageUri != null) {
                viewProfileImage.setImageURI(selectedImageUri)
            }
        }
    }


}