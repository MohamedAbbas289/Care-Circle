package com.example.carecircle.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.carecircle.R
import com.example.carecircle.databinding.ActivityForgetPasswordBinding
import com.example.carecircle.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        auth = Firebase.auth
        checkEmail()
    }

    private fun checkEmail() {
        binding.btnReset.setOnClickListener {
            var srtEmail = binding.edtForgotPasswordEmail.text.toString().trim()
            if (!TextUtils.isEmpty(srtEmail)) {
                resetPassword(srtEmail)
            } else {
                binding.edtForgotPasswordEmail.error = "Email field can't be empty"
            }
        }
    }

    private fun resetPassword(srtEmail: String) {
        binding.forgetPasswordProgressbar.visibility = View.VISIBLE
        binding.btnReset.visibility = View.INVISIBLE

        auth.sendPasswordResetEmail(srtEmail)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Reset Password link has been sent to your registered Email",
                    Toast.LENGTH_SHORT
                ).show();
                navigateToLogin()

            }
            .addOnFailureListener {
                Toast.makeText(this, "Error :- " + it.localizedMessage, Toast.LENGTH_SHORT).show();
                binding.forgetPasswordProgressbar.visibility = View.INVISIBLE
                binding.btnReset.visibility = View.VISIBLE

            }
    }

    private fun navigateToLogin() {
        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}