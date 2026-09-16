package com.example.umarmasjid.data.local_database.model

data class StudentAttendance(
    val studentId: String,
    val name: String,
    val status: AttendanceStatus? = AttendanceStatus.PRESENT // sensible default
)

enum class AttendanceStatus { PRESENT, ABSENT, LATE }
