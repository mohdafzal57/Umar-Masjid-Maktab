package com.example.umarmasjid.ui.bottom_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector

data class ModernNavigationItem(
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val title: String,
    val badge: String? = null,
    val route: String
)

val umarMasjidNavigationItems: List<ModernNavigationItem> = listOf(
    ModernNavigationItem(Icons.Filled.Home, Icons.Outlined.Home, "Home", route = "home"),
    ModernNavigationItem(Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, "Attendance", route = "attendance"),
    ModernNavigationItem(Icons.Filled.School, Icons.Outlined.School, "Students", route = "students"),
    ModernNavigationItem(Icons.Filled.Payment, Icons.Outlined.Payment, "Payments", route = "payments")
)