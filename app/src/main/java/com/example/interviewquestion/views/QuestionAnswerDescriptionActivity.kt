package com.example.interviewquestion.views

import android.os.Build
import android.os.Bundle
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
import com.example.interviewquestion.util.Constant
import com.example.interviewquestion.R
import com.example.interviewquestion.databinding.ActivityQuestionAnswerDescriptionBinding
import com.example.interviewquestion.db.AppDatabase
import com.example.interviewquestion.db.AppRepository
import com.example.interviewquestion.factory.DbFactory
import com.example.interviewquestion.model.BookmarkQuestion
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.ReadQuestion
import com.example.interviewquestion.viewModel.DbViewModel
import com.google.gson.Gson
import kotlin.properties.Delegates

class QuestionAnswerDescriptionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityQuestionAnswerDescriptionBinding
    private lateinit var viewModel: DbViewModel
    private lateinit var bookmarkQuestion: BookmarkQuestion
    private lateinit var readQuestion: ReadQuestion
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

        bookmarkQuestion = Gson().fromJson(intent.getStringExtra(Constant.SAVE_FOR_LATER), BookmarkQuestion::class.java)
//        bookmarkQuestion = intent.getSerializableExtra(Constant.SAVE_FOR_LATER, BookmarkQuestion::class.java)!!
        readQuestion = Gson().fromJson(intent.getStringExtra(Constant.MARKED_AS_READ), ReadQuestion::class.java)
//        readQuestion = intent.getSerializableExtra(Constant.MARKED_AS_READ, ReadQuestion::class.java)!!
        isSaveOrMarkedAsRead = intent.getBooleanExtra(Constant.IS_SAVE_OR_MARKED_AS_READ_DATA, false)
        position = intent.getIntExtra(Constant.CLICKED_POSITION, 0)
        tabName = intent.getStringExtra(Constant.TAB_NAME)!!
        if (tabName == "") tabName = Constant.TAB_ALL
        currentIndex = position

        viewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this@QuestionAnswerDescriptionActivity, it, Toast.LENGTH_SHORT).show()
            }
        })

        binding.tvQuestion.text = "Question - "+bookmarkQuestion?.SrNo+". "+bookmarkQuestion?.Question
        if (bookmarkQuestion?.isHtmlTag!!){
            binding.webView.visibility = View.VISIBLE
            binding.tvAnswer.visibility = View.GONE
            binding.webView.loadDataWithBaseURL(null,
                bookmarkQuestion.Answer, "text/html","utf-8", null)
        }else if (bookmarkQuestion.quesType.equals("Tips")){
            binding.webView.visibility = View.GONE
            binding.tvAnswer.visibility = View.GONE
            binding.rvTipsItem.visibility = View.VISIBLE
            var tipAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tipsList)
            binding.rvTipsItem.adapter =tipAdapter
            tipAdapter.notifyDataSetChanged()

        }else{
            binding.webView.visibility = View.GONE
            binding.tvAnswer.visibility = View.VISIBLE
            var txt = bookmarkQuestion!!.Answer.replace("\n", System.getProperty("line.separator"))
            binding.tvAnswer.text = addNewLine(bookmarkQuestion!!.Answer)
        }

        binding.btnNext.setOnClickListener(this)
        binding.btnPrev.setOnClickListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        myMenu = menu!!
        var menuItemSave = menu.findItem(R.id.item_bookmark)
        var menuItemMarkedAsRead = menu.findItem(R.id.item_read)

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
            R.id.item_read ->{
//                viewModel.saveAllBookmarkedAndReadQuestion(BookmarkedAndReadQuestion(saveForLaterData.SrNo, saveForLaterData.isHtmlTag, saveForLaterData.quesType, saveForLaterData.Question, saveForLaterData.Answer))
                viewModel.saveReadQuestion(readQuestion)
                viewModel.deleteQuestionAnswer(QuestionAnswer(readQuestion.SrNo, readQuestion.isHtmlTag, readQuestion.quesType, readQuestion.Question, readQuestion.Answer))
                this.finish()
            }
            R.id.item_bookmark ->{
//                viewModel.saveAllBookmarkedAndReadQuestion(BookmarkedAndReadQuestion(markedAsReadData.SrNo, markedAsReadData.isHtmlTag, markedAsReadData.quesType, markedAsReadData.Question, markedAsReadData.Answer))
                viewModel.saveBookmarkQuestion(bookmarkQuestion)
                viewModel.deleteQuestionAnswer(QuestionAnswer(bookmarkQuestion.SrNo, bookmarkQuestion.isHtmlTag, bookmarkQuestion.quesType, bookmarkQuestion.Question, bookmarkQuestion.Answer))
                this.finish()
            }

        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (tabName.equals(Constant.TAB_ALL)) {
            viewModel.get25QuestionAnswerData.observe(this, Observer {
                val list = ArrayList<QuestionAnswer>()
                for (data in it.listIterator()) {
                    list.add(data)
                }
                remainingList = list
            })
        }else if (tabName.equals(Constant.TAB_BOOKMARKS)){
            viewModel.getAllBookmarkQuestion.observe(this, Observer {
                val list = ArrayList<QuestionAnswer>()
                for (data in it.listIterator()) {
                    list.add(QuestionAnswer(data.SrNo, data.isHtmlTag, data.quesType, data.Question, data.Answer))
                }
                remainingList = list
            })
        }else if (tabName.equals(Constant.TAB_READ)){
            viewModel.getAllReadQuestion.observe(this, Observer {
                val list = ArrayList<QuestionAnswer>()
                for (data in it.listIterator()) {
                    list.add(QuestionAnswer(data.SrNo, data.isHtmlTag, data.quesType, data.Question, data.Answer))
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