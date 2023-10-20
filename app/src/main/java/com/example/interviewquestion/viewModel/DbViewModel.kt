package com.example.interviewquestion.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interviewquestion.db.AppRepository
import com.example.interviewquestion.db.MarkedAsReadQues
import com.example.interviewquestion.db.SaveForLaterQues
import com.example.interviewquestion.model.QuestionAnswer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbViewModel(private val repository: AppRepository): ViewModel() {
    var tempList: LiveData<ArrayList<SaveForLaterQues>> = MutableLiveData()

    fun saveForLater(data: SaveForLaterQues){
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveForLaterData(data)
        }
    }

    fun saveMarkedAsRead(data: MarkedAsReadQues){
        viewModelScope.launch(Dispatchers.IO) {
            repository.markedAsReadData(data)
        }
    }


    val getAllSaveForLaterData = repository.getSaveForLaterData
    val getAllMarkedAsReadData = repository.getMarkedAsReadData
}