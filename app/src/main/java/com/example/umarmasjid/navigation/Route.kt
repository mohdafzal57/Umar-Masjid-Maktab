package com.example.umarmasjid.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/***
 * Root represents the top-level destinations of your app.
 */
sealed interface Root : NavKey

/***
 * Authentication represents a group of authentication-related screens.
 */

sealed interface Authentication : NavKey

@Serializable
data object AuthenticationRoute: Root {

    @Serializable
    data object LoginRoute: Authentication

    @Serializable
    data object SignupRoute: Authentication

    @Serializable
    data object ResetPasswordRoute: Authentication

    @Serializable
    data object UpdatePasswordRoute: Authentication
}

/***
 * Connected routes
 */

sealed interface Connected: NavKey

@Serializable
data object ConnectedRoute: Root {

    @Serializable
    data object AttendanceRoute: Connected

    @Serializable
    data class AttendanceBarChartRoute(val studentId: String) : Connected

    @Serializable
    data object HomeRoute : Connected

    @Serializable
    data object PaymentRoute : Connected

    @Serializable
    data class StudentPaymentRoute(val studentId: String) : Connected

    @Serializable
    data object StudentRoute : Connected

    @Serializable
    data object SettingsRoute : Connected
}
