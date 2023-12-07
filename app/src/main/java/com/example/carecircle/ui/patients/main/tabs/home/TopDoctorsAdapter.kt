package com.example.carecircle.ui.patients.main.tabs.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carecircle.databinding.DocItemBinding
import com.example.carecircle.model.Doctor

class TopDoctorsAdapter(val doctorsList: MutableList<Doctor>) :
    RecyclerView.Adapter<TopDoctorsAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: DocItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(doctor: Doctor) {
            itemBinding.docName.text = doctor.name
            itemBinding.speciality.text = doctor.speciality
            itemBinding.ratingBar1.rating = doctor.ratting
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

    var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int, docId: String)
    }
}