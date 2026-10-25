package com.example.umarmasjid.feature.attendance

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.ConnectedRoute
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.attendanceEntry(
    navigator: UmarMasjidNavigator
) {
    entry<ConnectedRoute.AttendanceRoute> {
        val viewModel = hiltViewModel<AttendanceViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        AttendanceScreen(
            uiState = uiState,
            onEvent = viewModel::onAction,
        )
    }
}
