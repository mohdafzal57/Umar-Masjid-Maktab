package com.example.umarmasjid.repository

import androidx.room.withTransaction
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
import com.example.umarmasjid.data.networking.dto.AttendanceDto
import com.example.umarmasjid.data.networking.dto.PaymentDto
import com.example.umarmasjid.data.networking.dto.SoftDeletePayload
import com.example.umarmasjid.data.networking.dto.StudentDto
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
    suspend fun sync() {
        pushLocalChanges()
        pullRemoteChanges()
    }

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
        val syncWatermark = System.currentTimeMillis()

        val students = supabaseRepository.getByColumn<StudentDto>(
            tableName = STUDENTS_TABLE,
            columnName = TEACHER_ID_COLUMN,
            value = teacherId
        )

        val attendances = supabaseRepository.getAll<AttendanceDto>(
            tableName = ATTENDANCES_TABLE
        )

        val payments = supabaseRepository.getAll<PaymentDto>(
            tableName = PAYMENTS_TABLE
        )

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
                    lastSyncedAt = syncWatermark
                )
            )
        }
    }

    suspend fun incrementalSync(teacherId: String, lastSyncAt: Long) {
        // Capture the watermark BEFORE starting the pull.
        val syncWatermark = System.currentTimeMillis()

        // Get all changes since last sync
        // Goes to postgreSQL
        val lastSyncedAtIso = Instant.ofEpochMilli(lastSyncAt).toString()

        val students = supabaseRepository.getStudentsUpdatedAfter<StudentDto>(
            teacherId = teacherId,
            updatedAt = lastSyncedAtIso
        )
        println("SYNC_REPO: students ${students}")
        val attendances =
            supabaseRepository.getAttendancesUpdatedAfter<AttendanceDto>(
                teacherId = teacherId,
                updatedAt = lastSyncedAtIso
            )
        println("SYNC_REPO: attendances ${attendances}")
        val payments =
            supabaseRepository.getPaymentsUpdatedAfter<PaymentDto>(
                updatedAt = lastSyncedAtIso
            )
        println("SYNC_REPO: payments ${payments}")
        appDatabase.withTransaction {
            students.forEach { student ->
                if (student.deletedAt == null) {
                    println("SYNC_REPO: deleteAt is NULL ${student}")
                    studentDao.insertStudent(student.toEntity())
                } else {
                    println("SYNC_REPO: deleteAt is NOT null ${student}")
                    studentDao.deleteStudent(student.id, syncWatermark)
                }
            }

            attendances.forEach { attendance ->

                if (attendance.deletedAt != null) {
                    attendanceDao.softDeleteAttendance(attendance.id, syncWatermark)
                } else {
                    attendanceDao.insert(
                        attendance.toEntity()
                    )
                }
            }

            payments.forEach { payment ->

                if (payment.deletedAt != null) {
                    paymentDao.softDeletePayment(payment.id, syncWatermark)
                } else {
                    paymentDao.insertPayment(
                        payment.toEntity()
                    )
                }
            }

            syncStateDao.upsert(
                SyncStateEntity(
                    userId = teacherId,
                    lastSyncedAt = syncWatermark
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
                supabaseRepository.upsert(
                    tableName = PAYMENTS_TABLE,
                    value = payment.toDto()
                )
            }
            SyncOperationType.DELETE -> {
                supabaseRepository.updateByColumn(
                    tableName = PAYMENTS_TABLE,
                    columnName = ID_COLUMN,
                    value = op.entityId,
                    updateData = SoftDeletePayload(
                        deletedAt = Instant.ofEpochMilli(System.currentTimeMillis()).toString()
                    )
                )
            }
        }
    }

    private suspend fun syncAttendance(op: SyncOperationEntity) {
        when (op.operation) {
            SyncOperationType.INSERT, SyncOperationType.UPDATE -> {
                val attendance = attendanceDao.getAttendanceById(op.entityId) ?: return
                supabaseRepository.upsert(tableName = ATTENDANCES_TABLE, value = attendance.toDto())
            }
            SyncOperationType.DELETE -> {
                supabaseRepository.updateByColumn(
                    tableName = ATTENDANCES_TABLE,
                    columnName = ID_COLUMN,
                    value = op.entityId,
                    updateData = SoftDeletePayload(
                        deletedAt = Instant.ofEpochMilli(System.currentTimeMillis()).toString()
                    )
                )
            }
        }
    }

    private suspend fun syncStudent(op: SyncOperationEntity) {
        when(op.operation) {
            SyncOperationType.INSERT, SyncOperationType.UPDATE -> {
                val student =
                    studentDao.getStudentEntityById(op.entityId) ?: return
                supabaseRepository.upsert(
                    tableName = STUDENTS_TABLE,
                    value = student.toDto()
                )
            }
            SyncOperationType.DELETE -> {
                // Does not need anymore!
                /*supabaseRepository.updateByColumn(
                    tableName = "students",
                    columnName = "id",
                    value = op.entityId,
                    updateData = SoftDeletePayload(
                        deleted_at = Instant.ofEpochMilli(System.currentTimeMillis()).toString()
                    )
                )*/
                /***
                 * Create a fun in postgreSQL to soft-delete
                 * student as well as payments, attendances
                 */
                supabaseRepository.softDeleteStudent(
                    studentId = op.entityId,
                    timestamp = Instant.ofEpochMilli(System.currentTimeMillis()).toString()
                )
            }
        }
    }

    companion object {
        // Supabase table names
        const val STUDENTS_TABLE = "students"
        const val ATTENDANCES_TABLE = "attendances"
        const val PAYMENTS_TABLE = "payments"

        // Supabase column names
        const val ID_COLUMN = "id"
        const val TEACHER_ID_COLUMN = "teacher_id"
    }
}