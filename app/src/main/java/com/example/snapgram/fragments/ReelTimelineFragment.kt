package com.example.snapgram.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.snapgram.Models.Reel
import com.example.snapgram.Utils.REEL
import com.example.snapgram.adapters.ReelRvAdapter
import com.example.snapgram.databinding.FragmentReelTimelineBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ReelTimelineFragment : Fragment() {
    private lateinit var binding: FragmentReelTimelineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReelTimelineBinding.inflate(inflater, container, false)
        val reelList = ArrayList<Reel>()
        val adapter = ReelRvAdapter(requireContext(), reelList)
        binding.rv.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.rv.adapter = adapter

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + REEL).get()
            .addOnSuccessListener {
                val tempList = arrayListOf<Reel>()
                for (it in it.documents) {
                    val reel: Reel = it.toObject<Reel>()!!
                    tempList.add(reel)
                }
                reelList.addAll(tempList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("ReelTimelineFragment", "Error fetching reels: ", exception)
            }

        return binding.root
    }
}
