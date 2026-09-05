package com.example.umarmasjid.data.local_database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "attendances",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(
            value = ["student_id", "attendance_date"],
            unique = true
        )
    ]
)
data class AttendanceEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "student_id")
    val studentId: String,

    @ColumnInfo(name = "attendance_date")
    val attendanceDate: LocalDate,

    @ColumnInfo(name = "present")
    val isPresent: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null
)

data class DailyAttendanceCount(
    @ColumnInfo(name = "attendance_date") val date: Long,
    @ColumnInfo(name = "presentCount") val presentCount: Int
)
