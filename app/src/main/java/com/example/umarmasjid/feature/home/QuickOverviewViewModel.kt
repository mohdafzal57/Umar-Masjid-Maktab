package com.example.umarmasjid.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.repository.QuickOverviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuickOverviewState(
    val totalStudents: Int = 0,
    val presentToday: Int = 0,
    val totalPayments: Double = 0.0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

const val TAG = "QUICK_OVERVIEW_MODEL"

@HiltViewModel
class QuickOverviewViewModel @Inject constructor(
    private val repository: QuickOverviewRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuickOverviewState())
    val state = _state.asStateFlow()

    init {
        loadOverview()
    }

    fun loadOverview() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.getTotalStudents().collect { totalStudents ->
                    _state.update { it.copy(totalStudents = totalStudents) }
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.getTodayAttendance().collect { presentToday ->
                    _state.update { it.copy(presentToday = presentToday) }
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.getTotalPaymentThisMonth().collect { totalPayments ->
                    _state.update { it.copy(totalPayments = totalPayments ?: 0.0) }
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
