package com.example.interviewquestion.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.interviewquestion.model.BookmarkedAndReadQuestion
import com.example.interviewquestion.model.MarkedAsReadQues
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.SaveForLaterQues

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllQuestionAnswerData(data: List<QuestionAnswer>)

    @Query("SELECT * FROM questionanswer WHERE quesType != 'Tips' AND SrNo <= 259")
    fun get25QuestionAnswerData(): LiveData<List<QuestionAnswer>>

    @Query("SELECT * FROM questionanswer WHERE quesType != 'Tips'")
    fun getAllQuestionAnswerData(): LiveData<List<QuestionAnswer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveForLater(data: SaveForLaterQues): Long

    @Query("SELECT * FROM saveforlaterques")
    fun getAllSaveForLaterData(): LiveData<List<QuestionAnswer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMarkedAsRead(data: MarkedAsReadQues): Long

    @Query("SELECT * FROM markedasreadques")
    fun getAllMarkedAsReadData(): LiveData<List<QuestionAnswer>>

    @Delete
    suspend fun deleteQuestionAnswerData(data: QuestionAnswer): Int

    @Query("SELECT * FROM questionanswer WHERE quesType = 'Tips'")
    fun getAllTipsQuestion(): LiveData<List<QuestionAnswer>>

    @Query("DELETE FROM questionanswer")
    suspend fun deleteAllQuestionAnswer(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllBookmarkedAndReadQuestion(data: BookmarkedAndReadQuestion): Long

    @Query("SELECT * FROM bookmarkedandreadquestion")
    fun getAllBookmarkedAndReadQuestion(): LiveData<List<BookmarkedAndReadQuestion>>

}