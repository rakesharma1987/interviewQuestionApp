package com.example.interviewquestion.db

import com.example.interviewquestion.model.BookmarkedAndReadQuestion
import com.example.interviewquestion.model.BookmarkQuestion
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.ReadQuestion

class AppRepository(private val appDao: AppDao) {
    val getAllReadQuestion = appDao.getAllReadQuestion()
    val getAllBookmarkQuestion = appDao.getAllBookMarkQuestion()
    val getAllQuestionAnswerData = appDao.getAllQuestionAnswerData()
    val getTipsQuestion = appDao.getAllTipsQuestion()
    val get25QuestionAnswerData = appDao.get25QuestionAnswerData()
    val getAllBookmarkedAndReadQuestion = appDao.getAllBookmarkedAndReadQuestion()

    suspend fun saveReadQuestion(data: ReadQuestion): Long{
        return appDao.saveReadQuestion(data)
    }

    suspend fun saveBookmarkQuestion(data: BookmarkQuestion): Long{
        return appDao.saveBookmarkQuestion(data)
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

    suspend fun deleteReadQuestion(): Int{
        return appDao.deleteReadQuestion()
    }

    suspend fun deleteBookmarkQuestion(): Int{
        return appDao.deleteBookmarkQuestion()
    }

}