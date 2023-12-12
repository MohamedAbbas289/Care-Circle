package com.example.carecircle.ui.doctors.main.tabs.profile.appointments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carecircle.databinding.ItemAppointmentBinding
import com.example.carecircle.model.Appointment

class AppointmentsAdapter(var appointmentsList: MutableList<Appointment>) :
    RecyclerView.Adapter<AppointmentsAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(appointment: Appointment) {
            itemBinding.patientName.text = appointment.patientName
            itemBinding.date.text = "on date: ${appointment.date}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemAppointmentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = appointmentsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = appointmentsList[position]
        holder.bind(item)
        if (onItemClickListener != null) {
            holder.itemBinding.acceptBtn.setOnClickListener {
                if (item.id != null) {
                    onItemClickListener?.onItemClick(position, item.id)
                }
            }
        }
    }

    var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int, itemId: String?)
    }

}