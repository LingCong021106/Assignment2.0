package com.example.assignment.database.donate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

var donateList = mutableListOf<Donate>()
@Entity(tableName = "donate_table")
data class Donate(
    @PrimaryKey val donateId: Int?,
    @ColumnInfo(name = "adminId") val adminId: Int?,
    @ColumnInfo(name = "donateName") val donateName: String?,
    @ColumnInfo(name = "donateImage") val donateImage: String?,
    @ColumnInfo(name = "donateCategory") val donateCategory: String?,
    @ColumnInfo(name = "donateOrgname") val donateOrgname: String?,
    @ColumnInfo(name = "donateStartTime") val donateStartTime: String?,
    @ColumnInfo(name = "donateEndTime") val donateEndTime: String?,
    @ColumnInfo(name = "totalDonation") val target: Double?,
    @ColumnInfo(name = "donateDescription") val donateDescription: String?,
    @ColumnInfo(name = "isDeleted") val isDeleted: Int?,
    @ColumnInfo(name = "percentages") val percentages: Int?

)



