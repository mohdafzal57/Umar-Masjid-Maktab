package com.example.umarmasjid.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.umarmasjid.feature.attendance.StudentAttendanceNavKey
import com.example.umarmasjid.feature.home.HomeNavKey
import com.example.umarmasjid.feature.payments.PaymentsNavKey
import com.example.umarmasjid.feature.students.StudentProfileNavKey

data class TopLevelNavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val title: String,
    val badge: String? = null
)

val HOME = TopLevelNavItem(
    selectedIcon = Icons.Filled.Dashboard,
    unselectedIcon = Icons.Outlined.Dashboard,
    title = "Home"
)

val ATTENDANCE = TopLevelNavItem(
    selectedIcon = Icons.AutoMirrored.Filled.FactCheck,
    unselectedIcon = Icons.AutoMirrored.Outlined.FactCheck,
    title = "Attendance"
)

val STUDENTS = TopLevelNavItem(
    selectedIcon = Icons.Filled.Groups,
    unselectedIcon = Icons.Outlined.Groups,
    title = "Students"
)

val PAYMENTS = TopLevelNavItem(
    selectedIcon = Icons.Filled.AccountBalanceWallet,
    unselectedIcon = Icons.Outlined.AccountBalanceWallet,
    title = "Payments"
)

val TOP_LEVEL_NAV_ITEMS = mapOf(
    HomeNavKey to HOME,
    StudentAttendanceNavKey to ATTENDANCE,
    StudentProfileNavKey to STUDENTS,
    PaymentsNavKey to PAYMENTS
)
