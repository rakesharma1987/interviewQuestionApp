package com.example.interviewquestion.application

import android.app.Application
import com.example.interviewquestion.util.MyPreferences

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MyPreferences.init(this)
    }
}