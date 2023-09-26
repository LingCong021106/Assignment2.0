package com.example.assignment.databaseEvent

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EventDatabaseDao {

    @Query("SELECT * FROM event_table")
    fun getAll(): List<Event>
    @Query("SELECT * FROM event_table WHERE id LIKE :id")
    fun getUser(id: Int): Event

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(event: Event)

    @Delete
    fun delete(event: Event)
}