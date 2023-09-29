package com.example.assignment.databaseEvent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "eventName") val eventName: String?,
    @ColumnInfo(name = "eventPeople") val eventPeople: Int?

)
