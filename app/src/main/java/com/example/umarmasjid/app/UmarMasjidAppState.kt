package com.example.umarmasjid.app

import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.umarmasjid.navigation.UmarMasjidNavigationState
import com.example.umarmasjid.navigation.UmarMasjidNavigator
import com.example.umarmasjid.navigation.rememberUmarMasjidNavigationState

@OptIn(ExperimentalMaterial3Api::class)
class UmarMasjidAppState(
    val navigationState: UmarMasjidNavigationState,
    val navigator: UmarMasjidNavigator,
    val scrollBehavior: BottomAppBarScrollBehavior
) {
    val currentRoute get() = navigationState.currentKey
    val showBottomBar get() = navigationState.showBottomBar
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberUmarMasjidAppState(
    navigationState: UmarMasjidNavigationState = rememberUmarMasjidNavigationState(),
    scrollBehavior: BottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
): UmarMasjidAppState {
    val navigator = remember(navigationState) { UmarMasjidNavigator(navigationState) }
    return remember(navigationState, navigator, scrollBehavior) {
        UmarMasjidAppState(navigationState, navigator, scrollBehavior)
    }
}
