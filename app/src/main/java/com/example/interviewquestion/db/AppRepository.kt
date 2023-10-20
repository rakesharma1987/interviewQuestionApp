package com.example.interviewquestion.db

import androidx.lifecycle.LiveData
import com.example.interviewquestion.model.QuestionAnswer

class AppRepository(private val appDao: AppDao) {
    val getSaveForLaterData = appDao.getAllSaveForLaterData()
    val getMarkedAsReadData = appDao.getAllMarkedAsReadData()

    suspend fun saveForLaterData(data: SaveForLaterQues){
        appDao.saveForLater(data)
    }

    suspend fun markedAsReadData(data: MarkedAsReadQues){
        appDao.saveMarkedAsRead(data)
    }

}