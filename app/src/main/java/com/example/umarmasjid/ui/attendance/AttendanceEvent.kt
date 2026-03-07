package com.example.umarmasjid.ui.attendance

//sealed class AttendanceEvent {
//    data class MarkAttendance(val studentId: Int, val isPresent: Boolean) : AttendanceEvent()
//}

sealed class AttendanceEvent {
    data class MarkAttendance(val studentId: Int, val isPresent: Boolean) : AttendanceEvent()
    object MarkAllPresent : AttendanceEvent()
    object MarkAllAbsent : AttendanceEvent()
    object ResetAttendance : AttendanceEvent()
}