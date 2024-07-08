package com.example.snapgram.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapgram.Models.Post
import com.example.snapgram.Models.User
import com.example.snapgram.R
import com.example.snapgram.Utils.FOLLOW
import com.example.snapgram.Utils.POST
import com.example.snapgram.Utils.USER_NODE
import com.example.snapgram.adapters.FollowRvAdapter
import com.example.snapgram.adapters.PostAdapter
import com.example.snapgram.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var postList = ArrayList<Post>()
    private lateinit var postAdapter: PostAdapter
    private var followList = ArrayList<User>()
    private lateinit var followRvAdapter: FollowRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize adapters
        postAdapter = PostAdapter(requireContext(), postList)
        followRvAdapter = FollowRvAdapter(requireContext(), followList)

        // Set up RecyclerViews
        binding.postRv.layoutManager = LinearLayoutManager(requireContext())
        binding.postRv.adapter = postAdapter

        binding.followRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.followRv.adapter = followRvAdapter

        // Set the toolbar
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar2)

        // Fetch data
        fetchCurrentUser()
        fetchFollowedUsers()
        fetchPosts()

        return binding.root
    }

    private fun fetchCurrentUser() {
        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject<User>()
                user?.let {
                    if (!it.image.isNullOrEmpty()) {
                        Picasso.get().load(it.image).into(binding.profileImage)
                    }else{
                        Picasso.get().load(R.drawable.user).into(binding.profileImage)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
                exception.printStackTrace()
            }
    }

    private fun fetchFollowedUsers() {
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW).get()
            .addOnSuccessListener { documents ->
                val tempList = ArrayList<User>()
                for (document in documents) {
                    val user = document.toObject<User>()
                    user?.let { tempList.add(it) }
                }
                followList.clear()
                followList.addAll(tempList)
                followRvAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
                exception.printStackTrace()
            }
    }

    private fun fetchPosts() {
        Firebase.firestore.collection(POST).get()
            .addOnSuccessListener { documents ->
                val tempList = ArrayList<Post>()
                for (document in documents) {
                    val post = document.toObject<Post>()
                    post?.let { tempList.add(it) }
                }
                postList.clear()
                postList.addAll(tempList)
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
                exception.printStackTrace()
            }
    }
}
