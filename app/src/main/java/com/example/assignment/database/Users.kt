package com.example.assignment.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users_table")
data class User(
    @PrimaryKey()
    var userId: Int,
    @ColumnInfo(name="userName") var userName: String?,
    @ColumnInfo(name="password") var password: String?,
    @ColumnInfo(name="userEmail") var userEmail: String?,
    @ColumnInfo(name="phone") var phone: String?,
    @ColumnInfo(name="photo") var photo: String?,
    @ColumnInfo(name = "registerDate") var registerDate: String?
    ){
    constructor() : this(0, "", "", "", "", "", "")
}
