package com.example.umarmasjid.feature.settings

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.ConnectedRoute
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.settingsEntry(
    navigator: UmarMasjidNavigator
) {
    entry<ConnectedRoute.SettingsRoute> {
        SettingsScreen(
            onBack = { navigator.goBack() }
        )
    }
}
