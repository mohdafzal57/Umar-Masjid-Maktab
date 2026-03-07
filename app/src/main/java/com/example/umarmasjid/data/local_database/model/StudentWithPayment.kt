package com.example.umarmasjid.data.local_database.model

data class StudentWithPayment(
    val studentId: Int,
    val studentName: String,
    val phoneNumber: String,
    val paymentDate: Long,
    val amount: Double,
    val paymentMethod: String,
    val receiptNumber: String?
)