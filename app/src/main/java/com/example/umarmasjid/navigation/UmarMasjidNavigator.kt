package com.example.umarmasjid.navigation

import androidx.navigation3.runtime.NavKey

class UmarMasjidNavigator(
    val state: UmarMasjidNavigationState
) {
    init {
        logBackStacks("first Launch")
    }

    private fun logBackStacks(action: String) {
        println("========== NAVIGATION DEBUG ==========")
        println("ACTION: $action")

        println("ROOT BACKSTACK: ${state.rootBackStack.toList()}")
        println("AUTH BACKSTACK: ${state.authenticationBackStack.toList()}")

        state.backStacks.forEach { (route, stack) ->
            println("TOP LEVEL [$route]: ${stack.toList()}")
        }

        println("CURRENT ROOT: ${state.currentRootDestination}")
        println("CURRENT TOP LEVEL: ${state.topLevelRoute}")

        println("======================================")
    }

    fun navigate(route: NavKey) {
        when (route) {
            is Root -> {
                if (state.currentRootDestination != route) {
                    state.rootBackStack.removeLastOrNull()
                    state.rootBackStack.add(route)
                }
            }

            is Authentication -> {
                state.authenticationBackStack.add(route)
            }

            is Connected -> {
                if (route in state.backStacks.keys) {
                    state.topLevelRoute = route
                } else {
                    state.backStacks[state.topLevelRoute]?.add(route)
                }
            }
        }

        logBackStacks("navigate($route)")
    }

    fun goBack() {
        when (state.currentRootDestination) {
            AuthenticationRoute -> {
                state.authenticationBackStack.removeLastOrNull()
            }

            ConnectedRoute -> {
                val currentStack = state.backStacks[state.topLevelRoute]
                    ?: error("Stack for ${state.topLevelRoute} not found")

                val currentRoute = currentStack.last()

                if (currentRoute == state.topLevelRoute) {
                    state.topLevelRoute = state.topLevelStartRoute
                } else {
                    currentStack.removeLastOrNull()
                }
            }
        }

        logBackStacks("goBack()")
    }
}