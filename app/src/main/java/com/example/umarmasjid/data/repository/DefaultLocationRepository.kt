package com.example.umarmasjid.data.repository

import com.example.umarmasjid.core.data.location.LocationClient
import com.example.umarmasjid.core.data.preferences.PrayerSettingsPreferences
import com.example.umarmasjid.domain.repository.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultLocationRepository @Inject constructor(
    private val locationClient: LocationClient,
    private val preferences: PrayerSettingsPreferences
) : LocationRepository {

    override suspend fun fetchAndSaveCurrentLocation(markInitialized: Boolean): Boolean {
        val location = locationClient.getCurrentLocation() ?: return false

        preferences.setLocation(location.latitude, location.longitude)
        if (markInitialized) {
            preferences.setLocationInitialized(true)
        }

        return true
    }
}
