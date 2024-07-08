package com.example.snapgram.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapgram.Models.User
import com.example.snapgram.R
import com.example.snapgram.Utils.FOLLOW
import com.example.snapgram.databinding.SearchRvBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchAdapter(private val context: Context, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SearchRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]

        // Load user profile image
        Glide.with(context)
            .load(user.image)
            .placeholder(R.drawable.user)
            .into(holder.binding.profileImage)

        holder.binding.profileName.text = user.name
        holder.binding.profileUsername.text = user.username

        // Check if current user is already following this user
        checkFollowStatus(user.name!!, holder)

        // Set click listener for follow button
        holder.binding.followButton.setOnClickListener {
            toggleFollowStatus(user, holder)
        }
    }

    private fun checkFollowStatus(uid: String, holder: ViewHolder) {
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // User is followed
                    holder.binding.followButton.text = "Following"
                    holder.binding.followButton.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.button_grey)
                    )
                } else {
                    // User is not followed
                    holder.binding.followButton.text = "Follow"
                    holder.binding.followButton.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.blue)
                    )
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
                exception.printStackTrace()
            }
    }

    private fun toggleFollowStatus(user: User, holder: ViewHolder) {
        val userFollowRef = Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW)
            .document(user.name!!)

        userFollowRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Already following, so unfollow
                    userFollowRef.delete()
                        .addOnSuccessListener {
                            holder.binding.followButton.text = "Follow"
                        }
                } else {
                    // Not following, so follow now
                    userFollowRef.set(user)
                        .addOnSuccessListener {
                            holder.binding.followButton.text = "Following"
                            holder.binding.followButton.setBackgroundColor(
                                ContextCompat.getColor(context, R.color.button_grey)
                            )
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
                exception.printStackTrace()
            }
    }
}
