package com.example.assignment.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "admin_table")
data class Admin(
    @PrimaryKey()
    var aId: Int,
    @ColumnInfo(name="aName") var aName: String?,
    @ColumnInfo(name="aEmail") var aEmail: String?,
    @ColumnInfo(name="aPassword") var aPassword: String?,
    @ColumnInfo(name="aPhone") var aPhone: String?,
    @ColumnInfo(name="role") var role: String?,
    @ColumnInfo(name="photo") var photo: String?,
    @ColumnInfo(name = "joinedDate") var joinedDate: String?
){
    constructor() : this(0, "", "", "", "", "", "" , "")
}