package com.example.umarmasjid.core.data.preferences

import kotlinx.coroutines.flow.Flow

interface SystemSettingsPreferences {

    val settings: Flow<SystemSettings>

    suspend fun setDarkMode(enabled: Boolean)

    suspend fun setLanguage(language: String)

    suspend fun setNotificationsEnabled(enabled: Boolean)

    suspend fun setFirstLaunchCompleted(completed: Boolean)
}

data class SystemSettings(
    val darkMode: Boolean = false,
    val language: String = "en",
    val notificationsEnabled: Boolean = true,
    val firstLaunchCompleted: Boolean = false
)