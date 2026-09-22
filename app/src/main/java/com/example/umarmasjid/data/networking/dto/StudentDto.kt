package com.example.umarmasjid.data.networking.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentDto(
    val id: String,
    @SerialName("teacher_id")
    val teacherId: String,
    val name: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("enrollment_date")
    val enrollmentDate: String, // "YYYY-MM-DD"
    @SerialName("created_at")
    val createdAt: String,       // ISO-8601 string
    @SerialName("updated_at")
    val updatedAt: String,       // ISO-8601 string
    @SerialName("deleted_at")
    val deletedAt: String? = null
)
