package com.example.assignment.admin.donate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminDonateViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Donate Fragment"
    }
    val text: LiveData<String> = _text
}