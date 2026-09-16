package com.example.umarmasjid.repository

import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.data.local_database.model.StudentAttendance
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao
) {

    fun getStudentsWithAttendance(date: LocalDate): Flow<List<StudentAttendance>> {
        return attendanceDao.getStudentsWithAttendance(date)
    }

    suspend fun markAttendance(studentAttendance: StudentAttendance) {
        val today = LocalDate.now()
        attendanceDao.getAttendanceForToday(
            studentId = studentAttendance.studentId,
            today = today
        )?.let { existing ->
            attendanceDao.update(
                existing.copy(
                    attendanceDate = today,
                    attendanceStatus = studentAttendance.status!!,
                    updatedAt = System.currentTimeMillis(),
                )
            )
        } ?: attendanceDao.insert(
            AttendanceEntity(
                studentId = studentAttendance.studentId,
                attendanceDate = today,
                attendanceStatus = studentAttendance.status!!,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
        )
    }

    // weekly // monthly // yearly
    fun getAttendanceForMonth(
        studentId: String,
        startOfMonth: LocalDate,
        startOfNextMonth: LocalDate
    ): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceForMonth(
            studentId = studentId,
            startDate = startOfMonth,
            endDateExclusive = startOfNextMonth
        )
    }
}
