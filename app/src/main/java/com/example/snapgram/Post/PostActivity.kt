package com.example.snapgram.Post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snapgram.HomeActivity
import com.example.snapgram.Models.Post
import com.example.snapgram.Models.User
import com.example.snapgram.R
import com.example.snapgram.Utils.POST
import com.example.snapgram.Utils.POST_FOLDER
import com.example.snapgram.Utils.USER_NODE
import com.example.snapgram.Utils.uploadImage
import com.example.snapgram.databinding.ActivityPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class PostActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    private var imageUrl: String? = null

    private val launcher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Set image immediately for better UX
                binding.selectImage.setImageURI(uri)
                uploadImage(uri, POST_FOLDER) { url ->
                    imageUrl = url

                    Toast.makeText(
                        this@PostActivity,
                        if (imageUrl != null) "Image uploaded successfully" else "Failed to upload image. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_cancel)
        }
        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.selectImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.cancel.setOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            val uid = Firebase.auth.currentUser?.uid
            if (uid != null) {
                Firebase.firestore.collection(USER_NODE).document(uid).get()
                    .addOnSuccessListener { document ->
                        val user = document.toObject<User>()
                        if (user != null) {
                            if (imageUrl != null) {
                                val post = Post(
                                    imageUrl!!,
                                    caption = binding.caption.text.toString(),
                                    uid = uid,
                                    time = System.currentTimeMillis().toString()
                                )
                                Firebase.firestore.collection(POST).document().set(post)
                                    .addOnSuccessListener {
                                        Firebase.firestore.collection(uid)
                                            .document()
                                            .set(post).addOnSuccessListener {
                                                startActivity(
                                                    Intent(
                                                        this@PostActivity,
                                                        HomeActivity::class.java
                                                    )
                                                )
                                                finish()
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Failed to create post. Please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Please upload an image first.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
            }
        }
    }
}
