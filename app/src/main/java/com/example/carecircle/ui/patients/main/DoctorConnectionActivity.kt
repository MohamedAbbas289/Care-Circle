package com.example.carecircle.ui.patients.main

import User
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.ActivityDoctorConnectionBinding
import com.example.carecircle.model.Appointment
import com.example.carecircle.ui.patients.main.tabs.chat.ChatInboxActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class DoctorConnectionActivity : AppCompatActivity() {
    lateinit var binding: ActivityDoctorConnectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var formattedDate = ""


        val doctorId = intent.getStringExtra("DOCTOR_ID")

        binding.chatIcon.setOnClickListener {
            val intent = Intent(this, ChatInboxActivity::class.java).apply {
                putExtra("DOCTOR_ID", doctorId)
            }
            startActivity(intent)
        }


        getUserById(doctorId!!) { doctor ->
            if (doctor != null) {

                bindData(doctor)

            } else {
                // User data not found or not valid (neither Doctor nor Patient)
                Log.d("UserDetails", "User not found or invalid user type")
            }
        }

        binding.sendAppointmentBtn.setOnClickListener {
            val appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments")

            val newAppointmentId = appointmentsRef.push().key // Generate a unique ID

            val newAppointment = Appointment(
                id = newAppointmentId,
                patientName = FirebaseAuth.getInstance().currentUser?.displayName.toString(),
                doctorId = doctorId,
                date = formattedDate,
                status = "pending",
                patientId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            )

            if (newAppointmentId != null) {
                val newAppointmentRef = appointmentsRef.child(newAppointmentId)
                newAppointmentRef.setValue(newAppointment)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Data written successfully
                            Toast.makeText(
                                this,
                                "Appointment Sent Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Handle the error if data writing fails
                            Toast.makeText(this, "Failed to send appointment", Toast.LENGTH_SHORT)
                                .show()
                            Log.e(
                                "YourTag",
                                "Error sending appointment: ${task.exception?.message}"
                            )
                        }
                    }
            } else {
                // Handle the case where newAppointmentId is null
                Toast.makeText(this, "Failed to generate appointment ID", Toast.LENGTH_SHORT).show()
            }
        }



        binding.selectDate.setOnClickListener {
            // Get the current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create a date picker dialog
            val datePickerDialog = DatePickerDialog(
                this,  // Pass the context (activity context)
                { _, selectedYear, selectedMonth, selectedDay ->
                    // The user selected a date, update your UI or store the date as needed
                    formattedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"

                    binding.selectDate.text = formattedDate
                    // Use the selectedDay, selectedMonth, and selectedYear as needed
                    Log.d("DatePicker", "Selected Date: $formattedDate")
                },
                year,
                month,
                day
            )

            // Show the date picker dialog
            datePickerDialog.show()
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

    private fun showTabFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}