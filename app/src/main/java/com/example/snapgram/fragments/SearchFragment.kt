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
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchAdapter
    private val userList = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchAdapter(requireContext(), userList)
        binding.rv.adapter = adapter

        loadAllUsers()

        binding.searchButton.setOnClickListener {
            val text = binding.searchView.text.toString().trim()
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
                userList.clear()
                val currentUserID = Firebase.auth.currentUser!!.uid

                for (i in result.documents){
                    if(i.id.toString().equals(Firebase.auth.currentUser!!.uid.toString())){

                    }else{
                        var user: User = i.toObject<User>()!!
                        tempList.add(user)
                    }
                }


                userList.addAll(tempList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                exception.printStackTrace()
            }
    }

    private fun searchUsersByName(name: String) {
        val currentUserID = Firebase.auth.currentUser!!.uid
        Firebase.firestore.collection(USER_NODE)
            .orderBy("name")
            .startAt(name)
            .endAt(name + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                val tempList = ArrayList<User>()
                for (document in result.documents) {
                    val user = document.toObject(User::class.java)
                    if (user != null && document.id != currentUserID) {
                        tempList.add(user)
                    }
                }

                userList.clear()
                userList.addAll(tempList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                exception.printStackTrace()
            }
    }
}
