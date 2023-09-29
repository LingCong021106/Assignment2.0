package com.example.assignment.admin.user

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.database.user.User
import com.example.assignment.database.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserAdapter(
    var userList : List<User>,
    private val onActionClick: (type : String, user : User) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    private var id : Int? = 0
    private var name : String? = null
    private var role : String? = null
    private var imageString: String? = null

    private lateinit var appDb : UserDatabase

    inner class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        //val profile : ImageView = itemView.findViewById(R.id.profile)
        val username : TextView = itemView.findViewById(R.id.name)
        val role : TextView = itemView.findViewById(R.id.role)
        val profile : ImageView = itemView.findViewById(R.id.listProfile)
        val deletebtn : ImageView = itemView.findViewById(R.id.userDeleteBtn)
        val userCardView : ConstraintLayout =itemView.findViewById(R.id.userCardView)


    }

    fun setFilteredList(userList: List<User>){
        this.userList = userList
        notifyDataSetChanged()
    }

//    fun getBitMap(): Int {
//        val bitmap = BitmapConverter.convertStringToBitmap(imageString)
//        return userList.size
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_list_user , parent , false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        //holder.profile.setImageResource(userList[position].profile)
        val user = userList[position]

        holder.username.text = userList[position].userName
        holder.role.text = userList[position].role
        imageString = userList[position].profile
        val bitmap = BitmapConverter.convertStringToBitmap(imageString)
        holder.profile.setImageBitmap(bitmap)

        holder.deletebtn.setOnClickListener{
            onActionClick("delete", user)
        }

        holder.userCardView.setOnClickListener{
            onActionClick("view", user)
        }


        if(holder.role.text == "Admin"){
            holder.deletebtn.visibility = View.GONE
        }

    }


//    private fun deleteUser(user :User){
//
//        GlobalScope.launch(Dispatchers.IO) {
//            appDb.userDao().delete(user)
//        }
//    }


}