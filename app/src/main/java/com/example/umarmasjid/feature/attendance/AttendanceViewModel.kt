package com.example.umarmasjid.feature.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.AttendanceStatus
import com.example.umarmasjid.data.local_database.model.StudentAttendance
import com.example.umarmasjid.repository.OfflineAttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val offlineAttendanceRepository: OfflineAttendanceRepository
) : ViewModel() {

    val uiState: StateFlow<AttendanceUiState> = offlineAttendanceRepository
        .getStudentsWithAttendance(LocalDate.now())
        .map {
            println("students: $it")
            AttendanceUiState.Success(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendanceUiState.Loading
        )


    fun onAction(action: AttendanceAction) {
        when (action) {
            is AttendanceAction.MarkAttendance -> toggleAttendance(action.studentAttendance)
        }
    }

    private fun toggleAttendance(studentAttendance: StudentAttendance) {
        viewModelScope.launch {
            val newStatus = when (studentAttendance.status) {
                AttendanceStatus.PRESENT -> AttendanceStatus.ABSENT
                AttendanceStatus.ABSENT -> AttendanceStatus.LATE
                AttendanceStatus.LATE -> AttendanceStatus.PRESENT
                null -> AttendanceStatus.PRESENT
            }
            offlineAttendanceRepository.markAttendance(studentAttendance.copy(status = newStatus))
        }
    }
}
