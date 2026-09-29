package com.example.umarmasjid.feature.signup

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.AuthenticationRoute
import com.example.umarmasjid.navigation.ConnectedRoute
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.signupEntry(
    navigator: UmarMasjidNavigator
) {
    entry<AuthenticationRoute.SignupRoute> {
        SignUpScreen(
            onNavigateNext = { navigator.navigate(ConnectedRoute.HomeRoute) }
        )
    }
}
