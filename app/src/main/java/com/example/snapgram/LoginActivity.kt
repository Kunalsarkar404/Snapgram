package com.example.snapgram

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snapgram.Models.User
import com.example.snapgram.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class LoginActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root) // Set the content view to the binding's root

        val text = "<font color=#FF000000>Don't have an account?</font> <font color=#1877F2>Sign Up</font>"
        binding.signup.setText(Html.fromHtml(text))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.login.setOnClickListener {
            val email = binding.email.text?.toString()
            val password = binding.password.text?.toString()

            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(this@LoginActivity, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(email, password)
                Firebase.auth.signInWithEmailAndPassword(user.email!!, user.password!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    } else {
                        Toast.makeText(this@LoginActivity, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.signup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

    }
}