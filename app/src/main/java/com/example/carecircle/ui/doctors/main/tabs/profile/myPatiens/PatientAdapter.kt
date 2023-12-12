package com.example.carecircle.ui.doctors.main.tabs.profile.myPatiens

import User
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.MyPatientItemBinding

class PatientAdapter(val patientsList: MutableList<User>) :
    RecyclerView.Adapter<PatientAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: MyPatientItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(patient: User) {
            itemBinding.patientName.text = patient.userName
            Glide.with(itemBinding.root.context)
                .load(patient.profileImage)
                .placeholder(R.drawable.profile_pic) // Set a placeholder image
                .into(itemBinding.patientImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = MyPatientItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = patientsList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = patientsList.size

}


