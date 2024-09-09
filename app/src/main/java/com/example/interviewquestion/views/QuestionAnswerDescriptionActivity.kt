package com.example.interviewquestion.views

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.interviewquestion.Constant
import com.example.interviewquestion.R
import com.example.interviewquestion.databinding.ActivityQuestionAnswerDescriptionBinding
import com.example.interviewquestion.db.AppDatabase
import com.example.interviewquestion.db.AppRepository
import com.example.interviewquestion.factory.DbFactory
import com.example.interviewquestion.model.BookmarkedAndReadQuestion
import com.example.interviewquestion.model.MarkedAsReadQues
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.SaveForLaterQues
import com.example.interviewquestion.viewModel.DbViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.Delegates

class QuestionAnswerDescriptionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityQuestionAnswerDescriptionBinding
    private lateinit var viewModel: DbViewModel
    private lateinit var saveForLaterData: SaveForLaterQues
    private lateinit var markedAsReadData: MarkedAsReadQues
    private lateinit var myMenu: Menu
    private var isSaveOrMarkedAsRead by Delegates.notNull<Boolean>()
    private var tipsList = ArrayList<String>()
    private var remainingList: ArrayList<QuestionAnswer>? = null
    private var currentIndex: Int = 0
    private var position: Int = 0
    private var tabName: String = Constant.TAB_ALL

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question_answer_description)
        val tab = intent.getStringExtra(Constant.TAB_NAME)
        if (tab == "Tips"){
            supportActionBar!!.title = resources.getString(R.string.txt_tips)
        }else if (tab == "Bookmarks"){
            supportActionBar!!.title = resources.getString(R.string.txt_save_for_later)
        }else if (tab == "READ"){
            supportActionBar!!.title = resources.getString(R.string.txt_read)
        }else{
            supportActionBar!!.title = resources.getString(R.string.app_name)
        }

        val dao = AppDatabase.getInstance(this).dao
        val factory = DbFactory(AppRepository(dao))
        viewModel = ViewModelProvider(this, factory)[DbViewModel::class.java]

        saveForLaterData = intent.getSerializableExtra(Constant.SAVE_FOR_LATER, SaveForLaterQues::class.java)!!
        markedAsReadData = intent.getSerializableExtra(Constant.MARKED_AS_READ, MarkedAsReadQues::class.java)!!
        isSaveOrMarkedAsRead = intent.getBooleanExtra(Constant.IS_SAVE_OR_MARKED_AS_READ_DATA, false)
//        remainingList = intent.getParcelableArrayListExtra(Constant.REMAINING_DATA_LIST)
        position = intent.getIntExtra(Constant.CLICKED_POSITION, 0)
        tabName = intent.getStringExtra(Constant.TAB_NAME)!!
        if (tabName == "") tabName = Constant.TAB_ALL
        currentIndex = position
        Log.d("TAG", "onCreate: $remainingList")
        

        if (saveForLaterData.quesType == "Tips") {
            var tipsString = saveForLaterData.Answer.split("\n").toList()
            for (tips in tipsString.listIterator()){
                tipsList.add(tips)
            }

        }

        viewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this@QuestionAnswerDescriptionActivity, it, Toast.LENGTH_SHORT).show()
            }
        })

        binding.tvQuestion.text = "Question - "+saveForLaterData?.SrNo+". "+saveForLaterData?.Question
        if (saveForLaterData?.isHtmlTag!!){
            binding.webView.visibility = View.VISIBLE
            binding.tvAnswer.visibility = View.GONE
            binding.webView.loadDataWithBaseURL(null,
                saveForLaterData.Answer, "text/html","utf-8", null)
        }else if (saveForLaterData.quesType.equals("Tips")){
            binding.webView.visibility = View.GONE
            binding.tvAnswer.visibility = View.GONE
            binding.rvTipsItem.visibility = View.VISIBLE
            var tipAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tipsList)
            binding.rvTipsItem.adapter =tipAdapter
            tipAdapter.notifyDataSetChanged()

        }else{
            binding.webView.visibility = View.GONE
            binding.tvAnswer.visibility = View.VISIBLE
            var txt = saveForLaterData!!.Answer.replace("\n", System.getProperty("line.separator"))
//            binding.tvAnswer.text = HtmlCompat.fromHtml(saveForLaterData!!.Answer, 0)
//            binding.tvAnswer.text = saveForLaterData!!.Answer
            binding.tvAnswer.text = addNewLine(saveForLaterData!!.Answer)
        }

        binding.btnNext.setOnClickListener(this)
        binding.btnPrev.setOnClickListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        myMenu = menu!!
        var menuItemSave = menu.findItem(R.id.item_save)
        var menuItemMarkedAsRead = menu.findItem(R.id.item_mark_as_read)

        if (isSaveOrMarkedAsRead){
            menuItemSave.isVisible = false
            menuItemMarkedAsRead.isVisible = false
        }else{
            menuItemSave.isVisible = true
            menuItemMarkedAsRead.isVisible = true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_mark_as_read ->{

                viewModel.saveAllBookmarkedAndReadQuestion(BookmarkedAndReadQuestion(saveForLaterData.SrNo, saveForLaterData.isHtmlTag, saveForLaterData.quesType, saveForLaterData.Question, saveForLaterData.Answer))
                viewModel.saveForLater(saveForLaterData)
                viewModel.deleteQuestionAnswer(QuestionAnswer(saveForLaterData.SrNo, saveForLaterData.isHtmlTag, saveForLaterData.quesType, saveForLaterData.Question, saveForLaterData.Answer))
                this.finish()
            }
            R.id.item_save ->{
                viewModel.saveAllBookmarkedAndReadQuestion(BookmarkedAndReadQuestion(markedAsReadData.SrNo, markedAsReadData.isHtmlTag, markedAsReadData.quesType, markedAsReadData.Question, markedAsReadData.Answer))
                viewModel.saveMarkedAsRead(markedAsReadData)
                viewModel.deleteQuestionAnswer(QuestionAnswer(markedAsReadData.SrNo, markedAsReadData.isHtmlTag, markedAsReadData.quesType, markedAsReadData.Question, markedAsReadData.Answer))
                this.finish()
            }

        }
        return true
    }

    override fun onResume() {
        super.onResume()
//        if (isSaveOrMarkedAsRead){
//            myMenu.findItem(R.id.item_save).isVisible = true
//            myMenu.findItem(R.id.item_mark_as_read).isVisible = true
//        }else{
//            myMenu.findItem(R.id.item_save).isVisible = false
//            myMenu.findItem(R.id.item_mark_as_read).isVisible = false
//        }

        if (tabName.equals(Constant.TAB_ALL)) {
            viewModel.get25QuestionAnswerData.observe(this, Observer {
                val list = ArrayList<QuestionAnswer>()
                for (data in it.listIterator()) {
                    list.add(data)
                }
                remainingList = list
            })
        }else if (tabName.equals(Constant.TAB_BOOKMARKS)){
            viewModel.getAllMarkedAsReadData.observe(this, Observer {
                val list = ArrayList<QuestionAnswer>()
                for (data in it.listIterator()) {
                    list.add(data)
                }
                remainingList = list
            })
        }else if (tabName.equals(Constant.TAB_READ)){
            viewModel.getAllSaveForLaterData.observe(this, Observer {
                val list = ArrayList<QuestionAnswer>()
                for (data in it.listIterator()) {
                    list.add(data)
                }
                remainingList = list
            })
        }
    }

    fun addNewLine(string: String): String? {
        val count = string.split("\n".toRegex()).toTypedArray().size - 1
        val sb = StringBuilder(count)
        val splitString = string.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (i in splitString.indices) {
            sb.append(splitString[i])
            if (i != splitString.size - 1) sb.append("\n\n\n")
        }
        return sb.toString()
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_next ->{
                if (currentIndex < remainingList!!.size-1){
                    currentIndex++
                    val questionAnswer = remainingList!!.toMutableList()[currentIndex]
                    displayView(questionAnswer)
                }
            }

            R.id.btn_prev ->{
                if (currentIndex > 0){
                    currentIndex--
                    val questionAnswer = remainingList!!.toMutableList()[currentIndex]
                    displayView(questionAnswer)
                }
            }
        }
    }

    fun displayView(questionAnswer: QuestionAnswer){
        binding.tvQuestion.text = "Question - "+questionAnswer.SrNo+". "+questionAnswer.Question
        if (questionAnswer.isHtmlTag){
            binding.webView.visibility = View.VISIBLE
            binding.tvAnswer.visibility = View.GONE
            binding.webView.loadDataWithBaseURL(null,
                questionAnswer.Answer, "text/html","utf-8", null)
        }else{
            binding.webView.visibility = View.GONE
            binding.tvAnswer.visibility = View.VISIBLE
            binding.tvAnswer.text = addNewLine(questionAnswer!!.Answer)
        }
    }
}