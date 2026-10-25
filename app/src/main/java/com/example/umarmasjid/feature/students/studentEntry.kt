package com.example.umarmasjid.feature.students

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.ConnectedRoute
import com.example.umarmasjid.navigation.ListDetailScene
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.studentEntry(navigator: UmarMasjidNavigator) {
    entry<ConnectedRoute.StudentRoute>(
        metadata = ListDetailScene.listPane(),
    ) {
        val viewModel: StudentProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StudentScreen(
            students = uiState.students,
            isLoading = uiState.isLoading,
            onEvent = viewModel::onEvent,
            navigateToPayment = { id -> navigator.navigate(ConnectedRoute.StudentPaymentRoute(id)) },
            navigateToAttendanceBarChart = { id ->
                navigator.navigate(ConnectedRoute.AttendanceBarChartRoute(id))
            }
        )
    }
}
