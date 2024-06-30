package com.example.snapgram.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.snapgram.Models.User
import com.example.snapgram.R
import com.example.snapgram.Utils.USER_NODE
import com.example.snapgram.Utils.USER_PROFILE_FOLDER
import com.example.snapgram.Utils.uploadImage
import com.example.snapgram.adapters.ViewPagerAdapter
import com.example.snapgram.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso
import com.google.android.material.tabs.TabLayout

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadImage(it, USER_PROFILE_FOLDER) { imageUrl ->
                imageUrl?.let { url ->
                    user.image = url
                    binding.profileImage.setImageURI(uri)
                    // Update the user image URL in Firestore
                    Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
                        .update("image", url)
                        .addOnFailureListener { e ->
                            // Handle failure
                            e.printStackTrace()
                        }
                } ?: run {
                    // Handle the case where the upload failed and imageUrl is null
                    println("Image upload failed")
                }
            }
        } ?: run {
            // Handle case where uri is null
            println("Image selection failed or canceled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = User()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        viewPagerAdapter.addFragments(TimelineFragment(), "Post")
        viewPagerAdapter.addFragments(ReelTimelineFragment(), "Reels")
        viewPagerAdapter.addFragments(TagFragment(), "Tag")
        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        setupTabIcons()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileImage.setOnClickListener {
            launcher.launch("image/*")
        }
    }

    override fun onStart() {
        super.onStart()
        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val fetchedUser: User? = documentSnapshot.toObject<User>()
                fetchedUser?.let { user ->
                    binding.profileName.text = user.name
                    binding.username.text = user.username
                    if (!user.image.isNullOrEmpty()) {
                        Picasso.get().load(user.image).into(binding.profileImage)
                    }
                }
            }.addOnFailureListener { e ->
                // Handle failure
                e.printStackTrace()
            }
    }

    private fun setupTabIcons() {
        val tabIcons = listOf(
            R.drawable.ic_post,
            R.drawable.reel,
            R.drawable.ic_tag
        )

        for (i in tabIcons.indices) {
            val tab = binding.tabLayout.getTabAt(i)
            tab?.customView = LayoutInflater.from(context).inflate(R.layout.tab_icon, null).apply {
                findViewById<ImageView>(R.id.tab_icon).setImageResource(tabIcons[i])
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.isSelected = true
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.isSelected = false
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}
