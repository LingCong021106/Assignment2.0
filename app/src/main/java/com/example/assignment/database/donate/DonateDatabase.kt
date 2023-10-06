package com.example.assignment.database.donate


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Donate :: class], version = 15)
abstract class DonateDatabase : RoomDatabase() {

    abstract fun donateDao() : DonateDao

    companion object{

        @Volatile
        private var INSTANCE : DonateDatabase? = null

        fun getDatabase(context: Context): DonateDatabase {

            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DonateDatabase::class.java,
                    "donate_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }

    }

}