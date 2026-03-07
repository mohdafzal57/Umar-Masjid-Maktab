package com.example.umarmasjid.data.local_database.model

data class StudentWithAttendance(
    val studentId: Int,
    val name: String,
    val present: Boolean?   // matches `a.present`
)
