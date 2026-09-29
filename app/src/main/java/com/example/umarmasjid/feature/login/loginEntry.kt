package com.example.umarmasjid.feature.login

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.AuthenticationRoute
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.loginEntry(
    navigator: UmarMasjidNavigator
) {
    entry<AuthenticationRoute.LoginRoute> {
        LoginScreen(
            onNavigateToSignup = { navigator.navigate(AuthenticationRoute.SignupRoute) },
            onNavigateToResetPassword = { navigator.navigate(AuthenticationRoute.ResetPasswordRoute) },
        )
    }
}
