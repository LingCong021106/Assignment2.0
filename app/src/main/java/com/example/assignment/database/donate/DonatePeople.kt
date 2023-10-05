package com.example.assignment.database.donate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

var donatePeopleList = mutableListOf<DonatePeople>()
@Entity(tableName = "donate_people_table")
data class DonatePeople(
    @PrimaryKey val donatePersonId: Int?,
    @ColumnInfo(name = "donateId") val donateId: Int?,
    @ColumnInfo(name = "userId") val userId: Int?,
    @ColumnInfo(name = "userEmail") val userEmail: String?,
    @ColumnInfo(name = "userName") val userName: String?,
    @ColumnInfo(name = "userImage") val userImage: String?,
    @ColumnInfo(name = "userTotalDonate") val userTotalDonate: Double?,
    @ColumnInfo(name = "paymentMethod") val paymentMethod: String?,
    @ColumnInfo(name = "createDate") val createDate: String?
)
