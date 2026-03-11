package com.example.umarmasjid.feature.attendance_bar_chart

import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.UmarMasjidNavigator
import kotlinx.serialization.Serializable

@Serializable data class AttendanceChartNavKey(val studentId: Int) : NavKey

fun UmarMasjidNavigator.navigateToAttendanceChart(studentId: Int) {
    navigate(AttendanceChartNavKey(studentId))
}