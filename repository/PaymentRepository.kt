package com.example.umarmasjid.repository

import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.data.local_database.model.StudentWithPayment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val paymentDao: PaymentDao
) {
    suspend fun insertPayment(payment: PaymentEntity) {
        paymentDao.insertPayment(payment)
    }

    suspend fun updatePayment(payment: PaymentEntity) {
        paymentDao.updatePayment(payment)
    }

    suspend fun deletePayment(payment: PaymentEntity) {
        paymentDao.deletePayment(payment)
    }

    fun getPaymentsTillNowByStudentId(
        studentId: Int
    ): Flow<List<PaymentEntity>> {
        return paymentDao.getPaymentsTillNowByStudentId(
            studentId = studentId
        )
    }

    fun getAllStudentsWithPaymentWithinMonth(
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<List<StudentWithPayment>> {
        return paymentDao.getAllStudentsWithPayment(startOfMonth, endOfMonth)
    }
}
