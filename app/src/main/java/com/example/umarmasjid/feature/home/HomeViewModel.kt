package com.example.umarmasjid.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(buildInitialState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

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
        _state.update { it.copy(attendancePeriod = period, attendanceTrend = fakeAttendance(period)) }
    }

    private fun buildInitialState() = HomeState(
        prayerTimes = listOf(
            PrayerTime("Fajr", "05:45"),
            PrayerTime("Dhuhr", "12:30"),
            PrayerTime("Asr", "15:45", isNext = true),
            PrayerTime("Maghrib", "18:15"),
            PrayerTime("Isha", "19:45"),
        ),
        attendanceTrend = fakeAttendance(AttendancePeriod.SEVEN_DAYS),
        recentStudents = listOf(
            StudentSummary(1, "AH", "Ahmed Hassan",   "Grade 5", "STD-001", 96, FeeStatus.PAID,    0xFF5C8AFF),
            StudentSummary(2, "FA", "Fatima Ali",     "Grade 6", "STD-002", 98, FeeStatus.PAID,    0xFF7C6AF5),
            StudentSummary(3, "MK", "Mohammed Khan",  "Grade 4", "STD-003", 89, FeeStatus.PENDING, 0xFFF5A623),
            StudentSummary(4, "AS", "Aisha Siddiqui", "Grade 7", "STD-004", 95, FeeStatus.PAID,    0xFFE891C8),
            StudentSummary(5, "ZA", "Zainab Ahmed",   "Grade 5", "STD-005", 92, FeeStatus.PAID,    0xFF91C8E4),
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
            AttendancePoint("W1", 94f), AttendancePoint("W2", 96f),
            AttendancePoint("W3", 92f), AttendancePoint("W4", 97f),
        )
        AttendancePeriod.THREE_MONTHS -> listOf(
            AttendancePoint("Jan", 95f), AttendancePoint("Feb", 93f),
            AttendancePoint("Mar", 96f),
        )
    }
}
