package com.example.carecircle.ui.doctors.main.tabs.profile.myPatiens

import User
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.carecircle.databinding.ActivityMyPatintsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyPatientsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyPatintsBinding
    private lateinit var adapter: PatientAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPatintsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()

        // Fetch accepted appointments where doctorId is equal to the current user's ID
        fetchAcceptedAppointments()
    }

    private fun initRecycler() {
        adapter = PatientAdapter(mutableListOf())
        binding.myPatientsRecyclerView.adapter = adapter
    }

    private fun fetchAcceptedAppointments() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments")

            val query = appointmentsRef
                .orderByChild("status")
                .equalTo("accepted")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val acceptedPatients: MutableList<String> = mutableListOf()

                        for (appointmentSnapshot in dataSnapshot.children) {
                            val doctorId =
                                appointmentSnapshot.child("doctorId").getValue(String::class.java)
                            val patientId =
                                appointmentSnapshot.child("patientId").getValue(String::class.java)

                            // Check if the appointment is for the current doctor and has a valid patientId
                            if (doctorId == currentUserId && !patientId.isNullOrBlank()) {
                                acceptedPatients.add(patientId)
                            }
                        }

                        // After fetching accepted patients, retrieve patient details
                        fetchPatientsDetails(acceptedPatients)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle errors
                    }
                })
        }
    }

    private fun fetchPatientsDetails(patientIds: List<String>) {
        val patientsRef = FirebaseDatabase.getInstance().getReference("users")

        patientsRef.orderByChild("userId")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val patientsList: MutableList<User> = mutableListOf()

                    for (patientSnapshot in dataSnapshot.children) {
                        val userId = patientSnapshot.child("userId").getValue(String::class.java)

                        // Check if the patient ID is in the list of accepted patient IDs
                        if (userId != null && userId in patientIds) {
                            // Retrieve patient details
                            val userName =
                                patientSnapshot.child("userName").getValue(String::class.java)
                            val profileImage =
                                patientSnapshot.child("profileImage").getValue(String::class.java)

                            // Check for null values before creating a User object
                            if (userName != null) {
                                // Create a User object and add it to the list
                                val patient = User(
                                    userName = userName,
                                    userId = userId,
                                    profileImage = profileImage
                                )
                                patientsList.add(patient)
                            }
                        }
                    }

                    // Update the RecyclerView with the list of patients
                    adapter.patientsList.clear()
                    adapter.patientsList.addAll(patientsList)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
    }
}
