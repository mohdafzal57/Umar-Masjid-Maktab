package com.example.umarmasjid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.core.data.location.LocationClient
import com.example.umarmasjid.core.data.preferences.PrayerSettingsPreferences
import com.example.umarmasjid.core.data.preferences.SystemSettingsPreferences
import com.example.umarmasjid.domain.repository.AuthRepository
import com.example.umarmasjid.domain.repository.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val locationClient: LocationClient,
    private val preferences: PrayerSettingsPreferences,
    private val settings: SystemSettingsPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> =
        authRepository.authState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AuthState.LOADING
            )

    val darkMode: StateFlow<Boolean> =
        settings.settings
            .map { it.darkMode }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    val isLocationInitialized: StateFlow<Boolean?> =
        preferences.isLocationInitialized()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun fetchLocationOnce() {
        viewModelScope.launch {
            val location = locationClient.getCurrentLocation()
            if (location != null) {
                preferences.setLocation(location.latitude, location.longitude)
                preferences.setLocationInitialized(true)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
