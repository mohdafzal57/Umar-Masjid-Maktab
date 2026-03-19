package com.example.umarmasjid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.umarmasjid.feature.attendance.attendanceEntry
import com.example.umarmasjid.feature.attendance_bar_chart.attendanceBarChartEntry
import com.example.umarmasjid.feature.home.homeEntry
import com.example.umarmasjid.feature.payments.paymentsEntry
import com.example.umarmasjid.feature.student_payment.studentPaymentEntry
import com.example.umarmasjid.feature.students.studentEntry

@Composable
fun UmarMasjidNavGraph(
    navigationState: UmarMasjidNavigationState,
    navigator: UmarMasjidNavigator,
    modifier: Modifier = Modifier
) {
    val entryProvider = entryProvider {
        homeEntry(navigator)
        attendanceEntry(navigator)
        studentEntry(navigator)
        paymentsEntry(navigator)
        studentPaymentEntry(navigator)
        attendanceBarChartEntry(navigator)
    }

    NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() },
        modifier = modifier,
        predictivePopTransitionSpec = noneTransition()
    )
}