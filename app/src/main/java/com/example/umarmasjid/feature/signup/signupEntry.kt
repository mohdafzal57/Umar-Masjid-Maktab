package com.example.umarmasjid.feature.signup

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.feature.home.HomeNavKey
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.signupEntry(
    navigator: UmarMasjidNavigator
) {
    entry<SignUpNavKey> {
        SignUpScreen(
            onNavigateNext = { navigator.navigate(HomeNavKey) }
        )
    }
}