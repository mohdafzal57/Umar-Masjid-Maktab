package com.example.umarmasjid

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.core.data.preferences.PrayerSettingsPreferences
import com.example.umarmasjid.core.data.preferences.SystemSettingsPreferences
import com.example.umarmasjid.domain.repository.AuthRepository
import com.example.umarmasjid.domain.repository.AuthState
import com.example.umarmasjid.domain.repository.LocationRepository
import com.example.umarmasjid.navigation.deeplink.DeepLinkHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val deepLinkHandler: DeepLinkHandler
) : ViewModel() {

    private val _deepLinkNavKey = MutableSharedFlow<NavKey>()
    val deepLinkNavKey: SharedFlow<NavKey> = _deepLinkNavKey.asSharedFlow()

    val authState: StateFlow<AuthState> =
        authRepository.authState
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

            val navKey = deepLinkHandler.parseDeepLink(uri)
            if (navKey != null) {
                _deepLinkNavKey.emit(navKey)
            }
        }
    }

    fun fetchLocationOnce() {
        viewModelScope.launch {
            locationRepository.fetchAndSaveCurrentLocation()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun finishPasswordRecovery() {
        viewModelScope.launch {
            authRepository.finishPasswordRecovery()
        }
    }
}
