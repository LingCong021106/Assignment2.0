package com.example.assignment.user.donate

import android.icu.text.DecimalFormat
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.google.android.material.imageview.ShapeableImageView

class DonatePeopleAdapter (
    var donatePerson : List<DonatePerson>) :
    RecyclerView.Adapter<DonatePeopleAdapter.MyViewHolder>() {

    private var imageString: String? = null

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val peopleImg : ShapeableImageView = itemView.findViewById(R.id.people_donate_img)
        val comment : TextView = itemView.findViewById(R.id.people_donate_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_people_donate,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return donatePerson.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = donatePerson[position]
        var decimalFormat = DecimalFormat("#,##0.00")
        val userName = currentItem.userName
        val first = "$userName has donated "
        val middle = "RM" + decimalFormat.format(currentItem.userTotalDonate)
        val changeColor = "<font color = '#F30F0F'>$middle</font>"
        val last = " for this project!"
        holder.peopleImg.setImageResource(R.drawable.baseline_person_24)
        holder.comment.setText(Html.fromHtml(first + changeColor + last))
    }
}