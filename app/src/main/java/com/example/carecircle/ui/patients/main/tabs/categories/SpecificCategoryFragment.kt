package com.example.carecircle.ui.patients.main.tabs.categories

import SpecificCategoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carecircle.databinding.FragmentSpecificCategoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SpecificCategoryFragment : Fragment() {
    private lateinit var binding: FragmentSpecificCategoryBinding
    private lateinit var adapter: SpecificCategoryAdapter
    private lateinit var category: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpecificCategoryBinding.inflate(inflater, container, false)
        category = arguments?.getString("category") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        loadUsersForCategory()
    }

    private fun initRecyclerView() {
        adapter = SpecificCategoryAdapter()
        binding.doctorsList.layoutManager = LinearLayoutManager(requireContext())
        binding.doctorsList.adapter = adapter
    }

    private fun loadUsersForCategory() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users")

        databaseReference.orderByChild("category").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<Map<String, Any>>()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.value as Map<String, Any>
                        users.add(user)
                    }
                    adapter.setData(users)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}
