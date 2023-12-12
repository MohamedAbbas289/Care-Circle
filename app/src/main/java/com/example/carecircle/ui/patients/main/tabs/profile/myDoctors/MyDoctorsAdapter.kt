package com.example.carecircle.ui.patients.main.tabs.profile.myDoctors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.MyDoctorsItemBinding
import com.example.carecircle.model.Doctor

class MyDoctorsAdapter(val doctorsList: MutableList<Doctor>) :
    RecyclerView.Adapter<MyDoctorsAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: MyDoctorsItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(doctor: Doctor) {
            itemBinding.docName.text = doctor.name
            itemBinding.speciality.text = doctor.speciality
            itemBinding.ratingBar1.rating = doctor.ratting
            Glide.with(itemBinding.root.context)
                .load(doctor.profileImage)
                .placeholder(R.drawable.profile_pic) // Set a placeholder image
                .into(itemBinding.docImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = MyDoctorsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = doctorsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = doctorsList[position]
        holder.bind(item)
        if (onItemClickListener != null) {
            holder.itemBinding.chatIcon.setOnClickListener {
                onItemClickListener?.onItemClick(position, item.id!!)
            }
        }
    }

    var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int, docId: String)
    }
}