package com.example.umarmasjid.data.local_database.model

import androidx.room.ColumnInfo
import java.time.LocalDate

data class DailyAttendanceCount(
    @ColumnInfo(name = "attendance_date")
    val date: LocalDate,

    @ColumnInfo(name = "presentCount")
    val presentCount: Int
)