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
import com.example.snapgram.Utils.POST
import com.example.snapgram.Utils.USER_NODE
import com.example.snapgram.databinding.PostRvBinding
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.ktx.auth
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
                // Handle failure
            }

        // Set post data
        Glide.with(context)
            .load(post.postUrl)
            .placeholder(R.drawable.loading)
            .into(holder.binding.mainImage)

        val text = TimeAgo.using(postList[position].time!!.toLong())
        holder.binding.postedTime.text = text
        holder.binding.caption.text = post.caption
        holder.binding.countLikes.text = "${post.likes} likes"

        // Check if the current user has liked the post
        checkIfUserLikedPost(post, holder)

        // Handle like/unlike button click
        holder.binding.likeButton.setOnClickListener {
            if (post.likedByUser) {
                // User is unliking the post
                post.likes -= 1
                post.likedByUser = false
                holder.binding.countLikes.text = "${post.likes} likes"
                holder.binding.likeButton.setImageResource(R.drawable.like) // Set to outline or empty heart icon
                updateLikesInFirestore(post, false)
            } else {
                // User is liking the post
                post.likes += 1
                post.likedByUser = true
                holder.binding.countLikes.text = "${post.likes} likes"
                holder.binding.likeButton.setImageResource(R.drawable.heart) // Set to filled heart icon
                updateLikesInFirestore(post, true)
            }
        }

        // Handle share button click
        holder.binding.shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, post.postUrl)
            context.startActivity(Intent.createChooser(intent, "Share Post"))
        }
    }

    private fun checkIfUserLikedPost(post: Post, holder: ViewHolder) {
        val currentUserId = Firebase.auth.currentUser!!.uid
        val likesCollection = Firebase.firestore.collection(POST)
            .document(post.uid.toString())
            .collection("likes")

        likesCollection.document(currentUserId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    post.likedByUser = true
                    holder.binding.likeButton.setImageResource(R.drawable.heart) // Set to filled heart icon
                    holder.binding.countLikes.text = "${post.likes} likes"
                } else {
                    post.likedByUser = false
                    holder.binding.likeButton.setImageResource(R.drawable.like) // Set to outline or empty heart icon
                    holder.binding.countLikes.text = "${post.likes} likes"
                }
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    private fun updateLikesInFirestore(post: Post, isLiked: Boolean) {
        val postDocument = Firebase.firestore.collection(POST)
            .document(post.uid.toString())
        val currentUserId = Firebase.auth.currentUser!!.uid

        Firebase.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postDocument)
            val newLikesCount = if (isLiked) snapshot.getLong("likes")!! + 1 else snapshot.getLong("likes")!! - 1
            transaction.update(postDocument, "likes", newLikesCount)

            val likesCollection = postDocument.collection("likes")
            if (isLiked) {
                likesCollection.document(currentUserId).set(mapOf("timestamp" to System.currentTimeMillis()))
            } else {
                likesCollection.document(currentUserId).delete()
            }
        }.addOnFailureListener { exception ->
            // Handle error
            exception.printStackTrace()
        }
    }
}
