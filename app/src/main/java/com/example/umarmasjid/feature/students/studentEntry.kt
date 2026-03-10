package com.example.umarmasjid.feature.students

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.navigation.Destination
import com.example.umarmasjid.ui.add_payment.AddPaymentScreen

fun EntryProviderScope<NavKey>.studentEntry(
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    entry<Destination.StudentProfile> {
        val viewModel: StudentViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StudentScreen(
            students = uiState.students,
            isLoading = uiState.isLoading,
            popToHome = onBack,
            onEvent = viewModel::onEvent,
            navigateToPayment = { id -> onNavigate(Destination.StudentPayment(id)) },
            navigateToAttendance = { id -> onNavigate(Destination.AttendanceChart(id)) }
        )
    }

    entry<Destination.StudentPayment>(
        metadata = NavDisplay.transitionSpec {
            slideInHorizontally(tween(350)) { it } togetherWith
                    slideOutHorizontally(tween(350)) { -it / 3 }
        } + NavDisplay.popTransitionSpec {
            slideInHorizontally(tween(350)) { -it / 3 } togetherWith
                    slideOutHorizontally(tween(350)) { it }
        } + NavDisplay.predictivePopTransitionSpec { _ ->
            slideInHorizontally(tween(350)) { -it / 2 } togetherWith
                    slideOutHorizontally(tween(350)) { it }
        }
    ) { key ->
        val viewModel = hiltViewModel<StudentPaymentViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        AddPaymentScreen(
            student = uiState.student ?: StudentEntity(99, "", "", 0),
            existingPayments = uiState.payments,
            onEvent = viewModel::onEvent,
            onBack = onBack
        )
    }
}
