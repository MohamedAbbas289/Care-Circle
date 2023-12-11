package com.example.carecircle.ui.patients.main.tabs.chat

import User
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.ChatUserItemBinding


class ChatListAdapter(val users: List<User>) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {
    class ViewHolder(val binding: ChatUserItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChatUserItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val user = users[position]
            binding.userName.text = user.userName
            val context = holder.itemView.context

            Glide.with(context)
                .load(user.profileImage)
                .placeholder(R.drawable.profile_pic)
                .into(holder.binding.userImg)
            if (onItemClickListener != null) {
                binding.root.setOnClickListener {
                    onItemClickListener?.onItemClick(position, user.userId!!)
                }
            }
        }
    }

    var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int, userId: String)
    }
}