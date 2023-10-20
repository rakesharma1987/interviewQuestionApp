package com.example.interviewquestion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.interviewquestion.db.AppRepository
import com.example.interviewquestion.viewModel.DbViewModel

class DbFactory(private val repository: AppRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DbViewModel::class.java)){
            return DbViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown class")
    }
}