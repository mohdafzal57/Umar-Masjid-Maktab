package com.example.umarmasjid.feature.reset_password

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.feature.home.HomeNavKey
import com.example.umarmasjid.feature.login.LoginNavKey
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.updatePasswordEntry(
    navigator: UmarMasjidNavigator
) {
    entry<UpdatePasswordNavKey> {
        UpdatePasswordScreen(
            onSuccessDone = {
                if (navigator.state.isLoggedIn) {
                    navigator.navigate(HomeNavKey)
                } else {
                    navigator.navigate(LoginNavKey)
                }
            }
        )
    }
}
