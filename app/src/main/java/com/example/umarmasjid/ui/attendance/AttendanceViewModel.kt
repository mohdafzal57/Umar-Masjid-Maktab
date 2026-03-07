package com.example.umarmasjid.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.StudentWithAttendance
import com.example.umarmasjid.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

sealed interface AttendanceState {
    object Loading : AttendanceState
    data class Success(val students: List<StudentWithAttendance>) : AttendanceState
}

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

                    _uiState.value =
                        AttendanceState.Success(students)
                }
        }
    }

    fun onEvent(event: AttendanceEvent) {
        when (event) {

            is AttendanceEvent.MarkAttendance -> {
                toggleAttendance(event.studentId, event.isPresent)
            }

            AttendanceEvent.MarkAllAbsent -> { }

            AttendanceEvent.MarkAllPresent -> { }

            AttendanceEvent.ResetAttendance -> { }
        }
    }

    private fun toggleAttendance(studentId: Int, status: Boolean) {
        viewModelScope.launch {
            attendanceRepository.markAttendance(studentId, today, status)
        }
    }
}
