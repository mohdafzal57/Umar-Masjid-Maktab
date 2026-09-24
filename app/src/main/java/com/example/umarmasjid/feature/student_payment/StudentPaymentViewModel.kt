package com.example.umarmasjid.feature.student_payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.repository.OfflineFirstPaymentRepository
import com.example.umarmasjid.repository.OfflineFirstStudentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = StudentPaymentViewModel.Factory::class)
class StudentPaymentViewModel @AssistedInject constructor(
    private val offlineFirstStudentRepository: OfflineFirstStudentRepository,
    private val offlineFirstPaymentRepository: OfflineFirstPaymentRepository,
    @Assisted val studentId: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(studentId: String): StudentPaymentViewModel
    }

    private val _uiState = MutableStateFlow(StudentPaymentState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                offlineFirstStudentRepository.getStudentById(studentId),
                offlineFirstPaymentRepository.getPaymentsTillNowByStudentId(studentId)
            ) { student, payments ->
                student to payments
            }.collect { (student, payments) ->
                _uiState.update {
                    it.copy(
                        student = student,
                        payments = payments
                    )
                }
            }
        }
    }

    fun onEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.AddPayment -> {
                viewModelScope.launch {
                    offlineFirstPaymentRepository.insertPayment(
                        studentId = event.studentId,
                        amount = event.amount,
                        note = event.note,
                    )
                }
            }

            is PaymentEvent.DeletePayment -> {
                viewModelScope.launch {
                    offlineFirstPaymentRepository.deletePayment(
                        paymentId = event.payment.id,
                    )
                }
            }
        }
    }
}

data class StudentPaymentState(
    val student: StudentEntity? = null,
    val payments: List<PaymentEntity> = emptyList()
)

sealed class PaymentEvent {

    data class AddPayment(
        val studentId: String,
        val amount: Double,
        val note: String?
    ) : PaymentEvent()

    data class DeletePayment(
        val payment: PaymentEntity
    ) : PaymentEvent()
}