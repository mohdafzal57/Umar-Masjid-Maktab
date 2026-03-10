package com.example.umarmasjid.feature.attendance

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.Destination
import com.example.umarmasjid.ui.bar_attendance_barchar.AttendanceBarCharScreen

fun EntryProviderScope<NavKey>.attendanceEntry(
    onBack: () -> Unit
) {
    entry<Destination.StudentAttendance> {
        val viewModel = hiltViewModel<AttendanceViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        AttendanceScreen(
            popToHome = onBack,
            uiState = uiState,
            onEvent = viewModel::onAction,
        )
    }
    
    entry<Destination.AttendanceChart> { key ->
        AttendanceBarCharScreen(key.studentId)
    }
}
