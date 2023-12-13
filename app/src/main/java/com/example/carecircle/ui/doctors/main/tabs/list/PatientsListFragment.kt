package com.example.carecircle.ui.doctors.main.tabs.list

import User
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.carecircle.databinding.FragmentPatientsListBinding
import com.example.carecircle.ui.doctors.main.tabs.profile.myPatiens.PatientAdapter
import com.example.carecircle.ui.patients.main.tabs.chat.ChatInboxActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PatientsListFragment : Fragment() {
    private lateinit var binding: FragmentPatientsListBinding
    private lateinit var adapter: PatientAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPatientsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()

        // Fetch accepted appointments where doctorId is equal to the current user's ID
        fetchAcceptedAppointments()

    }

    private fun initRecycler() {
        adapter = PatientAdapter(mutableListOf())
        binding.myPatientsRecyclerView.adapter = adapter

        adapter.onItemClickListener = PatientAdapter.OnItemClickListener { position, docId ->
            val intent = Intent(requireActivity(), ChatInboxActivity::class.java).apply {
                putExtra("DOCTOR_ID", docId)
            }
            startActivity(intent)
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