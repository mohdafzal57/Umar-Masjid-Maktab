package com.example.umarmasjid.data.local_database

import androidx.room.TypeConverter
import com.example.umarmasjid.data.local_database.model.AttendanceStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toAttendanceStatus(value: String?): AttendanceStatus? {
        return value?.let { AttendanceStatus.valueOf(it) }
    }
}
