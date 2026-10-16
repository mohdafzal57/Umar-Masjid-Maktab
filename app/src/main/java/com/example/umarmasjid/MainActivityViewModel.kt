package com.example.umarmasjid

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.core.data.preferences.DarkThemeConfig
import com.example.umarmasjid.core.data.preferences.PrayerSettingsPreferences
import com.example.umarmasjid.core.data.preferences.SystemSettingsPreferences
import com.example.umarmasjid.domain.repository.AuthRepository
import com.example.umarmasjid.domain.repository.AuthState
import com.example.umarmasjid.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val preferences: PrayerSettingsPreferences,
    private val settings: SystemSettingsPreferences,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> =
        combine(
            authRepository.authState,
            settings.settings.map { it.darkThemeConfig },
            preferences.isLocationInitialized(),
        ) { authState, darkThemeConfig, isLocationInitialized ->
            MainActivityUiState(
                authState = authState,
                darkThemeConfig = darkThemeConfig,
                isLocationInitialized = isLocationInitialized,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainActivityUiState(),
        )


    fun handleIntent(intent: Intent) {
        viewModelScope.launch {
            val uri = intent.data ?: return@launch
            if (uri.scheme == "umarmasjid" && uri.host == "auth") {
                authRepository.handleDeeplink(intent)
            }
        }
    }

    fun fetchLocationOnce() {
        viewModelScope.launch {
            locationRepository.fetchAndSaveCurrentLocation()
        }
    }
}

data class MainActivityUiState(
    val authState: AuthState = AuthState.Loading,
    val darkThemeConfig: DarkThemeConfig =
        DarkThemeConfig.FOLLOW_SYSTEM,
    val isLocationInitialized: Boolean? = null,
) {
    fun shouldKeepSplashScreen(): Boolean =
        authState is AuthState.Loading

    fun shouldUseDarkTheme(
        isSystemDarkTheme: Boolean,
    ): Boolean =
        when (darkThemeConfig) {
            DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
            DarkThemeConfig.LIGHT -> false
            DarkThemeConfig.DARK -> true
        }
}