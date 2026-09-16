package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.data.local_database.model.StudentWithPayment
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("UPDATE payments SET deleted_at = :timestamp WHERE id = :paymentId")
    suspend fun softDeletePayment(paymentId: String, timestamp: Long)

    @Query("""
        SELECT 
            s.id AS studentId,
            s.name AS studentName,
            s.phone_number AS phoneNumber,
            p.payment_date AS paymentDate,
            p.amount AS amount,
            p.note AS note
        FROM students s
        INNER JOIN payments p 
            ON s.id = p.student_id
            AND p.payment_date >= :startOfMonth
            AND p.payment_date < :startOfNextMonth
        """)
    fun getAllStudentsWithPayment(
        startOfMonth: LocalDate,
        startOfNextMonth: LocalDate
    ): Flow<List<StudentWithPayment>>

    @Query(
        """
        SELECT * FROM payments
        WHERE student_id = :studentId
        ORDER BY payment_date DESC
        """
    )
    fun getPaymentsTillNowByStudentId(
        studentId: String
    ): Flow<List<PaymentEntity>>

    @Query(
        """
    SELECT SUM(amount)
    FROM payments
    WHERE payment_date >= :startOfMonth
      AND payment_date < :startOfNextMonth
    """
    )
    fun getMonthlyPaymentTotal(
        startOfMonth: LocalDate,
        startOfNextMonth: LocalDate
    ): Flow<Double?>
}