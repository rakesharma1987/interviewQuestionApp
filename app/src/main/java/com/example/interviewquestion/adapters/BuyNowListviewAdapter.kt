package com.example.interviewquestion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.interviewquestion.databinding.CustomListBinding.*

class BuyNowListviewAdapter(private val text: Array<String>): BaseAdapter() {
    override fun getCount(): Int {
        return text.size
    }

    override fun getItem(p0: Int): Any {
        return ""
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val binding = inflate(LayoutInflater.from(viewGroup!!.context), viewGroup, false)
        binding.tvTxt.text = text[position]
        return binding.root
    }
}