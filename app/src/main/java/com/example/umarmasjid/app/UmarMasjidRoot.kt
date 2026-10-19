package com.example.umarmasjid.app

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.umarmasjid.domain.repository.AuthState
import com.example.umarmasjid.navigation.AuthenticationRoute
import com.example.umarmasjid.navigation.ConnectedRoute
import com.example.umarmasjid.navigation.TOP_LEVEL_NAV_ITEMS
import com.example.umarmasjid.navigation.UmarMasjidNavigationSuiteScaffold
import com.example.umarmasjid.navigation.UmarMasjidNavigator
import com.example.umarmasjid.navigation.rememberUmarMasjidNavigationState
import com.example.umarmasjid.navigation.rootSection

@Composable
fun UmarMasjidRoot(
    authState: AuthState,
    isPasswordRecovery: Boolean
) {

    // Gate the Root Content until AuthState is Settled
    if (authState == AuthState.Initializing) return

    val initialRootRoute = remember(authState, isPasswordRecovery) {
        if (isPasswordRecovery) AuthenticationRoute
        else if (authState == AuthState.Authenticated) ConnectedRoute
        else AuthenticationRoute
    }

    val navigationState = rememberUmarMasjidNavigationState(
        rootStartRoute = initialRootRoute,
        authenticationStartRoute = if (isPasswordRecovery) {
            AuthenticationRoute.UpdatePasswordRoute
        } else {
            AuthenticationRoute.LoginRoute
        },
        topLevelStartRoute = ConnectedRoute.HomeRoute,
        topLevelRoutes = TOP_LEVEL_NAV_ITEMS.keys
    )

    val navigator = remember { UmarMasjidNavigator(navigationState) }

    LaunchedEffect(authState, isPasswordRecovery) {
        if (isPasswordRecovery) {
            navigator.navigate(AuthenticationRoute.UpdatePasswordRoute)
        } else {
            when (authState) {
                AuthState.Authenticated -> {
                    navigator.navigate(ConnectedRoute)
                }

                AuthState.Unauthenticated -> {
                    navigator.navigate(AuthenticationRoute)
                }
            }
        }
    }

    val showNavigationSuite = navigationState.currentRootDestination is ConnectedRoute

    UmarMasjidNavigationSuiteScaffold(
        shouldShowNavigationSuite = showNavigationSuite,
        navigationSuiteItems = {
            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                val selected = navKey == navigationState.topLevelRoute
                item(
                    selected = selected,
                    onClick = { navigator.navigate(navKey) },
                    icon = {
                        Icon(
                            imageVector = navItem.unselectedIcon,
                            contentDescription = navItem.title,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = navItem.selectedIcon,
                            contentDescription = navItem.title,
                        )
                    },
                    label = { Text(navItem.title) },
                    badge = navItem.badge?.let { badgeText ->
                        {
                            Badge { Text(badgeText) }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
}
