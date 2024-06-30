package com.example.snapgram.Post

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snapgram.HomeActivity
import com.example.snapgram.Models.Reel
import com.example.snapgram.Models.User
import com.example.snapgram.R
import com.example.snapgram.Utils.REEL
import com.example.snapgram.Utils.REEL_FOLDER
import com.example.snapgram.Utils.USER_NODE
import com.example.snapgram.Utils.uploadVideo
import com.example.snapgram.databinding.ActivityReelBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ReelActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityReelBinding.inflate(layoutInflater)
    }
    private lateinit var videoUrl:String
    lateinit var progressDialog:ProgressDialog
    private val launcher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uploadVideo(uri, REEL_FOLDER, progressDialog) {url ->
                    if (url != null){
                        videoUrl = url
                    }
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
        progressDialog=ProgressDialog(this)

        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_cancel)
        }
        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.selectReel.setOnClickListener {
            launcher.launch("video/*")
        }

        binding.cancel.setOnClickListener {
            startActivity(Intent(this@ReelActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                var user:User= it.toObject<User>()!!
                val reel: Reel = Reel(videoUrl!!, binding.caption.text.toString(), user.image!!)
                Firebase.firestore.collection(REEL).document().set(reel)
                    .addOnSuccessListener {
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + REEL).document()
                            .set(reel).addOnSuccessListener {
                                startActivity(Intent(this@ReelActivity, HomeActivity::class.java))
                                finish()
                            }
                    }
            }

        }
    }
}