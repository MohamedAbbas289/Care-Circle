package com.example.carecircle.ui.patients.main.tabs.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.carecircle.databinding.FragmentProfileBinding
import com.example.carecircle.ui.authentication.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
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
    }

    private fun navigateToLoginPage() {
        val intent = Intent(context,LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()

    }

    private fun editProfileInformation() {
        // navigate to edit profile screen
        val intent = Intent(context, EditProfileActivity::class.java)
        startActivity(intent)

    }
}