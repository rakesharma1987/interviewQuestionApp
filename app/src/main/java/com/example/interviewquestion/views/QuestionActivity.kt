package com.example.interviewquestion.views

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interviewquestion.util.Constant
import com.example.interviewquestion.R
import com.example.interviewquestion.adapters.QuestionActivityAdapter
import com.example.interviewquestion.databinding.ActivityQuestionBinding
import com.example.interviewquestion.db.AppDatabase
import com.example.interviewquestion.db.AppRepository
import com.example.interviewquestion.factory.DbFactory
import com.example.interviewquestion.interfaces.OnQuestionClickListener
import com.example.interviewquestion.model.BookmarkQuestion
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.QuestionAnswerList
import com.example.interviewquestion.model.ReadQuestion
import com.example.interviewquestion.util.MyPreferences
import com.example.interviewquestion.viewModel.DbViewModel
import com.google.gson.Gson


class QuestionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityQuestionBinding
    private lateinit var list: QuestionAnswerList
    private lateinit var viewModel2: DbViewModel
    private var isSaveOrMarkedOpen: Boolean = false
    private lateinit var allDataList:ArrayList<QuestionAnswer>
    private var oldFirstPos = -1
    private  var oldLastPos:kotlin.Int = -1
    private  var totalItemsViewed:kotlin.Int = 0
    private lateinit var layoutmanager: LinearLayoutManager
    var tempList = ArrayList<QuestionAnswer>()
    lateinit var questionAnswer: QuestionAnswer
    private var TAB_NAME = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question)
        binding.rvQuestionList.alpha
        if (MyPreferences.isPurchased()) binding.btnSubscription.text = getString(R.string.btn_txt_premium)
        layoutmanager = LinearLayoutManager(this)
        binding.rvQuestionList.layoutManager = layoutmanager

        allDataList = ArrayList<QuestionAnswer>()

        val dao = AppDatabase.getInstance(this).dao
        val factory2 = DbFactory(AppRepository(dao))
        viewModel2 = ViewModelProvider(this, factory2)[DbViewModel::class.java]

        list = QuestionAnswerList()
        viewModel2.getAllQuestionAnswerData.observe(this, Observer {
            allDataList.clear()
            if (it.isNotEmpty()) {
                    allDataList.addAll(it)
                    setUpRecyclerView(it)
                    binding.tvOopsMoment.visibility = View.GONE
                } else {
                    binding.tvOopsMoment.visibility = View.VISIBLE
                }
        })

        viewModel2.getTipsQuestion.observe(this, Observer {
            questionAnswer = it[0]
//            for (it in it.listIterator()){
////                tempList.add(it)
//            }
        })

        binding.btnAllQuestion.setOnClickListener(this)
        binding.btnSaveForLatter.setOnClickListener(this)
        binding.btnMarkedAsRead.setOnClickListener(this)
        binding.btnTips.setOnClickListener(this)
        binding.btnSubscription.setOnClickListener(this)

    }

    private fun setUpRecyclerView(tempList: List<QuestionAnswer>){
        val questionAnswerAdapter = QuestionActivityAdapter(this, tempList, object: OnQuestionClickListener{
            override fun onClick(position: Int, item: QuestionAnswer) {
                val lList = ArrayList<QuestionAnswer>()
                lList.addAll(tempList)
                lList.subList(0, position).clear()
                val intent = Intent(this@QuestionActivity, QuestionAnswerDescriptionActivity::class.java)
                val saveForLaterData = BookmarkQuestion(item.SrNo, item.isHtmlTag, item.quesType, item.Question, item.Answer)
                val markedAsReadData = ReadQuestion(item.SrNo, item.isHtmlTag, item.quesType, item.Question, item.Answer)
                intent.putExtra(Constant.QUESTION_ANSWER, Gson().toJson(item))
                intent.putExtra(Constant.SAVE_FOR_LATER, Gson().toJson(saveForLaterData))
                intent.putExtra(Constant.MARKED_AS_READ, Gson().toJson(markedAsReadData))
                intent.putExtra(Constant.IS_SAVE_OR_MARKED_AS_READ_DATA, isSaveOrMarkedOpen)
                intent.putExtra(Constant.TAB_NAME, TAB_NAME)
                intent.putExtra(Constant.CLICKED_POSITION, position)
                startActivity(intent)
            }

        })
        binding.rvQuestionList.adapter = questionAnswerAdapter
        questionAnswerAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_all_question ->{
                binding.btnSubscription.visibility = View.VISIBLE
                binding.btnAllQuestion.setBackgroundColor(applicationContext.getColor(R.color.purple_500))
                binding.btnSaveForLatter.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                binding.btnMarkedAsRead.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                binding.btnTips.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))

                isSaveOrMarkedOpen = false
                TAB_NAME = Constant.TAB_ALL
                allDataList.clear()
                viewModel2.getAllQuestionAnswerData.observe(this, Observer {
                    if (it.isNotEmpty()) {
                        allDataList.addAll(it)
                        setUpRecyclerView(it)
                        binding.tvOopsMoment.visibility = View.GONE
                    } else {
                        binding.tvOopsMoment.visibility = View.VISIBLE
                    }
                })
            }

            R.id.btn_save_for_latter ->{
                binding.btnSubscription.visibility = View.GONE
                binding.btnSaveForLatter.setBackgroundColor(applicationContext.getColor(R.color.purple_500))
                binding.btnMarkedAsRead.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                binding.btnTips.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                binding.btnAllQuestion.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))

                isSaveOrMarkedOpen = true
                TAB_NAME = Constant.TAB_BOOKMARKS
                viewModel2.getAllBookmarkQuestion.observe(this, Observer {
                    val list = ArrayList<BookmarkQuestion>()
                    for (data in it.listIterator()){
                        list.add(data)
                    }
                    val questionAnswerList: ArrayList<QuestionAnswer> = list.map {
                        QuestionAnswer(it.SrNo, it.isHtmlTag, it.quesType, it.Question, it.Answer)
                    } as ArrayList<QuestionAnswer>
                    setUpRecyclerView(questionAnswerList)
                })
            }

            R.id.btn_marked_as_read ->{
                binding.btnSubscription.visibility = View.GONE
                binding.btnSaveForLatter.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                binding.btnMarkedAsRead.setBackgroundColor(applicationContext.getColor(R.color.purple_500))
                binding.btnTips.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                binding.btnAllQuestion.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))

                isSaveOrMarkedOpen = true
                TAB_NAME = Constant.TAB_READ
                viewModel2.getAllReadQuestion.observe(this, Observer {
                    val list = ArrayList<ReadQuestion>()
                    for (data in it.listIterator()){
                        list.add(data)
                    }
                    val questionAnswerList: ArrayList<QuestionAnswer> = list.map {
                        QuestionAnswer(it.SrNo, it.isHtmlTag, it.quesType, it.Question, it.Answer)
                    } as ArrayList<QuestionAnswer>
                    setUpRecyclerView(questionAnswerList)
                })
            }

            R.id.btn_tips ->{
                binding.btnSubscription.visibility = View.GONE
                binding.btnSaveForLatter.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                binding.btnMarkedAsRead.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                binding.btnTips.setBackgroundColor(applicationContext.getColor(R.color.purple_500))
                binding.btnAllQuestion.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                isSaveOrMarkedOpen = true
                TAB_NAME = "Tips"
                var _intent = Intent(this, TipsActivity::class.java)
                _intent.putExtra(Constant.TAB_NAME, TAB_NAME)
                _intent.putExtra(Constant.TIPS, Gson().toJson(questionAnswer))
                startActivity(_intent)

                binding.btnSubscription.visibility = View.VISIBLE
                binding.btnAllQuestion.setBackgroundColor(applicationContext.getColor(R.color.purple_500))
                binding.btnSaveForLatter.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                binding.btnMarkedAsRead.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))
                binding.btnTips.setBackgroundColor(applicationContext.getColor(R.color.tab_default_color))

            }

            R.id.btn_subscription ->{
                startActivity(
                    Intent(
                        this@QuestionActivity,
                        BuyNowActivity::class.java
                    )
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dahboard, menu)
        val searchItem = menu!!.findItem(R.id.item_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText!!)
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_rate ->{
                val appPackageName = packageName
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }
            R.id.item_share ->{
                val intent= Intent()
                intent.action=Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT,resources.getString(R.string.txt_share))
                intent.type="text/plain"
                startActivity(Intent.createChooser(intent,"Share To:"))
            }
            R.id.item_other_app ->{
                val uri = Uri.parse(resources.getString(R.string.txt_other_app))
                val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
                try {
                    startActivity(myAppLinkToMarket)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "Impossible to find an application for the market", Toast.LENGTH_LONG).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun filter(text: String){
        val filteredList = ArrayList<QuestionAnswer>()
        for (item in allDataList){
            if (item.Question.lowercase().contains(text.lowercase())){
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(this, "No Data Found.", Toast.LENGTH_SHORT).show()
        }else{
            setUpRecyclerView(filteredList)
        }

    }

//    override fun onResume() {
//        super.onResume()
////        binding.rvQuestionList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
////            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
////                super.onScrolled(recyclerView, dx, dy)
////                val currFirstPos = layoutmanager!!.findFirstCompletelyVisibleItemPosition()
////                val currLastPos = layoutmanager.findLastCompletelyVisibleItemPosition()
////                val totalItemCount = layoutmanager.itemCount
////                if (oldFirstPos === -1) {
////                    totalItemsViewed += currLastPos - currFirstPos + 1
////                } else {
////                    if (dy > 0) {
////                        totalItemsViewed += Math.abs(currLastPos - oldLastPos)
////                    } else {
////                        totalItemsViewed -= Math.abs(oldLastPos - currLastPos)
////                    }
////                }
////                oldLastPos = currLastPos
////                oldFirstPos = currFirstPos
////                Log.e("totalItemsViewed", totalItemsViewed.toString())
////                if (totalItemsViewed == 25){
////                    binding.rvQuestionList.suppressLayout(true)
////                    if (!MyPreferences.isPurchased()){
////                            val dialog = AlertDialog.Builder(this@QuestionActivity)
////                            dialog.setCancelable(false)
////                            dialog.setTitle(R.string.app_name)
////                            dialog.setMessage(getString(R.string.msg_subscription))
////                            dialog.setPositiveButton(
////                                "SUBSCRIBE",
////                                object : DialogInterface.OnClickListener {
////                                    override fun onClick(dialog: DialogInterface?, which: Int) {
////                                        dialog!!.dismiss()
////                                        startActivity(
////                                            Intent(
////                                                this@QuestionActivity,
////                                                BillingActivity::class.java
////                                            )
////                                        )
////                                    }
////
////                                })
////                            dialog.setNegativeButton(
////                                "Cancel",
////                                object : DialogInterface.OnClickListener {
////                                    override fun onClick(dialog: DialogInterface?, which: Int) {
////                                        dialog!!.dismiss()
////                                    }
////
////                                })
////                        dialog.create().show()
////                    }
////                }
////            }
////        })
//    }

    override fun attachBaseContext(newBase: Context?) {
        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }
}