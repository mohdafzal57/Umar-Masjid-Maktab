package com.example.umarmasjid.feature.home

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.feature.attendance.StudentAttendanceNavKey
import com.example.umarmasjid.feature.payments.PaymentsNavKey
import com.example.umarmasjid.feature.students.StudentProfileNavKey
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.homeEntry(
    navigator: UmarMasjidNavigator
) {
    entry<HomeNavKey> {
        HomeScreen(
            navigateToAttendance = { navigator.navigate(StudentAttendanceNavKey) },
            navigateToStudentManagement = { navigator.navigate(StudentProfileNavKey) },
            navigateToFeeManagement = { navigator.navigate(PaymentsNavKey) }
        )
    }
}
