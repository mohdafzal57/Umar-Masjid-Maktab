package com.example.umarmasjid.app

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.umarmasjid.navigation.UmarMasjidBottomBar
import com.example.umarmasjid.navigation.UmarMasjidNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UmarMasjidRoot(
    isLoggedIn: Boolean,
    appState: UmarMasjidAppState = rememberUmarMasjidAppState(
        isLoggedIn = isLoggedIn
    )
) {
    val showBottomBar = appState.showBottomBar

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                UmarMasjidBottomBar(
                    currentTopLevelKey = appState.navigationState.currentTopLevelKey,
                    onNavigate = { appState.navigator.navigate(it) }
                )
            }
        }
    ) { innerPadding ->
        UmarMasjidNavGraph(
            navigationState = appState.navigationState,
            navigator = appState.navigator,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                )
        )
    }
}
