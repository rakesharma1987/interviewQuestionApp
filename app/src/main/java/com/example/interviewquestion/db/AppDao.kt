package com.example.interviewquestion.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.interviewquestion.model.BookmarkedAndReadQuestion
import com.example.interviewquestion.model.BookmarkQuestion
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.ReadQuestion

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllQuestionAnswerData(data: List<QuestionAnswer>)

    @Query("SELECT * FROM questionanswer")
    fun get25QuestionAnswerData(): LiveData<List<QuestionAnswer>>

    @Query("SELECT * FROM questionanswer")
    fun getAllQuestionAnswerData(): LiveData<List<QuestionAnswer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadQuestion(data: QuestionAnswer): Long

    @Query("SELECT * FROM readquestion")
    fun getAllReadQuestion(): LiveData<List<QuestionAnswer>>

    @Query("DELETE FROM readquestion")
    suspend fun deleteReadQuestion(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBookmarkQuestion(data: QuestionAnswer): Long

    @Query("SELECT * FROM bookmarkedandreadquestion")
    fun getAllBookMarkQuestion(): LiveData<List<QuestionAnswer>>

    @Query("DELETE FROM bookmarkquestion")
    suspend fun deleteBookmarkQuestion(): Int


    @Delete
    suspend fun deleteQuestionAnswerData(data: QuestionAnswer): Int

    @Query("SELECT * FROM questionanswer")
    fun getAllTipsQuestion(): LiveData<List<QuestionAnswer>>

    @Query("DELETE FROM questionanswer")
    suspend fun deleteAllQuestionAnswer(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllBookmarkedAndReadQuestion(data: BookmarkedAndReadQuestion): Long

    @Query("SELECT * FROM bookmarkedandreadquestion")
    fun getAllBookmarkedAndReadQuestion(): LiveData<List<BookmarkedAndReadQuestion>>

}