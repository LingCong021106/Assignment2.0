package com.example.assignment.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "admin_table")
data class Admin(
    @PrimaryKey(autoGenerate = true)
    var aId: Long = 0L,
    @ColumnInfo(name="aName") val aName: String?,
    @ColumnInfo(name="aEmail") val aEmail: String?,
    @ColumnInfo(name="aPassword") val aPassword: String?,
    @ColumnInfo(name="aPhone") val aPhone: String?,
    @ColumnInfo(name="role") val role: String?,
    @ColumnInfo(name="photo") val photo: String?,
    @ColumnInfo(name = "joinedDate") val joinedDate: String?
)