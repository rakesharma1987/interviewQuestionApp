package com.example.interviewquestion.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interviewquestion.db.AppRepository
import com.example.interviewquestion.model.MarkedAsReadQues
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.SaveForLaterQues
import com.example.interviewquestion.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbViewModel(private val repository: AppRepository): ViewModel() {
    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
    get() = statusMessage

    fun saveForLater(data: SaveForLaterQues){
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.saveForLaterData(data)
            withContext(Dispatchers.Main){
                if (id > -1){
                    statusMessage.value = Event("Question saved for later reading.")
                }else{
                    statusMessage.value = Event("Some Error Occured.")
                }
            }
        }
    }

    fun saveMarkedAsRead(data: MarkedAsReadQues){
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.markedAsReadData(data)
            withContext(Dispatchers.Main){
                if (id > -1){
                    statusMessage.value = Event("Question marked as read.")
                }else{
                    statusMessage.value = Event("Some Error Occured.")
                }
            }
        }
    }

    fun saveAllQuestionAnswer(data: List<QuestionAnswer>){
        viewModelScope.launch {
            repository.saveAllQuestionAnswer(data)
        }
    }

    fun deleteQuestionAnswer(data: QuestionAnswer){
        viewModelScope.launch {
            repository.deleteQuestionAnswer(data)
        }
    }


    val getAllSaveForLaterData = repository.getSaveForLaterData
    val getAllMarkedAsReadData = repository.getMarkedAsReadData
    val getAllQuestionAnswerData = repository.getAllQuestionAnswerData
}