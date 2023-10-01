package com.example.assignment.databaseEvent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")
data class EventData(
    @PrimaryKey()
    var eventId: Int? = 0,

    @ColumnInfo(name = "eventName")
    var eventName: String? = "",

    @ColumnInfo(name = "eventCategory")
    var eventCategory: String? = "",

    @ColumnInfo(name = "eventDescription")
    var eventDescription: String? = "",

    @ColumnInfo(name = "eventImage")
    var eventImage: Long? = 0,

    @ColumnInfo(name = "eventRegEndDate")
    var eventRegEndDate: String? = "",

    @ColumnInfo(name = "eventOrgName")
    var eventOrgName: String? = "",

    @ColumnInfo(name = "eventContactNumber")
    var eventContactNumber: String? = "",

    @ColumnInfo(name = "eventContactPerson")
    var eventContactPerson: String? = "",

    @ColumnInfo(name = "eventMaxPerson")
    var eventMaxPerson: Int? = 0,

    @ColumnInfo(name = "eventDate")
    var eventDate : String? = "",

    @ColumnInfo(name = "eventLocation")
    var eventLocation : String? = ""
)
