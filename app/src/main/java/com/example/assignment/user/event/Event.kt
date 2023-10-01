package com.example.assignment.user.event

var eventList = mutableListOf<Event>()
var eventJoinedList = mutableListOf<EventJoined>()
var searchList = mutableListOf<Event>()
data class Event(
    var id: Int,
    var name:String,
    var category:String,
    var description:String,
    var image:String,
    var registrationEndDate:String,
    var orgatnizationName:String,
    var contactNumber:String,
    var contactPerson:String,
    var maxPerson:Int,
    var eventDate:String,
    var location:String,
)

data class EventJoined(
    var eventId: Int,
    var userEmail: String,
    var userImage: String,
    var userName: String,
)

