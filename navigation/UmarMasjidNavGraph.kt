package com.example.umarmasjid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.umarmasjid.feature.attendance.attendanceEntry
import com.example.umarmasjid.feature.home.homeEntry
import com.example.umarmasjid.feature.payments.paymentEntry
import com.example.umarmasjid.feature.students.studentEntry

@Composable
fun UmarMasjidNavGraph(
    navigationState: UmarMasjidNavigationState,
    navigator: UmarMasjidNavigator,
    modifier: Modifier = Modifier
) {
    val entryProvider = entryProvider<NavKey> {
        homeEntry(onNavigate = { navigator.navigate(it) })
        attendanceEntry(onBack = { navigator.goBack() })
        studentEntry(
            onNavigate = { navigator.navigate(it) },
            onBack = { navigator.goBack() }
        )
        paymentEntry(onBack = { navigator.goBack() })
    }

    NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() },
        modifier = modifier
    )
}
