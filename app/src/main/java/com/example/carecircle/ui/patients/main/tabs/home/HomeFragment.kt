package com.example.carecircle.ui.patients.main.tabs.home

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.FragmentHomeBinding
import com.example.carecircle.model.CategoryData
import com.example.carecircle.model.Doctor
import com.example.carecircle.model.Token
import com.example.carecircle.ui.patients.main.DoctorConnectionActivity
import com.example.carecircle.ui.patients.main.FragmentCallback
import com.example.carecircle.ui.patients.main.tabs.categories.CategoriesAdapter
import com.example.carecircle.ui.patients.main.tabs.categories.CategoriesFragment
import com.example.carecircle.ui.patients.main.tabs.categories.SpecificCategoryFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import java.util.Locale


class HomeFragment : Fragment(), FragmentCallback {
    private lateinit var binding: FragmentHomeBinding
    private var categories: ArrayList<CategoryData> = ArrayList()
    lateinit var adapter: CategoriesAdapter
    lateinit var doctorsAadapter: TopDoctorsAdapter
    private var firebaseUser: FirebaseUser? = null
    private var callback: FragmentCallback? = null

    var doctorList: MutableList<Doctor> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doctorsAadapter = TopDoctorsAdapter(mutableListOf()) // Initialize with an empty list
        firebaseUser = FirebaseAuth.getInstance().currentUser
        binding.doctorsRecycler.adapter = doctorsAadapter
        underLineText()

        initRecyclerView()
        fetchDataFromDatabase()
        initProfileImage()

        binding.seeAllTextView.setOnClickListener {
            navigateToCategoryFragment()
        }
        bindUserName()
        adapter.onItemClickListener =
            CategoriesAdapter.OnItemClickListener { position, category ->
                showSpecificCategory(category)
            }
        doctorsAadapter.onItemClickListener =
            TopDoctorsAdapter.OnItemClickListener { position, docId ->
                navigateToDoctorConnectionActivity(docId)
            }
        updateToken(FirebaseInstanceId.getInstance().token)

    }

    private fun initProfileImage() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(Firebase.auth.uid!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the fragment is added to its activity and the view is not destroyed
                if (!isAdded || activity == null || view == null) {
                    return
                }

                val profileImage = snapshot.child("profileImage").getValue(String::class.java)
                Glide.with(this@HomeFragment)
                    .load(profileImage)
                    .placeholder(R.drawable.profile_pic)
                    .into(binding.profileImage)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }

    private fun navigateToDoctorConnectionActivity(docId: String) {
        val intent = Intent(requireContext(), DoctorConnectionActivity::class.java)
        intent.putExtra("DOCTOR_ID", docId)
        startActivity(intent)
    }


    private fun initRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesList.layoutManager = layoutManager
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
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterlist(newText)
                return true
            }


        })

    }

    private fun filterlist(newText: String?) {
        if (newText != null){
            val filteredList = ArrayList<Doctor>()
            for (i in doctorList ){
                if (i.name?.lowercase(Locale.ROOT)?.contains(newText) == true){
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()){
                Toast.makeText(context,"No Data Found", Toast.LENGTH_SHORT).show()
            } else{
                doctorsAadapter.setFilteredList(filteredList)
            }
        }
    }

    private fun navigateToCategoryFragment() {
        val anotherFragment = CategoriesFragment()

        // Get the FragmentManager
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

        // Begin the fragment transaction
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace the current Fragment with the new Fragment
        transaction.replace(R.id.fragment_container, anotherFragment)

        transaction.addToBackStack(null)
        // Commit the transaction
        transaction.commit()
    }

    private fun underLineText() {
        // Your text
        val yourText = binding.seeAllTextView.text.toString()

        // Create a SpannableString with UnderlineSpan
        val spannableString = SpannableString(yourText)
        spannableString.setSpan(
            UnderlineSpan(),
            0,
            yourText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the SpannableString to the TextView
        binding.seeAllTextView.text = spannableString
    }

    // Inside your HomeFragment class

// ...

    private fun fetchDataFromDatabase() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                for (userSnapshot in dataSnapshot.children) {
                    val userType = userSnapshot.child("userType").getValue(String::class.java)

                    // Check if the user type is "Doctor"
                    if (userType == "Doctor") {
                        // Retrieve other relevant data for doctors
                        val name = userSnapshot.child("userName").getValue(String::class.java)
                        val id = userSnapshot.child("userId").getValue(String::class.java)
                        val speciality = userSnapshot.child("category").getValue(String::class.java)
                        val rating = userSnapshot.child("rating").getValue(Float::class.java)
                        val profileImage =
                            userSnapshot.child("profileImage").getValue(String::class.java)

                        // Check for null values before creating a Doctor object
                        if (name != null && id != null && speciality != null && rating != null) {
                            // Create a Doctor object and add it to the list
                            val doctor = Doctor(name, id, speciality, rating, profileImage)
                            doctorList.add(doctor)
                        } else {
                            // Log a message or handle the case when data is missing
                            Log.e("HomeFragment", "Doctor data is null or incomplete")
                        }
                    }
                }
                val sortedDoctorList = doctorList.sortedByDescending { it.ratting }

                // Update the data in the adapter
                doctorsAadapter.doctorsList.clear()
                doctorsAadapter.doctorsList.addAll(sortedDoctorList)
                doctorsAadapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e(
                    "HomeFragment",
                    "Error fetching data from the database: ${databaseError.message}"
                )
            }
        })
    }

// ...


    private fun bindUserName() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userName = currentUser.displayName

            if (userName != null) {
                // Now, userName contains the username of the currently authenticated user
                // You can use it as needed
                binding.userName.text = userName

            } else {
                Log.d("YourFragment", "Username is null")
            }
        }
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

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    override fun onCommandReceived(command: String) {
        TODO("Not yet implemented")
    }

    fun setCallback(callback: FragmentCallback) {
        this.callback = callback
    }

}