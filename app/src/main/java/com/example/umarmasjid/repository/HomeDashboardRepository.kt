package com.example.umarmasjid.repository

import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.feature.home.AttendancePeriod
import com.example.umarmasjid.feature.home.AttendancePoint
import com.example.umarmasjid.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeDashboardRepository @Inject constructor(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val paymentDao: PaymentDao
) {

    private val startOfTheMonth = DateUtils.getCurrentMonthRange().first
    private val startOfNextMonth = DateUtils.getCurrentMonthRange().second

    fun getTotalStudents(): Flow<Int> {
        return studentDao.getStudentCount()
    }

    fun getRecentStudents(): Flow<List<StudentEntity>> =
        studentDao.getRecentStudents(limit = 5)

    fun getStudentsWithPendingFees(): Flow<List<StudentEntity>> {
        return studentDao.getStudentsWhoNotPaid(startOfTheMonth, startOfNextMonth)
    }

    fun getTodayAttendance(): Flow<Int> {
        return attendanceDao.getPresentCount(LocalDate.now())
    }

    fun getAttendanceTrend(period: AttendancePeriod): Flow<List<AttendancePoint>> {
        val ranges = period.dateRanges()
        return combine(
            ranges.map { (label, start, end) ->
                combine(
                    attendanceDao.getDailyPresentCounts(startOfTheMonth, startOfNextMonth),
                    studentDao.getStudentCount()
                ) { dailyCounts, totalStudents ->
                    val present = dailyCounts.sumOf { it.presentCount }
                    val percentage = if (totalStudents > 0)
                        (present.toFloat() / totalStudents * 100f).coerceIn(0f, 100f)
                    else 0f
                    AttendancePoint(day = label, percentage = percentage)
                }
            }
        ) { it.toList() }
    }

    fun getMonthlyFeesCollected(): Flow<Double> {
        return paymentDao.getMonthlyPaymentTotal(startOfTheMonth, startOfNextMonth).map { it ?: 0.0 }
    }

    private data class PeriodRange(val label: String, val start: Long, val end: Long)

    private fun AttendancePeriod.dateRanges(): List<PeriodRange> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now()
        return when (this) {
            AttendancePeriod.SEVEN_DAYS -> (6 downTo 0).map { daysAgo ->
                val date = today.minusDays(daysAgo.toLong())
                PeriodRange(
                    label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    start = date.atStartOfDay(zone).toInstant().toEpochMilli(),
                    end = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
                )
            }

            AttendancePeriod.THIRTY_DAYS -> (3 downTo 0).map { weeksAgo ->
                val weekStart = today.minusWeeks(weeksAgo.toLong()).with(DayOfWeek.MONDAY)
                val weekEnd = weekStart.plusDays(6)
                PeriodRange(
                    label = "W${4 - weeksAgo}",
                    start = weekStart.atStartOfDay(zone).toInstant().toEpochMilli(),
                    end = weekEnd.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()
                )
            }

            AttendancePeriod.THREE_MONTHS -> (2 downTo 0).map { monthsAgo ->
                val month = today.minusMonths(monthsAgo.toLong())
                val monthStart = month.withDayOfMonth(1)
                val monthEnd = month.withDayOfMonth(month.lengthOfMonth())
                PeriodRange(
                    label = month.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    start = monthStart.atStartOfDay(zone).toInstant().toEpochMilli(),
                    end = monthEnd.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()
                )
            }
        }
    }
}