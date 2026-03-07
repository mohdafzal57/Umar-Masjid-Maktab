package com.example.umarmasjid.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.ui.add_payment.AddPaymentScreen
import com.example.umarmasjid.ui.attendance.AttendanceScreen
import com.example.umarmasjid.ui.attendance.AttendanceViewModel
import com.example.umarmasjid.ui.bar_attendance_barchar.AttendanceBarCharScreen
import com.example.umarmasjid.ui.bottom_bar.UmarMasjidBottomBar
import com.example.umarmasjid.ui.home.HomeScreen
import com.example.umarmasjid.ui.payment.PaymentScreen
import com.example.umarmasjid.ui.payment.PaymentViewModel
import com.example.umarmasjid.ui.student.StudentScreen
import com.example.umarmasjid.ui.student.StudentViewModel
import com.example.umarmasjid.ui.student_detail.StudentPaymentViewModel

@Composable
fun UmarMasjidNavGraph() {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: UmarMasjidDestinations.HOME_ROUTE

    val navigationActions = remember(navController) {
        UmarMasjidNavigationActions(navController)
    }

    val showBottomBar = when (currentRoute) {
        in UmarMasjidDestinations.HOME_ROUTE,
        UmarMasjidDestinations.STUDENT_ATTENDANCE_ROUTE -> true
        else -> false
    }

    val selectedIndex = when (currentRoute) {
        UmarMasjidDestinations.HOME_ROUTE -> 0
        UmarMasjidDestinations.STUDENT_ATTENDANCE_ROUTE -> 1
        UmarMasjidDestinations.STUDENT_PROFILE_ROUTE -> 2
        UmarMasjidDestinations.FEE_PAYMENT_ROUTE -> 3
        else -> 0
    }
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically(animationSpec = tween(500)) { it },
                    exit = slideOutVertically(animationSpec = tween(500)) { it }
                ) {
                    UmarMasjidBottomBar(
                        onSelectedItem = { selectedItem ->
                            when (selectedItem) {
                                0 -> navigationActions.navigateToHome()
                                1 -> navigationActions.navigateToStudentAttendance()
                                2 -> navigationActions.navigateToStudentManagement()
                                3 -> navigationActions.navigateToFeeManagement()
                            }
                        },
                        selectedIndex = selectedIndex,
                    )
                }

            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = UmarMasjidDestinations.HOME_ROUTE,
                enterTransition = {
                    // Forward navigation → Z-axis (fade + scale up)
                    fadeIn(animationSpec = tween(300)) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec = tween(300)
                            )
                },
                exitTransition = {
                    // Forward navigation exit → fade out + scale down slightly
                    fadeOut(animationSpec = tween(200)) +
                            scaleOut(
                                targetScale = 1.08f,
                                animationSpec = tween(200)
                            )
                },
                popEnterTransition = {
                    // Back navigation → fade in + scale down
                    fadeIn(animationSpec = tween(300)) +
                            scaleIn(
                                initialScale = 1.08f,
                                animationSpec = tween(300)
                            )
                },
                popExitTransition = {
                    // Back navigation exit → fade out + scale shrink
                    fadeOut(animationSpec = tween(200)) +
                            scaleOut(
                                targetScale = 0.92f,
                                animationSpec = tween(200)
                            )
                }
            ) {
                composable(route = UmarMasjidDestinations.HOME_ROUTE) {
                    HomeScreen(
                        innerPadding = innerPadding,
                        navigateToHome = navigationActions.navigateToHome,
                        navigateToAttendance = navigationActions.navigateToStudentAttendance,
                        navigateToStudentManagement = { navController.navigate(UmarMasjidDestinations.STUDENT_PROFILE_ROUTE) },
                        navigateToFeeManagement = { navController.navigate(UmarMasjidDestinations.FEE_PAYMENT_ROUTE) }
                    )
                }
                composable(route = UmarMasjidDestinations.STUDENT_PROFILE_ROUTE,
                    ) {
                    val viewModel: StudentViewModel = hiltViewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    StudentScreen(
                        students = uiState.students,
                        isLoading = uiState.isLoading,
                        popToHome = { navController.popBackStack() },
                        onEvent = viewModel::onEvent,
                        navigateToPayment = { studentId ->
                            navController.navigate("${UmarMasjidDestinations.STUDENT_PAYMENT}/$studentId")
                        },
                        navigateToAttendance = { studentId ->
                            navController.navigate("${UmarMasjidDestinations.ATTENDANCE_CHART}/$studentId")
                        }
                    )
                }
                composable(route = UmarMasjidDestinations.STUDENT_ATTENDANCE_ROUTE) {
                    val viewModel = hiltViewModel<AttendanceViewModel>()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    AttendanceScreen(
                        popToHome = { navController.popBackStack() },
                        uiState = uiState.studentWithAttendance,
                        isLoading = uiState.isLoading,
                        onEvent = viewModel::onEvent,
                    )
                }
                composable(route = UmarMasjidDestinations.FEE_PAYMENT_ROUTE) {
                    val viewModel = hiltViewModel<PaymentViewModel>()
                    val uiState by viewModel.studentsWithPaymentStatus.collectAsStateWithLifecycle()
                    PaymentScreen(
                        popToHome = { navController.popBackStack() },
                        uiState = uiState,
                        onPaymentDone = viewModel::insertPayment
                    )

                }
                composable(
                    route = "${UmarMasjidDestinations.STUDENT_PAYMENT}/{$STUDENT_ID}",
                    arguments = listOf(navArgument(STUDENT_ID) { type = NavType.IntType }),
                ) {
                    val viewModel = hiltViewModel<StudentPaymentViewModel>()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    AddPaymentScreen(
                        student = uiState.student ?: StudentEntity(
                            name = "",
                            phoneNumber = "",
                            id = 0,
                            enrollmentDate = 0
                        ),
                        existingPayments = uiState.payments,
                        onEvent = viewModel::onEvent,
                    ) {
                        navController.popBackStack()
                    }

                }
                composable(route = "${UmarMasjidDestinations.ATTENDANCE_CHART}/{$STUDENT_ID}",
                    arguments = listOf(navArgument(STUDENT_ID) { type = NavType.IntType })
                ) {
                    val studentId = it.arguments?.getInt(STUDENT_ID) ?: 0
                    AttendanceBarCharScreen(studentId)
                }
                composable(route = UmarMasjidDestinations.REPORT_ROUTE) {
                    //            ReportScreen()
                }
            }

        }
    }
}