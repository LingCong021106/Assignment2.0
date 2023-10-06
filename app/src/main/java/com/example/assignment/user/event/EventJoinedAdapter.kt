package com.example.assignment.user.event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.assignment.BitmapConverter
import com.example.assignment.ListDonate
import com.example.assignment.R
import com.google.android.material.imageview.ShapeableImageView

class EventJoinedAdapter(var eventJoined : List<EventJoined>) :
    RecyclerView.Adapter<EventJoinedAdapter.MyViewHolder>() {

    private var imageString: String? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val peopleImg : ShapeableImageView = itemView.findViewById(R.id.people_donate_img)
        val peopleName : TextView = itemView.findViewById(R.id.people_donate_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_people_donate,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return eventJoined.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = eventJoined[position]
        val userName = currentItem.userName
        imageString = currentItem.userImage
        var bitmap = BitmapConverter.convertStringToBitmap(imageString)
        Glide.with(holder.itemView.context)
            .load(bitmap)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(holder.peopleImg)
        holder.peopleImg.setImageBitmap(bitmap)
        holder.peopleName.text = "$userName joined this event !"
    }
}