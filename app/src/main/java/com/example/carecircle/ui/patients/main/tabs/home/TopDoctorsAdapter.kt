package com.example.carecircle.ui.patients.main.tabs.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.DocItemBinding
import com.example.carecircle.model.Doctor
import java.util.ArrayList

class TopDoctorsAdapter(var doctorsList: MutableList<Doctor>) :
    RecyclerView.Adapter<TopDoctorsAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: DocItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
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
        val itemBinding = DocItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = doctorsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = doctorsList[position]
        holder.bind(item)
        if (onItemClickListener != null) {
            holder.itemBinding.root.setOnClickListener {
                onItemClickListener?.onItemClick(position, item.id!!)
            }
        }

    }

    fun setFilteredList(mlist: ArrayList<Doctor>) {
        this.doctorsList = mlist
        notifyDataSetChanged()
    }

    var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int, docId: String)
    }
}