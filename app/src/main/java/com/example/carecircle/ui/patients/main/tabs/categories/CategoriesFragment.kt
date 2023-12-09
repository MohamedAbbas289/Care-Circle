package com.example.carecircle.ui.patients.main.tabs.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.FragmentCategoriesBinding
import com.example.carecircle.model.CategoryData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoriesFragment : Fragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private var categories: ArrayList<CategoryData> = ArrayList()
    lateinit var adapter: CategoriesAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDataFromFireBase()
        initProfileImage()
        initRecyclerView()
        adapter.onItemClickListener =
            CategoriesAdapter.OnItemClickListener { position, category ->
                showSpecificCategory(category)
            }
    }

    private fun initProfileImage() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users").child((Firebase.auth.uid!!))
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isAdded) {
                    val profileImage = snapshot.child("profileImage").getValue(String::class.java)
                    Glide.with(this@CategoriesFragment)
                        .load(profileImage)
                        .placeholder(R.drawable.profile_pic)
                        .into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun initRecyclerView() {
        categories.add(CategoryData("Cardiologist", R.drawable.cardiologist_img))
        categories.add(CategoryData("Dermatologist", R.drawable.dermatologist_img))
        categories.add(CategoryData("Orthopedic", R.drawable.orthopedic_surgeon_img))
        categories.add(CategoryData("Gynecologist", R.drawable.gynecologist_img))
        categories.add(CategoryData("Pediatrician", R.drawable.pediatrician_img))
        categories.add(CategoryData("Neurologist", R.drawable.neurologist_img))
        categories.add(CategoryData("Psychiatrist", R.drawable.psychiatrist_img))
        categories.add(CategoryData("Ophthalmologist", R.drawable.ophthalmologist_img))
        categories.add(CategoryData("Oncologist", R.drawable.oncologist_img))
        categories.add(CategoryData("Dentist", R.drawable.dentist_img))
        adapter = CategoriesAdapter(categories)
        binding.categoriesList.adapter = adapter
    }

    private fun showDataFromFireBase() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users").child((Firebase.auth.uid!!))

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("userName").getValue(String::class.java)
                binding.userName.text = name
            }

            override fun onCancelled(error: DatabaseError) {
                // show error
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun showSpecificCategory(category: String) {
        // Create a bundle to pass data to SpecificCategoryFragment
        val bundle = Bundle()
        bundle.putString("category", category)

        // Navigate to SpecificCategoryFragment
        val specificCategoryFragment = SpecificCategoryFragment()
        specificCategoryFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, specificCategoryFragment)
            .addToBackStack(null)
            .commit()
    }
}