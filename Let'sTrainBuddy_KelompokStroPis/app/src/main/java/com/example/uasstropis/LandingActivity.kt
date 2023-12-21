package com.example.uasstropis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LandingActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        mAuth = FirebaseAuth.getInstance();

        val loginButton = findViewById<Button>(R.id.button_login)
        loginButton.setOnClickListener {
            val emailInput = findViewById<TextInputEditText>(R.id.input_email)?.text.toString().trim()
            val passwordInput = findViewById<TextInputEditText>(R.id.input_password)?.text.toString().trim()

            when {
                emailInput.isEmpty() || passwordInput.isEmpty() -> {
                    Toast.makeText(this, "Name or Password cannot be empty", Toast.LENGTH_LONG).apply {
                        setGravity(Gravity.CENTER, 0, 0)
                        show()
                    }
                }
                else -> {
                    mAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                        .addOnCompleteListener() { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Authentication success.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Authentication failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            }
        }


        val regisBtn = findViewById<TextView>(R.id.tv_register)
        regisBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}