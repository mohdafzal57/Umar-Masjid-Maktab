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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.umarmasjid.domain.repository.AuthState
import com.example.umarmasjid.navigation.AuthenticationRoute
import com.example.umarmasjid.navigation.ConnectedRoute
import com.example.umarmasjid.navigation.TOP_LEVEL_NAV_ITEMS
import com.example.umarmasjid.navigation.UmarMasjidBottomBar
import com.example.umarmasjid.navigation.UmarMasjidNavigator
import com.example.umarmasjid.navigation.rememberUmarMasjidNavigationState
import com.example.umarmasjid.navigation.rootSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UmarMasjidRoot(authState: AuthState) {
    val navigationState = rememberUmarMasjidNavigationState(
        rootStartRoute = AuthenticationRoute,
        authenticationStartRoute = AuthenticationRoute.LoginRoute,
        topLevelStartRoute = ConnectedRoute.HomeRoute,
        topLevelRoutes = TOP_LEVEL_NAV_ITEMS.keys
    )

    val navigator = remember { UmarMasjidNavigator(navigationState) }

    when(authState) {
        AuthState.Authenticated -> navigator.navigate(ConnectedRoute.HomeRoute)
        AuthState.Loading -> Unit
        AuthState.PasswordRecovery -> {}
        AuthState.Unauthenticated -> navigator.navigate(AuthenticationRoute.LoginRoute)
    }

    val showBottomBar = navigationState.currentRootDestination == ConnectedRoute

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                UmarMasjidBottomBar(
                    currentTopLevelKey = navigationState.topLevelRoute,
                    onNavigate = { navigator.navigate(it) }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = navigationState.rootBackStack,
            entryProvider = entryProvider {
                rootSection(
                    navigator = navigator
                )
            },
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
