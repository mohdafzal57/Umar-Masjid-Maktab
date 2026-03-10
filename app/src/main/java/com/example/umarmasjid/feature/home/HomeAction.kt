package com.example.umarmasjid.feature.home

sealed interface HomeAction {
    data object ToggleDarkMode : HomeAction
    data object RefreshData : HomeAction
    data object DismissError : HomeAction
    data class SelectAttendancePeriod(val period: AttendancePeriod) : HomeAction

    data object NavigateToAttendance : HomeAction
    data object NavigateToStudents : HomeAction
    data object NavigateToFees : HomeAction
    data class NavigateToStudent(val studentId: Int) : HomeAction
}
