package com.example.umarmasjid.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.umarmasjid.ui.navigation.UmarMasjidDestinations
import com.example.umarmasjid.ui.navigation.UmarMasjidNavGraph
import com.example.umarmasjid.ui.navigation.UmarMasjidNavigationActions
import com.example.umarmasjid.ui.theme.UmarMasjidTheme

@Composable
fun UmarMasjidApp() {
    UmarMasjidTheme {
        UmarMasjidNavGraph()
    }
}