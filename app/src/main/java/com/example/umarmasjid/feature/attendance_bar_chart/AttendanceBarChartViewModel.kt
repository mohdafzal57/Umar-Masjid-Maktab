package com.example.umarmasjid.feature.attendance_bar_chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.repository.AttendanceRepository
import com.example.umarmasjid.util.getCurrentMonthRange
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

const val TAG = "AttendanceBarChartViewModel"

@HiltViewModel(assistedFactory = AttendanceBarChartViewModel.Factory::class)
class AttendanceBarChartViewModel @AssistedInject constructor(
    private val repository: AttendanceRepository,
    @Assisted val studentId: Int,
) : ViewModel() {

    private val dateRange = getCurrentMonthRange()

    val attendanceState: StateFlow<List<AttendanceEntity>> =
        repository.getAttendanceForMonth(studentId, dateRange.first, dateRange.second)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    @AssistedFactory
    interface Factory {
        fun create(
            studentId: Int
        ): AttendanceBarChartViewModel
    }
}
