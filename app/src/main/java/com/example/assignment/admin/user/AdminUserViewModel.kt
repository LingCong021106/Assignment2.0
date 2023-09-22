package com.example.assignment.admin.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminUserViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is User Fragment"
    }
    val text: LiveData<String> = _text
}