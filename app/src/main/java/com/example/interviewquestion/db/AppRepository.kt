package com.example.interviewquestion.db

import com.example.interviewquestion.model.BookmarkedAndReadQuestion
import com.example.interviewquestion.model.MarkedAsReadQues
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.SaveForLaterQues

class AppRepository(private val appDao: AppDao) {
    val getSaveForLaterData = appDao.getAllSaveForLaterData()
    val getMarkedAsReadData = appDao.getAllMarkedAsReadData()
    val getAllQuestionAnswerData = appDao.getAllQuestionAnswerData()
    val getTipsQuestion = appDao.getAllTipsQuestion()
    val get25QuestionAnswerData = appDao.get25QuestionAnswerData()
    val getAllBookmarkedAndReadQuestion = appDao.getAllBookmarkedAndReadQuestion()

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

    suspend fun saveAllBookmarkedAndReadQuestion(data: BookmarkedAndReadQuestion): Long{
        return appDao.saveAllBookmarkedAndReadQuestion(data)
    }

    suspend fun deleteAllQuestionAnswer(): Int{
        return appDao.deleteAllQuestionAnswer()
    }

}