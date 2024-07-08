package com.example.snapgram.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.snapgram.Models.User
import com.example.snapgram.R
import com.example.snapgram.SignUpActivity
import com.example.snapgram.Utils.FOLLOW
import com.example.snapgram.Utils.REEL
import com.example.snapgram.Utils.USER_NODE
import com.example.snapgram.Utils.USER_PROFILE_FOLDER
import com.example.snapgram.Utils.uploadImage
import com.example.snapgram.adapters.FollowRvAdapter
import com.example.snapgram.adapters.ViewPagerAdapter
import com.example.snapgram.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var followRvAdapter: FollowRvAdapter
    private var followList = ArrayList<User>()
    private lateinit var user: User

    private val launcher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uploadImage(it, USER_PROFILE_FOLDER) { imageUrl ->
                    imageUrl?.let { url ->
                        user.image = url
                        binding.profileImage.setImageURI(uri)
                        // Update the user image URL in Firestore
                        Firebase.firestore.collection(USER_NODE)
                            .document(Firebase.auth.currentUser!!.uid)
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
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        followRvAdapter = FollowRvAdapter(requireContext(), followList)
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
        fetchUserDetails()
        fetchCounts()

        binding.logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(activity, SignUpActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun fetchUserDetails() {
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
                e.printStackTrace()
            }
    }

    private fun fetchCounts() {
        val userId = Firebase.auth.currentUser!!.uid

        // Fetch post count
        Firebase.firestore.collection(userId).get()
            .addOnSuccessListener { postDocuments ->
                val postCount = postDocuments.size()

                // Fetch reel count
                Firebase.firestore.collection(userId + REEL).get()
                    .addOnSuccessListener { reelDocuments ->
                        val reelCount = reelDocuments.size()
                        val totalCount = postCount + reelCount
                        binding.postsCount.text = totalCount.toString()
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }

        // Fetch following count
        Firebase.firestore.collection(userId + FOLLOW).get()
            .addOnSuccessListener { documents ->
                val tempList = ArrayList<User>()
                for (document in documents) {
                    val user = document.toObject<User>()
                    user?.let { tempList.add(it) }
                }
                followList.clear()
                followList.addAll(tempList)
                followRvAdapter.notifyDataSetChanged()

                // Update following users count
                binding.followingCount.text = followList.size.toString()
            }
            .addOnFailureListener { exception ->
                // Handle error
                exception.printStackTrace()
            }

    }

    override fun onStart() {
        super.onStart()
        fetchUserDetails()
        fetchCounts()
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
