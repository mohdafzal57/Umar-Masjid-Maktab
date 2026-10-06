package com.example.umarmasjid.feature.home

import com.example.umarmasjid.domain.usecase.home.HomeDashboardData


object HomeUiMapper {
    fun mapDashboard(dashboard: HomeDashboardData): HomeDashboardUi {
        return HomeDashboardUi(
            stats = DashboardStats(
                totalStudents = dashboard.stats.totalStudents,
                presentToday = dashboard.stats.presentToday,
                attendanceRate = formatAttendanceRate(dashboard.stats.attendanceRate),
                feesCollected = "₹${formatAmount(dashboard.stats.feesCollected)}",
                pendingFees = "₹${formatAmount(dashboard.stats.pendingFees)}",
                pendingStudentsCount = dashboard.stats.pendingStudentsCount,
            ),
            recentStudents = dashboard.recentStudents.map { student ->
                StudentSummary(
                    id = student.id,
                    initials = student.name.initials(),
                    name = student.name,
                    feeStatus = if (student.isFeePending) FeeStatus.PENDING else FeeStatus.PAID,
                )
            },
            attendanceTrend = dashboard.attendanceTrend,
        )
    }

    private fun formatAttendanceRate(rate: Float): String =
        "%.1f%% rate".format(rate * 100f)

    private fun formatAmount(amount: Double): String =
        when {
            amount >= 1000 -> "%.1fK".format(amount / 1000)
            else -> "%.0f".format(amount)
        }

    private fun String.initials(): String =
        split(" ")
            .filter(String::isNotBlank)
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
}

data class HomeDashboardUi(
    val stats: DashboardStats,
    val recentStudents: List<StudentSummary>,
    val attendanceTrend: List<AttendancePoint>,
)
