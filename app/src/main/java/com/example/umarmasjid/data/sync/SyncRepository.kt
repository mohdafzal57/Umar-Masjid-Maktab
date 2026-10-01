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
            SyncOperationType.INSERT, SyncOperationType.UPDATE -> {
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
        val groupedOps = pendingOps.groupBy { it.entityType }

        groupedOps[SyncEntityType.STUDENT]?.let { ops ->
            syncStudents(ops)
        }
        groupedOps[SyncEntityType.ATTENDANCE]?.let { ops ->
            syncAttendances(ops)
        }
        groupedOps[SyncEntityType.PAYMENT]?.let { ops ->
            syncPayments(ops)
        }
    }

    private suspend fun syncStudents(op: List<SyncOperationEntity>) {
        val upsertOps = op.filter {
            it.operation in listOf(SyncOperationType.INSERT, SyncOperationType.UPDATE)
        }
        val deleteOps = op.filter { it.operation == SyncOperationType.DELETE }

        if (upsertOps.isNotEmpty()) {
            val studentIds = upsertOps.map { it.entityId }
            val students: List<StudentDto> = studentDao
                .getStudentEntitiesByIds(studentIds)
                .map { it.toDto() }

            if (students.isNotEmpty()) {
                // Upsert a List of DTOs in ONE network call
                supabaseRemoteDataSource
                    .upsertAll(
                        tableName = STUDENTS_TABLE,
                        values = students
                    )
            }
            // Delete synced operations from local database
            syncDao.deleteByIds(op.map { it.id })
        }

        /**
         * Created a PostgreSQL function to soft-delete students using tombstones
         * and cascade the deletion to their attendances and payments.
         */
        if (deleteOps.isNotEmpty()) {
            val studentIds = deleteOps.map { it.entityId }
            val timestamp = Instant.ofEpochMilli(System.currentTimeMillis()).toString()

            if (studentIds.isNotEmpty()) {
                supabaseRemoteDataSource.softDeleteStudents(
                    studentIds = studentIds,
                    timestamp = timestamp
                )
            }
            syncDao.deleteByIds(deleteOps.map { it.id })
        }
    }

    private suspend fun syncAttendances(op: List<SyncOperationEntity>) {
        val upsertOps = op.filter {
            it.operation in listOf(
                SyncOperationType.INSERT,
                SyncOperationType.UPDATE
            )
        }

        val deleteOps = op.filter {
            it.operation == SyncOperationType.DELETE
        }

        // Upsert
        if (upsertOps.isNotEmpty()) {
            val attendanceIds = upsertOps.map { it.entityId }

            val attendances: List<AttendanceDto> = attendanceDao
                .getAttendanceEntitiesByIds(attendanceIds)
                .map { it.toDto() }

            if (attendances.isNotEmpty()) {
                supabaseRemoteDataSource.upsertAll(
                    tableName = ATTENDANCES_TABLE,
                    values = attendances
                )
            }

            // Delete synced upsert operations
            syncDao.deleteByIds(upsertOps.map { it.id })
        }

        // Soft delete
        if (deleteOps.isNotEmpty()) {
            val attendanceIds = deleteOps.map { it.entityId }

            val timestamp =
                Instant.ofEpochMilli(System.currentTimeMillis()).toString()

            supabaseRemoteDataSource.updateByColumnIn(
                tableName = ATTENDANCES_TABLE,
                columnName = ID_COLUMN,
                values = attendanceIds,
                updateData = SoftDeletePayload(
                    deletedAt = timestamp,
                    updatedAt = timestamp
                )
            )

            // Delete synced delete operations
            syncDao.deleteByIds(deleteOps.map { it.id })
        }
    }

    private suspend fun syncPayments(op: List<SyncOperationEntity>) {
        val upsertOps = op.filter {
            it.operation in listOf(
                SyncOperationType.INSERT,
                SyncOperationType.UPDATE
            )
        }

        val deleteOps = op.filter {
            it.operation == SyncOperationType.DELETE
        }

        // Upsert
        if (upsertOps.isNotEmpty()) {
            val paymentIds = upsertOps.map { it.entityId }

            val payments: List<PaymentDto> = paymentDao
                .getPaymentEntitiesByIds(paymentIds)
                .map { it.toDto() }

            if (payments.isNotEmpty()) {
                supabaseRemoteDataSource.upsertAll(
                    tableName = PAYMENTS_TABLE,
                    values = payments
                )
            }

            // Delete synced upsert operations
            syncDao.deleteByIds(upsertOps.map { it.id })
        }

        // Soft delete
        if (deleteOps.isNotEmpty()) {
            val paymentIds = deleteOps.map { it.entityId }

            val timestamp =
                Instant.ofEpochMilli(System.currentTimeMillis()).toString()

            supabaseRemoteDataSource.updateByColumnIn(
                tableName = PAYMENTS_TABLE,
                columnName = ID_COLUMN,
                values = paymentIds,
                updateData = SoftDeletePayload(
                    deletedAt = timestamp,
                    updatedAt = timestamp
                )
            )

            // Delete synced delete operations
            syncDao.deleteByIds(deleteOps.map { it.id })
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