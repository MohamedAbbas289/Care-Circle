package com.example.carecircle.ui.patients.main

import User
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.ActivityDoctorConnectionBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DoctorConnectionActivity : AppCompatActivity() {
    lateinit var binding: ActivityDoctorConnectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val doctorId = intent.getStringExtra("DOCTOR_ID")


        getUserById(doctorId!!) { doctor ->
            if (doctor != null) {

                bindData(doctor)

            } else {
                // User data not found or not valid (neither Doctor nor Patient)
                Log.d("UserDetails", "User not found or invalid user type")
            }
        }
    }

    fun getUserById(userId: String, callback: (User?) -> Unit) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users")

        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userType = dataSnapshot.child("userType").getValue(String::class.java)

                if (userType == "Doctor") {
                    val category = dataSnapshot.child("category").getValue(String::class.java)
                    val email = dataSnapshot.child("email").getValue(String::class.java)
                    val gender = dataSnapshot.child("gender").getValue(String::class.java)
                    val phoneNumber = dataSnapshot.child("phoneNumber").getValue(String::class.java)
                    val profileImage =
                        dataSnapshot.child("profileImage").getValue(String::class.java)
                    val rating = dataSnapshot.child("rating").getValue(Float::class.java)
                    val token = dataSnapshot.child("token").getValue(String::class.java)
                    val userName = dataSnapshot.child("userName").getValue(String::class.java)

                    val user = User(
                        category,
                        email,
                        gender,
                        phoneNumber,
                        profileImage,
                        rating,
                        token,
                        userId,
                        userName,
                        userType
                    )

                    callback(user)
                } else {
                    // If the user type is neither Doctor nor Patient, return null
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                callback(null)
            }
        })
    }

    private fun bindData(doctor: User) {
        binding.docName.text = doctor.userName.toString()
        binding.rateBar.rating = doctor.rating!!
        binding.speciality.text = doctor.category.toString()
        Glide.with(this)
            .load(doctor.profileImage)
            .placeholder(R.drawable.profile_pic)
            .into(binding.profileImage)
        val phoneNumber = doctor.phoneNumber

        binding.callIcon.setOnClickListener {
            // Check if the phone number is not null or empty
            if (!phoneNumber.isNullOrEmpty()) {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                startActivity(dialIntent)
            }
        }
    }
}