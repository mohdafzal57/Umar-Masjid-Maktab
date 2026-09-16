package com.example.umarmasjid.data.local_database.model

import java.time.LocalDate

data class StudentWithPayment(
    val studentId: String,
    val studentName: String,
    val phoneNumber: String,
    val paymentDate: LocalDate,
    val amount: Double,
    val note: String?
)