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
import com.example.snapgram.Utils.USER_NODE
import com.example.snapgram.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SignUpActivity : AppCompatActivity() {
    val binding by lazy{
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    lateinit var user: com.example.snapgram.Models.User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        user = User()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val text = "<font color=#FF000000>Have an account?</font> <font color=#1877F2>Login</font>"
        binding.login.setText(Html.fromHtml(text))
        binding.signup.setOnClickListener {
            if(binding.email.text?.toString().equals("") or
                binding.name.text?.toString().equals("") or
                binding.username.text?.toString().equals("") or
                binding.password.text?.toString().equals("")){
                Toast.makeText(this@SignUpActivity, "fill the field",Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString()).addOnCompleteListener {
                    result->

                    if(result.isSuccessful){
                        user.email = binding.email.text?.toString()
                        user.name = binding.name.text?.toString()
                        user.username = binding.username.text?.toString()
                        user.password = binding.password.text?.toString()
                        Firebase.firestore.collection(USER_NODE).document(FirebaseAuth.getInstance().currentUser!!.uid)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this@SignUpActivity, "Login", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                            }
                    }else{
                        Toast.makeText(this@SignUpActivity, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.login.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }
    }
}