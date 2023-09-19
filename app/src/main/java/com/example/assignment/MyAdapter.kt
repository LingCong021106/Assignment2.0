package com.example.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class MyAdapter(private val peopleList : ArrayList<ListDonate>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_people_donate,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return peopleList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = peopleList[position]
        holder.peopleImg.setImageResource(currentItem.img)
        holder.peopleName.text = currentItem.name
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val peopleImg : ShapeableImageView = itemView.findViewById(R.id.people_donate_img)
        val peopleName : TextView = itemView.findViewById(R.id.people_donate_name)


    }
}