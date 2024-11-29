package com.example.interviewquestion.views

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity:AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val configuration = Configuration(newBase.resources.configuration)
        configuration.fontScale = 1.0f
        configuration.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE
        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }

//    override fun attachBaseContext(newBase: Context?) {
//        val newOverride = Configuration(newBase?.resources?.configuration)
//        newOverride.fontScale = 1.0f
//        applyOverrideConfiguration(newOverride)
//        super.attachBaseContext(newBase)
//    }
}