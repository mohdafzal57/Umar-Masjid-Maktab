package com.example.umarmasjid.core.data.preferences

import kotlinx.coroutines.flow.Flow

interface SystemSettingsPreferences {

    val settings: Flow<SystemSettings>

    suspend fun setDarkThemeConfig(config: DarkThemeConfig)

    suspend fun setLanguage(language: String)

    suspend fun setNotificationsEnabled(enabled: Boolean)

    suspend fun setFirstLaunchCompleted(completed: Boolean)
}

data class SystemSettings(
    val language: String = "en",
    val notificationsEnabled: Boolean = true,
    val firstLaunchCompleted: Boolean = false,
    val darkThemeConfig: DarkThemeConfig,
)

enum class DarkThemeConfig {
    FOLLOW_SYSTEM,
    LIGHT,
    DARK,
}