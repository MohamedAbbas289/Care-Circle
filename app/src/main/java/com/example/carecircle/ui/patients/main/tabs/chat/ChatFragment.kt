package com.example.carecircle.ui.patients.main.tabs.chat

import User
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.FragmentChatBinding
import com.example.carecircle.model.ChatList
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private var adapter: ChatListAdapter? = null
    private var mUsers: List<User>? = null
    private var usersChatList: List<ChatList>? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersChatList = ArrayList()
        val ref =
            FirebaseDatabase.getInstance().reference.child("chatList").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (usersChatList as ArrayList).clear()
                for (dataSnapShot in snapshot.children) {
                    val chatList = dataSnapShot.getValue(ChatList::class.java)
                    (usersChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        initProfileImage()

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
                val userName = snapshot.child("userName").getValue(String::class.java)
                binding.userName.text = userName
                val profileImage = snapshot.child("profileImage").getValue(String::class.java)
                Glide.with(this@ChatFragment)
                    .load(profileImage)
                    .placeholder(R.drawable.profile_pic)
                    .into(binding.profileImage)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }

    private fun retrieveChatList() {
        mUsers = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for (dataSnapShot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)
                    for (eachChatList in usersChatList!!) {
                        if (user!!.userId.equals(eachChatList.getId())) {
                            (mUsers as ArrayList).add(user)
                        }

                    }
                }
                adapter = ChatListAdapter((mUsers as ArrayList<User>))
                binding.chatsRecycler.adapter = adapter
                adapter!!.onItemClickListener =
                    ChatListAdapter.OnItemClickListener { position, userId ->
                        val intent = Intent(context, ChatInboxActivity::class.java).apply {
                            putExtra("DOCTOR_ID", userId)
                        }
                        startActivity(intent)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}