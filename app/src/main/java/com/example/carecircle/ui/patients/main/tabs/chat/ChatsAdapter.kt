package com.example.carecircle.ui.patients.main.tabs.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout.LayoutParams
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChatsAdapter(
    context: Context,
    chatList: List<Chat>,
    imageUrl: String
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    private val context: Context
    private val chatList: List<Chat>
    private val imageUrl: String
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.context = context
        this.chatList = chatList
        this.imageUrl = imageUrl
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null

        init {
            text_message = itemView.findViewById(R.id.text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int = chatList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = chatList[position]

        //image message
        if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {
            //image message - right side
            if (chat.sender.equals(firebaseUser.uid)) {
                holder.text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Glide.with(context)
                    .load(chat.url)
                    .into(holder.right_image_view!!)
            }
            //image message - left side
            else if (!chat.sender.equals(firebaseUser.uid)) {
                holder.text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Glide.with(context)
                    .load(chat.url)
                    .into(holder.left_image_view!!)
            }
        }
        //text message
        else {
            holder.text_message!!.text = chat.message
        }
        //sent and seen messages
        if (position == chatList.size - 1) {
            if (chat.isSeen) {
                holder.text_seen!!.text = "Seen"
                if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {
                    val lp: LayoutParams? = holder.text_seen!!.layoutParams as LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            } else {
                holder.text_seen!!.text = "Sent"
                if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {
                    val lp: LayoutParams? = holder.text_seen!!.layoutParams as LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
        } else {
            holder.text_seen!!.visibility = View.GONE
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].sender.equals(firebaseUser.uid)) {
            1
        } else {
            0
        }
    }
}