package com.example.umarmasjid.feature.attendance

import com.example.umarmasjid.data.local_database.model.StudentAttendance

sealed class AttendanceUiState {
    data object Loading: AttendanceUiState()
    data class Success(val students: List<StudentAttendance>): AttendanceUiState()
}