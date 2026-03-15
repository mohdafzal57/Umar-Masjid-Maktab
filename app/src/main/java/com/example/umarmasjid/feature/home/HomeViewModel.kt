package com.example.umarmasjid.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.core.domain.util.onError
import com.example.umarmasjid.core.domain.util.onSuccess
import com.example.umarmasjid.domain.MonthlyPrayerTimesRequest
import com.example.umarmasjid.domain.model.PrayerDay
import com.example.umarmasjid.domain.repository.PrayerTimesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prayerTimesRepository: PrayerTimesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(buildInitialState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadPrayerTimes()
    }

    private fun loadPrayerTimes() {
        viewModelScope.launch {

            val now = LocalDate.now()
            val year = now.year
            val month = now.monthValue

            prayerTimesRepository
                .getMonthlyPrayerTimes(year, month)
                .onSuccess { prayerDays ->
                    val today = now.dayOfMonth - 1

                    val prayerDay = prayerDays[today]

                    _state.update {
                        it.copy(
                            prayerTimes = mapPrayerTimes(prayerDay),
                            islamicDate = it.islamicDate.copy(
                                hijriDate = formatHijri(prayerDay.date.hijriDate),
                                gregorianDate = formatGregorian(prayerDay.date.gregorianDate)
                            )
                        )
                    }
                }
                .onError {
                    _state.update { it.copy(error = "Failed to load prayer times") }
                }

        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.ToggleDarkMode -> toggleDarkMode()
            is HomeAction.RefreshData -> refresh()
            is HomeAction.DismissError -> _state.update { it.copy(error = null) }
            is HomeAction.SelectAttendancePeriod -> selectPeriod(action.period)
            is HomeAction.NavigateToAttendance,
            is HomeAction.NavigateToStudents,
            is HomeAction.NavigateToFees,
            is HomeAction.NavigateToStudent -> Unit
        }
    }

    private fun toggleDarkMode() {
        _state.update { s ->
            s.copy(islamicDate = s.islamicDate.copy(isDarkMode = !s.islamicDate.isDarkMode))
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(800)
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun selectPeriod(period: AttendancePeriod) {
        _state.update {
            it.copy(
                attendancePeriod = period,
                attendanceTrend = fakeAttendance(period)
            )
        }
    }

    private fun buildInitialState() = HomeUiState(
        prayerTimes = listOf(
            PrayerTime("Fajr", "--:--   "),
            PrayerTime("Dhuhr", "--:--   "),
            PrayerTime("Asr", "--:--   "),
            PrayerTime("Maghrib", "--:--   "),
            PrayerTime("Isha", "--:--   "),
        ),
        attendanceTrend = fakeAttendance(AttendancePeriod.SEVEN_DAYS),
        recentStudents = listOf(
            StudentSummary(
                1,
                "AH",
                "Ahmed Hassan",
                "Grade 5",
                "STD-001",
                96,
                FeeStatus.PAID,
                0xFF5C8AFF
            ),
            StudentSummary(
                2,
                "FA",
                "Fatima Ali",
                "Grade 6",
                "STD-002",
                98,
                FeeStatus.PAID,
                0xFF7C6AF5
            ),
            StudentSummary(
                3,
                "MK",
                "Mohammed Khan",
                "Grade 4",
                "STD-003",
                89,
                FeeStatus.PENDING,
                0xFFF5A623
            ),
            StudentSummary(
                4,
                "AS",
                "Aisha Siddiqui",
                "Grade 7",
                "STD-004",
                95,
                FeeStatus.PAID,
                0xFFE891C8
            ),
            StudentSummary(
                5,
                "ZA",
                "Zainab Ahmed",
                "Grade 5",
                "STD-005",
                92,
                FeeStatus.PAID,
                0xFF91C8E4
            ),
        ),
    )

    private fun fakeAttendance(period: AttendancePeriod) = when (period) {
        AttendancePeriod.SEVEN_DAYS -> listOf(
            AttendancePoint("Mon", 95f), AttendancePoint("Tue", 94f),
            AttendancePoint("Wed", 98f), AttendancePoint("Thu", 96f),
            AttendancePoint("Fri", 99f), AttendancePoint("Sat", 93f),
            AttendancePoint("Sun", 95f),
        )

        AttendancePeriod.THIRTY_DAYS -> listOf(
            AttendancePoint("W1", 94f),
            AttendancePoint("W2", 96f),
            AttendancePoint("W3", 92f),
            AttendancePoint("W4", 97f),
        )

        AttendancePeriod.THREE_MONTHS -> listOf(
            AttendancePoint("Jan", 95f),
            AttendancePoint("Feb", 93f),
            AttendancePoint("Mar", 96f),
        )
    }
}

private fun mapPrayerTimesOld(day: PrayerDay): List<PrayerTime> {
    val t = day.timings

    return listOf(
        PrayerTime("Fajr", t.fajr.to12HourFormat()),
        PrayerTime("Dhuhr", t.dhuhr.to12HourFormat()),
        PrayerTime("Asr", t.asr.to12HourFormat()),
        PrayerTime("Maghrib", t.maghrib.to12HourFormat()),
        PrayerTime("Isha", t.isha.to12HourFormat())
    )
}

fun String.to12HourFormat(): String {
    val clean = substringBefore(" ")

    val formatter24 = DateTimeFormatter.ofPattern("HH:mm")
    val formatter12 = DateTimeFormatter.ofPattern("hh:mm a")

    val time = LocalTime.parse(clean, formatter24)

    return time.format(formatter12)
}

private fun mapPrayerTimes(day: PrayerDay): List<PrayerTime> {

    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    val prayers = listOf(
        "Fajr" to day.timings.fajr,
        "Dhuhr" to day.timings.dhuhr,
        "Asr" to day.timings.asr,
        "Maghrib" to day.timings.maghrib,
        "Isha" to day.timings.isha
    )

    val now = LocalTime.now()

    val prayerTimes = prayers.map { (name, time) ->
        val clean = time.substringBefore(" ")
        name to LocalTime.parse(clean, formatter)
    }

    val nextPrayerIndex =
        prayerTimes.indexOfFirst { (_, time) -> time.isAfter(now) }
            .let { if (it == -1) 0 else it }

    return prayerTimes.mapIndexed { index, (name, time) ->
        PrayerTime(
            name = name,
            time = time.format(DateTimeFormatter.ofPattern("hh:mm a")),
            isNext = index == nextPrayerIndex
        )
    }
}

private fun formatGregorian(date: String): String {
    val input = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val output = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

    val parsed = LocalDate.parse(date, input)

    return parsed.format(output)
}

private val hijriMonths = listOf(
    "Muharram",
    "Safar",
    "Rabi al-Awwal",
    "Rabi al-Thani",
    "Jumada al-Awwal",
    "Jumada al-Thani",
    "Rajab",
    "Sha'ban",
    "Ramadan",
    "Shawwal",
    "Dhu al-Qadah",
    "Dhu al-Hijjah"
)

private fun formatHijri(date: String): String {
    val parts = date.split("-")

    val day = parts[0].toInt()
    val month = hijriMonths[parts[1].toInt() - 1]
    val year = parts[2]

    return "$day $month $year"
}