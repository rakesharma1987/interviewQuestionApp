package com.example.interviewquestion.views

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewquestion.Constant
import com.example.interviewquestion.R
import com.example.interviewquestion.adapters.QuestionActivityAdapter
import com.example.interviewquestion.databinding.ActivityQuestionBinding
import com.example.interviewquestion.db.AppDatabase
import com.example.interviewquestion.db.AppRepository
import com.example.interviewquestion.db.MarkedAsReadQues
import com.example.interviewquestion.db.SaveForLaterQues
import com.example.interviewquestion.factory.DbFactory
import com.example.interviewquestion.interfaces.OnQuestionClickListener
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.QuestionAnswerList
import com.example.interviewquestion.util.MyPreferences
import com.example.interviewquestion.viewModel.DbViewModel


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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question)
        MyPreferences.init(this)
        layoutmanager = LinearLayoutManager(this)
        binding.rvQuestionList.layoutManager = layoutmanager

        allDataList = ArrayList<QuestionAnswer>()

        val dao = AppDatabase.getInstance(this).dao
        val factory2 = DbFactory(AppRepository(dao))
        viewModel2 = ViewModelProvider(this, factory2)[DbViewModel::class.java]

        list = QuestionAnswerList()
        viewModel2.getAllQuestionAnswerData.observe(this, Observer {
            if (it.isNotEmpty()){
            allDataList.addAll(it)
            setUpRecyclerView(it)
                binding.tvOopsMoment.visibility = View.GONE
            }else{
                binding.tvOopsMoment.visibility = View.VISIBLE
            }
        })

        binding.btnAllQuestion.setOnClickListener(this)
        binding.btnSaveForLatter.setOnClickListener(this)
        binding.btnMarkedAsRead.setOnClickListener(this)
        binding.btnTips.setOnClickListener(this)

    }

    private fun setUpRecyclerView(tempList: List<QuestionAnswer>){
        var questionAnswerAdapter = QuestionActivityAdapter(this, tempList, object: OnQuestionClickListener{
            override fun onClick(position: Int, item: QuestionAnswer) {
                var intent = Intent(this@QuestionActivity, QuestionAnswerDescriptionActivity::class.java)
                val saveForLaterData = SaveForLaterQues(item.SrNo, item.isHtmlTag, item.quesType, item.Question, item.Answer)
                val markedAsReadData = MarkedAsReadQues(item.SrNo, item.isHtmlTag, item.quesType, item.Question, item.Answer)
                intent.putExtra(Constant.QUESTION_ANSWER, item)
                intent.putExtra(Constant.SAVE_FOR_LATER, saveForLaterData)
                intent.putExtra(Constant.MARKED_AS_READ, markedAsReadData)
                intent.putExtra(Constant.IS_SAVE_OR_MARKED_AS_READ_DATA, isSaveOrMarkedOpen)
                startActivity(intent)
            }

        })
        binding.rvQuestionList.adapter = questionAnswerAdapter
        questionAnswerAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_all_question ->{
                binding.btnAllQuestion.setBackgroundColor(applicationContext.getColor(R.color.purple_500))
                binding.btnSaveForLatter.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))
                binding.btnMarkedAsRead.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))
                binding.btnTips.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))

                isSaveOrMarkedOpen = false
                viewModel2.getAllQuestionAnswerData.observe(this, Observer {
                    if (it.isNotEmpty()){
                        setUpRecyclerView(it)
                    }
                })
            }

            R.id.btn_save_for_latter ->{
                binding.btnSaveForLatter.setBackgroundColor(applicationContext.getColor(R.color.purple_500))
                binding.btnMarkedAsRead.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))
                binding.btnTips.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))
                binding.btnAllQuestion.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))

                isSaveOrMarkedOpen = true
                viewModel2.getAllSaveForLaterData.observe(this, Observer {
                    var list = ArrayList<QuestionAnswer>()
                    for (it in it.listIterator()){
                        list.add(it)
                    }
                    setUpRecyclerView(list)
                })
            }

            R.id.btn_marked_as_read ->{
                binding.btnSaveForLatter.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))
                binding.btnMarkedAsRead.setBackgroundColor(applicationContext.getColor(R.color.purple_500))
                binding.btnTips.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))
                binding.btnAllQuestion.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))

                isSaveOrMarkedOpen = true
                viewModel2.getAllMarkedAsReadData.observe(this, Observer {
                    var list = ArrayList<QuestionAnswer>()
                    for (it in it.listIterator()){
                        list.add(it)
                    }
                    setUpRecyclerView(list)
                })
            }

            R.id.btn_tips ->{
                binding.btnSaveForLatter.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))
                binding.btnMarkedAsRead.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))
                binding.btnTips.setBackgroundColor(applicationContext.getColor(R.color.purple_500))
                binding.btnAllQuestion.setBackgroundColor(applicationContext.getColor(R.color.card_foreground_color))

                Toast.makeText(this, "Not implemented yet.", Toast.LENGTH_SHORT).show()
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
                intent.putExtra(Intent.EXTRA_TEXT,"Hey Check out this Great app:")
                intent.type="text/plain"
                startActivity(Intent.createChooser(intent,"Share To:"))
            }
            R.id.item_other_app ->{
                val uri = Uri.parse("https://play.google.com/store/apps/developer?id=Suvarna+Tech+Lab")
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

    override fun onResume() {
        super.onResume()
        binding.rvQuestionList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currFirstPos = layoutmanager!!.findFirstCompletelyVisibleItemPosition()
                val currLastPos = layoutmanager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutmanager.itemCount
                if (oldFirstPos === -1) {
                    totalItemsViewed += currLastPos - currFirstPos + 1
                } else {
                    if (dy > 0) {
                        totalItemsViewed += Math.abs(currLastPos - oldLastPos)
                    } else {
                        totalItemsViewed -= Math.abs(oldLastPos - currLastPos)
                    }
                }
                oldLastPos = currLastPos
                oldFirstPos = currFirstPos
                Log.e("totalItemsViewed", totalItemsViewed.toString())
                if (totalItemsViewed >= 20){
                    if (!MyPreferences.isPurchased()){
                        val dialog = AlertDialog.Builder(this@QuestionActivity)
                        dialog.setCancelable(false)
                        dialog.setTitle(R.string.app_name)
                        dialog.setMessage(getString(R.string.msg_subscription))
                        dialog.setPositiveButton("SUBSCRIBE", object: DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                startActivity(Intent(this@QuestionActivity, BillingActivity::class.java))
                                dialog!!.dismiss()
                            }

                        })
                        dialog.show()
                    }
                }
            }
        })
    }
}