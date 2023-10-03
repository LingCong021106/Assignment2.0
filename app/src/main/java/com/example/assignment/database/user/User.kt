package com.example.assignment.database.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "userName") val userName: String?,
    @ColumnInfo(name = "role") val role: String?,
    @ColumnInfo(name = "profile") val profile: String?

)
