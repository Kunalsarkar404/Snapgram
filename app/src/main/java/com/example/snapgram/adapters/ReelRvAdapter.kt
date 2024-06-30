package com.example.snapgram.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.snapgram.Models.Reel
import com.example.snapgram.databinding.ReelRvDesignBinding

class ReelRvAdapter(var context: Context, var reelList: ArrayList<Reel>) :
    RecyclerView.Adapter<ReelRvAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: ReelRvDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var binding = ReelRvDesignBinding.inflate(LayoutInflater.from(context), p0, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Glide.with(context).load(reelList.get(p1).reelUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(p0.binding.postReel)
    }


}


