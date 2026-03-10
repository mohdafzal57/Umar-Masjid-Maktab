package com.example.umarmasjid.navigation

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 *
 * @param state - The navigation state that will be updated in response to navigation events.
 */
class UmarMasjidNavigator(val state: UmarMasjidNavigationState) {

    /**
     * Navigate to a navigation key
     *
     * @param key - the navigation key to navigate to.
     */
    fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    /**
     * Go back to the previous navigation key.
     */
    fun goBack() {
        when (state.currentKey) {
            state.startKey -> {
                // You cannot go back from the start route
            }
            state.currentTopLevelKey -> {
                // We're at the base of the current sub stack, go back to the previous top level
                // stack.
                if (state.topLevelStack.size > 1) {
                    state.topLevelStack.removeLastOrNull()
                }
            }
            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    /**
     * Go to a non top level key.
     */
    private fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            // Remove it if it's already in the stack so it's added at the end.
            remove(key)
            add(key)
        }
    }

    /**
     * Go to a top level stack.
     */
    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startKey) {
                // This is the start key. Clear the stack so it's added as the only key.
                clear()
            } else {
                // Remove it if it's already in the stack so it's added at the end.
                remove(key)
            }
            add(key)
        }
    }

    /**
     * Clearing all but the root key in the current sub stack.
     */
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
    startKey: NavKey = Destination.Home,
    topLevelKeys: Set<NavKey> = TOP_LEVEL_NAV_ITEMS.keys,
): UmarMasjidNavigationState {
    val topLevelStack = rememberNavBackStack(startKey)
    val subStacks = topLevelKeys.associateWith { key -> rememberNavBackStack(key) }

    return remember(startKey, topLevelKeys) {
        UmarMasjidNavigationState(
            startKey = startKey,
            topLevelStack = topLevelStack,
            subStacks = subStacks,
        )
    }
}

/**
 * State holder for navigation state.
 *
 * @param startKey - the starting navigation key. The user will exit the app through this key.
 * @param topLevelStack - the top level back stack. It holds only top level keys.
 * @param subStacks - the back stacks for each top level key
 */
class UmarMasjidNavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    val currentTopLevelKey: NavKey by derivedStateOf { topLevelStack.last() }

    val topLevelKeys
        get() = subStacks.keys

    @get:VisibleForTesting
    val currentSubStack: NavBackStack<NavKey>
        get() = subStacks[currentTopLevelKey]
            ?: error("Sub stack for $currentTopLevelKey does not exist")

    @get:VisibleForTesting
    val currentKey: NavKey by derivedStateOf { currentSubStack.last() }

    val showBottomBar: Boolean
        get() = currentTopLevelKey is Destination.Home || 
                currentTopLevelKey is Destination.StudentAttendance
}

/**
 * Convert NavigationState into NavEntries.
 */
@Composable
fun UmarMasjidNavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = subStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator<NavKey>(),
        )
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
