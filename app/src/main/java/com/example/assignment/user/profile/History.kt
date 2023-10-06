package com.example.assignment.user.profile

var eventHistoryList = mutableListOf<History>()
var donateHistoryList = mutableListOf<History>()

data class History (
    var id : Int,
    var title : String,
    var image : String,
    var status : String,
    var createTime : String,
)
