package com.example.interviewquestion.util

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.Delegates

object MyPreferences {
    private lateinit var prefs : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private const val PREFS_NAME = "_iq"
    private const val IS_PURCHASED_No : String = "is_purchased"
    private const val FIRST_LAUNCH_AFTER_PAID = "paid"
    private const val VERSION = "_version"
    private const val DELETE_RESTRED = "_delete_restore"
    private const val FREE = "_free"
    private const val RESTORE = "_restore"

    fun init(context: Context){
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = prefs.edit()
        editor.commit()
    }

    public fun savePurchaseValueToPref(b : Boolean){
        editor.putBoolean(IS_PURCHASED_No, b)
        editor.commit()
    }

    fun isPurchased() : Boolean{
        return prefs.getBoolean(IS_PURCHASED_No, false)
    }

    fun saveFirstLaunchAfterPaid(b: Boolean){
        editor.putBoolean(FIRST_LAUNCH_AFTER_PAID, b)
        editor.commit()
    }

    fun isFirstLaunchAfterPaid(): Boolean{
        return prefs.getBoolean(FIRST_LAUNCH_AFTER_PAID, false)
    }

    fun setVersion(version: Int){
        editor.putInt(VERSION, version)
        editor.commit()
    }

    fun getVersion(): Int{
        return prefs.getInt(VERSION, 0)
    }

    fun saveDeleteAndRestoredValue(flag: Boolean){
        editor.putBoolean(DELETE_RESTRED, flag)
        editor.commit()

    }

    fun isDeletedAndRestored(): Boolean{
        return prefs.getBoolean(DELETE_RESTRED, true)
    }

    fun setFreeVersion(flag: Boolean){
        editor.putBoolean(FREE, flag)
        editor.commit()
    }

    fun isFreeVersion(): Boolean{
        return prefs.getBoolean(FREE, true)
    }

    fun setRestoreValue(flag: Boolean){
        editor.putBoolean(RESTORE, flag)
        editor.commit()
    }

    fun isRestored(): Boolean{
        return prefs.getBoolean(RESTORE, false)
    }
}