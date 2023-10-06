package com.example.assignment.user.home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.admin.news.AdminNewsAdapter
import com.example.assignment.admin.news.News
import com.example.assignment.admin.news.newsList
import com.example.assignment.databinding.FragmentUserEventBinding
import com.example.assignment.databinding.FragmentUserHomeBinding
import com.example.assignment.user.event.EventAdapter
import com.example.assignment.user.event.eventList
import com.example.assignment.admin.news.searchList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var viewpager2: ViewPager2
    lateinit var recyclerViewNew : RecyclerView
    private lateinit var adapter: UserNewsAdapter
    private lateinit var mToolbar : Toolbar

    // Implementing auto slide facility
    private val slideHandler= Handler()
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
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        val view : View = binding.root

        viewpager2 = view.findViewById(R.id.viewPager)
        recyclerViewNew = view.findViewById(R.id.newsRecycleView)

        val slideItems = ArrayList<SlideItem>()
        slideItems.add(SlideItem(R.drawable.slide1))
        slideItems.add(SlideItem(R.drawable.slide2))
        slideItems.add(SlideItem(R.drawable.slide3))
        slideItems.add(SlideItem(R.drawable.slide4))
        slideItems.add(SlideItem(R.drawable.slide5))
        slideItems.add(SlideItem(R.drawable.slide6))

        val slideAdapter = SlideAdapter(
            slideItems,
            viewpager2
        )
        viewpager2.adapter = slideAdapter

        viewpager2.clipToPadding = false
        viewpager2.clipChildren = false
        viewpager2.offscreenPageLimit = 5
        viewpager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()

        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        viewpager2.setPageTransformer(compositePageTransformer)

        viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                slideHandler.removeCallbacks(slideerRunnable)
                slideHandler.postDelayed(slideerRunnable, 3000)
            }
        })

        newsList.clear()
        recyclerViewNew = binding.newsRecycleView
        recyclerViewNew.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewNew.setHasFixedSize(true)
        newsGetAll()

        //toolbar
        setHasOptionsMenu(true)
        mToolbar = requireActivity().findViewById(R.id.toolbar)
        if (mToolbar != null) {
            (activity as AppCompatActivity?)?.setSupportActionBar(mToolbar)
        }
        mToolbar.title = "Azure Future"
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(com.example.assignment.R.menu.top_nav_menu,menu)
        val searchItem = menu.findItem(R.id.search)
        if(searchItem != null){
            val searchView = searchItem.actionView as SearchView
            searchView.queryHint = "Type here to search..."
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                        adapter = UserNewsAdapter(searchList)
                        binding.newsRecycleView.adapter  = adapter
                        recyclerViewNew.adapter!!.notifyDataSetChanged()
                    }
                    else{
                        searchList.clear()
                        searchList.addAll(newsList)
                        adapter = UserNewsAdapter(searchList)
                        binding.newsRecycleView.adapter  = adapter
                        recyclerViewNew.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }

    private val slideerRunnable = Runnable {
        viewpager2.currentItem = viewpager2.currentItem+1
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
                        }
                        adapter = UserNewsAdapter(newsList)
                        binding.newsRecycleView.adapter  = adapter
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