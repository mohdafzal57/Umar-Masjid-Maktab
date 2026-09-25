package com.example.umarmasjid.feature.login

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.feature.reset_password.ResetPasswordNavKey
import com.example.umarmasjid.feature.signup.SignUpNavKey
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.loginEntry(
    navigator: UmarMasjidNavigator,
    onLoginSuccess: () -> Unit
) {
    entry<LoginNavKey> {
        LoginScreen(
            onNavigateToSignup = { navigator.navigate(SignUpNavKey) },
            onNavigateToResetPassword = { navigator.navigate(ResetPasswordNavKey) },
            onLoginSuccess = onLoginSuccess
        )
    }
}
