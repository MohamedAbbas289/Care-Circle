package com.example.carecircle.ui.doctors.main.tabs.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.FragmentDocProfileBinding
import com.example.carecircle.ui.authentication.LoginActivity
import com.example.carecircle.ui.doctors.main.tabs.profile.appointments.AppointmentsActivity
import com.example.carecircle.ui.doctors.main.tabs.profile.myPatiens.MyPatientsActivity
import com.example.carecircle.ui.patients.main.tabs.profile.EditProfileActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DocProfileFragment : Fragment() {
    private lateinit var binding: FragmentDocProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDocProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDataFromFireBase()
        initViews()
    }

    private fun showDataFromFireBase() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users").child((Firebase.auth.uid!!))

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("userName").getValue(String::class.java)
                val email = snapshot.child("email").getValue(String::class.java)
                binding.userName.text = name
                binding.userEmail.text = email
            }


            override fun onCancelled(error: DatabaseError) {
                // show error
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun initViews() {
        binding.editProfile.setOnClickListener {
            // edit the fields
            // save it in firebase
            editProfileInformation()
        }
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            navigateToLoginPage()

        }
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users").child((Firebase.auth.uid!!))
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isAdded) { // Check if the fragment is attached
                    val profileImage = snapshot.child("profileImage").getValue(String::class.java)
                    Glide.with(this@DocProfileFragment)
                        .load(profileImage)
                        .placeholder(R.drawable.profile_pic)
                        .into(binding.profilePic)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })

        binding.appointemnts.setOnClickListener {
            navigateToAppointmentsPage()
        }
        binding.myPatients.setOnClickListener {
            navigateToMyPatientsPage()
        }
    }

    private fun navigateToLoginPage() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()

    }

    private fun navigateToAppointmentsPage() {
        val intent = Intent(context, AppointmentsActivity::class.java)
        startActivity(intent)

    }

    private fun navigateToMyPatientsPage() {
        val intent = Intent(context, MyPatientsActivity::class.java)
        startActivity(intent)

    }

    private fun editProfileInformation() {
        // navigate to edit profile screen
        val intent = Intent(context, EditProfileActivity::class.java)
        startActivity(intent)

    }
}