package com.example.umarmasjid.feature.home

import androidx.compose.runtime.Immutable

@Immutable
data class HomeState(
    val greeting: String = "Assalamu Alaikum",
    val islamicDate: IslamicDate = IslamicDate(),
    val prayerTimes: List<PrayerTime> = emptyList(),
    val stats: DashboardStats = DashboardStats(),
    val attendanceTrend: List<AttendancePoint> = emptyList(),
    val attendancePeriod: AttendancePeriod = AttendancePeriod.SEVEN_DAYS,
    val recentStudents: List<StudentSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@Immutable
data class IslamicDate(
    val hijriDate: String = "15 Rajab 1446",
    val gregorianDate: String = "Thursday, January 16, 2025",
    val isDarkMode: Boolean = false,
)

@Immutable
data class PrayerTime(
    val name: String,
    val time: String,
    val isNext: Boolean = false,
)

@Immutable
data class DashboardStats(
    val totalStudents: Int = 248,
    val studentDelta: Int = 12,
    val presentToday: Int = 235,
    val attendanceRate: String = "94.8% rate",
    val feesCollected: String = "₹18.5K",
    val feesCollectedPeriod: String = "This month",
    val pendingFees: String = "₹4.2K",
    val pendingStudentsCount: Int = 23,
)

@Immutable
data class AttendancePoint(
    val day: String,
    val percentage: Float,
)

@Immutable
data class StudentSummary(
    val id: Int,
    val initials: String,
    val name: String,
    val grade: String,
    val studentCode: String,
    val attendancePercent: Int,
    val feeStatus: FeeStatus,
    val avatarColor: Long,
)

enum class FeeStatus { PAID, PENDING }

enum class AttendancePeriod(val label: String) {
    SEVEN_DAYS("7 Days"),
    THIRTY_DAYS("30 Days"),
    THREE_MONTHS("3 Months"),
}
