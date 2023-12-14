package com.example.carecircle.ui.doctors.main.tabs.profile.myPatiens

import User
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.PatientCardItemBinding

class PatientCardAdapter(val patientsList: MutableList<User>) :
    RecyclerView.Adapter<PatientCardAdapter.ViewHolder>() { // Fix: Change PatientAdapter.ViewHolder to PatientCardAdapter.ViewHolder

    class ViewHolder(val itemBinding: PatientCardItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(patient: User) {
            itemBinding.userName.text = patient.userName
            Glide.with(itemBinding.root.context)
                .load(patient.profileImage)
                .placeholder(R.drawable.profile_pic) // Set a placeholder image
                .into(itemBinding.profileImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = PatientCardItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = patientsList[position]
        holder.bind(item)
        if (onItemClickListener != null) {
            holder.itemBinding.root.setOnClickListener {
                onItemClickListener?.onItemClick(position, item.userId!!)
            }
        }
    }

    override fun getItemCount(): Int = patientsList.size

    var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int, docId: String)
    }
}
