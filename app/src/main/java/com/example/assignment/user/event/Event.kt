package com.example.assignment.user.event

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

var eventList = mutableListOf<Event>()
var eventJoinedList = mutableListOf<EventJoined>()
var searchList = mutableListOf<Event>()
@Entity(tableName = "event_table")
data class Event(
    @PrimaryKey()
    var eventId: Int,

    @ColumnInfo(name = "eventName")
    var eventName:String,

    @ColumnInfo(name = "eventCategory")
    var eventCategory:String,

    @ColumnInfo(name = "eventImage")
    var eventImage:String,

    @ColumnInfo(name = "eventDescription")
    var eventDescription:String,

    @ColumnInfo(name = "eventRegEndDate")
    var eventRegEndDate:String,

    @ColumnInfo(name = "eventOrgName")
    var eventOrgName:String,

    @ColumnInfo(name = "eventContactNumber")
    var eventContactNumber:String,

    @ColumnInfo(name = "eventContactPerson")
    var eventContactPerson:String,

    @ColumnInfo(name = "eventMaxPerson")
    var eventMaxPerson:Int,

    @ColumnInfo(name = "eventDate")
    var eventDate:String,

    @ColumnInfo(name = "eventLocation")
    var eventLocation:String,
)

@Entity(tableName = "event_joined_table")
data class EventJoined(
    @PrimaryKey()
    var eventJoinedId: Int,

    @ColumnInfo(name = "eventId")
    var eventId: Int,

    @ColumnInfo(name = "userEmail")
    var userEmail: String,

    @ColumnInfo(name = "userImage")
    var userImage: String,

    @ColumnInfo(name = "userName")
    var userName: String,
)

