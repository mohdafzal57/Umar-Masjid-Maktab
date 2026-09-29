package com.example.umarmasjid.navigation

import androidx.navigation3.runtime.NavKey

class UmarMasjidNavigator(val state: UmarMasjidNavigationState) {

    fun navigate(route: NavKey) {
        when (route) {
            is Root -> {
                // If transitioning from Authentication, update root backstack first
                if (state.currentRootDestination == AuthenticationRoute) {
                    state.rootBackStack.removeLastOrNull()
                    state.rootBackStack.add(ConnectedRoute)
                }
            }

            // Navigation within Authentication graph
            is Authentication -> {
                state.authenticationBackStack.add(route)
            }

            // Navigation within Connected graph (or transitioning to it from Authentication)
            is Connected -> {
                if (route in state.backStacks.keys) {
                    // This is a top level route, just switch to it
                    state.topLevelRoute = route
                } else {
                    state.backStacks[state.topLevelRoute]?.add(route)
                }
            }
        }
    }

    /**
     * Go back. Handles back navigation within both Authentication and Connected graphs.
     */
    fun goBack() {
        when (state.currentRootDestination) {
            AuthenticationRoute -> {
                state.authenticationBackStack.removeLastOrNull()
            }

            ConnectedRoute -> {
                val currentStack = state.backStacks[state.topLevelRoute]
                    ?: error("Stack for ${state.topLevelRoute} not found")
                val currentRoute = currentStack.last()

                // If we're at the base of the current route, go back to the start route stack.
                if (currentRoute == state.topLevelRoute) {
                    state.topLevelRoute = state.topLevelStartRoute
                } else {
                    currentStack.removeLastOrNull()
                }
            }
        }
    }

}