package com.example.carecircle.ui.patients.main.tabs.profile

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carecircle.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var refDatabase: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        refDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)

        binding.save.setOnClickListener {
            update()
        }
    }
    private fun update() {
        val email = binding.emailEt.text.toString()
        val password = binding.passwordEt.text.toString()
        val name = binding.fullNameEt.text.toString()
        val phoneNumber = binding.numberEt.text.toString()
        val oldPassword = binding.oldPasswordTv.text.toString()

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(applicationContext, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != binding.confirmPasswordEt.text.toString()) {
            binding.passwordContainer.error = "Passwords do not match"
            return
        }

        val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, oldPassword)
        firebaseUser.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                firebaseUser.verifyBeforeUpdateEmail(email).addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        refDatabase.child("email").setValue(email)
                        Toast.makeText(applicationContext, "Please Check Your Email To Verify", Toast.LENGTH_SHORT).show()

                        firebaseUser.updatePassword(password).addOnCompleteListener { passwordTask ->
                            if (passwordTask.isSuccessful) {
                                Toast.makeText(applicationContext, "Password updated successfully", Toast.LENGTH_SHORT).show()

                                refDatabase.child("userName").setValue(name)
                                refDatabase.child("phoneNumber").setValue(phoneNumber)

                                Toast.makeText(applicationContext, "Successfully updated user", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(applicationContext, "Failed to update password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(applicationContext, "Failed to update email", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
