package com.example.assignment.database.user

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table ORDER BY role")
    fun getAll(): List<User>

    @Query("SELECT * FROM user_table WHERE role LIKE :role")
    fun getUsers(role : String): List<User>

   /* @Query("SELECT * FROM student_table WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Student>*/

//    @Query("SELECT * FROM student_table WHERE roll_no LIKE :roll LIMIT 1")
//    suspend fun findByRoll(roll: Int): Student

    @Query("SELECT * FROM user_table WHERE id LIKE :id")
    fun getUser(id: Int): User

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

//    @Query("UPDATE user_table SET userName=:name, role=:role, profile=:profile WHERE id=:id")
//    fun update(id : Int, name : String, role: String, profile : String)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

//    @Query("DELETE FROM student_table")
//    suspend fun deleteAll()
}