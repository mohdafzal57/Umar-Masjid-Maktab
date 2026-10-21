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
        private val THEME_KEY = stringPreferencesKey("dark_theme_config")
        private val LANGUAGE = stringPreferencesKey("language")
        private val NOTIFICATIONS = booleanPreferencesKey("notifications")
        private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")

        private val TEACHER_ID = stringPreferencesKey("teacher_id")
    }

    override val settings: Flow<SystemSettings> =
        dataStore.data.map { prefs ->
            SystemSettings(
                language = prefs[LANGUAGE] ?: "en",
                notificationsEnabled = prefs[NOTIFICATIONS] ?: true,
                firstLaunchCompleted = prefs[FIRST_LAUNCH] ?: false,
                darkThemeConfig = when (prefs[THEME_KEY]) {
                    DarkThemeConfig.LIGHT.name -> DarkThemeConfig.LIGHT
                    DarkThemeConfig.DARK.name -> DarkThemeConfig.DARK
                    else -> DarkThemeConfig.FOLLOW_SYSTEM
                },
                teacherId = prefs[TEACHER_ID] ?: ""
            )
        }

    override suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = config.name
        }
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

    override suspend fun setTeacherId(teacherId: String) {
        dataStore.edit { it[TEACHER_ID] = teacherId }
    }
}

val Context.systemSettingsDataStore: DataStore<Preferences> by preferencesDataStore("system_settings")
