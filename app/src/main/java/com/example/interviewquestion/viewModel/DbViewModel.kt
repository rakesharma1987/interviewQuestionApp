package com.example.interviewquestion.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interviewquestion.db.AppRepository
import com.example.interviewquestion.model.BookmarkedAndReadQuestion
import com.example.interviewquestion.model.BookmarkQuestion
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.ReadQuestion
import com.example.interviewquestion.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbViewModel(private val repository: AppRepository): ViewModel() {
    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
    get() = statusMessage

    fun saveBookmarkQuestion(data: BookmarkQuestion){
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.saveBookmarkQuestion(data)
            withContext(Dispatchers.Main){
                if (id > -1){
                    statusMessage.value = Event("Question saved for later reading.")
                }else{
                    statusMessage.value = Event("Some Error Occured.")
                }
            }
        }
    }

    fun saveReadQuestion(data: ReadQuestion){
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.saveReadQuestion(data)
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

    fun saveAllBookmarkedAndReadQuestion(data: BookmarkedAndReadQuestion){
        viewModelScope.launch {
            repository.saveAllBookmarkedAndReadQuestion(data)
        }
    }

    fun deleteAllQuestion(){
        viewModelScope.launch {
            repository.deleteAllQuestionAnswer()
        }
    }

    fun deleteReadQuestion(){
        viewModelScope.launch {
            repository.deleteReadQuestion()
        }
    }

    fun deleteBookmarkQuestion(){
        viewModelScope.launch {
            repository.deleteBookmarkQuestion()
        }
    }


    val getAllBookmarkQuestion = repository.getAllBookmarkQuestion
    val getAllReadQuestion = repository.getAllReadQuestion
    val getAllQuestionAnswerData = repository.getAllQuestionAnswerData
    val getTipsQuestion = repository.getTipsQuestion
    val get25QuestionAnswerData = repository.get25QuestionAnswerData
    val getAllBookmarkedAndReadQuestion = repository.getAllBookmarkedAndReadQuestion

}