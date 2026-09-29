package com.example.umarmasjid.feature.payments

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.ConnectedRoute
import com.example.umarmasjid.navigation.UmarMasjidNavigator

fun EntryProviderScope<NavKey>.paymentsEntry(
    navigator: UmarMasjidNavigator
) {
    entry<ConnectedRoute.PaymentRoute> {
        val viewModel = hiltViewModel<PaymentsViewModel>()
        val uiState by viewModel.studentsWithPaymentStatus.collectAsStateWithLifecycle()
        PaymentsScreen(
            popToHome = navigator::goBack,
            uiState = uiState,
            onPaymentDone = viewModel::insertPayment
        )
    }
}
