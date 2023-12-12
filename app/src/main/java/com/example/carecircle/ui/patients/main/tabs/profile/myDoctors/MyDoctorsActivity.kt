package com.example.carecircle.ui.patients.main.tabs.profile.myDoctors

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.carecircle.databinding.ActivityMyDoctorsBinding
import com.example.carecircle.model.Appointment
import com.example.carecircle.model.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyDoctorsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyDoctorsBinding
    lateinit var adapter: MyDoctorsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyDoctorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()
        // Fetch appointments with status "accepted" and patientId equal to the current user id
        fetchAcceptedAppointments()
    }

    private fun initRecycler() {
        adapter = MyDoctorsAdapter(mutableListOf())
        binding.myDoctorsRecyclerView.adapter = adapter

        adapter.onItemClickListener = MyDoctorsAdapter
            .OnItemClickListener { position, docId ->
                // Handle item click here
            }

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
                        val acceptedAppointments: MutableList<Appointment> = mutableListOf()

                        for (appointmentSnapshot in dataSnapshot.children) {
                            val id = appointmentSnapshot.child("id").getValue(String::class.java)
                            val patientName = appointmentSnapshot.child("patientName")
                                .getValue(String::class.java)
                            val doctorId =
                                appointmentSnapshot.child("doctorId").getValue(String::class.java)
                            val patientId =
                                appointmentSnapshot.child("patientId").getValue(String::class.java)
                            val date =
                                appointmentSnapshot.child("date").getValue(String::class.java)
                            val status =
                                appointmentSnapshot.child("status").getValue(String::class.java)
                            // Check for null values before creating an Appointment object
                            if (id != null && patientName != null && doctorId != null &&
                                patientId != null && date != null && status != null
                            ) {
                                // Create an Appointment object and add it to the list
                                val appointment =
                                    Appointment(id, patientName, doctorId, patientId, date, status)
                                acceptedAppointments.add(appointment)
                            } else {
                                // Log a message or handle the case when data is missing
                                Log.e("MyDocActivity", "Appointment data is null or incomplete")
                            }
                        }

                        // Log the IDs of accepted appointments
                        Log.d(
                            "MyDocActivity",
                            "Accepted Appointments docId: ${acceptedAppointments[0].doctorId}"
                        )

                        // After fetching accepted appointments, retrieve doctors based on doctorId
                        fetchDoctorsForAcceptedAppointments(acceptedAppointments)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle errors
                        Log.e(
                            "MyDocActivity",
                            "Error fetching accepted appointments: ${databaseError.message}"
                        )
                    }
                })
        }
    }

    private fun fetchDoctorsForAcceptedAppointments(appointments: List<Appointment>) {
        val doctorIds = appointments.map { it.doctorId }

        val doctorsRef = FirebaseDatabase.getInstance().getReference("users")

        doctorsRef.orderByChild("userType").equalTo("Doctor")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val doctorsList: MutableList<Doctor> = mutableListOf()

                    for (doctorSnapshot in dataSnapshot.children) {
                        val doctorId = doctorSnapshot.child("userId").getValue(String::class.java)

                        // Check if the doctor ID is in the list of accepted doctor IDs
                        if (doctorId != null && doctorId in doctorIds) {
                            // Retrieve other relevant data for doctors
                            val name = doctorSnapshot.child("userName").getValue(String::class.java)
                            val speciality =
                                doctorSnapshot.child("category").getValue(String::class.java)
                            val rating = doctorSnapshot.child("rating").getValue(Float::class.java)

                            // Check for null values before creating a Doctor object
                            if (name != null && doctorId != null && speciality != null && rating != null) {
                                // Create a Doctor object and add it to the list
                                val doctor = Doctor(name, doctorId, speciality, rating)
                                doctorsList.add(doctor)
                                Log.e(
                                    "MyDocActivity",
                                    "Doctor data: $name, $doctorId, $speciality, $rating"
                                )
                            } else {
                                // Log a message or handle the case when data is missing
                                Log.e("MyDocActivity", "Doctor data is null or incomplete")
                            }
                        } else {
                            Log.e(
                                "MyDocActivity",
                                "Doctor ID is null or not in the list of accepted doctor IDs"
                            )
                        }
                    }

                    // Update the RecyclerView with the filtered doctors
                    adapter.doctorsList.clear()
                    adapter.doctorsList.addAll(doctorsList)
                    adapter.notifyDataSetChanged()

                    Log.e("MyDocActivity", "Final list of doctors: $doctorsList")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.e("MyDocActivity", "Error fetching doctors: ${databaseError.message}")
                }
            })
    }


}