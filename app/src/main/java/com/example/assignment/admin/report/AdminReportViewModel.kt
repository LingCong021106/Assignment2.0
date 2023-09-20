package com.example.assignment.admin.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminReportViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Report Fragment"
    }
    val text: LiveData<String> = _text
}