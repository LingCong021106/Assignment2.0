package com.example.assignment.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Insert

@Dao
interface AdminDao {

    @Query("SELECT * FROM admin_table")
    fun getAll(): List<Admin>

//    @Query("SELECT * FROM users_table WHERE userId= :key")
//    suspend fun findById(key: Int): LiveData<User>

    @Insert
    fun insert(admin: Admin)

    @Query("SELECT * FROM admin_table WHERE aEmail = :email AND aPassword = :password")
    fun checkAdmin(email: String, password: String): Admin?

    @Query("SELECT * FROM admin_table WHERE aEmail = :email")
    fun getAdminByEmail(email: String): Admin?

    @Query("UPDATE admin_table SET aName = :newName, aEmail = :newEmail, aName = :newPhone, photo = :profileImageUrl WHERE aEmail = :originalEmail")
    fun updateAdminInfo(originalEmail: String, newName: String, newEmail: String, newPhone: String, profileImageUrl: String)

//    @Delete
//    suspend fun delete(user: User)
//
//    @Query("DELETE FROM users_table WHERE userId= :key")
//    suspend fun deleteUser(key: Int): LiveData<User>
}