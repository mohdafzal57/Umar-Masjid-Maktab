package com.example.umarmasjid.repository

import androidx.room.withTransaction
import com.example.umarmasjid.data.local_database.AppDatabase
import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.dao.SyncEntityType
import com.example.umarmasjid.data.local_database.dao.SyncOperationDao
import com.example.umarmasjid.data.local_database.dao.SyncOperationType
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.data.local_database.model.StudentAttendance
import com.example.umarmasjid.data.local_database.model.SyncOperationEntity
import com.example.umarmasjid.data.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineAttendanceRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val attendanceDao: AttendanceDao,
    private val syncOperationDao: SyncOperationDao,
    private val syncManager: SyncManager
) {

    fun getStudentsWithAttendance(date: LocalDate): Flow<List<StudentAttendance>> {
        return attendanceDao.getStudentsWithAttendance(date)
    }

    suspend fun markAttendance(studentAttendance: StudentAttendance) {
        val today = LocalDate.now()
        val now = System.currentTimeMillis()
        val status = studentAttendance.status ?: return

        appDatabase.withTransaction {
            val existing = attendanceDao.getAttendanceForToday(
                studentId = studentAttendance.studentId,
                today = today
            )

            if (existing != null) {
                val updated = existing.copy(
                    attendanceDate = today,
                    attendanceStatus = status,
                    updatedAt = now
                )
                val syncOp = SyncOperationEntity(
                    entityId = updated.id,
                    entityType = SyncEntityType.ATTENDANCE,
                    operation = SyncOperationType.UPDATE,
                    createdAt = now
                )

                attendanceDao.update(updated)
                syncOperationDao.insert(syncOp)
            } else {
                val recordId = UUID.randomUUID().toString()
                val newRecord = AttendanceEntity(
                    id = recordId,
                    studentId = studentAttendance.studentId,
                    attendanceDate = today,
                    attendanceStatus = status,
                    createdAt = now,
                    updatedAt = now
                )
                val syncOp = SyncOperationEntity(
                    entityId = recordId,
                    entityType = SyncEntityType.ATTENDANCE,
                    operation = SyncOperationType.INSERT,
                    createdAt = now
                )

                attendanceDao.insert(newRecord)
                syncOperationDao.insert(syncOp)
            }
        }

        syncManager.requestSync()
    }

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