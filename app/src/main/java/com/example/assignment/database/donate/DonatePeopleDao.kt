package com.example.assignment.database.donate

import androidx.room.*

@Dao
interface DonatePeopleDao {
    @Query("SELECT * FROM donate_people_table")
    fun getAll(): List<DonatePeople>

    @Query("SELECT * FROM donate_people_table WHERE donateID LIKE :donateID")
    fun getDonatePeople(donateID : Int): List<DonatePeople>

    /* @Query("SELECT * FROM student_table WHERE uid IN (:userIds)")
     fun loadAllByIds(userIds: IntArray): List<Student>*/

//    @Query("SELECT * FROM student_table WHERE roll_no LIKE :roll LIMIT 1")
//    suspend fun findByRoll(roll: Int): Student

//    @Query("SELECT * FROM user_table WHERE id LIKE :id")
//    fun getUser(id: Int): Donate

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(donatePeople : DonatePeople)

//    @Query("UPDATE user_table SET userName=:name, role=:role, profile=:profile WHERE id=:id")
//    fun update(id : Int, name : String, role: String, profile : String)

    @Update
    fun update(donatePeople : DonatePeople)

    @Delete
    fun delete(donatePeople : DonatePeople)

    @Query("DELETE FROM donate_people_table")
    fun deleteAll()
}