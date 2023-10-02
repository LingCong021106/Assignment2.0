package com.example.assignment.user.donate

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Donate::class, DonatePerson::class], version = 6)
abstract class DonateDatabase : RoomDatabase() {

    abstract fun donateDatabaseDao() : DonateDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: DonateDatabase? = null

        fun getInstance(context: Context): DonateDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DonateDatabase::class.java,
                        "donate_database"
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