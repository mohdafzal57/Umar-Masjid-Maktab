package com.example.umarmasjid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
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
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer


@Composable
fun rememberUmarMasjidNavigationState(
    rootStartRoute: NavKey,
    authenticationStartRoute: NavKey,
    topLevelStartRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): UmarMasjidNavigationState {

    val rootBackStack = rememberNavBackStack(rootStartRoute)
    val authenticationBackStack = rememberNavBackStack(authenticationStartRoute)
    val topLevelRoute = rememberSerializable(
        topLevelStartRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) {
        mutableStateOf(topLevelStartRoute)
    }
    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }

    return remember(topLevelStartRoute, topLevelRoutes) {
        UmarMasjidNavigationState(
            rootStartRoute = rootStartRoute,
            rootBackStack = rootBackStack,
            authenticationBackStack = authenticationBackStack,
            topLevelStartRoute = topLevelStartRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks
        )
    }
}

class UmarMasjidNavigationState(
    val rootStartRoute: NavKey,
    val rootBackStack: NavBackStack<NavKey>,
    val authenticationBackStack: NavBackStack<NavKey>,
    val topLevelStartRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute: NavKey by topLevelRoute
    val currentRootDestination: NavKey
        get() = rootBackStack.lastOrNull() ?: rootStartRoute

    @Composable
    fun toDecoratedEntries(
        entryProvider: (NavKey) -> NavEntry<NavKey>
    ): SnapshotStateList<NavEntry<NavKey>> {

        val decoratedEntries = backStacks.mapValues { (_, stack) ->
            val decorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
                rememberViewModelStoreNavEntryDecorator<NavKey>()
            )
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = decorators,
                entryProvider = entryProvider
            )
        }

        return getTopLevelRoutesInUse()
            .flatMap { decoratedEntries[it] ?: emptyList() }
            .toMutableStateList()
    }

    private fun getTopLevelRoutesInUse(): List<NavKey> =
        if (topLevelRoute == topLevelStartRoute) {
            listOf(topLevelStartRoute)
        } else {
            listOf(topLevelStartRoute, topLevelRoute)
        }
}
