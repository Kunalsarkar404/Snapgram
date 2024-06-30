package com.example.snapgram.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapgram.Models.User
import com.example.snapgram.Utils.USER_NODE
import com.example.snapgram.adapters.SearchAdapter
import com.example.snapgram.databinding.FragmentSearchBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchAdapter
    private val userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchAdapter(requireContext(), userList)
        binding.rv.adapter = adapter

        loadAllUsers()

        binding.searchButton.setOnClickListener {
            val text = binding.searchView.text.toString()
            if (text.isNotEmpty()) {
                searchUsersByName(text)
            } else {
                loadAllUsers()
            }
        }

        return binding.root
    }

    private fun loadAllUsers() {
        Firebase.firestore.collection(USER_NODE).get()
            .addOnSuccessListener { result ->
                val tempList = ArrayList<User>()
                val currentUserID = Firebase.auth.currentUser!!.uid

                for (document in result.documents) {
                    val user = document.toObject<User>()
                    if (user != null && document.id != currentUserID) {
                        tempList.add(user)
                    }
                }

                userList.clear()
                userList.addAll(tempList)
                adapter.notifyDataSetChanged()
            }
    }

    private fun searchUsersByName(name: String) {
        Firebase.firestore.collection(USER_NODE)
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { result ->
                val tempList = ArrayList<User>()
                val currentUserID = Firebase.auth.currentUser!!.uid

                for (document in result.documents) {
                    val user = document.toObject<User>()
                    if (user != null && document.id != currentUserID) {
                        tempList.add(user)
                    }
                }

                userList.clear()
                userList.addAll(tempList)
                adapter.notifyDataSetChanged()
            }
    }
}
