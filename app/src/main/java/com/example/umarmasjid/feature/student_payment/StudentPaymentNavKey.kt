package com.example.umarmasjid.feature.student_payment

import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.UmarMasjidNavigator
import kotlinx.serialization.Serializable

@Serializable data class StudentPaymentNavKey(val studentId: Int) : NavKey

fun UmarMasjidNavigator.navigateToStudentPayment(studentId: Int) {
    navigate(StudentPaymentNavKey(studentId))
}