package com.example.umarmasjid.repository

import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.data.local_database.model.StudentWithPayment
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val paymentDao: PaymentDao
) {
    suspend fun insertPayment(payment: PaymentEntity) {
        paymentDao.insertPayment(payment)
    }

    suspend fun updatePayment(payment: PaymentEntity) {
        paymentDao.updatePayment(payment)
    }

    suspend fun deletePayment(paymentId: String, timestamp: Long) {
        paymentDao.softDeletePayment(paymentId, timestamp)
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
