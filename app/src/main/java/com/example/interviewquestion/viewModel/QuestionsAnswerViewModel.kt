package com.example.interviewquestion.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.interviewquestion.model.QuestionAnswer

class QuestionsAnswerViewModel(private val list: ArrayList<QuestionAnswer>): ViewModel() {
    var tempList: MutableLiveData<ArrayList<QuestionAnswer>> = MutableLiveData()
    var quesAnswerList = ArrayList<QuestionAnswer>()
    var isTxtShowHide: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        false
    }

    fun questionBasedOnType(){
        quesAnswerList.clear()
        for (list in list.iterator()){
            quesAnswerList.add(list)
        }
        isTxtShowHide.value = quesAnswerList.size == 0

        tempList.value = quesAnswerList
    }
}