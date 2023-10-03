package com.example.assignment.user.donate

import com.example.assignment.user.event.Event

var donateList = mutableListOf<Donate>()
var donatePersonList = mutableListOf<DonatePerson>()
var searchList = mutableListOf<Donate>()


data class Donate(
    var id: Int,
    var name:String,
    var image:String,
    var category:String,
    var organizationName:String,
    var startTime:String,
    var endTime:String,
    var totalDonation:Double,
    var description:String,
)

data class DonatePerson(
    var donateId:Int,
    var userEmail:String,
    var userName:String,
    var userImage:String,
    var userTotalDonate:Double
)