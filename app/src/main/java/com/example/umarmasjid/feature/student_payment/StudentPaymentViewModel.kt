package com.example.umarmasjid.feature.student_payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.repository.OfflinePaymentRepository
import com.example.umarmasjid.repository.OfflineStudentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel(assistedFactory = StudentPaymentViewModel.Factory::class)
class StudentPaymentViewModel @AssistedInject constructor(
    private val offlineStudentRepository: OfflineStudentRepository,
    private val offlinePaymentRepository: OfflinePaymentRepository,
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
                offlineStudentRepository.getStudentById(studentId),
                offlinePaymentRepository.getPaymentsTillNowByStudentId(studentId)
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
                    offlinePaymentRepository.insertPayment(
                        PaymentEntity(
                            studentId = event.studentId,
                            amount = event.amount,
                            note = event.note,
                            paymentDate = LocalDate.now(),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            }

            is PaymentEvent.DeletePayment -> {
                viewModelScope.launch {
                    offlinePaymentRepository.deletePayment(
                        paymentId = event.payment.id,
                        timestamp = System.currentTimeMillis()
                    )
                }
            }
        }
    }}

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