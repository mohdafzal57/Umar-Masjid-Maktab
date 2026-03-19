package com.example.umarmasjid.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.core.data.preferences.SystemSettings
import com.example.umarmasjid.core.data.preferences.SystemSettingsPreferences
import com.example.umarmasjid.core.domain.util.onError
import com.example.umarmasjid.core.domain.util.onSuccess
import com.example.umarmasjid.domain.model.PrayerDay
import com.example.umarmasjid.domain.repository.PrayerTimesRepository
import com.example.umarmasjid.repository.HomeDashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeDashboardRepository,
    private val prayerTimesRepository: PrayerTimesRepository,
    private val settings: SystemSettingsPreferences
) : ViewModel() {

    private val _attendancePeriod = MutableStateFlow(AttendancePeriod.SEVEN_DAYS)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _prayerResult = MutableStateFlow(PrayerResult())

    init {
        loadPrayerTimes()
    }

    @Suppress("UNCHECKED_CAST")
    val state: StateFlow<HomeUiState> = combine(
        settings.settings,
        _prayerResult,
        dashboardStatsFlow(),
        recentStudentsFlow(),
        attendanceTrendFlow(),
        _attendancePeriod,
        _isLoading,
        _error
    ) { values ->
        val systemSettings = values[0] as SystemSettings
        val prayerRes = values[1] as PrayerResult
        val stats = values[2] as DashboardStats
        val recentStudents = values[3] as List<StudentSummary>
        val trend = values[4] as List<AttendancePoint>
        val period = values[5] as AttendancePeriod
        val loading = values[6] as Boolean
        val error = values[7] as String?

        HomeUiState(
            isDarkMode = systemSettings.darkMode,
            islamicDate = prayerRes.islamicDate,
            prayerTimes = prayerRes.prayerTimes,
            stats = stats,
            recentStudents = recentStudents,
            attendanceTrend = trend,
            attendancePeriod = period,
            isLoading = loading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    private fun loadPrayerTimes() {
        viewModelScope.launch {
            val now = LocalDate.now()
            _isLoading.update { true }
            _error.update { null }
            
            prayerTimesRepository
                .getMonthlyPrayerTimes(now.year, now.monthValue)
                .onSuccess { prayerDays ->
                    val prayerDay = prayerDays.getOrNull(now.dayOfMonth - 1)
                    if (prayerDay != null) {
                        _prayerResult.update {
                            PrayerResult(
                                prayerTimes = mapPrayerTimes(prayerDay),
                                islamicDate = IslamicDate(
                                    hijriDate = formatHijri(prayerDay.date.hijriDate),
                                    gregorianDate = formatGregorian(prayerDay.date.gregorianDate)
                                )
                            )
                        }
                    } else {
                        _error.update { "Prayer times unavailable for today" }
                    }
                }
                .onError { _error.update { "Failed to load prayer times" } }
            
            _isLoading.update { false }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.ToggleDarkMode -> toggleDarkMode()
            is HomeAction.SelectAttendancePeriod -> _attendancePeriod.update { action.period }
            else -> Unit
        }
    }

    private fun dashboardStatsFlow(): Flow<DashboardStats> = combine(
        homeRepository.getTotalStudents(),
        homeRepository.getTodayAttendance(),
        homeRepository.getMonthlyFeesCollected(),
        homeRepository.getStudentsWithPendingFees()
    ) { total, present, feesCollected, pendingStudents ->
        val rate = if (total > 0) "%.1f%%".format((present.toFloat() / total) * 100f) else "0%"
        val averageFee = 200.00 
        
        DashboardStats(
            totalStudents = total,
            presentToday = present,
            attendanceRate = "$rate rate",
            feesCollected = "₹${formatAmount(feesCollected)}",
            pendingFees = "₹${formatAmount(pendingStudents.size * averageFee)}",
            pendingStudentsCount = pendingStudents.size
        )
    }

    private fun recentStudentsFlow(): Flow<List<StudentSummary>> =
        combine(
            homeRepository.getRecentStudents(),
            homeRepository.getStudentsWithPendingFees()
        ) { recent, pending ->
            val pendingIds = pending.map { it.id }.toSet()
            recent.map { student ->
                StudentSummary(
                    id = student.id,
                    initials = student.name.initials(),
                    name = student.name,
                    feeStatus = if (student.id in pendingIds) FeeStatus.PENDING else FeeStatus.PAID
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun attendanceTrendFlow(): Flow<List<AttendancePoint>> =
        _attendancePeriod.flatMapLatest { period ->
            homeRepository.getAttendanceTrend(period)
        }

    private fun toggleDarkMode() {
        viewModelScope.launch {
            settings.setDarkMode(!state.value.isDarkMode)
        }
    }

    private fun String.initials(): String =
        split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }

    private fun formatAmount(amount: Double): String =
        when {
            amount >= 1000 -> "%.1fK".format(amount / 1000)
            else -> "%.0f".format(amount)
        }

    private fun mapPrayerTimes(day: PrayerDay): List<PrayerTime> {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val now = LocalTime.now()

        val prayerList = listOf(
            "Fajr" to day.timings.fajr,
            "Dhuhr" to day.timings.dhuhr,
            "Asr" to day.timings.asr,
            "Maghrib" to day.timings.maghrib,
            "Isha" to day.timings.isha
        )

        val parsedPrayers = prayerList.map { (name, timeStr) ->
            val cleanTime = timeStr.substringBefore(" ")
            name to try {
                LocalTime.parse(cleanTime, formatter)
            } catch (e: Exception) {
                LocalTime.MIDNIGHT
            }
        }

        val nextPrayerIndex = parsedPrayers.indexOfFirst { it.second.isAfter(now) }
            .let { if (it == -1) 0 else it }

        val displayFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

        return parsedPrayers.mapIndexed { index, (name, time) ->
            PrayerTime(
                name = name,
                time = time.format(displayFormatter),
                isNext = index == nextPrayerIndex
            )
        }
    }

    private fun formatGregorian(date: String): String {
        return try {
            val input = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val output = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)
            LocalDate.parse(date, input).format(output)
        } catch (e: Exception) {
            date
        }
    }

    private fun formatHijri(date: String): String {
        return try {
            val parts = date.split("-")
            val day = parts[0].toInt()
            val monthIndex = parts[1].toInt() - 1
            val year = parts[2]
            "$day ${hijriMonths.getOrElse(monthIndex) { parts[1] }} $year"
        } catch (e: Exception) {
            date
        }
    }

    private val hijriMonths = listOf(
        "Muharram", "Safar", "Rabi al-Awwal", "Rabi al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qadah", "Dhu al-Hijjah"
    )
}

private data class PrayerResult(
    val prayerTimes: List<PrayerTime> = emptyList(),
    val islamicDate: IslamicDate = IslamicDate()
)
