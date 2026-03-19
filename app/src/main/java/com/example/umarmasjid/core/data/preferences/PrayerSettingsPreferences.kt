package com.example.umarmasjid.core.data.preferences

import com.example.umarmasjid.domain.model.PrayerSettings
import kotlinx.coroutines.flow.Flow

interface PrayerSettingsPreferences {

    val settings: Flow<PrayerSettings>

    suspend fun setLocationInitialized(enabled: Boolean)

    fun isLocationInitialized(): Flow<Boolean>

    suspend fun setLocation(latitude: Double, longitude: Double)

    suspend fun setCountryAndCity(country: String, city: String)

    suspend fun setMethod(method: Int)

    suspend fun setSchool(school: Int)

    suspend fun setMidnightMode(mode: Int)

    suspend fun setLatitudeAdjustmentMethod(method: Int?)

    suspend fun setCalendarMethod(method: String)

    suspend fun setTimezone(timezone: String)

    suspend fun setShafaq(shafaq: String?)

    suspend fun setTune(tune: String?)

    suspend fun setAdjustment(days: Int?)

    suspend fun setIso8601(enabled: Boolean)
}