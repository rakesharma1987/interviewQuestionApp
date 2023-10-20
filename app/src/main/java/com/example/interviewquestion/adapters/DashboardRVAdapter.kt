package com.example.interviewquestion.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewquestion.R
import com.example.interviewquestion.databinding.LayoutDashboardItemBinding
import com.example.interviewquestion.interfaces.OnItemClickListener
import com.example.interviewquestion.model.TechnologyName
import com.example.interviewquestion.views.DashboardActivity

class DashboardRVAdapter(private var techNameList: List<String>, private var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<DashboardRVAdapter.MyViewHolder>() {

    inner class MyViewHolder(val itemDashboardItemBinding: LayoutDashboardItemBinding?): RecyclerView.ViewHolder(itemDashboardItemBinding!!.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var myViewHolder: LayoutDashboardItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_dashboard_item, parent, false)
        return MyViewHolder(myViewHolder)
    }

    override fun getItemCount(): Int {
        return techNameList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var techName = techNameList[position].toString()
        holder.itemDashboardItemBinding!!.itemName.text = techName
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick()
        }
    }
}