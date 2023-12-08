package com.example.interviewquestion.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.interviewquestion.model.MarkedAsReadQues
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.SaveForLaterQues

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllQuestionAnswerData(data: List<QuestionAnswer>)

    @Query("SELECT * FROM questionanswer")
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

}