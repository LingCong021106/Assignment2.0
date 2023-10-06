package com.example.assignment.admin.news

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.admin.event.AdminEventAdapter
import com.example.assignment.databinding.FragmentAdminNewsBinding
import java.util.Locale
import com.example.assignment.admin.news.*
import com.example.assignment.user.event.Event
import com.example.assignment.user.event.eventJoinedList
import com.example.assignment.user.event.eventList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class AdminNewsFragment : Fragment() {

    private var _binding: FragmentAdminNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: AdminNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //change toolbar title
        (activity as AppCompatActivity).supportActionBar?.title = "News"

        binding.moveAddEventFragment.setOnClickListener{
            loadFragment(AdminNewAddFragment())
        }

        searchBySearchView(binding.adminEventSearch)

        newsList.clear()
        recyclerView = binding.adminNewsRecycleView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        newsGetAll()
        return root
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun searchBySearchView(searchView: android.widget.SearchView){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if(newText!!.isNotEmpty()){
                    searchList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    newsList.forEach {
                        if(it.newsTitle.lowercase(Locale.getDefault()).contains(search)){
                            searchList.add(it)
                        }
                    }
                    adapter = AdminNewsAdapter(searchList)
                    binding.adminNewsRecycleView.adapter  = adapter
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                else{
                    searchList.clear()
                    searchList.addAll(newsList)
                    adapter = AdminNewsAdapter(searchList)
                    binding.adminNewsRecycleView.adapter  = adapter
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })
    }

    private fun newsGetAll(){
        val url = "http://10.0.2.2/Assignment(Mobile)/newsGetAll.php"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("News", response)
                try {
                    if (response != null) {
                        val array = JSONArray(response)

                        for (i in 0 until array.length()) {
                            val news = array.getJSONObject(i)
                            val data = News(
                                news.getInt("newsId"),
                                news.getString("newsImage"),
                                news.getString("newsTitle"),
                                news.getString("newsUrl"),
                                news.getString("createDate"),
                            )
                            newsList.add(data)

                            CoroutineScope(Dispatchers.IO).launch{
//                                eventDB.eventDatabaseDao().insertEvent(data)
                            }
                        }
                        adapter = AdminNewsAdapter(newsList)
                        binding.adminNewsRecycleView.adapter  = adapter

                        binding.eventBar.visibility = View.GONE
                        binding.loadingText.visibility = View.GONE

                    }
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    activity,
                    "Error Connection, Please Try Later",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
        }
        val requestQueue = Volley.newRequestQueue(context?.applicationContext)
        requestQueue.add(stringRequest)
    }
}