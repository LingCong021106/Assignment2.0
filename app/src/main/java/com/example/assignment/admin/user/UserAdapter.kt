package com.example.assignment.admin.user

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.database.User
import com.google.android.material.imageview.ShapeableImageView
import javax.sql.DataSource

class UserAdapter
    (
    private val peopleList: ArrayList<ListUsers>,
    private val role: String,
    private val onActionClick: (type: String, user: ListUsers) -> Unit


) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    // Existing code for onCreateViewHolder and getItemCount

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = peopleList[position]

        holder.nameTextView?.text = currentItem.name
        holder.emailTextView?.text = currentItem.email
        var imageString = currentItem.photo
        var bitmap = BitmapConverter.convertStringToBitmap(imageString)
        val encodedImage = "/9j/4AAQSkZJRgABAQAAAQABAAD/4gIoSUNDX1BST0ZJTEUAAQEAAAIYAAAAAAQwAABtbnRyUkdC"
        val bitmap2 = BitmapConverter.convertStringToBitmap(encodedImage)

        holder.imageView?.let {
            Glide.with(it.context)
                .load(bitmap ?: bitmap2)
                .placeholder(R.drawable.baseline_person_24)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(holder.imageView)
        }

//        holder.imageView?.setImageBitmap(bitmap ?: bitmap2)


        if (role == "admin") {
            holder.deleteBtn?.visibility = View.GONE
        } else {
            holder.deleteBtn?.visibility = View.VISIBLE
        }

        holder.deleteBtn?.setOnClickListener{
            onActionClick("delete", currentItem)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.admin_list_user,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return peopleList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailTextView: TextView? = itemView.findViewById(R.id.email)
        val nameTextView: TextView? = itemView.findViewById(R.id.name)
        val imageView: ImageView? = itemView.findViewById(R.id.listProfile)
        val deleteBtn: ImageButton? = itemView.findViewById(R.id.userDeleteBtn)
    }

}


