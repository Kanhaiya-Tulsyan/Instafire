package com.example.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser!=null)
        {
            getPostActivity()
        }

        btnLogin.setOnClickListener {
            btnLogin.isEnabled=false
            val email = etEmail.text.toString();
            val password = etPass.text.toString();
            if (password.isBlank() || email.isBlank()) {
                Toast.makeText(this, "Enter valid email/password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                btnLogin.isEnabled=true
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Succesfull", Toast.LENGTH_SHORT).show()
                    getPostActivity();
                }
                else{
                    Toast.makeText(this, "Login failed.!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPostActivity() {
        val intent=Intent(this,PostActivity::class.java)
        startActivity(intent)
        finish()
    }
}