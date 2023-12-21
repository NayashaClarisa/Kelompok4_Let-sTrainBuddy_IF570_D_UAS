package com.example.uasstropis

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbreferences : DatabaseReference

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance();
        dbreferences = FirebaseDatabase.getInstance().getReference().child("userdata")

        val submitButton = findViewById<Button>(R.id.button_register)
        submitButton.setOnClickListener {
            val nameInput = findViewById<TextInputEditText>(R.id.input_name)?.text.toString().trim()
            val emailInput = findViewById<TextInputEditText>(R.id.input_email)?.text.toString().trim()
            val passwordInput = findViewById<TextInputEditText>(R.id.input_password)?.text.toString().trim()

            when {
                nameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() -> {
                    Toast.makeText(this, "Name/Email/Password cannot be empty", Toast.LENGTH_LONG).apply {
                        setGravity(Gravity.CENTER, 0, 0)
                        show()
                    }
                }
                else -> {
                    mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                        .addOnCompleteListener() { task ->
                            if (task.isSuccessful) {
                                val currentUser = mAuth.currentUser
                                val uid = currentUser?.uid

                                uid?.let {userId ->
                                    val userRef = dbreferences.child(userId)
                                    val userData = HashMap<String, Any>()
                                    userData["name"] = nameInput
                                    userData["email"] = emailInput
                                    userData["exerciseProgram"] = "none"

                                userRef.setValue(userData)
                                    .addOnSuccessListener {
                                        val intent = Intent(this, LandingActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(TAG, "Failed to register: $exception")
                                        Toast.makeText(this, "Failed to register.", Toast.LENGTH_SHORT).show()
                                    }

                                }
                            } else {
                                Toast.makeText(
                                    this, "Authentication failed.", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }


        }


        val loginBtn = findViewById<TextView>(R.id.tv_login)
        loginBtn.setOnClickListener {
            startActivity(Intent(this, LandingActivity::class.java))
        }
    }
}