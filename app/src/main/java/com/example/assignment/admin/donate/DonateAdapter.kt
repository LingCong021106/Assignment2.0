package com.example.assignment.admin.donate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.BitmapConverter
import com.example.assignment.database.donate.Donate
import com.example.assignment.R


class DonateAdapter(
    var donateList : List<Donate>,
    private val onActionClick: (type : String, donate : Donate) -> Unit) :
    RecyclerView.Adapter<DonateAdapter.DonateViewHolder>() {


    private var id : Int? = 0
    private var name : String? = null
    private var role : String? = null
    private var imageString: String? = null


    inner class DonateViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        //val profile : ImageView = itemView.findViewById(R.id.profile)
        val title : TextView = itemView.findViewById(R.id.donatetitle)
        val organization : TextView = itemView.findViewById(R.id.organization)
        val donateImage : ImageView = itemView.findViewById(R.id.donateImage)
        val deletebtn : ImageView = itemView.findViewById(R.id.donateDeleteBtn)
        val donateCardView : ConstraintLayout =itemView.findViewById(R.id.donateCardView)


    }

    fun setFilteredList(donateList: List<Donate>){
        this.donateList = donateList
        notifyDataSetChanged()
    }

//    fun getBitMap(): Int {
//        val bitmap = BitmapConverter.convertStringToBitmap(imageString)
//        return userList.size
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_list_donate , parent , false)
        return DonateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return donateList.size
    }

    override fun onBindViewHolder(holder: DonateViewHolder, position: Int) {
        //holder.profile.setImageResource(userList[position].profile)
        val donate = donateList[position]

        holder.title.text = donateList[position].title
        holder.organization.text = donateList[position].organization
        imageString = donateList[position].donateImage
        val bitmap = BitmapConverter.convertStringToBitmap(imageString)
        holder.donateImage.setImageBitmap(bitmap)

        holder.deletebtn.setOnClickListener{
            onActionClick("delete", donate)
        }

        holder.donateCardView.setOnClickListener{
            onActionClick("view", donate)
        }



//        if(holder.role.text == "Admin"){
//            holder.deletebtn.visibility = View.GONE
//        }

    }


//    private fun deleteUser(user :User){
//
//        GlobalScope.launch(Dispatchers.IO) {
//            appDb.userDao().delete(user)
//        }
//    }


}