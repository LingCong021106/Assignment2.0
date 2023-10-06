package com.example.assignment.admin.news

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.admin.event.AdminEventFragment
import com.example.assignment.admin.event.AdminEventUpdateFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AdminNewsAdapter (var newsList: List<News>):RecyclerView.Adapter<AdminNewsAdapter.AdminNewsViewHolder>(){
    private var context: Context? = null
    private var imageString: String? = null
    lateinit var builder : AlertDialog.Builder

    inner class AdminNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val newsImage : ImageView = itemView.findViewById(R.id.admin_event_image) as ImageView
        val newsTitle: TextView = itemView.findViewById(R.id.admin_event_name) as TextView
        val editBtn: ImageButton = itemView.findViewById(R.id.admin_event_editBtn) as ImageButton
        val deleteBtn: ImageButton = itemView.findViewById(R.id.admin_event_deleteBtn) as ImageButton
        val newsCardView : CardView = itemView.findViewById(R.id.admin_event_card) as CardView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminNewsAdapter.AdminNewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_event_card,parent, false)
        val vh = AdminNewsViewHolder(view)
        context = parent.context
        builder = AlertDialog.Builder(context!!)

        return vh
    }

    override fun onBindViewHolder(holder: AdminNewsAdapter.AdminNewsViewHolder, position: Int) {
        val currentNews = newsList[position]
        imageString = currentNews.newsImage
        var bitmap = BitmapConverter.convertStringToBitmap(imageString)
        holder.newsImage.setImageBitmap(bitmap)
        holder.newsTitle.text = currentNews.newsTitle
        holder.newsCardView.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentNews.newsUrl))
            context?.startActivity(intent)
        }
        holder.editBtn.setOnClickListener{
            val bundle = Bundle()
            val fragment = AdminNewsUpdateFragment()
            bundle.putInt("newsId", currentNews.newsId)
            fragment.arguments = bundle
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        holder.deleteBtn.setOnClickListener{
            builder.setMessage("Are you sure want to delete this event?")
                .setCancelable(true)
                .setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss() // Dismiss the first dialog
                    deleteNews(currentNews.newsId)
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })
                .show()
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    private fun deleteNews(newsId:Int){

        val url : String = "http://10.0.2.2/Assignment(Mobile)/deleteNews.php"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("Register",response)
                if (response == "success") {
                    val fragmentManager = (context as FragmentActivity).supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, AdminNewsFragment())
                    transaction.commit()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    context,
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                data["newsId"] = newsId.toString()
                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }
}