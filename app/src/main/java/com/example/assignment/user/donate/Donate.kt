package com.example.assignment.user.donate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignment.user.event.Event

var donateList = mutableListOf<Donate>()
var donatePersonList = mutableListOf<DonatePerson>()
var donatePersonListById = mutableListOf<DonatePerson>()
var searchList = mutableListOf<Donate>()

@Entity(tableName = "donate_table")
data class Donate(
    @PrimaryKey()
    var donateId: Int,

    @ColumnInfo(name = "adminId")
    var adminId:Int,

    @ColumnInfo(name = "donateName")
    var donateName:String,

    @ColumnInfo(name = "donateImage")
    var donateImage:String?,

    @ColumnInfo(name = "donateCategory")
    var donateCategory:String,

    @ColumnInfo(name = "donateOrgName")
    var donateOrgName:String,

    @ColumnInfo(name = "donateStartTime")
    var donateStartTime:String,

    @ColumnInfo(name = "donateEndTime")
    var donateEndTime:String,

    @ColumnInfo(name = "totalDonation")
    var totalDonation:Double,

    @ColumnInfo(name = "donateDescription")
    var donateDescription:String,

    @ColumnInfo(name = "isDeleted")
    var deleted:Int,
)

@Entity(tableName = "donate_person_table")
data class DonatePerson(
    @PrimaryKey(autoGenerate = true)
    var donatePersonId : Int,

    @ColumnInfo(name = "donateId")
    var donateId:Int,

    @ColumnInfo(name = "userId")
    var userId: Int,

    @ColumnInfo(name = "userEmail")
    var userEmail:String,

    @ColumnInfo(name = "userName")
    var userName:String,

    @ColumnInfo(name = "userImage")
    var userImage:String?,

    @ColumnInfo(name = "userTotalDonate")
    var userTotalDonate:Double,

    @ColumnInfo(name = "paymentMethod")
    var paymentMethod:String,

    @ColumnInfo(name = "createDate")
    var createDate: String
)