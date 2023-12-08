package com.example.interviewquestion.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.interviewquestion.Constant
import com.example.interviewquestion.R
import com.example.interviewquestion.adapters.DashboardRVAdapter
import com.example.interviewquestion.databinding.ActivityDashboardBinding
import com.example.interviewquestion.db.AppDatabase
import com.example.interviewquestion.db.AppRepository
import com.example.interviewquestion.factory.DbFactory
import com.example.interviewquestion.interfaces.OnItemClickListener
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.QuestionAnswerList
import com.example.interviewquestion.model.TechnologyName
import com.example.interviewquestion.viewModel.DbViewModel
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DbViewModel
    private lateinit var listData: ArrayList<QuestionAnswer>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        supportActionBar!!.hide()

        listData = ArrayList<QuestionAnswer>()
        var jsonFile = readJSONFromAsset("Interview_Question_Answers_Sample.json")
//        var tipsJsonFile = readJSONFromAsset("tips.json")
        val list = Gson().fromJson(jsonFile, QuestionAnswerList::class.java)
        val dao = AppDatabase.getInstance(this).dao
        val factory = DbFactory(AppRepository(dao))
        viewModel = ViewModelProvider(this, factory)[DbViewModel::class.java]
        viewModel.getAllQuestionAnswerData.observe(this, Observer {
            for (it in it.listIterator()){
                listData.add(it)
            }
            if (it.isEmpty()){
                viewModel.saveAllQuestionAnswer(list)
            }
        })

        Handler().postDelayed({
            var intent = Intent(this@DashboardActivity, QuestionActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)

    }

    private fun readJSONFromAsset(jsonFileName: String): String{
        val assetManager = assets
        val inputStream = assetManager.open(jsonFileName)
        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = bufferReader.readText()
        return jsonString
    }
}