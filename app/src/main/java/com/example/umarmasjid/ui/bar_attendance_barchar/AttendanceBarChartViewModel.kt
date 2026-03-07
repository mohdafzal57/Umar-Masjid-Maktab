package com.example.umarmasjid.ui.bar_attendance_barchar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.repository.AttendanceRepository
import com.example.umarmasjid.util.getCurrentMonthRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "AttendanceViewModel"

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: AttendanceRepository
) : ViewModel() {

    private val _attendanceState = MutableStateFlow<List<AttendanceEntity>>(emptyList())
    val attendanceState: StateFlow<List<AttendanceEntity>> = _attendanceState.asStateFlow()

    fun loadAttendance(studentId: Int/*, month: Int, year: Int*/) {
        val (startDate, endDate) = getCurrentMonthRange()
        viewModelScope.launch {
            repository.getAttendanceForMonth(studentId, startDate, endDate)
                .collect { attendanceList ->
                    _attendanceState.value = attendanceList
                }
        }
    }
}
