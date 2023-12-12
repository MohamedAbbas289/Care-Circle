package com.example.carecircle.model

data class Appointment(
    var id: String? = null,
    val patientName: String? = null,
    val doctorId: String? = null,
    val patientId: String? = null,
    val date: String? = null,
    val status: String? = null
)
