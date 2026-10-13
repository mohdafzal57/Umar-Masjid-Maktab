package com.example.umarmasjid

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.core.data.preferences.PrayerSettingsPreferences
import com.example.umarmasjid.core.data.preferences.SystemSettingsPreferences
import com.example.umarmasjid.domain.repository.AuthRepository
import com.example.umarmasjid.domain.repository.AuthState
import com.example.umarmasjid.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val preferences: PrayerSettingsPreferences,
    private val settings: SystemSettingsPreferences,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val authState: StateFlow<AuthState> =
        authRepository.authState
            .onStart {
                // Auth state changes instantly
                // this displays the splash screen for 1.5 seconds
                delay(1500.milliseconds)
            }
            .catch { e ->
                Log.e("AuthViewModel", "Auth state failed", e)
                emit(AuthState.Unauthenticated)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AuthState.Loading
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
