package com.example.assignment.databaseEvent


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EventData :: class], version = 1)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao() : EventDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getInstance(context: Context): EventDatabase {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "event_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }


        }

    }
}