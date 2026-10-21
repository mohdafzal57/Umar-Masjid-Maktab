package com.example.umarmasjid.feature.attendance_bar_chart

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.feature.attendance_bar_chart.AttendanceBarChartViewModel.Factory
import com.example.umarmasjid.navigation.ConnectedRoute
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.attendanceBarChartEntry(
    navigator: UmarMasjidNavigator
) {
    entry<ConnectedRoute.AttendanceBarChartRoute> { key ->
        val studentId = key.studentId
        AttendanceBarChartScreen(
            viewModel = hiltViewModel<AttendanceBarChartViewModel, Factory>(
                key = "attendance_chart_$studentId",
                creationCallback = { factory ->
                    factory.create(studentId)
                }
            )
        )
    }
}
