package com.example.assignment.user.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.databinding.FragmentUserEventBinding
import org.json.JSONArray
import org.json.JSONException
import java.time.LocalDate


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentUserEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: EventAdapter
    private val URL : String = "http://10.0.2.2/Assignment(Mobile)/eventGetAll.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
//        eventGetAll()

//        recyclerView = binding.eventRecycleView
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.setHasFixedSize(true)
//        eventGetAll()
//        adapter = EventAdapter(eventList)
//        binding.eventRecycleView.adapter  = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserEventBinding.inflate(inflater, container, false)
        val rootView : View = binding.root

        recyclerView = binding.eventRecycleView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        eventGetAll()
//        adapter = EventAdapter(eventList)
//        binding.eventRecycleView.adapter  = adapter

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EventFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun eventGetAll(){
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, URL,
            Response.Listener { response ->
                Log.d("Event", response)
                try {
                    if (response != null) {
                        val array = JSONArray(response)

                        for (i in 0 until array.length()) {
                            val event = array.getJSONObject(i)
                            eventList.add(
                                Event(
                                    event.getInt("eventId"),
                                    event.getString("eventName"),
                                    event.getString("eventCategory"),
                                    event.getString("eventDescription"),
                                    event.getInt("eventImage"),
                                    event.getString("eventRegEndTime"),
                                    event.getString("eventOrgName"),
                                    event.getString("eventContactNumber"),
                                    event.getString("eventContactPerson"),
                                    event.getInt("eventMaxPerson"),
                                    event.getString("eventDate"),
                                    event.getString("eventLocation"),
                                )
                            )

                        }
                        adapter = EventAdapter(eventList)
                        binding.eventRecycleView.adapter  = adapter
                    }
                }catch (e:JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    getActivity(),
                    error.toString().trim { it <= ' ' },
                    Toast.LENGTH_SHORT
                ).show()
            }) {
        }
        val requestQueue = Volley.newRequestQueue(getActivity()?.applicationContext)
        requestQueue.add(stringRequest)
    }


}