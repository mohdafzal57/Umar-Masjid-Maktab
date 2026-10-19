package com.example.umarmasjid.domain.repository

import android.content.Intent
import com.example.umarmasjid.core.domain.util.EmptyResult
import com.example.umarmasjid.core.domain.util.SupabaseAuthError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed interface AuthState {
    data object Initializing : AuthState
    data object Unauthenticated : AuthState
    data object Authenticated : AuthState
}

sealed interface AuthUiEvent {
    data class DeepLinkError(val message: String) : AuthUiEvent
    data class RefreshError(val message: String) : AuthUiEvent
}

interface AuthRepository {
    val teacherId: String
    val authState: StateFlow<AuthState>
    val passwordRecovery: StateFlow<Boolean>
    val authEvents: Flow<AuthUiEvent>
    suspend fun login(email: String, password: String): EmptyResult<SupabaseAuthError>
    suspend fun signUp(email: String, password: String): EmptyResult<SupabaseAuthError>
    suspend fun resetPassword(email: String): EmptyResult<SupabaseAuthError>
    suspend fun updatePassword(newPassword: String): EmptyResult<SupabaseAuthError>
    suspend fun handleDeeplink(intent: Intent)
    suspend fun logout()
}
