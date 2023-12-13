package com.example.interviewquestion.views

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.interviewquestion.Constant
import com.example.interviewquestion.R
import com.example.interviewquestion.databinding.ActivityQuestionAnswerDescriptionBinding
import com.example.interviewquestion.db.AppDatabase
import com.example.interviewquestion.db.AppRepository
import com.example.interviewquestion.factory.DbFactory
import com.example.interviewquestion.model.MarkedAsReadQues
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.SaveForLaterQues
import com.example.interviewquestion.viewModel.DbViewModel
import kotlin.properties.Delegates

class QuestionAnswerDescriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionAnswerDescriptionBinding
    private lateinit var viewModel: DbViewModel
    private lateinit var saveForLaterData: SaveForLaterQues
    private lateinit var markedAsReadData: MarkedAsReadQues
    private lateinit var myMenu: Menu
    private var isSaveOrMarkedAsRead by Delegates.notNull<Boolean>()
    private var tipsList = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question_answer_description)
        val tab = intent.getStringExtra(Constant.TAB_NAME)
        if (tab == "TIPS"){
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
            binding.webView.loadDataWithBaseURL(null, saveForLaterData?.Answer!!, "text/html","utf-8", null)
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
            binding.tvAnswer.text = HtmlCompat.fromHtml(saveForLaterData?.Answer!!, 0)
        }

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
                viewModel.saveForLater(saveForLaterData)
                viewModel.deleteQuestionAnswer(QuestionAnswer(saveForLaterData.SrNo, saveForLaterData.isHtmlTag, saveForLaterData.quesType, saveForLaterData.Question, saveForLaterData.Answer))
            }
            R.id.item_save ->{
                viewModel.saveMarkedAsRead(markedAsReadData)
                viewModel.deleteQuestionAnswer(QuestionAnswer(markedAsReadData.SrNo, markedAsReadData.isHtmlTag, markedAsReadData.quesType, markedAsReadData.Question, markedAsReadData.Answer))
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
    }
}