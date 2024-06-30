package com.example.snapgram.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapgram.Models.User
import com.example.snapgram.R
import com.example.snapgram.Utils.FOLLOW
import com.example.snapgram.databinding.SearchRvBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchAdapter(var context: Context, var userList: ArrayList<User>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: SearchRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var binding = SearchRvBinding.inflate(LayoutInflater.from(context), p0, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Glide.with(context).load(userList.get(p1).image).placeholder(R.drawable.user)
            .into(p0.binding.profileImage)
        p0.binding.profileName.text = userList.get(p1).name
        p0.binding.profileUsername.text = userList.get(p1).username
        var isfollow = false
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW)
            .whereEqualTo("email", userList.get(p1).email).get().addOnSuccessListener {
            if (it.documents.size == 0) {
                isfollow = false
            } else {
                p0.binding.followButton.text = "Following"
                isfollow = true
            }
        }
        p0.binding.followButton.setOnClickListener {
            if (isfollow) {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW)
                    .whereEqualTo("email", userList.get(p1).email).get().addOnSuccessListener {
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOW).document(it.documents.get(0).id).delete()
                        p0.binding.followButton.text="Follow"
                        isfollow = false
                }
            } else {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW).document()
                    .set(userList.get(p1))
                p0.binding.followButton.text = "Following"
                isfollow = true
            }
        }
    }
}