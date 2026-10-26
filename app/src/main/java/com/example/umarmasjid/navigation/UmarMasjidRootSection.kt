package com.example.umarmasjid.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.umarmasjid.feature.attendance.attendanceEntry
import com.example.umarmasjid.feature.attendance_bar_chart.attendanceBarChartEntry
import com.example.umarmasjid.feature.home.homeEntry
import com.example.umarmasjid.feature.login.loginEntry
import com.example.umarmasjid.feature.payments.paymentsEntry
import com.example.umarmasjid.feature.reset_password.resetPasswordEntry
import com.example.umarmasjid.feature.reset_password.updatePasswordEntry
import com.example.umarmasjid.feature.signup.signupEntry
import com.example.umarmasjid.feature.student_payment.studentPaymentEntry
import com.example.umarmasjid.feature.students.studentEntry
import com.example.umarmasjid.feature.settings.settingsEntry

fun EntryProviderScope<NavKey>.rootSection(
    navigator: UmarMasjidNavigator,
) {
    entry<AuthenticationRoute> {
        NavDisplay(
            backStack = navigator.state.authenticationBackStack,
            entryProvider = entryProvider {
                authenticationSection(navigator)
            },
        )
    }

    entry<ConnectedRoute> {
        val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

        SharedTransitionLayout {
            NavDisplay(
                entries = navigator.state.toDecoratedEntries(
                    entryProvider = entryProvider {
                        homeEntry(navigator)
                        attendanceEntry(navigator)
                        studentEntry(navigator)
                        paymentsEntry(navigator)
                        studentPaymentEntry(navigator)
                        attendanceBarChartEntry(navigator)
                        settingsEntry(navigator)
                    }
                ),
                sceneStrategies = listOf(listDetailStrategy),
                sharedTransitionScope = this,
                onBack = {
                    navigator.goBack()
                }
            )
        }
    }
}

fun EntryProviderScope<NavKey>.authenticationSection(
    navigator: UmarMasjidNavigator
) {
    loginEntry(navigator)
    signupEntry(navigator)
    resetPasswordEntry(navigator)
    updatePasswordEntry(navigator)
}

//private fun NavBackStack<NavKey>.addDetail(detailRoute: ConnectedRoute.StudentPaymentRoute) {
//
//    // Remove any existing detail routes before adding this detail route.
//    // In certain scenarios, such as when multiple detail panes can be shown at once, it may
//    // be desirable to keep existing detail routes on the back stack.
//    removeIf { it is ConnectedRoute.StudentPaymentRoute }
//    add(detailRoute)
//}

inline fun <reified T : NavKey>
        NavBackStack<NavKey>.addSingleDetail(route: T) {
    removeAll { it is T }
    add(route)
}