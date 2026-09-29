package com.example.umarmasjid.feature.reset_password

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.AuthenticationRoute
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.updatePasswordEntry(
    navigator: UmarMasjidNavigator
) {
    entry<AuthenticationRoute.UpdatePasswordRoute> {
        UpdatePasswordScreen(
            onSuccessDone = {
                /*if (navigator.state.isLoggedIn) {
                    navigator.navigate(ConnectedRoute.HomeRoute)
                } else {
                    navigator.navigate(AuthenticationRoute.LoginRoute)
                }*/
            }
        )
    }
}
