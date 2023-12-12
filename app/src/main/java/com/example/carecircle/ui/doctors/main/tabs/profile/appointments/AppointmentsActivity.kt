package com.example.carecircle.ui.doctors.main.tabs.profile.appointments

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carecircle.databinding.ActivityAppointmentsBinding
import com.example.carecircle.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AppointmentsActivity : AppCompatActivity() {

    lateinit var binding: ActivityAppointmentsBinding
    lateinit var adapter: AppointmentsAdapter
    private var appointmentsList: ArrayList<Appointment> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()

        adapter.onItemClickListener = AppointmentsAdapter
            .OnItemClickListener { position, itemId ->
                updateAppointmentStatus(itemId!!, "accepted")
                Toast.makeText(this, "Request accepted successfully", Toast.LENGTH_SHORT).show()
                appointmentsList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }

    }

    private fun initRecycler() {
        adapter = AppointmentsAdapter(mutableListOf())
        binding.appointmentsAdapter.adapter = adapter
        adapter.appointmentsList = appointmentsList

        fetchAppointmentsForCurrentDoctor()
    }

    private fun fetchAppointmentsForCurrentDoctor() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments")

            // Use orderByChild to filter by doctorId
            val query = appointmentsRef.orderByChild("doctorId").equalTo(currentUserId)

            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val appointmentsList: MutableList<Appointment> = mutableListOf()

                    for (appointmentSnapshot in dataSnapshot.children) {
                        val appointment = appointmentSnapshot.getValue(Appointment::class.java)

                        // Add another condition to filter by status "pending"
                        if (appointment != null && appointment.status == "pending") {
                            appointmentsList.add(appointment)
                        }
                    }

                    // Update the RecyclerView with the filtered appointments
                    updateRecyclerView(appointmentsList)
                    Log.e(
                        "AppointmentsActivity",
                        "id for first appointment: ${appointmentsList.getOrNull(0)?.id}"
                    )
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.e(
                        "AppointmentsActivity",
                        "Error fetching appointments: ${databaseError.message}"
                    )
                }
            })
        }
    }


    private fun updateRecyclerView(appointments: List<Appointment>) {
        appointmentsList.clear()
        appointmentsList.addAll(appointments)
        adapter.notifyDataSetChanged()
    }

    private fun updateAppointmentStatus(appointmentId: String, newStatus: String) {
        val appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments")

        // Get a reference to the specific appointment
        val appointmentRef = appointmentsRef.child(appointmentId)

        // Update the status
        appointmentRef.child("status").setValue(newStatus)
            .addOnSuccessListener {
                Log.d("AppointmentsActivity", "Status updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("AppointmentsActivity", "Error updating status: ${e.message}")
            }
    }

}