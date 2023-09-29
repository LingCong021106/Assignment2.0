package com.example.assignment.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Insert

@Dao
interface UserDao {

    @Query("SELECT * FROM users_table WHERE userEmail = :email")
    fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users_table")
    fun getAll(): List<User>

    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM users_table WHERE userEmail = :email AND password = :password")
    fun checkUser(email: String, password: String): User?

    @Query("UPDATE users_table SET userName = :newName, userEmail = :newEmail, phone = :newPhone, photo = :profileImageUrl WHERE userEmail = :originalEmail")
    fun updateUserInfo(originalEmail: String, newName: String, newEmail: String, newPhone: String, profileImageUrl: String)

    @Query("UPDATE users_table SET password = :password WHERE userEmail = :userEmail")
    fun updateUserPassword(password: String, userEmail: String,)

}
