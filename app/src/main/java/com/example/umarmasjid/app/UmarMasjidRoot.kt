package com.example.umarmasjid.app

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.umarmasjid.navigation.UmarMasjidBottomBar
import com.example.umarmasjid.navigation.UmarMasjidNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UmarMasjidRoot(
    appState: UmarMasjidAppState = rememberUmarMasjidAppState()
) {
    val scrollBehavior = appState.scrollBehavior
    val state = scrollBehavior.state
    val showBottomBar = appState.showBottomBar

    LaunchedEffect(appState.currentRoute) {
        val target = if (showBottomBar) 0f else state.heightOffsetLimit
        animate(
            initialValue = state.heightOffset,
            targetValue = target,
            animationSpec = tween(300)
        ) { value, _ ->
            state.heightOffset = value
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                UmarMasjidBottomBar(
                    scrollBehavior = scrollBehavior,
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
