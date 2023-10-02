package com.example.assignment.user.event

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.assignment.user.donate.DonateDatabase
import com.example.assignment.user.donate.DonateDatabaseDao

@Database(entities = [Event::class, EventJoined::class], version = 6)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDatabaseDao() : EventDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getInstance(context: Context): EventDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        EventDatabase::class.java,
                        "event_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}