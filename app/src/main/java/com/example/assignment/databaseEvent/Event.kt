package com.example.assignment.databaseEvent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "event_table")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val eventId: Int?,

    @ColumnInfo(name = "eventName")
    val eventName: String?,

    @ColumnInfo(name = "eventDescription")
    val eventDescription: String?,

//    @ColumnInfo(name = "eventRegEndDate")
//    val eventRegEndDate: Date?,

    @ColumnInfo(name = "eventOrgName")
    val eventOrgName: String?,

    @ColumnInfo(name = "eventContactNumber")
    val eventContactNumber: String?,

    @ColumnInfo(name = "eventContactPerson")
    val eventContactPerson: String?,

    @ColumnInfo(name = "eventMaxPerson")
    val eventMaxPerson: Int?,

//    @ColumnInfo(name = "eventDate")
//    val eventDate : Date?,

    @ColumnInfo(name = "eventLocation")
    val eventLocation : String?
)

