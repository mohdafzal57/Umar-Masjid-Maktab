package com.example.umarmasjid.data.networking.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceDto(
    val id: String,
    @SerialName("student_id")
    val studentId: String,
    @SerialName("attendance_date")
    val attendanceDate: String,
    @SerialName("attendance_status")
    val attendanceStatus: String,
    @SerialName("created_at")
    val createdAt: String,       // ISO-8601 string
    @SerialName("updated_at")
    val updatedAt: String,       // ISO-8601 string
    @SerialName("deleted_at")
    val deletedAt: String? = null
)
