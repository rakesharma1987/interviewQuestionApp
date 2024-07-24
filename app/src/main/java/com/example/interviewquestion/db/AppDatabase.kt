package com.example.interviewquestion.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.interviewquestion.model.BookmarkedAndReadQuestion
import com.example.interviewquestion.model.MarkedAsReadQues
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.SaveForLaterQues

@Database(entities = [SaveForLaterQues::class, MarkedAsReadQues::class, QuestionAnswer::class, BookmarkedAndReadQuestion::class], version = 3)
abstract class AppDatabase: RoomDatabase(){
    abstract val dao: AppDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(2, 3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS 'BookmarkedAndReadQuestion' ('SrNo' INTEGER PRIMARY KEY, 'isHtmlTag' BOOLEAN, 'quesType' TEXT, 'Question' TEXT, 'Answer' TEXT)")
            }
        }

        fun getInstance(context: Context): AppDatabase{
            var instance = INSTANCE
            synchronized(this){
                if (instance == null){
                    instance = Room.databaseBuilder(context, AppDatabase::class.java, "Question")
                        .addMigrations(MIGRATION_1_2)
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
            }
            return instance!!
        }
    }
}