package com.example.umarmasjid.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.islamicdashboard.ui.home.HomeScreen
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.ui.add_payment.AddPaymentScreen
import com.example.umarmasjid.ui.attendance.AttendanceScreen
import com.example.umarmasjid.ui.attendance.AttendanceViewModel
import com.example.umarmasjid.ui.bar_attendance_barchar.AttendanceBarCharScreen
import com.example.umarmasjid.ui.bottom_bar.UmarMasjidBottomBar
import com.example.umarmasjid.ui.payment.PaymentScreen
import com.example.umarmasjid.ui.payment.PaymentViewModel
import com.example.umarmasjid.ui.student.StudentScreen
import com.example.umarmasjid.ui.student.StudentViewModel
import com.example.umarmasjid.ui.student_detail.StudentPaymentViewModel

@Composable
fun UmarMasjidNavGraph() {
    val navigationState = rememberNavigationState(
        startRoute = Destination.Home,
        topLevelRoutes = setOf(
            Destination.Home,
            Destination.StudentAttendance,
            Destination.StudentProfile,
            Destination.FeePayment
        )
    )

    val navigator = remember { Navigator(navigationState) }
    val currentRoute = navigationState.topLevelRoute

    // Logic for Bottom Bar visibility
    val showBottomBar = currentRoute is Destination.Home || 
                        currentRoute is Destination.StudentAttendance //||
//                        currentRoute is Destination.StudentProfile ||
//                        currentRoute is Destination.FeePayment

    val selectedIndex = when (currentRoute) {
        is Destination.Home -> 0
        is Destination.StudentAttendance -> 1
        is Destination.StudentProfile -> 2
        is Destination.FeePayment -> 3
        else -> 0
    }

    val entryProvider = entryProvider<NavKey> {
        entry<Destination.Home> {
            HomeScreen(
                navigateToHome = { navigator.navigate(Destination.Home) },
                navigateToAttendance = { navigator.navigate(Destination.StudentAttendance) },
                navigateToStudentManagement = { navigator.navigate(Destination.StudentProfile) },
                navigateToFeeManagement = { navigator.navigate(Destination.FeePayment) }
            )
        }
        entry<Destination.StudentProfile> {
            val viewModel: StudentViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            StudentScreen(
                students = uiState.students,
                isLoading = uiState.isLoading,
                popToHome = { navigator.goBack() },
                onEvent = viewModel::onEvent,
                navigateToPayment = { id -> navigator.navigate(Destination.StudentPayment(id)) },
                navigateToAttendance = { id -> navigator.navigate(Destination.AttendanceChart(id)) }
            )
        }
        entry<Destination.StudentAttendance> {
            val viewModel = hiltViewModel<AttendanceViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            AttendanceScreen(
                popToHome = { navigator.goBack() },
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )
        }
        entry<Destination.FeePayment> {
            val viewModel = hiltViewModel<PaymentViewModel>()
            val uiState by viewModel.studentsWithPaymentStatus.collectAsStateWithLifecycle()
            PaymentScreen(
                popToHome = { navigator.goBack() },
                uiState = uiState,
                onPaymentDone = viewModel::insertPayment
            )
        }
        entry<Destination.StudentPayment> { key ->
            val viewModel = hiltViewModel<StudentPaymentViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            AddPaymentScreen(
                student = uiState.student ?: StudentEntity(99, "", "", 0),
                existingPayments = uiState.payments,
                onEvent = viewModel::onEvent,
                onBack = { navigator.goBack() }
            )
        }
        entry<Destination.AttendanceChart> { key ->
            AttendanceBarCharScreen(key.studentId)
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            bottomBar = {
                UmarMasjidBottomBar(
                    onSelectedItem = { index ->
                        val dest = when (index) {
                            0 -> Destination.Home
                            1 -> Destination.StudentAttendance
                            2 -> Destination.StudentProfile
                            3 -> Destination.FeePayment
                            else -> Destination.Home
                        }
                        navigator.navigate(dest)
                    },
                    selectedIndex = selectedIndex
                )

            }
        ) { innerPadding ->
            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
