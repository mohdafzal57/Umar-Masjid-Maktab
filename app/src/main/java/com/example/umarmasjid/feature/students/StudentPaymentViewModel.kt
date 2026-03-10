package com.example.umarmasjid.feature.students

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.repository.PaymentRepository
import com.example.umarmasjid.repository.StudentRepository
import com.example.umarmasjid.ui.add_payment.PaymentEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentPaymentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val studentRepository: StudentRepository,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentPaymentState())
    val uiState = _uiState.asStateFlow()

    private val studentId: Int = checkNotNull(savedStateHandle["studentId"])

    init {
        viewModelScope.launch {
            combine(
                studentRepository.getStudentById(studentId),
                paymentRepository.getPaymentsTillNowByStudentId(studentId)
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
                    paymentRepository.insertPayment(
                        PaymentEntity(
                            studentId = event.studentId,
                            amount = event.amount,
                            paymentMethod = event.paymentMethod,
                            receiptNumber = event.receiptNumber,
                            paymentDate = System.currentTimeMillis()
                        )
                    )
                }
            }
            is PaymentEvent.DeletePayment -> {
                viewModelScope.launch {
                    paymentRepository.deletePayment(event.payment)
                }
            }
        }
    }
}

data class StudentPaymentState(
    val student: StudentEntity? = null,
    val payments: List<PaymentEntity> = emptyList()
)
