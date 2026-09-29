package com.example.umarmasjid.feature.home

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.ConnectedRoute
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.homeEntry(
    navigator: UmarMasjidNavigator
) {
    entry<ConnectedRoute.HomeRoute> {
        HomeScreen(
            navigateToAttendance = { navigator.navigate(ConnectedRoute.AttendanceRoute) },
            navigateToStudentManagement = { navigator.navigate(ConnectedRoute.StudentRoute) },
            navigateToFeeManagement = { navigator.navigate(ConnectedRoute.PaymentRoute) }
        )
    }
}
