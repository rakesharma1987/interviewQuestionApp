package com.example.interviewquestion.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class SaveForLaterQues(
    @PrimaryKey
    var SrNo: Int,
    var isHtmlTag: Boolean,
    var quesType: String,
    var Question: String,
    var Answer: String
): Serializable
