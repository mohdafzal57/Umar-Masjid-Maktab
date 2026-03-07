package com.example.umarmasjid.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.data.local_database.model.StudentWithAttendance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId


class AttendanceRepository(private val attendanceDao: AttendanceDao) {

    suspend fun markAttendance(studentId: Int, date: Long, status: Boolean) {
        withContext(Dispatchers.IO) {
            val existing = attendanceDao.getAttendanceByDateRange(
                studentId = studentId,
                startOfDay = getDayRange().first,
                endOfDay = getDayRange().second
            )
            if (existing != null) {
                attendanceDao.update(
                    existing.copy(
                        studentId = studentId,
                        attendanceDate = date,
                        isPresent = status
                    )
                )
            } else {
                attendanceDao.insert(
                    AttendanceEntity(
                        studentId = studentId,
                        attendanceDate = date,
                        isPresent = status
                    )
                )
            }
        }

    }

    suspend fun insertAttendance(attendance: AttendanceEntity) {
        attendanceDao.insert(attendance)
    }
    suspend fun updateAttendance(attendance: AttendanceEntity) {
        attendanceDao.update(attendance)
    }

//    fun getAllAttendanceByDate(date: String): Flow<List<AttendanceEntity>> =
//        attendanceDao.getAllAttendanceByDate(date)

    suspend fun deleteAttendance(attendance: AttendanceEntity) {
        attendanceDao.delete(attendance)
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

@RequiresApi(Build.VERSION_CODES.O)
fun getDayRange(): Pair<Long, Long> {
    val date = LocalDate.now()
    val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endOfDay = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return startOfDay to endOfDay
}
