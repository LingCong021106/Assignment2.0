package com.example.assignment.database.user

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAll(): List<User>

   /* @Query("SELECT * FROM student_table WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Student>*/

//    @Query("SELECT * FROM student_table WHERE roll_no LIKE :roll LIMIT 1")
//    suspend fun findByRoll(roll: Int): Student

    @Query("SELECT * FROM user_table WHERE id LIKE :id")
    fun getUser(id: Int): User

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

//    @Query("DELETE FROM student_table")
//    suspend fun deleteAll()
}