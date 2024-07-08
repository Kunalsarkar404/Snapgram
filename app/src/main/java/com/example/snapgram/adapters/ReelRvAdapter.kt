package com.example.snapgram.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgram.Models.Reel
import com.example.snapgram.databinding.ReelRvDesignBinding

class ReelRvAdapter(var context: Context, var reelList: ArrayList<Reel>) :
    RecyclerView.Adapter<ReelRvAdapter.ViewHolder>() {

    private var currentPlayingVideo: VideoView? = null
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
        val reel = reelList[p1]

        // Set up the VideoView to play the reel video
        val videoUri = Uri.parse(reel.reelUrl)
        p0.binding.videoView.setVideoURI(videoUri)
        p0.binding.videoView.setOnPreparedListener { mediaPlayer ->
            p0.binding.progressBar.visibility = View.GONE
            mediaPlayer.start()
            mediaPlayer.pause()
            mediaPlayer.setLooping(true) // Loop the video if needed
        }
        // Show the progress bar until the video is ready to play
        p0.binding.progressBar.visibility = View.VISIBLE
        p0.binding.videoView.setOnErrorListener { _, _, _ ->
            p0.binding.progressBar.visibility = View.GONE
            true
        }
        // Handle click events to play/pause the video
        p0.binding.videoView.setOnClickListener {
            if (p0.binding.videoView.isPlaying) {
                p0.binding.videoView.pause()
            } else {
                currentPlayingVideo?.pause()
                currentPlayingVideo = p0.binding.videoView
                p0.binding.videoView.start()
            }
        }
    }

    fun stopAllVideos() {
        currentPlayingVideo?.pause()
    }
}


