package com.example.snapgram.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapgram.Models.User
import com.example.snapgram.R
import com.example.snapgram.databinding.FollowRvBinding

class FollowRvAdapter(var context: Context, var followList: ArrayList<User>): RecyclerView.Adapter<FollowRvAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: FollowRvBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var binding=FollowRvBinding.inflate(LayoutInflater.from(context), p0, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return followList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Glide.with(context).load(followList.get(p1).image).placeholder(R.drawable.user).into(p0.binding.profileImage)
        p0.binding.name.text=followList.get(p1).name
    }
}