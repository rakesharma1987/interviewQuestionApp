package com.example.interviewquestion.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.interviewquestion.Constant
import com.example.interviewquestion.R
import com.example.interviewquestion.databinding.ActivityTipsBinding
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.util.MyPreferences
import com.google.gson.Gson
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type

class TipsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTipsBinding
    private var tipsList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = resources.getString(R.string.txt_tips)

        if (!MyPreferences.isPurchased()){
            BeautifulDialog.build(this)
                .title("Subscription", titleColor = R.color.purple_500)
                .description("For more tips subscribe now.",  color = R.color.purple_700)
                .type(type= BeautifulDialog.TYPE.INFO)
                .position(BeautifulDialog.POSITIONS.BOTTOM)
                .onPositive(text = "Subscribe", shouldIDismissOnClick = false, buttonBackgroundColor = R.color.purple_500) {
                    startActivity(Intent(this@TipsActivity, BillingActivity::class.java))
                    this.finish()
                }
                .onNegative(text = "Cancel", shouldIDismissOnClick = false, buttonBackgroundColor = R.color.purple_500) {
                    this.finish()
                }
                .setCancelable(false)


        }


        var intent = getIntent().getStringExtra(Constant.TIPS)
        var tipsQuestionAnswer = Gson().fromJson<QuestionAnswer>(intent, QuestionAnswer::class.java)

        binding.tvQuestion.text = tipsQuestionAnswer.Question

        var tipAnswer = tipsQuestionAnswer.Answer.split("\n").toList()
        for (tips in tipAnswer.listIterator()){
            tipsList.add(tips)
        }

        var tipAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tipsList)
        binding.rvTipsItem.adapter =tipAdapter
        tipAdapter.notifyDataSetChanged()


    }
}