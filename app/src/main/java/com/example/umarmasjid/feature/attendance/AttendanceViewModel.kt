package com.example.umarmasjid.feature.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.StudentWithAttendance
import com.example.umarmasjid.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val today = LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    private val _uiState =
        MutableStateFlow<AttendanceState>(AttendanceState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            attendanceRepository
                .getStudentsWithAttendance(today)
                .collect { students ->
                    _uiState.value = AttendanceState.Success(students)
                }
        }
    }

    fun onAction(event: AttendanceAction) {
        when (event) {
            is AttendanceAction.MarkAttendance -> {
                toggleAttendance(event.studentId, event.isPresent)
            }

            AttendanceAction.MarkAllAbsent -> {}
            AttendanceAction.MarkAllPresent -> {}
            AttendanceAction.ResetAttendance -> {}
        }
    }

    private fun toggleAttendance(studentId: Int, status: Boolean) {
        viewModelScope.launch {
            attendanceRepository.markAttendance(studentId, today, status)
        }
    }
}

sealed interface AttendanceState {
    object Loading : AttendanceState
    data class Success(val students: List<StudentWithAttendance>) : AttendanceState
}