package com.example.interviewquestion.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.interviewquestion.model.QuestionAnswer

@Database(entities = [SaveForLaterQues::class, MarkedAsReadQues::class, QuestionAnswer::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract val dao: AppDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

//        val MIGRATION_1_2 = object : Migration(1, 2){
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE IF NOT EXISTS 'QuestionAnswer'('SrNo' INTEGER PRIMARY KEY, 'isHtmlTag' BOOLEAN, 'quesType' TEXT, 'Question' TEXT, 'Answer' TEXT)")
//            }
//        }

        fun getInstance(context: Context): AppDatabase{
            var instance = INSTANCE
            synchronized(this){
                if (instance == null){
                    instance = Room.databaseBuilder(context, AppDatabase::class.java, "Questions")
//                        .addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE = instance
                }
            }
            return instance!!
        }
    }
}