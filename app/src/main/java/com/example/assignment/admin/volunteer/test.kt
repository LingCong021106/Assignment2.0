package com.example.assignment.admin.volunteer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.assignment.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [test.newInstance] factory method to
 * create an instance of this fragment.
 */
class test : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_test, container, false)

        val receivedValue = arguments?.getString("test")
        val userId = arguments?.getString("test")?.toIntOrNull() ?: 0




        var test : TextView = view.findViewById(R.id.testvalue)

        test.text = userId.toString()

        return view
    }


}