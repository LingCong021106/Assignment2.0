package com.example.assignment.database.donate


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DonatePeople :: class], version = 1)
abstract class DonatePeopleDatabase : RoomDatabase() {

    abstract fun donatePeopleDao() : DonatePeopleDao

    companion object{

        @Volatile
        private var INSTANCE : DonatePeopleDatabase? = null

        fun getDatabase(context: Context): DonatePeopleDatabase {

            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DonatePeopleDatabase::class.java,
                    "donate_people_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }

    }

}