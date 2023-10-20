package com.example.interviewquestion.db

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.interviewquestion.model.QuestionAnswer

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveForLater(data: SaveForLaterQues)

    @Query("SELECT * FROM saveforlaterques")
    fun getAllSaveForLaterData(): LiveData<List<QuestionAnswer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMarkedAsRead(data: MarkedAsReadQues)

    @Query("SELECT * FROM markedasreadques")
    fun getAllMarkedAsReadData(): LiveData<List<QuestionAnswer>>

}