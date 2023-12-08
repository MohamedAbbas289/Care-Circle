package com.example.carecircle.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.carecircle.databinding.ActivitySplashScreenBinding
import com.example.carecircle.ui.authentication.LoginActivity
import com.example.carecircle.ui.doctors.main.DocMainActivity
import com.example.carecircle.ui.patients.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        // Check if the user is already logged in
        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser != null) {
            // User is already authenticated, fetch user type
            fetchUserType(currentUser.uid)
        } else {
            // User is not authenticated, navigate to LoginActivity
            startLoginActivity()
        }
    }

    private fun fetchUserType(userId: String) {
        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userType = snapshot.child("userType").getValue(String::class.java)
                navigateBasedOnUserType(userType)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                startLoginActivity()
            }
        })
    }

    private fun navigateBasedOnUserType(userType: String?) {
        when (userType) {
            "Doctor" -> startDocMainActivity()
            "Patient" -> startMainActivity()
            else -> startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        Handler(Looper.getMainLooper())
            .postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
    }

    private fun startMainActivity() {
        Handler(Looper.getMainLooper())
            .postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
    }

    private fun startDocMainActivity() {
        Handler(Looper.getMainLooper())
            .postDelayed({
                val intent = Intent(this, DocMainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
    }
}
