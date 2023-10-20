package com.example.interviewquestion.db

import androidx.lifecycle.LiveData
import com.example.interviewquestion.model.QuestionAnswer

class AppRepository(private val appDao: AppDao) {
    val getSaveForLaterData = appDao.getAllSaveForLaterData()
    val getMarkedAsReadData = appDao.getAllMarkedAsReadData()
    val getAllQuestionAnswerData = appDao.getAllQuestionAnswerData()

    suspend fun saveForLaterData(data: SaveForLaterQues): Long{
        return appDao.saveForLater(data)
    }

    suspend fun markedAsReadData(data: MarkedAsReadQues): Long{
        return appDao.saveMarkedAsRead(data)
    }

    suspend fun saveAllQuestionAnswer(data: List<QuestionAnswer>){
        appDao.saveAllQuestionAnswerData(data)
    }

    suspend fun deleteQuestionAnswer(data: QuestionAnswer): Int{
        return appDao.deleteQuestionAnswerData(data)
    }

}