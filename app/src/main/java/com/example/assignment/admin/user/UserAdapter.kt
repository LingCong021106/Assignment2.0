package com.example.assignment.admin.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.database.user.User


class UserAdapter(var userList : List<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var imageString: String? = null

    inner class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        //val profile : ImageView = itemView.findViewById(R.id.profile)
        val username : TextView = itemView.findViewById(R.id.name)
        val role : TextView = itemView.findViewById(R.id.role)
        val profile : ImageView = itemView.findViewById(R.id.listProfile)


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
        holder.username.text = userList[position].userName
        holder.role.text = userList[position].role

        imageString = userList[position].profile
        val bitmap = BitmapConverter.convertStringToBitmap(imageString)

        holder.profile.setImageBitmap(bitmap)
    }


}