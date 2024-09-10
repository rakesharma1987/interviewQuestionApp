package com.example.interviewquestion.views

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.example.interviewquestion.util.MyPreferences
import com.example.interviewquestion.viewModel.DbViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DbViewModel
    private lateinit var listData: ArrayList<QuestionAnswer>
    private lateinit var listData25: ArrayList<QuestionAnswer>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        supportActionBar!!.hide()
        MyPreferences.init(this)

        listData = ArrayList<QuestionAnswer>()
        listData25 = ArrayList<QuestionAnswer>()
        var jsonFile = readJSONFromAsset("Interview_Question_Answers_Sample.json")
        val list = Gson().fromJson(jsonFile, QuestionAnswerList::class.java)
        val dao = AppDatabase.getInstance(this).dao
        val factory = DbFactory(AppRepository(dao))
        viewModel = ViewModelProvider(this, factory)[DbViewModel::class.java]
        viewModel.getAllQuestionAnswerData.observe(this, Observer {
            for (data in it.listIterator()){
                listData.add(data)
            }
            if (it.isEmpty()){
                if (MyPreferences.isPurchased()) {
                    viewModel.saveAllQuestionAnswer(list)
                }
            }
        })
        viewModel.get25QuestionAnswerData.observe(this, Observer{
            for (data in it.listIterator()){
                listData25.add(data)
            }
            if (it.isEmpty()){
                if (!MyPreferences.isPurchased()) {
                    viewModel.saveAllQuestionAnswer(list)
                }
            }
        })

        Handler().postDelayed({
            if (MyPreferences.isPurchased()){
//                if (MyPreferences.isFirstLaunchAfterPaid()){
//                    viewModel.deleteAllQuestion()
//                    MyPreferences.saveFirstLaunchAfterPaid(false)
//                    viewModel.getAllBookmarkedAndReadQuestion.observe(this, Observer {
//                        for (item in it.listIterator()){
//                            viewModel.deleteQuestionAnswer(QuestionAnswer(item.SrNo, item.isHtmlTag, item.quesType, item.Question, item.Answer))
//                        }
//                    })
//                }
                viewModel.saveAllQuestionAnswer(listData)
                val intent = Intent(this@DashboardActivity, QuestionActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                viewModel.saveAllQuestionAnswer(listData25)
                val intent = Intent(this@DashboardActivity, QuestionTwentyFiveActivity::class.java)
                startActivity(intent)
                finish()
            }
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