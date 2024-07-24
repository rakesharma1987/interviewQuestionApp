package com.example.interviewquestion.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BookmarkedAndReadQuestion")
data class BookmarkedAndReadQuestion(
    @PrimaryKey
    var SrNo: Int,
    var isHtmlTag: Boolean,
    var quesType: String,
    var Question: String,
    var Answer: String
)
