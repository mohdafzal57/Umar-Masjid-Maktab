package com.example.umarmasjid.repository

import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.data.local_database.model.StudentWithAttendance
import com.example.umarmasjid.util.todayRange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao
) {

    suspend fun markAttendance(
        studentId: Int,
        date: Long,
        status: Boolean
    ) {
        attendanceDao.getAttendanceByDateRange(
            studentId = studentId,
            startOfDay = todayRange().first,
            endOfDay = todayRange().second
        )?.let { existing ->
            attendanceDao.update(
                existing.copy(
                    attendanceDate = date,
                    isPresent = status
                )
            )
        } ?: attendanceDao.insert(
            AttendanceEntity(
                studentId = studentId,
                attendanceDate = date,
                isPresent = status
            )
        )
    }

    fun getStudentsWithAttendance(date: Long): Flow<List<StudentWithAttendance>> {
        return attendanceDao.getStudentsWithAttendance(date)
    }

    fun getAttendanceForMonth(studentId: Int, startOfDay: Long, endOfDay: Long):
            Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByStudentId(
            studentId = studentId,
            startOfDay = startOfDay,
            endOfDay = endOfDay
        )
    }
}
