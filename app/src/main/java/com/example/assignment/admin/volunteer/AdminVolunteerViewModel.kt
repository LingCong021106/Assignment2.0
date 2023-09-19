package com.example.assignment.admin.volunteer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminVolunteerViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Volunteer Fragment"
    }
    val text: LiveData<String> = _text
}