package com.example.snapgram.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgram.Models.Post
import com.example.snapgram.databinding.TimelineRvDesignBinding
import com.squareup.picasso.Picasso

class TimelineRvAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<TimelineRvAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: TimelineRvDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var binding = TimelineRvDesignBinding.inflate(LayoutInflater.from(context), p0, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Picasso.get().load(postList.get(p1).postUrl).into(p0.binding.postImage)
    }
}