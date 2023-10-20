package com.example.interviewquestion.model

import androidx.room.Entity
import java.io.Serializable

data class QuestionAnswer(
    var SrNo: Int,
    var isHtmlTag: Boolean,
    var quesType: String,
    var Question: String,
    var Answer: String
): Serializable
