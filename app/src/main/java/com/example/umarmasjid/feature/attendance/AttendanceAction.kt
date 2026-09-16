package com.example.umarmasjid.feature.attendance

import com.example.umarmasjid.data.local_database.model.StudentAttendance

sealed class AttendanceAction {
    data class MarkAttendance(
        val studentAttendance: StudentAttendance
    ) : AttendanceAction()
}