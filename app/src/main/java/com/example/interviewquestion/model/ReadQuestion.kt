package com.example.interviewquestion.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class ReadQuestion(
    @PrimaryKey
    var SrNo: Int,
    var isHtmlTag: Boolean,
    var quesType: String,
    var Question: String,
    var Answer: String
): Serializable
