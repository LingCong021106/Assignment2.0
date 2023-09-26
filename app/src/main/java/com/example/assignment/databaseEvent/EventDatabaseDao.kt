package com.example.assignment.databaseEvent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.assignment.EventDetail

@Dao
interface EventDatabaseDao {

    @Insert
    suspend fun insert(event:EventDetail)

    @Update
    suspend fun update(event:EventDetail)

    @Query("SELECT * from event_table WHERE eventID = :key")
    suspend fun get(key: Long): EventDetail?

    @Query("DELETE FROM event_table")
    suspend fun clear()
}