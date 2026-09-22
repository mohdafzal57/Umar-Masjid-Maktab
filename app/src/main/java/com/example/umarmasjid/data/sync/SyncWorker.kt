package com.example.umarmasjid.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.data.local_database.dao.SyncEntityType
import com.example.umarmasjid.data.local_database.dao.SyncOperationDao
import com.example.umarmasjid.data.local_database.dao.SyncOperationType
import com.example.umarmasjid.data.local_database.model.SyncOperationEntity
import com.example.umarmasjid.data.mappers.toDto
import com.example.umarmasjid.repository.PullRepository
import com.example.umarmasjid.repository.SupabaseRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncDao: SyncOperationDao,
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val paymentDao: PaymentDao,
    private val supabaseRepository: SupabaseRepository,
    private val pullRepository: PullRepository
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {
        return try {
            push()
            pullRepository.pull()

            Result.success()
        } catch (e: Exception) {
            // THIS IS WHY IT IS RETRYING
            Log.e("SyncWorker", "Sync failed! Attempt: $runAttemptCount", e)

            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun push() {
        val pendingOps = syncDao.getAllPending()
        Log.d("SyncWorker", "Found ${pendingOps.size} pending operations")

        for (op in pendingOps) {
            Log.d("SyncWorker", "Processing op: ${op.id}, entity: ${op.entityType}, type: ${op.operation}")
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
