package com.example.umarmasjid.feature.attendance_bar_chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.repository.OfflineFirstAttendanceRepository
import com.example.umarmasjid.util.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = AttendanceBarChartViewModel.Factory::class)
class AttendanceBarChartViewModel @AssistedInject constructor(
    private val repository: OfflineFirstAttendanceRepository,
    @Assisted val studentId: String,
) : ViewModel() {

    private val startOfTheMonth = DateUtils.getCurrentMonthRange().first
    private val startOfNextMonth = DateUtils.getCurrentMonthRange().second

    val attendanceState: StateFlow<List<AttendanceEntity>> =
        repository.getAttendanceForMonth(
            studentId,
            startOfTheMonth,
            startOfNextMonth
        )
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    @AssistedFactory
    interface Factory {
        fun create(
            studentId: String
        ): AttendanceBarChartViewModel
    }
}
