package com.example.umarmasjid.ui.payment

import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.data.local_database.model.StudentWithPayment

data class PaymentState(
    val isLoading: Boolean = false,
    val paidStudents: List<StudentWithPayment> = emptyList(),
    val unpaidStudents: List<StudentEntity> = emptyList()
)