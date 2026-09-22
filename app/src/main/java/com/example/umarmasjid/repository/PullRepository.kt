package com.example.umarmasjid.repository

import androidx.room.withTransaction
import com.example.umarmasjid.data.dto.AttendanceDto
import com.example.umarmasjid.data.dto.PaymentDto
import com.example.umarmasjid.data.dto.StudentDto
import com.example.umarmasjid.data.local_database.AppDatabase
import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.data.local_database.dao.SyncStateDao
import com.example.umarmasjid.data.local_database.model.SyncStateEntity
import com.example.umarmasjid.data.mappers.toEntity
import com.example.umarmasjid.data.repository.SupabaseAuthRepository
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PullRepository @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
    private val supabaseAuthRepository: SupabaseAuthRepository,
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val paymentDao: PaymentDao,
    private val syncStateDao: SyncStateDao,
    private val appDatabase: AppDatabase
) {
    suspend fun pull() {
        val teacherId = supabaseAuthRepository.teacherId
        val lastSyncAt = syncStateDao.getLastSyncedAt(teacherId)

        if (lastSyncAt == null) {
            initialSync(teacherId)
        } else {
            incrementalSync(teacherId, lastSyncAt)
        }
    }

    suspend fun initialSync(teacherId: String) {
        val students = supabaseRepository.getByColumn<StudentDto>(
            tableName = "students",
            columnName = "teacher_id",
            value = teacherId
        )
        val attendances = supabaseRepository.getAll<AttendanceDto>(tableName = "attendances")
        val payments = supabaseRepository.getAll<PaymentDto>(tableName = "payments")
        val now = System.currentTimeMillis()

        appDatabase.withTransaction {
            students.forEach { student->
                if (student.deletedAt == null) {
                    studentDao.insertStudent(student.toEntity())
                }
            }
            attendances.forEach {
                if (it.deletedAt == null) {
                    attendanceDao.insert(it.toEntity())
                }
            }
            payments.forEach {
                if (it.deletedAt == null) {
                    paymentDao.insertPayment(it.toEntity())
                }
            }
            syncStateDao.upsert(
                SyncStateEntity(
                    userId = teacherId,
                    lastSyncedAt = now
                )
            )
        }
    }

    suspend fun incrementalSync(teacherId: String, lastSyncAt: Long) {
        val lastSyncedAtIso =
            Instant.ofEpochMilli(lastSyncAt).toString()

        val students = supabaseRepository.getStudentsUpdatedAfter<StudentDto>(
            teacherId = teacherId,
            updatedAt = lastSyncedAtIso
        )
        val attendances =
            supabaseRepository.getAttendancesUpdatedAfter<AttendanceDto>(
                teacherId = teacherId,
                updatedAt = lastSyncedAtIso
            )

        val payments =
            supabaseRepository.getPaymentsUpdatedAfter<PaymentDto>(
                teacherId = teacherId,
                updatedAt = lastSyncedAtIso
            )

        val now = System.currentTimeMillis()

        appDatabase.withTransaction {
            students.forEach { student ->
                if (student.deletedAt == null) {
                    studentDao.insertStudent(student.toEntity())
                } else {
                    studentDao.deleteStudent(student.id, now)
                }
            }

            attendances.forEach { attendance ->

                if (attendance.deletedAt != null) {
                    attendanceDao.softDeleteAttendance(attendance.id, now)
                } else {
                    attendanceDao.insert(
                        attendance.toEntity()
                    )
                }
            }

            payments.forEach { payment ->

                if (payment.deletedAt != null) {
                    paymentDao.softDeletePayment(payment.id, now)
                } else {
                    paymentDao.insertPayment(
                        payment.toEntity()
                    )
                }
            }

            syncStateDao.upsert(
                SyncStateEntity(
                    userId = teacherId,
                    lastSyncedAt = now
                )
            )
        }
    }

}