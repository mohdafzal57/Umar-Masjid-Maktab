package com.example.umarmasjid.navigation

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.example.umarmasjid.feature.home.HomeNavKey
import com.example.umarmasjid.feature.login.LoginNavKey

class UmarMasjidNavigator(
    val state: UmarMasjidNavigationState,
    private val onLogout: () -> Unit = {}
) {

    fun logout() {
        state.topLevelStack.run {
            if (size > 1) subList(1, size).clear()
        }
        state.subStacks.values.forEach { stack ->
            if (stack.size > 1) stack.subList(1, stack.size).clear()
        }
        state.authStack.run {
            if (size > 1) subList(1, size).clear()
        }
        onLogout()
    }

    fun navigate(key: NavKey) {
        if (!state.isLoggedIn) {
            goToAuthKey(key)
            return
        }
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    fun goBack() {
        if (state.currentKey == state.startKey) return

        if (state.isLoggedIn && state.currentKey == state.currentTopLevelKey) {
            if (state.topLevelStack.size > 1) {
                state.topLevelStack.removeLastOrNull()
            }
        } else {
            state.currentSubStack.removeLastOrNull()
        }
    }

    private fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            remove(key)
            add(key)
        }
    }

    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == HomeNavKey) {
                clear()
            } else {
                remove(key)
            }
            add(key)
        }
    }

    private fun goToAuthKey(key: NavKey) {
        state.authStack.apply {
            remove(key)
            add(key)
        }
    }

    private fun clearSubStack() {
        state.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }
}

/**
 * Create a navigation state that persists config changes and process death.
 */
@Composable
fun rememberUmarMasjidNavigationState(
    isLoggedIn: Boolean,
    topLevelKeys: Set<NavKey> = TOP_LEVEL_NAV_ITEMS.keys,
): UmarMasjidNavigationState {
    val authStack = rememberNavBackStack(LoginNavKey)
    val topLevelStack = rememberNavBackStack(HomeNavKey)
    val subStacks = topLevelKeys.associateWith { key -> rememberNavBackStack(key) }

    val state = remember(topLevelKeys) {
        UmarMasjidNavigationState(
            isLoggedIn = isLoggedIn,
            authStack = authStack,
            topLevelStack = topLevelStack,
            subStacks = subStacks,
        )
    }

    LaunchedEffect(isLoggedIn) {
        state.isLoggedIn = isLoggedIn
        if (!isLoggedIn) {
            authStack.run { if (size > 1) subList(1, size).clear() }
            topLevelStack.run { if (size > 1) subList(1, size).clear() }
            subStacks.values.forEach { stack ->
                if (stack.size > 1) stack.subList(1, stack.size).clear()
            }
        }
    }

    return state
}

class UmarMasjidNavigationState(
    isLoggedIn: Boolean,
    val authStack: NavBackStack<NavKey>,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    var isLoggedIn: Boolean by mutableStateOf(isLoggedIn)
        internal set

    val currentTopLevelKey: NavKey by derivedStateOf {
        if (!isLoggedIn) LoginNavKey else topLevelStack.last()
    }

    val topLevelKeys
        get() = subStacks.keys

    @get:VisibleForTesting
    val currentSubStack: NavBackStack<NavKey>
        get() = if (!isLoggedIn) authStack else (subStacks[currentTopLevelKey]
            ?: error("Sub stack for $currentTopLevelKey does not exist"))

    @get:VisibleForTesting
    val currentKey: NavKey by derivedStateOf { currentSubStack.last() }

    val startKey: NavKey
        get() = if (!isLoggedIn) LoginNavKey else HomeNavKey

    val showBottomBar: Boolean
        get() = isLoggedIn && currentTopLevelKey in TOP_LEVEL_NAV_ITEMS
}

@Composable
fun UmarMasjidNavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
    val decorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
        rememberViewModelStoreNavEntryDecorator<NavKey>(),
    )

    if (!isLoggedIn) {
        return rememberDecoratedNavEntries(
            backStack = authStack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        ).toMutableStateList()
    }

    val decoratedEntries = subStacks.mapValues { (_, stack) ->
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        )
    }

    return topLevelStack
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
