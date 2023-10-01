package com.example.assignment.admin.donate

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
import com.example.assignment.database.donate.Donate
import com.example.assignment.R
import com.example.assignment.database.donate.DonatePeople
import com.example.assignment.database.user.User
import com.example.assignment.database.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RecordAdapter(
    var donatePeople : List<DonatePeople>) :
    RecyclerView.Adapter<RecordAdapter.DonatePeopleViewHolder>() {


    private var id : Int? = 0
    private var name : String? = null
    private var role : String? = null
    private var imageString: String? = null


    inner class DonatePeopleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        //val profile : ImageView = itemView.findViewById(R.id.profile)
        val name : TextView = itemView.findViewById(R.id.namePeople)
        val amount : TextView = itemView.findViewById(R.id.amount)
        val date : TextView = itemView.findViewById(R.id.date)
        val peopleImage : ImageView = itemView.findViewById(R.id.peopleProfile)



    }

    fun setFilteredList(donatePeopleList: List<DonatePeople>){
        this.donatePeople = donatePeopleList
        notifyDataSetChanged()
    }

//    fun getBitMap(): Int {
//        val bitmap = BitmapConverter.convertStringToBitmap(imageString)
//        return userList.size
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonatePeopleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_list_donate_people , parent , false)
        return DonatePeopleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return donatePeople.size
    }

    override fun onBindViewHolder(holder: DonatePeopleViewHolder, position: Int) {
        //holder.profile.setImageResource(userList[position].profile)
        val donate = donatePeople[position]

        holder.name.text = donatePeople[position].userName
        holder.amount.text = donatePeople[position].amount.toString()
        holder.date.text = donatePeople[position].date
        val drawableResId = R.drawable.baseline_person_24
        holder.peopleImage.setImageResource(drawableResId)



    }





}