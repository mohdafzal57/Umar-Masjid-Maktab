package com.example.umarmasjid.data.networking.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentDto(
    val id: String,
    @SerialName("student_id")
    val studentId: String,
    @SerialName("payment_date")
    val paymentDate: String,
    val amount: Double,
    val note: String? = null,
    @SerialName("created_at")
    val createdAt: String,       // ISO-8601 string
    @SerialName("updated_at")
    val updatedAt: String,       // ISO-8601 string
    @SerialName("deleted_at")
    val deletedAt: String? = null
)
