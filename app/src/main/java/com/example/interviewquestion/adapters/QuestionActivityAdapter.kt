package com.example.interviewquestion.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewquestion.R
import com.example.interviewquestion.databinding.LayoutDashboardItemBinding
import com.example.interviewquestion.databinding.LayoutQuestionListBinding
import com.example.interviewquestion.interfaces.OnItemClickListener
import com.example.interviewquestion.interfaces.OnQuestionClickListener
import com.example.interviewquestion.model.QuestionAnswer
import com.example.interviewquestion.model.QuestionAnswerList

class QuestionActivityAdapter(private var context: Context, private var list: List<QuestionAnswer>, private var onItemClickListener: OnQuestionClickListener): RecyclerView.Adapter<QuestionActivityAdapter.MyViewHolder>() {

    inner class MyViewHolder(val itemDashboardItemBinding: LayoutQuestionListBinding?): RecyclerView.ViewHolder(itemDashboardItemBinding!!.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var myViewHolder: LayoutQuestionListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_question_list, parent, false)
        return MyViewHolder(myViewHolder)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var questionAnswer = list[position]
        holder.itemDashboardItemBinding!!.tvSrnoValue.text = questionAnswer.SrNo.toString()
        holder.itemDashboardItemBinding.tvQuesTitleValue!!.text = questionAnswer.Question
//        holder.itemDashboardItemBinding.tvPriorityValue!!.text = questionAnswer.quesType
        if (questionAnswer.quesType == "Low"){
            holder.itemDashboardItemBinding.tvPriorityValue!!.text = "L"
//            holder.itemDashboardItemBinding.tvPriorityValue!!.setTextColor(context.getColor(R.color.color_low))
            holder.itemDashboardItemBinding.tvPriorityValue.setBackgroundDrawable(context.getDrawable(R.drawable.drawable_low_priority))
        }else if (questionAnswer.quesType == "Medium"){
            holder.itemDashboardItemBinding.tvPriorityValue!!.text = "M"
//            holder.itemDashboardItemBinding.tvPriorityValue!!.setTextColor(context.getColor(R.color.color_medium))
            holder.itemDashboardItemBinding.tvPriorityValue.setBackgroundDrawable(context.getDrawable(R.drawable.drawable_medium_priority))
        }else{
            holder.itemDashboardItemBinding.tvPriorityValue!!.text = "H"
//            holder.itemDashboardItemBinding.tvPriorityValue!!.setTextColor(context.getColor(R.color.color_high))
            holder.itemDashboardItemBinding.tvPriorityValue.setBackgroundDrawable(context.getDrawable(R.drawable.drawable_high_priority))
        }
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(position, questionAnswer)
        }
    }
}