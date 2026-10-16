package com.example.umarmasjid.feature.home

import androidx.compose.runtime.Immutable
import com.example.umarmasjid.core.data.preferences.DarkThemeConfig
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale

@Immutable
data class HomeUiState(
    val greeting: String = "Assalamu Alaikum",
    val islamicDate: IslamicDate = IslamicDate(),
    val prayerTimes: List<PrayerTime> = emptyList(),
    val stats: DashboardStats = DashboardStats(),
    val attendanceTrend: List<AttendancePoint> = emptyList(),
    val attendancePeriod: AttendancePeriod = AttendancePeriod.SEVEN_DAYS,
    val recentStudents: List<StudentSummary> = emptyList(),
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@Immutable
data class IslamicDate(
    val hijriDate: String = getHijriDate(),
    val gregorianDate: String = getGregorianDate()
)

fun getGregorianDate(): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    return LocalDate.now().format(formatter)
}

fun getHijriDate(): String {
    val hijri = HijrahDate.now()

    val formatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.DAY_OF_MONTH)
        .appendLiteral(" ")
        .appendText(ChronoField.MONTH_OF_YEAR) // Month name
        .appendLiteral(" ")
        .appendValue(ChronoField.YEAR)
        .toFormatter(Locale.ENGLISH)

    return formatter.format(hijri)
}

@Immutable
data class PrayerTime(
    val name: String,
    val time: String,
    val isNext: Boolean = false,
)

@Immutable
data class DashboardStats(
    val totalStudents: Int = 0,
    val studentDelta: Int = 0,
    val presentToday: Int = 0,
    val attendanceRate: String = "",
    val feesCollected: String = "",
    val feesCollectedPeriod: String = "This month",
    val pendingFees: String = "",
    val pendingStudentsCount: Int = 0,
)

@Immutable
data class AttendancePoint(
    val day: String,
    val percentage: Float,
)

@Immutable
data class StudentSummary(
    val id: String,
    val initials: String,
    val name: String,
    val feeStatus: FeeStatus,
)

enum class FeeStatus { PAID, PENDING }

enum class AttendancePeriod(val label: String) {
    SEVEN_DAYS("7 Days"),
    THIRTY_DAYS("30 Days"),
    THREE_MONTHS("3 Months"),
}
