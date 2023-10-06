package com.example.assignment.admin.news

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.BitmapConverter
import com.example.assignment.R
import com.example.assignment.admin.event.AdminEventFragment
import com.example.assignment.databinding.FragmentAdminNewsBinding
import com.example.assignment.databinding.FragmentAdminNewsUpdateBinding
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 * Use the [AdminNewsUpdateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminNewsUpdateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentAdminNewsUpdateBinding? = null
    private val binding get() = _binding!!
    private var newsImage: String = ""
    private var newsTitle : String = ""
    private var newsUrl : String = ""
    private var createDate : String = ""
    private lateinit var builder : AlertDialog.Builder
    private var news : News = News()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminNewsUpdateBinding.inflate(inflater, container, false)
        val view = binding.root

        newsTitle = binding.newTitle.toString()
        newsUrl = binding.newURL.toString()
        createDate = LocalDateTime.now().toString()
        builder = AlertDialog.Builder(requireContext())

        binding.addNewImage.setOnClickListener{ selectImageFromGallery() }
        binding.newBack.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, AdminNewsFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val data = arguments
        val newsId = data!!.getInt("newsId")
        news = findNews(newsId)!!

        if(news != null){
            newsImage = news.newsImage
            var bitmap = BitmapConverter.convertStringToBitmap(newsImage)
            binding.addNewImage.setImageBitmap(bitmap)

            binding.addNewsTitle.setText(news.newsTitle)
            binding.addnewURL.setText(news.newsUrl)

        }

        return view
    }

    fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            val inputStream: InputStream? =
                selectedImageUri?.let { requireContext().contentResolver.openInputStream(it) }
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            newsImage = BitmapConverter.convertBitmapToString(bitmap)

            if (selectedImageUri != null) {
                binding.addNewImage.setImageURI(selectedImageUri)
            }

            if(checkValidation()){
                builder.setMessage("Confirm to add news?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        storeNews()
                    })
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .show()
            }
        }
    }

    private fun checkValidation(): Boolean{newsTitle = binding.newTitle.toString()
        newsUrl = binding.addnewURL.text.toString()
        newsTitle = binding.addNewsTitle.text.toString()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        createDate = LocalDateTime.now().format(formatter)

        if(newsImage.isEmpty()||newsTitle.isEmpty()||newsUrl.isEmpty()){
            Toast.makeText(context, "Please fill in all information", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun storeNews(){
        val url : String = "http://10.0.2.2/Assignment(Mobile)/insertNews.php"
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("insertEvent",response)
                if (response == "success") {
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container,AdminEventFragment())
                    transaction.commit()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    requireContext(),
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val data: MutableMap<String, String> = HashMap()
                //database
                data["newsImage"] = newsImage
                data["newsTitle"] = newsTitle
                data["newsUrl"] = newsUrl
                data["createDate"] = createDate


                return data
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    private fun findNews(newsId:Int): News? {
        for(news in newsList){
            if(news.newsId == newsId){
                return news
            }
        }
        return null
    }
}