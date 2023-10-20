package com.example.interviewquestion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.QuestionAnswerList
import com.example.interviewquestion.viewModel.QuestionsAnswerViewModel

class QuestionAnswerFactory(private val list: ArrayList<QuestionAnswer>): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionsAnswerViewModel::class.java)){
            return QuestionsAnswerViewModel(list) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}