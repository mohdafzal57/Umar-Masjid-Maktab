package com.example.umarmasjid.feature.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.repository.OfflinePaymentRepository
import com.example.umarmasjid.repository.OfflineStudentRepository
import com.example.umarmasjid.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val offlineStudentRepository: OfflineStudentRepository,
    private val offlinePaymentRepository: OfflinePaymentRepository
) : ViewModel() {

    private val _studentsWithPaymentStatus = MutableStateFlow(PaymentState())
    val studentsWithPaymentStatus = _studentsWithPaymentStatus.asStateFlow()

    /*private val start = DateUtils.getStartOfMonthMillis()
    private val end = DateUtils.getEndOfMonthMillis()*/

    private val startOfTheMonth = DateUtils.getCurrentMonthRange().first
    private val startOfNextMonth = DateUtils.getCurrentMonthRange().second

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _studentsWithPaymentStatus.update { it.copy(isLoading = true) }

            combine(
                offlinePaymentRepository.getAllStudentsWithPaymentWithinMonth(startOfTheMonth, startOfNextMonth),
                offlineStudentRepository.getStudentsWhoNotPaid(startOfTheMonth, startOfNextMonth)
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

    fun insertPayment(
        studentId: String,
        amount: Double,
        note: String?
    ) {
        viewModelScope.launch {
            offlinePaymentRepository.insertPayment(
                PaymentEntity(
                    studentId = studentId,
                    paymentDate = LocalDate.now(),
                    amount = amount,
                    note = note,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}

enum class PaymentStatus(val label: String) {
    PAID("Paid"),
    UNPAID("Unpaid")
}
