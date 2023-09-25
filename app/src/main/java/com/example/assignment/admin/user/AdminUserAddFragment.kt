package com.example.assignment.admin.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.assignment.R
import com.example.assignment.database.user.User
import com.example.assignment.database.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AdminUserAddFragment : Fragment() {

    private lateinit var appDb : UserDatabase
    private lateinit var addName : EditText
    private lateinit var addRole : EditText
    private lateinit var addbtn : Button

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

        addbtn.setOnClickListener {
            addUser()
        }

        return view
    }

    private fun addUser(){

        val name = addName.text.toString()
        val role = addRole.text.toString()

        if(name.isNotEmpty() && role.isNotEmpty()) {
            val user = User(
                null, name, role
            )
            GlobalScope.launch(Dispatchers.IO) {

                appDb.studentDao().insert(user)
            }

            addName.text.clear()
            addRole.text.clear()

            Toast.makeText(requireContext(),"Successfully written", Toast.LENGTH_SHORT).show()
        }else Toast.makeText(requireContext(),"PLease Enter Data", Toast.LENGTH_SHORT).show()

    }


}