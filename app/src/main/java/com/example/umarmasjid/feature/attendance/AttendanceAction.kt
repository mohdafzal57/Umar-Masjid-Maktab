package com.example.umarmasjid.feature.attendance

sealed class AttendanceAction {
    data class MarkAttendance(val studentId: Int, val isPresent: Boolean) : AttendanceAction()
    object MarkAllPresent : AttendanceAction()
    object MarkAllAbsent : AttendanceAction()
    object ResetAttendance : AttendanceAction()
}