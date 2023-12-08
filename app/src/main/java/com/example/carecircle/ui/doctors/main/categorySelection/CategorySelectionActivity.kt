package com.example.carecircle.ui.doctors.main.categorySelection

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carecircle.databinding.ActivityCategorySelectionBinding
import com.example.carecircle.ui.doctors.main.DocMainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class CategorySelectionActivity : AppCompatActivity() {
    lateinit var binding: ActivityCategorySelectionBinding
    lateinit var adapter: CategorySelectionAdapter
    private lateinit var auth: FirebaseAuth
    var categories = listOf(
        "Cardiologist",
        "Dermatologist",
        "Orthopedic",
        "Gynecologist",
        "Pediatrician",
        "Neurologist",
        "Psychiatrist",
        "Ophthalmologist",
        "Oncologist",
        "Dentist"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        auth = Firebase.auth
        adapter = CategorySelectionAdapter(categories)
        adapter.setUserId(auth.currentUser?.uid ?: "")

        adapter.onItemClickListener =
            CategorySelectionAdapter.OnItemClickListener { position, category, userId ->
                // Save the selected category to Firebase Realtime Database
                saveCategoryToFirebase(userId, category)
            }

        // Set the adapter for your RecyclerView
        binding.categoriesList.adapter = adapter
    }

    private fun saveCategoryToFirebase(userId: String, category: String) {
        // Get a reference to your Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)

        // Create a HashMap to store only the updated fields
        val updatedFields = HashMap<String, Any>()
        updatedFields["category"] = category
        updatedFields["rating"] = 2.5 // Set the initial rating value

        // Update only the specified fields in the user's data
        databaseReference.updateChildren(updatedFields)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Category and rating saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to DocMainActivity only if the category is saved
                    intent = Intent(this@CategorySelectionActivity, DocMainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to save category and rating",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}
