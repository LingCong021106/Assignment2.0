package com.example.assignment.user.event

import java.util.Date

var eventList = mutableListOf<Event>()

data class Event(
    var id:Int,
    var name:String,
    var description:String,
    var category:String,
    var image:Int,
    var registrationEndDate:String,
    var orgatnizationName:String,
    var contactNumber:String,
    var contactPerson:String,
    var maxPerson:Int,
    var eventDate:String,
    var location:String,
)