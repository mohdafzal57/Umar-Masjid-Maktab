package com.example.umarmasjid.repository

import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.dao.StudentDao
import kotlinx.coroutines.flow.Flow

class QuickOverviewRepository(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val paymentDao: PaymentDao
) {
    fun getTotalStudents(): Flow<Int> {
        return studentDao.getStudentCount()
    }
    fun getTodayAttendance(): Flow<Int> {
        val (start, end) = getDayRange()
        return attendanceDao.getPresentCount(start, end)
    }
    fun getTotalPaymentThisMonth(): Flow<Double?> {
        val (start, end) = getDayRange()
        return paymentDao.getMonthlyPaymentTotal(start, end)
    }
}
