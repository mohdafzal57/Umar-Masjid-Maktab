package com.example.umarmasjid.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {
    @Serializable data object Home : Destination
    @Serializable data object StudentProfile : Destination
    @Serializable data object StudentAttendance : Destination
    @Serializable data object FeePayment : Destination
    @Serializable data class StudentPayment(val studentId: Int) : Destination
    @Serializable data class AttendanceChart(val studentId: Int) : Destination
    @Serializable data object Report : Destination
}

const val STUDENT_ID = "studentId"
