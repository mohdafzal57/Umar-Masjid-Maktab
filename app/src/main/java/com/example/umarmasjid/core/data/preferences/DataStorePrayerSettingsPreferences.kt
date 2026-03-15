package com.example.umarmasjid.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.umarmasjid.domain.model.PrayerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStorePrayerSettingsPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PrayerSettingsPreferences {

    override val settings: Flow<PrayerSettings> =
        dataStore.data.map { prefs ->

        PrayerSettings(
            latitude = prefs[LATITUDE],
            longitude = prefs[LONGITUDE],
            cityName = prefs[CITY_NAME] ?: "",
            method = prefs[METHOD] ?: 1,
            school = prefs[SCHOOL] ?: 1,
            midnightMode = prefs[MIDNIGHT_MODE] ?: 0,
            latitudeAdjustmentMethod = prefs[LAT_ADJUSTMENT],
            calendarMethod = prefs[CALENDAR_METHOD] ?: "MATHEMATICAL",
            timezone = prefs[TIMEZONE] ?: "Asia/Kolkata",
            shafaq = prefs[SHAFAQ],
            tune = prefs[TUNE],
            adjustment = prefs[ADJUSTMENT] ?: -1,
            iso8601 = prefs[ISO8601] ?: false
        )
    }

    override suspend fun setLocation(latitude: Double, longitude: Double) {
        dataStore.edit {
            it[LATITUDE] = latitude
            it[LONGITUDE] = longitude
        }
    }

    override suspend fun setCityName(cityName: String) {
        dataStore.edit { it[CITY_NAME] = cityName }
    }


    override suspend fun setMethod(method: Int) {
        dataStore.edit { it[METHOD] = method }
    }

    override suspend fun setSchool(school: Int) {
        dataStore.edit { it[SCHOOL] = school }
    }

    override suspend fun setMidnightMode(mode: Int) {
        dataStore.edit { it[MIDNIGHT_MODE] = mode }
    }

    override suspend fun setLatitudeAdjustmentMethod(method: Int?) {
        dataStore.edit {
            if (method == null) it.remove(LAT_ADJUSTMENT)
            else it[LAT_ADJUSTMENT] = method
        }
    }

    override suspend fun setCalendarMethod(method: String) {
        dataStore.edit { it[CALENDAR_METHOD] = method }
    }

    override suspend fun setTimezone(timezone: String) {
        dataStore.edit { it[TIMEZONE] = timezone }
    }

    override suspend fun setShafaq(shafaq: String?) {
        dataStore.edit {
            if (shafaq == null) it.remove(SHAFAQ)
            else it[SHAFAQ] = shafaq
        }
    }

    override suspend fun setTune(tune: String?) {
        dataStore.edit {
            if (tune == null) it.remove(TUNE)
            else it[TUNE] = tune
        }
    }

    override suspend fun setAdjustment(days: Int?) {
        dataStore.edit {
            if (days == null) it.remove(ADJUSTMENT)
            else it[ADJUSTMENT] = days
        }
    }

    override suspend fun setIso8601(enabled: Boolean) {
        dataStore.edit { it[ISO8601] = enabled }
    }

    companion object {
        val LATITUDE = doublePreferencesKey("latitude")

        val LONGITUDE = doublePreferencesKey("longitude")

        val METHOD = intPreferencesKey("method")

        val CITY_NAME = stringPreferencesKey("city_name")

        val SCHOOL = intPreferencesKey("school")

        val MIDNIGHT_MODE = intPreferencesKey("midnight_mode")

        val LAT_ADJUSTMENT = intPreferencesKey("lat_adjustment")

        val CALENDAR_METHOD = stringPreferencesKey("calendar_method")

        val TIMEZONE = stringPreferencesKey("timezone")

        val SHAFAQ = stringPreferencesKey("shafaq")

        val TUNE = stringPreferencesKey("tune")

        val ADJUSTMENT = intPreferencesKey("adjustment")

        val ISO8601 = booleanPreferencesKey("iso8601")
    }
}

val Context.prayerSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "prayer_settings"
)
