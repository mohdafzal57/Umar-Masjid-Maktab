package com.example.umarmasjid.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.umarmasjid.di.SystemSettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreSystemSettingsPreferences @Inject constructor(
    @SystemSettingsDataStore
    private val dataStore: DataStore<Preferences>
) : SystemSettingsPreferences {

    companion object {
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val LANGUAGE = stringPreferencesKey("language")
        private val NOTIFICATIONS = booleanPreferencesKey("notifications")
        private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    override val settings: Flow<SystemSettings> =
        dataStore.data.map { pref ->
            SystemSettings(
                darkMode = pref[DARK_MODE] ?: false,
                language = pref[LANGUAGE] ?: "en",
                notificationsEnabled = pref[NOTIFICATIONS] ?: true,
                firstLaunchCompleted = pref[FIRST_LAUNCH] ?: false
            )
        }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE] = enabled }
    }

    override suspend fun setLanguage(language: String) {
        dataStore.edit { it[LANGUAGE] = language }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS] = enabled }
    }

    override suspend fun setFirstLaunchCompleted(completed: Boolean) {
        dataStore.edit { it[FIRST_LAUNCH] = completed }
    }
}

val Context.systemSettingsDataStore: DataStore<Preferences> by preferencesDataStore("system_settings")
