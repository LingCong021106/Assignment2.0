package com.example.assignment.database.donate

import androidx.room.*

@Dao
interface DonateDao {
    @Query("SELECT * FROM donate_table")
    fun getAll(): List<Donate>

//    @Query("SELECT * FROM user_table WHERE role LIKE :role")
//    fun getUsers(role : String): List<Donate>

    /* @Query("SELECT * FROM student_table WHERE uid IN (:userIds)")
     fun loadAllByIds(userIds: IntArray): List<Student>*/

//    @Query("SELECT * FROM student_table WHERE roll_no LIKE :roll LIMIT 1")
//    suspend fun findByRoll(roll: Int): Student

//    @Query("SELECT * FROM user_table WHERE id LIKE :id")
//    fun getUser(id: Int): Donate

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(donate: Donate)

//    @Query("UPDATE user_table SET userName=:name, role=:role, profile=:profile WHERE id=:id")
//    fun update(id : Int, name : String, role: String, profile : String)

    @Update
    fun update(donate: Donate)

    @Delete
    fun delete(donate: Donate)

    @Query("DELETE FROM donate_table")
    fun deleteAll()
}