package com.example.snapgram.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgram.Models.Reel
import com.example.snapgram.R
import com.example.snapgram.databinding.ReelDgBinding
import com.squareup.picasso.Picasso

class ReelAdapter(var context: Context, var reelList: ArrayList<Reel>) :RecyclerView.Adapter<ReelAdapter.ViewHolder>(){

    inner class ViewHolder(var binding: ReelDgBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var binding = ReelDgBinding.inflate(LayoutInflater.from(context), p0, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Picasso.get().load(reelList.get(p1).profileLink).placeholder(R.drawable.user).into(p0.binding.profileImage)
        p0.binding.caption.setText(reelList.get(p1).caption)
        p0.binding.videoView.setVideoPath(reelList.get(p1).reelUrl)
        p0.binding.videoView.setOnPreparedListener{
            p0.binding.progressBar.visibility= View.GONE
            p0.binding.videoView.start()
        }
    }


}