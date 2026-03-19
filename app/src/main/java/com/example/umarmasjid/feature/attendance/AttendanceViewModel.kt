package com.example.umarmasjid.feature.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.StudentWithAttendance
import com.example.umarmasjid.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val uiState: StateFlow<AttendanceState> = attendanceRepository
        .getStudentsWithAttendance(today)
        .map { AttendanceState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendanceState.Loading
        )

    fun onAction(action: AttendanceAction) {
        when (action) {
            is AttendanceAction.MarkAttendance -> {
                toggleAttendance(action.studentId, action.isPresent)
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