package com.example.interviewquestion.interfaces

import com.example.interviewquestion.model.QuestionAnswer

interface OnQuestionClickListener {
    fun onClick(position: Int, item: QuestionAnswer)
}