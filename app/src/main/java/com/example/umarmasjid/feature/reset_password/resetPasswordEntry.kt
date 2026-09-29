package com.example.umarmasjid.feature.reset_password

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.AuthenticationRoute
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.resetPasswordEntry(
    navigator: UmarMasjidNavigator
) {
    entry<AuthenticationRoute.ResetPasswordRoute> {
        ResetPasswordScreen(
            onNavigateBack = { navigator.goBack() }
        )
    }
}
