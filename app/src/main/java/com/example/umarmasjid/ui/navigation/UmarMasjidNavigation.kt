package com.example.umarmasjid.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object UmarMasjidDestinations {
    const val HOME_ROUTE = "home"
    const val STUDENT_ATTENDANCE_ROUTE = "student_attendance"
    const val FEE_PAYMENT_ROUTE = "fee_payment"
    const val STUDENT_PROFILE_ROUTE = "student_profile"
    const val REPORT_ROUTE = "report"
    const val ATTENDANCE_CHART = "attendance_chart"
    const val STUDENT_PAYMENT = "student_payment"
}

const val STUDENT_ID = "studentId"

class UmarMasjidNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(UmarMasjidDestinations.HOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToStudentAttendance: () -> Unit = {
        navController.navigate(UmarMasjidDestinations.STUDENT_ATTENDANCE_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToStudentManagement: () -> Unit = {
        navController.navigate(UmarMasjidDestinations.STUDENT_PROFILE_ROUTE){
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToFeeManagement: () -> Unit = {
        navController.navigate(UmarMasjidDestinations.FEE_PAYMENT_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

