package com.example.carecircle.ui.patients.main.tabs.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carecircle.databinding.ActivityEditProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    lateinit var ref: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()

        showData()
        /*
                binding.save.setOnClickListener {
                    if (isNameChanged()){
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(this, "NoChange", Toast.LENGTH_SHORT).show();

                    }
                }
                */

    }

    private fun showData() {
        val intent = intent
        // nameUser = intent.getStringExtra("name")?:""


    }

    private fun initViews() {
        ref = FirebaseDatabase.getInstance().getReference("users")

    }
    /*fun isNameChanged() : Boolean {
        if (!nameUser.equals(binding.fullNameEt.getText().toString())){
            ref.child(usernameUser).child("name").setValue(binding.fullNameEt.getText().toString());
            nameUser = binding.fullNameEt.getText().toString();
            return true;
        } else {
            return false;
        }
    }
*/
}