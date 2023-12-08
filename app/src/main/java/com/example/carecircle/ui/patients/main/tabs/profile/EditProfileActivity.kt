package com.example.carecircle.ui.patients.main.tabs.profile

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carecircle.databinding.ActivityEditProfileBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.util.UUID

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var refDatabase: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser

    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST: Int = 2023

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        refDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        binding.save.setOnClickListener {
            update()
            uploadImage()
            binding.progressBar.visibility = View.VISIBLE

            finish()
        }

        binding.profilePic.setOnClickListener {
            chooseImage()
        }
    }

    private fun update() {
        val email = binding.emailEt.text.toString()
        val password = binding.passwordEt.text.toString()
        val name = binding.fullNameEt.text.toString()
        val phoneNumber = binding.numberEt.text.toString()
        val oldPassword = binding.oldPasswordTv.text.toString()

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(applicationContext, "Please fill in all the fields", Toast.LENGTH_SHORT)
                .show()
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
                        firebaseUser.updatePassword(password)
                            .addOnCompleteListener { passwordTask ->
                                if (passwordTask.isSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Password updated successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    refDatabase.child("userName").setValue(name)
                                    refDatabase.child("phoneNumber").setValue(phoneNumber)

                                    Toast.makeText(
                                        applicationContext,
                                        "Successfully updated user",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Failed to update password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Failed to update email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun chooseImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null) {
            filePath = data!!.data
            try {
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.profilePic.setImageBitmap(bitmap)
                binding.save.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            var ref: StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    val hashMap:HashMap<String,String> = HashMap()
                        hashMap.put("username",binding.editProfEv.text.toString())
                    hashMap.put("profileImage","")
                    refDatabase.updateChildren(hashMap as Map<String,Any>)
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "uploaded", Toast.LENGTH_SHORT).show()
                    binding.save.visibility = View.GONE
                }

                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        "failed " + it.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
    }

}
