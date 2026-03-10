package com.example.umarmasjid.feature.home

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.Destination

fun EntryProviderScope<NavKey>.homeEntry(
    onNavigate: (NavKey) -> Unit
) {
    entry<Destination.Home> {
        HomeScreen(
            navigateToAttendance = { onNavigate(Destination.StudentAttendance) },
            navigateToStudentManagement = { onNavigate(Destination.StudentProfile) },
            navigateToFeeManagement = { onNavigate(Destination.FeePayment) }
        )
    }
}
