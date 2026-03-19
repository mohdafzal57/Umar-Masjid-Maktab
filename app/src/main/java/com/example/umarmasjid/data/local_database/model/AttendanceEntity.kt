package com.example.umarmasjid.data.local_database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendances",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [ Index(value = ["student_id", "id"], unique = true) ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "attendance_date")
    val attendanceDate: Long,

    @ColumnInfo(name = "present")
    val isPresent: Boolean,
)

data class DailyAttendanceCount(
    @ColumnInfo(name = "attendance_date") val date: Long,
    @ColumnInfo(name = "presentCount") val presentCount: Int
)
