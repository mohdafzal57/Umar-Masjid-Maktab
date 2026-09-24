package com.example.umarmasjid.repository

import androidx.room.withTransaction
import com.example.umarmasjid.data.local_database.AppDatabase
import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.dao.SyncEntityType
import com.example.umarmasjid.data.local_database.dao.SyncOperationDao
import com.example.umarmasjid.data.local_database.dao.SyncOperationType
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.data.local_database.model.StudentWithPayment
import com.example.umarmasjid.data.local_database.model.SyncOperationEntity
import com.example.umarmasjid.data.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineFirstPaymentRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val paymentDao: PaymentDao,
    private val syncOperationDao: SyncOperationDao,
    private val syncManager: SyncManager
) {
    suspend fun insertPayment(
        studentId: String,
        amount: Double,
        note: String?,
    ) {
        val now = System.currentTimeMillis()
        val payment = PaymentEntity(
            studentId = studentId,
            amount = amount,
            note = note,
            paymentDate = LocalDate.now(),
            createdAt = now,
            updatedAt = now
        )

        val syncOp = SyncOperationEntity(
            entityId = payment.id,
            entityType = SyncEntityType.PAYMENT,
            operation = SyncOperationType.INSERT,
            createdAt = now
        )

        appDatabase.withTransaction {
            paymentDao.insertPayment(payment)
            syncOperationDao.insert(syncOp)
        }

        syncManager.requestSync()
    }

    // We don't give update payment functionality
    // user has to delete it and create new.
    suspend fun updatePayment(payment: PaymentEntity) {
        val now = System.currentTimeMillis()
        val syncOp = SyncOperationEntity(
            entityId = payment.id,
            entityType = SyncEntityType.PAYMENT,
            operation = SyncOperationType.UPDATE,
            createdAt = now
        )

        appDatabase.withTransaction {
            paymentDao.updatePayment(payment)
            syncOperationDao.insert(syncOp)
        }

        syncManager.requestSync()
    }

    suspend fun deletePayment(paymentId: String) {
        val timestamp = System.currentTimeMillis()

        val syncOp = SyncOperationEntity(
            entityId = paymentId,
            entityType = SyncEntityType.PAYMENT,
            operation = SyncOperationType.DELETE,
            createdAt = timestamp
        )

        appDatabase.withTransaction {
            paymentDao.softDeletePayment(paymentId, timestamp)
            syncOperationDao.insert(syncOp)
        }

        syncManager.requestSync()
    }

    fun getPaymentsTillNowByStudentId(
        studentId: String
    ): Flow<List<PaymentEntity>> {
        return paymentDao.getPaymentsTillNowByStudentId(
            studentId = studentId
        )
    }

    fun getAllStudentsWithPaymentWithinMonth(
        startOfMonth: LocalDate,
        startOfNextMonth: LocalDate
    ): Flow<List<StudentWithPayment>> {
        return paymentDao.getAllStudentsWithPayment(
            startOfMonth = startOfMonth,
            startOfNextMonth = startOfNextMonth
        )
    }
}