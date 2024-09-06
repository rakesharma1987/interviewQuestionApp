package com.example.interviewquestion.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "QuestionAnswer")
@Parcelize
class QuestionAnswer(
    @PrimaryKey
    var SrNo: Int,
    var isHtmlTag: Boolean,
    var quesType: String,
    var Question: String,
    var Answer: String
): Parcelable
