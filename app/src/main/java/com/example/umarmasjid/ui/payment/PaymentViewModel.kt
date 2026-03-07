package com.example.umarmasjid.ui.payment

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.repository.PaymentRepository
import com.example.umarmasjid.repository.StudentRepository
import com.example.umarmasjid.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _studentsWithPaymentStatus = MutableStateFlow(PaymentState())
    val studentsWithPaymentStatus = _studentsWithPaymentStatus.asStateFlow()


    fun insertPayment(
        studentId: Int,
        amount: Double,
        paymentMethod: String,
        receiptNumber: String?,
    ) {
        viewModelScope.launch {
            paymentRepository.insertPayment(
                PaymentEntity(
                    studentId = studentId,
                    paymentDate = System.currentTimeMillis(),
                    amount = amount,
                    paymentMethod = paymentMethod,
                    receiptNumber = receiptNumber ?: UUID.randomUUID().toString()
                )
            )
        }
    }
    val start = DateUtils.getStartOfMonthMillis()
    val end = DateUtils.getEndOfMonthMillis()



    init {
        viewModelScope.launch {
            _studentsWithPaymentStatus.update {
                it.copy(
                    isLoading = true
                )
            }
            combine(
                paymentRepository.getAllStudentsWithPaymentWithinMonth(start, end),
                studentRepository.getStudentsWhoNotPaid(start, end)
            ) { paid, unpaid ->
                paid to unpaid
            }.collect { (paidStudents, unpaidStudents) ->
                _studentsWithPaymentStatus.update {
                    it.copy(
                        paidStudents = paidStudents,
                        unpaidStudents = unpaidStudents,
                        isLoading = false
                    )
                }
            }
        }
    }


}


enum class PaymentStatus(val label: String) {
    PAID("Paid"),
    UNPAID("Unpaid")
}