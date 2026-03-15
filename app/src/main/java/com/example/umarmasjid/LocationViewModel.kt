package com.example.umarmasjid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.core.data.location.LocationClient
import com.example.umarmasjid.core.data.preferences.PrayerSettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationClient: LocationClient,
    private val preferences: PrayerSettingsPreferences
) : ViewModel() {

    init {
        println("LocationViewModel initialized")
    }

    fun updateLocationAutomatically() {
        viewModelScope.launch {
            val location = locationClient.getCurrentLocation()
            location?.let {
                // Save to DataStore so it persists
                preferences.setLocation(it.latitude, it.longitude)
                println("Location updated: ${it.latitude}, ${it.longitude}")
            }
        }
    }
}