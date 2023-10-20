package com.example.interviewquestion.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.interviewquestion.Constant
import com.example.interviewquestion.R
import com.example.interviewquestion.adapters.DashboardRVAdapter
import com.example.interviewquestion.databinding.ActivityDashboardBinding
import com.example.interviewquestion.interfaces.OnItemClickListener
import com.example.interviewquestion.model.QuestionAnswerList
import com.example.interviewquestion.model.TechnologyName
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var techArray: ArrayList<String>
//    private lateinit var dashboardRvAdapter: DashboardRVAdapter
    private val TAG = "DashboardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        supportActionBar!!.hide()

        var jsonFile = readJSONFromAsset("Interview_Question_Answers_Sample.json")

        Handler().postDelayed({
            var intent = Intent(this@DashboardActivity, QuestionActivity::class.java)
            intent.putExtra(Constant.QUESTIONANSWERLIST, jsonFile)
            startActivity(intent)
            finish()
        }, 500)

    }

    private fun readJSONFromAsset(jsonFileName: String): String{
        val assetManager = assets
        val inputStream = assetManager.open(jsonFileName)
        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = bufferReader.readText()
        return jsonString
    }
}