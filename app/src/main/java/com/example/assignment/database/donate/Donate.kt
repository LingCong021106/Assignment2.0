package com.example.assignment.database.donate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

var donateList = mutableListOf<Donate>()
var donatePeopleList = mutableListOf<DonatePeople>()
@Entity(tableName = "donate_table")
data class Donate(
    @PrimaryKey(autoGenerate = true) val donateID: Int?,
    @ColumnInfo(name = "donateImage") val donateImage: String?,
    @ColumnInfo(name = "organization") val organization: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "target") val target: Int?,
    @ColumnInfo(name = "startDate") val startDate: String?,
    @ColumnInfo(name = "endDate") val endDate: String?,
    @ColumnInfo(name = "Description") val description: String?,
    @ColumnInfo(name = "userID") val userID: Int?
)


@Entity(tableName = "donate_people_table")
data class DonatePeople(
    @PrimaryKey(autoGenerate = true) val donatePeopleID: Int?,
    @ColumnInfo(name = "donateID") val donateID: Int?,
    @ColumnInfo(name = "userID") val userID: Int?,
    @ColumnInfo(name = "userName") val userName: String?,
    @ColumnInfo(name = "amount") val amount: Int?,
    @ColumnInfo(name = "date") val date: String?
)
