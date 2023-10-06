package com.example.assignment.user.event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: Event)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEventJoined(eventJoined: EventJoined)

    @Query("delete from event_table")
    fun deleteAllEvent()

    @Query("delete from event_joined_table")
    fun deleteEventJoined()

    @Query("select * from event_table where isDeleted = 0")
    fun getAllEvent() : MutableList<Event>

    @Query("select * from event_joined_table")
    fun getAllEventJoined() : MutableList<EventJoined>
}