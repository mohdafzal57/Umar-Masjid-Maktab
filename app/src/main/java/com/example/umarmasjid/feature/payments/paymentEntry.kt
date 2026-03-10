package com.example.umarmasjid.feature.payments

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.navigation.Destination

fun EntryProviderScope<NavKey>.paymentEntry(
    onBack: () -> Unit
) {
    entry<Destination.FeePayment> {
        val viewModel = hiltViewModel<PaymentViewModel>()
        val uiState by viewModel.studentsWithPaymentStatus.collectAsStateWithLifecycle()
        PaymentScreen(
            popToHome = onBack,
            uiState = uiState,
            onPaymentDone = viewModel::insertPayment
        )
    }
}
