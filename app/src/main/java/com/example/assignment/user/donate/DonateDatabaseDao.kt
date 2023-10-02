package com.example.assignment.user.donate

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DonateDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDonate(donate: Donate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDonatePerson(donatePerson: DonatePerson)

    @Delete
    fun delete(donate: Donate)

    @Query("delete from donate_table")
    fun deleteAllFromDonate()

    @Query("delete from donate_person_table")
    fun deleteAllFromDonatePerson()

    @Query("select * from donate_table")
    fun getAllDonate() : MutableList<Donate>

    @Query("select * from donate_person_table")
    fun getAllDonatePerson() : MutableList<DonatePerson>
}