package com.example.umarmasjid.feature.attendance_bar_chart

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.UmarMasjidNavigator
import com.example.umarmasjid.feature.attendance_bar_chart.AttendanceBarChartViewModel.Factory

fun EntryProviderScope<NavKey>.attendanceBarChartEntry(
    navigator: UmarMasjidNavigator
) {
    entry<AttendanceChartNavKey> { key ->
        val studentId = key.studentId
        AttendanceBarCharScreen(
            viewModel = hiltViewModel<AttendanceBarChartViewModel, Factory>(
                key = "attendance_chart_$studentId",
                creationCallback = { factory ->
                    factory.create(studentId)
                }
            )
        )
    }
}