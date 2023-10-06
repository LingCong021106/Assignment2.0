package com.example.assignment.user.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.admin.news.News
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


class UserNewsAdapter (var newsList: List<News>) : RecyclerView.Adapter<UserNewsAdapter.UserNewsViewHolder>() {
    private var context: Context? = null
    private var imageString: String? = null

    inner class UserNewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val newsCardView : CardView = itemView.findViewById(R.id.card_view)
        val newsTitle : TextView = itemView.findViewById(R.id.newsTitle)
        val newsImage : ImageView = itemView.findViewById(R.id.newsImage)
        val newsTime : TextView = itemView.findViewById(R.id.newsTime)
        val shareBtn : ImageButton = itemView.findViewById(R.id.shareBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserNewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_home_news_card,parent, false)
        val vh = UserNewsViewHolder(view)
        context = parent.context
        return vh
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: UserNewsViewHolder, position: Int) {
        val currentNews = newsList[position]
        imageString = currentNews.newsImage
        var bitmap = BitmapConverter.convertStringToBitmap(imageString)
        holder.newsImage.setImageBitmap(bitmap)
        holder.newsTitle.text = currentNews.newsTitle

        //calculate date passed
        val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val newsDateTime = dateTimeFormat.parse(currentNews.createDate)
        val currentDateTime = Date()
        val differenceInMillis = currentDateTime.time - newsDateTime.time
        val days = TimeUnit.MILLISECONDS.toDays(differenceInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(differenceInMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis) % 60

        when {
            days > 0 -> holder.newsTime.text = "$days days ago"
            hours > 0 -> holder.newsTime.text = "$hours hours ago"
            else -> holder.newsTime.text = "$minutes minutes ago"
        }

        holder.newsCardView.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentNews.newsUrl))
            context?.startActivity(intent)
        }

        holder.shareBtn.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, currentNews.newsUrl)

            context?.startActivity(Intent.createChooser(intent, "Share via"))
        }
    }
}