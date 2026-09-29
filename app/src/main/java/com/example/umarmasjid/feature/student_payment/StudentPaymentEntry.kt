package com.example.umarmasjid.feature.student_payment

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.example.umarmasjid.navigation.ConnectedRoute
import com.example.umarmasjid.navigation.UmarMasjidNavigator
import com.example.umarmasjid.navigation.horizontalSlideTransition

fun EntryProviderScope<NavKey>.studentPaymentEntry(navigator: UmarMasjidNavigator) {
    entry<ConnectedRoute.StudentPaymentRoute>(
        metadata = NavDisplay.horizontalSlideTransition()
    ) { key ->
        val studentId = key.studentId
        val viewModel = hiltViewModel<StudentPaymentViewModel, StudentPaymentViewModel.Factory>(
            key = "student_payment_$studentId"
        ) { factory ->
            factory.create(studentId)
        }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        StudentPaymentScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onBack = navigator::goBack
        )
    }
}
