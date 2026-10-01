package com.example.umarmasjid.data.sync

import androidx.room.withTransaction
import com.example.umarmasjid.data.local_database.AppDatabase
import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.data.local_database.dao.SyncCursorDao
import com.example.umarmasjid.data.local_database.dao.SyncEntityType
import com.example.umarmasjid.data.local_database.dao.SyncOperationDao
import com.example.umarmasjid.data.local_database.dao.SyncOperationType
import com.example.umarmasjid.data.local_database.model.SyncCursorEntity
import com.example.umarmasjid.data.local_database.model.SyncOperationEntity
import com.example.umarmasjid.data.mappers.toDto
import com.example.umarmasjid.data.mappers.toEntity
import com.example.umarmasjid.data.networking.SupabaseRemoteDataSource
import com.example.umarmasjid.data.networking.dto.AttendanceDto
import com.example.umarmasjid.data.networking.dto.PaymentDto
import com.example.umarmasjid.data.networking.dto.SoftDeletePayload
import com.example.umarmasjid.data.networking.dto.StudentDto
import com.example.umarmasjid.data.networking.dto.SyncChangeDto
import com.example.umarmasjid.domain.repository.AuthRepository
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val supabaseRemoteDataSource: SupabaseRemoteDataSource,
    private val authRepository: AuthRepository,
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val paymentDao: PaymentDao,
    private val syncCursorDao: SyncCursorDao,
    private val appDatabase: AppDatabase,
    private val syncDao: SyncOperationDao,
) {
    suspend fun sync() {
        println("SYNC_REPO: sync()")
        pushLocalChanges()
        pullRemoteChanges()
    }

    suspend fun pullRemoteChanges() {
        val teacherId = authRepository.teacherId
        val lastVersion = syncCursorDao.getLastVersion()

        if (lastVersion == null) {
            initialSync(teacherId)
        } else {
            incrementalSync(teacherId, lastVersion)
        }
    }

    private suspend fun incrementalSync(teacherId: String, lastVersion: Long) {
        val changes =
            supabaseRemoteDataSource.getChangesAfterVersion(
                teacherId = teacherId,
                version = lastVersion
            )
        if (changes.isEmpty()) return

        appDatabase.withTransaction {
            for (change in changes) {
                when (change.entityType) {
                    SyncEntityType.STUDENT -> applyStudentChange(change)
                    SyncEntityType.ATTENDANCE -> applyAttendanceChange(change)
                    SyncEntityType.PAYMENT -> applyPaymentChange(change)
                }
            }
        }
    }

    private suspend fun applyStudentChange(change: SyncChangeDto) {
        when (change.operation) {
            SyncOperationType.INSERT,
            SyncOperationType.UPDATE -> {
                val student = supabaseRemoteDataSource.getById<StudentDto>(
                    tableName = STUDENTS_TABLE,
                    id = change.entityId
                )
                if (student == null) return

                if (student.deletedAt == null) {
                    studentDao.insertStudent(student.toEntity())
                } else {
                    studentDao.deleteStudent(
                        studentId = student.id,
                        timestamp = System.currentTimeMillis()
                    )
                }
            }

            SyncOperationType.DELETE -> {
                studentDao.deleteStudent(
                    studentId = change.entityId,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }

    private suspend fun applyAttendanceChange(change: SyncChangeDto) {
        when (change.operation) {
            SyncOperationType.INSERT,
            SyncOperationType.UPDATE -> {
                val attendance = supabaseRemoteDataSource.getById<AttendanceDto>(
                    tableName = ATTENDANCES_TABLE,
                    id = change.entityId
                )
                if (attendance == null) return

                if (attendance.deletedAt == null) {
                    attendanceDao.insert(attendance.toEntity())
                } else {
                    attendanceDao.softDeleteAttendance(
                        attendanceId = attendance.id,
                        timestamp = System.currentTimeMillis()
                    )
                }
            }
            SyncOperationType.DELETE -> {
                attendanceDao.softDeleteAttendance(
                    attendanceId = change.entityId,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }

    private suspend fun applyPaymentChange(change: SyncChangeDto) {
        when (change.operation) {
            SyncOperationType.INSERT,
            SyncOperationType.UPDATE -> {
                val payment =
                    supabaseRemoteDataSource.getById<PaymentDto>(
                        tableName = PAYMENTS_TABLE,
                        id = change.entityId
                    )
                if (payment == null) {
                    return
                }

                if (payment.deletedAt == null) {
                    paymentDao.insertPayment(
                        payment.toEntity()
                    )
                } else {
                    paymentDao.softDeletePayment(
                        paymentId = payment.id,
                        timestamp = System.currentTimeMillis()
                    )
                }
            }

            SyncOperationType.DELETE -> {
                paymentDao.softDeletePayment(
                    paymentId = change.entityId,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }

    suspend fun initialSync(teacherId: String) {
        val initialVersion = supabaseRemoteDataSource.getCurrentSyncVersion()

        val students = supabaseRemoteDataSource.getByColumn<StudentDto>(
            tableName = STUDENTS_TABLE,
            columnName = TEACHER_ID_COLUMN,
            value = teacherId
        )

        val attendances = supabaseRemoteDataSource.getAll<AttendanceDto>(
            tableName = ATTENDANCES_TABLE
        )

        val payments = supabaseRemoteDataSource.getAll<PaymentDto>(
            tableName = PAYMENTS_TABLE
        )

        println("SYNC_REPO [initial-sync] students: $students ")
        println("SYNC_REPO [initial-sync] attendance: $attendances")
        println("SYNC_REPO [initial-sync] payments: $payments")

        // The transaction guarantees that Students insert completely,
        // followed by Attendances, then Payments, and finally the Sync Cursor.
        // If any step fails, the entire block rolls back.
        appDatabase.withTransaction {
            val validStudents = students
                .filter { it.deletedAt == null }
                .map { it.toEntity() }
            studentDao.upsertAllStudents(validStudents)

            val validAttendances = attendances
                .filter { it.deletedAt == null }
                .map { it.toEntity() }
            attendanceDao.upsertAllAttendances(validAttendances)

            val validPayments = payments
                .filter { it.deletedAt == null }
                .map { it.toEntity() }
            paymentDao.upsertAllPayments(validPayments)

            syncCursorDao.upsert(
                SyncCursorEntity(
                    lastVersion = initialVersion
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
        when (op.entityType) {
            SyncEntityType.STUDENT -> syncStudent(op)
            SyncEntityType.ATTENDANCE -> syncAttendance(op)
            SyncEntityType.PAYMENT -> syncPayment(op)
        }
    }

    private suspend fun syncPayment(op: SyncOperationEntity) {
        when (op.operation) {
            SyncOperationType.INSERT, SyncOperationType.UPDATE -> {
                val payment = paymentDao.getPaymentById(op.entityId) ?: return
                println("SYNC_REPO: syncPayment(): $payment")
                supabaseRemoteDataSource.upsert(
                    tableName = PAYMENTS_TABLE,
                    value = payment.toDto()
                )
            }

            SyncOperationType.DELETE -> {
                val timestamp = Instant.ofEpochMilli(System.currentTimeMillis()).toString()
                supabaseRemoteDataSource.updateByColumn(
                    tableName = PAYMENTS_TABLE,
                    columnName = ID_COLUMN,
                    value = op.entityId,
                    updateData = SoftDeletePayload(
                        deletedAt = timestamp,
                        updatedAt = timestamp
                    )
                )
            }
        }
    }

    private suspend fun syncAttendance(op: SyncOperationEntity) {
        when (op.operation) {
            SyncOperationType.INSERT, SyncOperationType.UPDATE -> {
                val attendance = attendanceDao.getAttendanceById(op.entityId) ?: return
                supabaseRemoteDataSource.upsert(
                    tableName = ATTENDANCES_TABLE,
                    value = attendance.toDto()
                )
            }

            SyncOperationType.DELETE -> {
                val timestamp = Instant.ofEpochMilli(System.currentTimeMillis()).toString()
                supabaseRemoteDataSource.updateByColumn(
                    tableName = ATTENDANCES_TABLE,
                    columnName = ID_COLUMN,
                    value = op.entityId,
                    updateData = SoftDeletePayload(
                        deletedAt = timestamp,
                        updatedAt = timestamp
                    )
                )
            }
        }
    }

    private suspend fun syncStudent(op: SyncOperationEntity) {
        when (op.operation) {
            SyncOperationType.INSERT, SyncOperationType.UPDATE -> {
                val student =
                    studentDao.getStudentEntityById(op.entityId) ?: return
                supabaseRemoteDataSource.upsert(
                    tableName = STUDENTS_TABLE,
                    value = student.toDto()
                )
            }

            SyncOperationType.DELETE -> {
                // Does not need anymore! because it only deletes student
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
                supabaseRemoteDataSource.softDeleteStudent(
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