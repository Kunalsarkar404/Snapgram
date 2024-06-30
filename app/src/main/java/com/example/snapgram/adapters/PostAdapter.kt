package com.example.snapgram.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapgram.Models.Post
import com.example.snapgram.Models.User
import com.example.snapgram.R
import com.example.snapgram.Utils.USER_NODE
import com.example.snapgram.databinding.PostRvBinding
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class PostAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: PostRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]

        // Fetch user data
        Firebase.firestore.collection(USER_NODE)
            .document(post.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject<User>()
                if (user != null) {
                    Glide.with(context)
                        .load(user.image)
                        .placeholder(R.drawable.user)
                        .into(holder.binding.profileImage)
                    holder.binding.profileName.text = user.name
                }
            }
            .addOnFailureListener {

            }

        // Set post data
        Glide.with(context)
            .load(post.postUrl)
            .placeholder(R.drawable.loading)
            .into(holder.binding.mainImage)
        val text = TimeAgo.using(postList.get(position).time!!.toLong())
        holder.binding.postedTime.text = text
        holder.binding.caption.text = post.caption
        holder.binding.likeButton.setOnClickListener {
            holder.binding.likeButton.setImageResource(R.drawable.heart)
        }
        holder.binding.shareButton.setOnClickListener {
            var i = Intent(android.content.Intent.ACTION_SEND)
            i.type="text/plain"
            i.putExtra(Intent.EXTRA_TEXT, postList.get(position).postUrl)
            context.startActivity(i)
        }
    }
}
