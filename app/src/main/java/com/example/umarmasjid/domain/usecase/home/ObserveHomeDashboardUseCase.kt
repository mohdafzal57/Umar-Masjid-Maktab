package com.example.umarmasjid.domain.usecase.home

import com.example.umarmasjid.feature.home.AttendancePeriod
import com.example.umarmasjid.feature.home.AttendancePoint
import com.example.umarmasjid.repository.HomeDashboardRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ObserveHomeDashboardUseCase @Inject constructor(
    private val repository: HomeDashboardRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(period: Flow<AttendancePeriod>): Flow<HomeDashboardData> {
        val pendingStudents = repository.getStudentsWithPendingFees()

        val stats = combine(
            repository.getTotalStudents(),
            repository.getTodayAttendance(),
            repository.getMonthlyFeesCollected(),
            pendingStudents,
        ) { total, present, feesCollected, pending ->
            HomeDashboardStats(
                totalStudents = total,
                presentToday = present,
                attendanceRate = if (total > 0) present.toFloat() / total else 0f,
                feesCollected = feesCollected,
                pendingFees = pending.size * AVERAGE_MONTHLY_FEE,
                pendingStudentsCount = pending.size,
            )
        }

        val recentStudents = combine(
            repository.getRecentStudents(),
            pendingStudents,
        ) { recent, pending ->
            val pendingIds = pending.map { it.id }.toSet()
            recent.map { student ->
                StudentSummaryData(
                    id = student.id,
                    name = student.name,
                    isFeePending = student.id in pendingIds,
                )
            }
        }

        val attendanceTrend = period.flatMapLatest { selectedPeriod ->
            repository.getAttendanceTrend(selectedPeriod)
        }

        return combine(stats, recentStudents, attendanceTrend) { dashboardStats, students, trend ->
            HomeDashboardData(
                stats = dashboardStats,
                recentStudents = students,
                attendanceTrend = trend,
            )
        }
    }

    private companion object {
        // Todo: User will set this
        const val AVERAGE_MONTHLY_FEE = 200.0
    }
}

data class HomeDashboardData(
    val stats: HomeDashboardStats,
    val recentStudents: List<StudentSummaryData>,
    val attendanceTrend: List<AttendancePoint>,
)

data class HomeDashboardStats(
    val totalStudents: Int,
    val presentToday: Int,
    val attendanceRate: Float,
    val feesCollected: Double,
    val pendingFees: Double,
    val pendingStudentsCount: Int,
)

data class StudentSummaryData(
    val id: String,
    val name: String,
    val isFeePending: Boolean,
)
