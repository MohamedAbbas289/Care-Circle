package com.example.carecircle.ui.patients.main.tabs.chat

import User
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.api.ApiServer
import com.example.carecircle.databinding.ActivityChatInboxBinding
import com.example.carecircle.model.Chat
import com.example.carecircle.model.MyResponse
import com.example.carecircle.model.Sender
import com.example.carecircle.model.Token
import com.example.carecircle.ui.notification.Client
import com.example.carecircle.ui.notification.NewData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatInboxActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatInboxBinding
    private lateinit var doctorId: String
    private lateinit var firebaseUser: FirebaseUser
    private var chatsAdapter: ChatsAdapter? = null
    private var chatList: List<Chat>? = null
    private lateinit var recyclerViewChat: RecyclerView
    var reference: DatabaseReference? = null
    var notify = false
    var apiServer: ApiServer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatInboxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        apiServer =
            Client.Client.getClient("https://fcm.googleapis.com/")!!.create(ApiServer::class.java)

        doctorId = intent.getStringExtra("DOCTOR_ID").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        recyclerViewChat = binding.recyclerViewChat
        recyclerViewChat.setHasFixedSize(true)


        reference = FirebaseDatabase.getInstance().reference
            .child("users").child(doctorId)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)
                binding.userName.text = user?.userName
                Glide.with(this@ChatInboxActivity)
                    .load(user?.profileImage)
                    .placeholder(R.drawable.profile_pic)
                    .into(binding.profileImage)

                retrieveMessages(firebaseUser.uid, doctorId, user!!.profileImage)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        binding.sendBtn.setOnClickListener {
            notify = true
            val message = binding.messageTxt.text.toString()
            if (message.isNotBlank()) {
                sendMessageToUser(firebaseUser.uid, doctorId, message)
                binding.messageTxt.setText("")
            }

        }

        binding.attachmentBtn.setOnClickListener {
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }
        seenMessage(doctorId)
    }

//


    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isSeen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey!!

        reference.child("chats")
            .child(messageKey)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("chatList")
                        .child(firebaseUser.uid)
                        .child(doctorId)
                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatsListReference.child("id").setValue(doctorId)
                            }
                            val chatsListReceiverRef = FirebaseDatabase.getInstance()
                                .reference
                                .child("chatList")
                                .child(doctorId)
                                .child(firebaseUser.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseUser.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
                }
            }
        // implement the push notification using FCM
        val userReference = FirebaseDatabase.getInstance().reference
            .child("users").child(firebaseUser.uid)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (notify) {
                    sendNotification(doctorId, user!!.userName, message)
                }
                notify = false
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun sendNotification(doctorId: String, userName: String?, message: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")

        val query = ref.orderByKey().equalTo(doctorId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot in snapshot.children) {
                    val token: Token? = dataSnapShot.getValue(Token::class.java)
                    val data = NewData(
                        firebaseUser.uid,
                        R.mipmap.ic_launcher,
                        "$userName: $message",
                        "New Message",
                        doctorId
                    )
                    val sender = Sender(data, token!!.token.toString())

                    apiServer!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()!!.success != 1) {
                                        Toast.makeText(
                                            this@ChatInboxActivity,
                                            "Failed, nothing happened",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }

                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 438 && resultCode == Activity.RESULT_OK && data?.data != null) {
            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("please wait")
            loadingBar.show()
            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("chat images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)
            uploadTask.continueWithTask<Uri>(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any>()
                    messageHashMap["sender"] = firebaseUser.uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = doctorId
                    messageHashMap["isSeen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId!!
                    ref.child("chats").child(messageId).setValue(messageHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                loadingBar.dismiss()
                                // implement the push notification using FCM
                                val reference = FirebaseDatabase.getInstance().reference
                                    .child("users").child(firebaseUser.uid)
                                reference.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val user = snapshot.getValue(User::class.java)
                                        if (notify) {
                                            sendNotification(
                                                doctorId,
                                                user!!.userName,
                                                "sent you an image."
                                            )
                                        }
                                        notify = false
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }

                                })
                            }
                        }

                }
            }
        }
    }

    private fun retrieveMessages(senderId: String, receiverId: String, receiverImageUrl: String?) {
        chatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (chatList as ArrayList<Chat>).clear()

                for (snapshot in snapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.receiver.equals(senderId) && chat.sender.equals(receiverId)
                        || chat.receiver.equals(receiverId) && chat.sender.equals(senderId)
                    ) {
                        // Update isSeen based on the value from Firebase
                        chat.isSeen =
                            snapshot.child("isSeen").getValue(Boolean::class.java) ?: false
                        (chatList as ArrayList<Chat>).add(chat)
                    }
                }

                chatsAdapter = ChatsAdapter(
                    this@ChatInboxActivity,
                    chatList as ArrayList<Chat>,
                    receiverImageUrl!!
                )
                recyclerViewChat.adapter = chatsAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    var seenListener: ValueEventListener? = null
    private fun seenMessage(doctorId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if (chat!!.receiver.equals(firebaseUser.uid) && chat.sender.equals(doctorId)) {
                        val hashmap = HashMap<String, Any>()
                        hashmap["isSeen"] = true
                        dataSnapShot.ref.updateChildren(hashmap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}
