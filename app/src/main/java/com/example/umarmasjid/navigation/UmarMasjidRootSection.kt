package com.example.umarmasjid.navigation

import androidx.navigation3.runtime.EntryProviderScope
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
        NavDisplay(
            entries = navigator.state.toDecoratedEntries(
                entryProvider = entryProvider {
                    homeEntry(navigator)
                    attendanceEntry(navigator)
                    studentEntry(navigator)
                    paymentsEntry(navigator)
                    studentPaymentEntry(navigator)
                    attendanceBarChartEntry(navigator)
                }
            ),
            onBack = {
                navigator.goBack()
            }
        )
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