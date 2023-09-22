package com.example.assignment.admin.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminNewsViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is News Fragment"
    }
    val text: LiveData<String> = _text
}