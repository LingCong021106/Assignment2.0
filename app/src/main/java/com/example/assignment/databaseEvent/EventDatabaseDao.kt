package com.example.assignment.databaseEvent

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.assignment.EventDetails

@Dao
interface EventDatabaseDao {

    @Insert
    suspend fun insert(event:EventDetails)

    @Update
    suspend fun update(event:EventDetails)

    @Query("SELECT * from event_table WHERE eventID = :key")
    suspend fun get(key: Long): EventDetails?

    @Query("DELETE FROM event_table")
    suspend fun clear()
}