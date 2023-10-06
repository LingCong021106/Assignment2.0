package com.example.assignment.admin.news

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

var newsList = mutableListOf<News>()
var searchList = mutableListOf<News>()

@Entity(tableName = "news_table")
data class News (
    @PrimaryKey()
    var newsId: Int,

    @ColumnInfo(name = "newsImage")
    var newsImage : String,

    @ColumnInfo(name = "newsTitle")
    var newsTitle : String,

    @ColumnInfo(name = "newsUrl")
    var newsUrl:String,

    @ColumnInfo(name = "createDate")
    var createDate: String
){
    constructor() : this(0, "", "", "", "")
}