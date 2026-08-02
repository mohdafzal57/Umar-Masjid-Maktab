package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.data.local_database.model.StudentWithPayment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    @Query(
        """
        SELECT 
            s.id AS studentId,
            s.name AS studentName,
            s.phone_number AS phoneNumber,
            p.payment_date AS paymentDate,
            p.amount AS amount,
            p.note AS note
        FROM students s
        INNER JOIN payments p ON s.id = p.student_id
        WHERE p.payment_date BETWEEN :startOfMonth AND :endOfMonth
        """
    )
    fun getAllStudentsWithPayment(
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<List<StudentWithPayment>>

    @Query(
        """
        SELECT
            id,
            student_id,
            payment_date,
            amount,
            note
        FROM payments
        WHERE student_id = :studentId
        ORDER BY payment_date DESC
        """
    )
    fun getPaymentsTillNowByStudentId(
        studentId: Int
    ): Flow<List<PaymentEntity>>

    @Query(
        """
        SELECT SUM(amount)
        FROM payments
        WHERE payment_date BETWEEN :startOfMonth AND :endOfMonth
        """
    )
    fun getMonthlyPaymentTotal(
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<Double?>
}