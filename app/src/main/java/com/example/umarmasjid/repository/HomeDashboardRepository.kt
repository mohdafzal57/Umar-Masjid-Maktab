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

        // Create a list of Flows for each period range
        val pointFlows = ranges.map { range ->
            attendanceDao.getAttendancePercentageForRange(range.start, range.endExclusive)
                .map { percentage ->
                    AttendancePoint(
                        day = range.label,
                        percentage = percentage ?: 0f // Default to 0 if no data
                    )
                }
        }

        // Combine them all into a single Flow<List<AttendancePoint>>
        return combine(pointFlows) { it.toList() }
    }

    fun getMonthlyFeesCollected(): Flow<Double> {
        return paymentDao.getMonthlyPaymentTotal(startOfTheMonth, startOfNextMonth)
            .map { it ?: 0.0 }
    }

    private data class PeriodRange(
        val label: String,
        val start: LocalDate,
        val endExclusive: LocalDate
    )

    private fun AttendancePeriod.dateRanges(): List<PeriodRange> {
        val today = LocalDate.now()
        return when (this) {
            AttendancePeriod.SEVEN_DAYS -> (6 downTo 0).map { daysAgo ->
                val date = today.minusDays(daysAgo.toLong())
                PeriodRange(
                    label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    start = date,
                    endExclusive = date.plusDays(1)
                )
            }

            AttendancePeriod.THIRTY_DAYS -> (3 downTo 0).map { weeksAgo ->
                val weekStart = today.minusWeeks(weeksAgo.toLong()).with(DayOfWeek.MONDAY)
                PeriodRange(
                    label = "W${4 - weeksAgo}",
                    start = weekStart,
                    endExclusive = weekStart.plusWeeks(1)
                )
            }

            AttendancePeriod.THREE_MONTHS -> (2 downTo 0).map { monthsAgo ->
                val month = today.minusMonths(monthsAgo.toLong())
                val monthStart = month.withDayOfMonth(1)
                PeriodRange(
                    label = month.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    start = monthStart,
                    endExclusive = monthStart.plusMonths(1)
                )
            }
        }
    }
}