package com.example.snapgram.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snapgram.Post.PostActivity
import com.example.snapgram.Post.ReelActivity
import com.example.snapgram.R
import com.example.snapgram.databinding.FragmentPostBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PostFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = FragmentPostBinding.inflate(inflater, container, false)

        binding.post.setOnClickListener {
            activity?.startActivity(Intent(requireContext(),PostActivity::class.java))
            activity?.finish()
        }
        binding.reel.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), ReelActivity::class.java))
        }
        return binding.root
    }

    companion object {

    }
}