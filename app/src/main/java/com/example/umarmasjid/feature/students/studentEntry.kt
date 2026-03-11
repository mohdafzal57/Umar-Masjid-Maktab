package com.example.umarmasjid.feature.students

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.feature.attendance_bar_chart.navigateToAttendanceChart
import com.example.umarmasjid.feature.student_payment.navigateToStudentPayment
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.studentEntry(navigator: UmarMasjidNavigator) {
    entry<StudentProfileNavKey> {
        val viewModel: StudentProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StudentScreen(
            students = uiState.students,
            isLoading = uiState.isLoading,
            popToHome = navigator::goBack,
            onEvent = viewModel::onEvent,
            navigateToPayment = { id -> navigator.navigateToStudentPayment(id) },
            navigateToAttendanceBarChart = { id ->
                navigator.navigateToAttendanceChart(id)
            }
        )
    }
}