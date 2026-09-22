package com.example.umarmasjid.repository

import androidx.room.withTransaction
import com.example.umarmasjid.data.dto.AttendanceDto
import com.example.umarmasjid.data.dto.PaymentDto
import com.example.umarmasjid.data.dto.StudentDto
import com.example.umarmasjid.data.local_database.AppDatabase
import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.data.local_database.dao.SyncEntityType
import com.example.umarmasjid.data.local_database.dao.SyncOperationDao
import com.example.umarmasjid.data.local_database.dao.SyncOperationType
import com.example.umarmasjid.data.local_database.dao.SyncStateDao
import com.example.umarmasjid.data.local_database.model.SyncOperationEntity
import com.example.umarmasjid.data.local_database.model.SyncStateEntity
import com.example.umarmasjid.data.mappers.toDto
import com.example.umarmasjid.data.mappers.toEntity
import com.example.umarmasjid.data.repository.SupabaseAuthRepository
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
    private val supabaseAuthRepository: SupabaseAuthRepository,
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val paymentDao: PaymentDao,
    private val syncStateDao: SyncStateDao,
    private val appDatabase: AppDatabase,
    private val syncDao: SyncOperationDao,
) {
    suspend fun pullRemoteChanges() {
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

    suspend fun pushLocalChanges() {
        val pendingOps = syncDao.getAllPending()
        for (op in pendingOps) {
            processOperation(op)
            syncDao.deleteById(op.id)
        }
    }

    private suspend fun processOperation(op: SyncOperationEntity) {
        when(op.entityType) {
            SyncEntityType.STUDENT -> syncStudent(op)
            SyncEntityType.ATTENDANCE -> syncAttendance(op)
            SyncEntityType.PAYMENT -> syncPayment(op)
        }
    }

    private suspend fun syncPayment(op: SyncOperationEntity) {
        when (op.operation) {
            SyncOperationType.INSERT, SyncOperationType.UPDATE -> {
                val payment = paymentDao.getPaymentById(op.entityId) ?: return
                supabaseRepository.upsert(tableName = "payments", value = payment.toDto())
            }
            SyncOperationType.DELETE -> {
                supabaseRepository.deleteByColumn(
                    tableName = "payments",
                    columnName = "id",
                    value = op.entityId
                )
            }
        }
    }

    private suspend fun syncAttendance(op: SyncOperationEntity) {
        when (op.operation) {
            SyncOperationType.INSERT, SyncOperationType.UPDATE -> {
                val attendance = attendanceDao.getAttendanceById(op.entityId) ?: return
                supabaseRepository.upsert(tableName = "attendances", value = attendance.toDto())
            }
            SyncOperationType.DELETE -> {
                supabaseRepository.deleteByColumn(
                    tableName = "attendances",
                    columnName = "id",
                    value = op.entityId
                )
            }
        }
    }

    private suspend fun syncStudent(op: SyncOperationEntity) {
        when(op.operation) {
            SyncOperationType.INSERT, SyncOperationType.UPDATE -> {
                val student = studentDao.getStudentEntityById(op.entityId) ?: return
                supabaseRepository.upsert(tableName = "students", value = student.toDto())
            }
            SyncOperationType.DELETE -> {
                supabaseRepository.deleteByColumn(
                    tableName = "students",
                    columnName = "id",
                    value = op.entityId
                )
            }
        }
    }

}