package com.example.carecircle.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carecircle.databinding.ActivityLoginBinding
import com.example.carecircle.ui.doctors.main.DocMainActivity
import com.example.carecircle.ui.patients.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingProgressBar = binding.loadingProgressBar
        initViews()
        binding.forgetPassword.setOnClickListener {
            navigateToForgetPasswordActivity()
        }
        autoLogin()
    }

    private fun navigateToForgetPasswordActivity() {
        val intent = Intent(this,ForgetPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun checkAllFields(): Boolean {
        val email = binding.emailEt.text.toString()
        if (binding.emailEt.text.isBlank()) {
            binding.emailContainer.error = "required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailContainer.error = "wrong email format"
            return false
        }
        if (binding.passwordEt.text.length <= 6) {
            binding.passwordContainer.error = "must be more than 6 characters"
            binding.passwordContainer.errorIconDrawable = null
            return false
        }
        return true
    }

    private fun initViews() {
        auth = FirebaseAuth.getInstance()

        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        binding.createAccountBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            if (checkAllFields()) {
                setUiEnabled(false)

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    setUiEnabled(true)

                    if (it.isSuccessful) {
                        // Check user type and navigate accordingly
                        checkUserTypeAndNavigate(email)
                    } else {
                        binding.passwordContainer.error = "Wrong email or password"
                    }
                }
            }
        }
    }

    private fun checkUserTypeAndNavigate(email: String) {
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val userType =
                                userSnapshot.child("userType").getValue(String::class.java)

                            if (userType == "Doctor") {
                                val intent = Intent(this@LoginActivity, DocMainActivity::class.java)
                                startActivity(intent)
                            } else if (userType == "Patient") {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }

                            Toast.makeText(
                                this@LoginActivity,
                                "Signed in successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Database error", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun setUiEnabled(enabled: Boolean) {
        binding.emailEt.isEnabled = enabled
        binding.passwordEt.isEnabled = enabled
        binding.loginBtn.isEnabled = enabled
        binding.createAccountBtn.isEnabled = enabled

        // Show or hide the progress bar based on the enabled parameter
        loadingProgressBar.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    private fun autoLogin() {
        // Check if the user is already logged in
        if (auth.currentUser != null) {
            val email = auth.currentUser?.email ?: return

            // Initialize binding if it is null
            if (!::binding.isInitialized) {
                binding = ActivityLoginBinding.inflate(layoutInflater)
                setContentView(binding.root)
                loadingProgressBar = binding.loadingProgressBar
                initViews()
            }

            // Check user type and navigate accordingly
            checkUserTypeAndNavigate(email)
        }
    }


}
