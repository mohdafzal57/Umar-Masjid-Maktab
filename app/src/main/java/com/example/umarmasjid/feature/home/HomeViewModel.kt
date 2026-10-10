package com.example.umarmasjid.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.core.data.preferences.SystemSettingsPreferences
import com.example.umarmasjid.domain.usecase.home.ObserveHomeDashboardUseCase
import com.example.umarmasjid.domain.usecase.home.ObserveTodayPrayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeHomeDashboard: ObserveHomeDashboardUseCase,
    observeTodayPrayer: ObserveTodayPrayerUseCase,
    private val settings: SystemSettingsPreferences,
) : ViewModel() {

    private val attendancePeriod =
        MutableStateFlow(AttendancePeriod.SEVEN_DAYS)

    private val dashboard =
        observeHomeDashboard(attendancePeriod)

    private val prayer = observeTodayPrayer().map { entity ->
        entity?.let(PrayerTimeUiMapper::map) ?: PrayerResult()
    }

    /** [Remember]
     * The combine operator waits for every single flow to produce an initial emission:
     * If three of them emit instantly but one is waiting on a slow network request
     * or database query, combine will wait.
     */
    val state: StateFlow<HomeUiState> = combine(
        settings.settings,
        dashboard,
        prayer,
        attendancePeriod,
    ) { systemSettings, dashboardData, prayerResult, period ->
        val dashboardUi = HomeUiMapper.mapDashboard(dashboardData)

        HomeUiState(
            isDarkMode = systemSettings.darkMode,
            islamicDate = prayerResult.islamicDate,
            prayerTimes = prayerResult.prayerTimes,
            stats = dashboardUi.stats,
            recentStudents = dashboardUi.recentStudents,
            attendanceTrend = dashboardUi.attendanceTrend,
            attendancePeriod = period,
            isLoading = false,
            error = null,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(isLoading = true),
    )

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.ToggleDarkMode -> toggleDarkMode()
            is HomeAction.SelectAttendancePeriod ->
                attendancePeriod.update { action.period }
            else -> Unit
        }
    }

    private fun toggleDarkMode() {
        viewModelScope.launch {
            settings.setDarkMode(!state.value.isDarkMode)
        }
    }
}

data class PrayerResult(
    val prayerTimes: List<PrayerTime> = emptyList(),
    val islamicDate: IslamicDate = IslamicDate()
)