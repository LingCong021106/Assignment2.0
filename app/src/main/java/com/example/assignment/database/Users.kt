package com.example.assignment.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0L,
    @ColumnInfo(name="userName") val userName: String?,
    @ColumnInfo(name="password") val password: String?,
    @ColumnInfo(name="userEmail") val userEmail: String?,
    @ColumnInfo(name="phone") val phone: String?,
    @ColumnInfo(name="photo") val photo: String?,
    @ColumnInfo(name = "registerDate") val registerDate: String?
    )
