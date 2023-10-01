package com.example.assignment.databaseEvent

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventDatabaseDao {

    @Query("SELECT * FROM event_table")
    fun getAll(): MutableList<EventData>
    @Query("SELECT * FROM event_table WHERE eventId LIKE :eventId")
    fun getEvent(eventId: Int): List<EventData>


    @Insert
    fun insert(event:EventData)

    @Delete
    fun delete(event:EventData)

    @Query("DELETE FROM event_table")
    fun clear()
}