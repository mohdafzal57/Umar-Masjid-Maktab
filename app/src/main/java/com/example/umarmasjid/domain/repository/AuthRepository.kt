package com.example.umarmasjid.domain.repository

import android.content.Intent
import com.example.umarmasjid.core.domain.util.EmptyResult
import com.example.umarmasjid.core.domain.util.SupabaseAuthError
import kotlinx.coroutines.flow.Flow

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data object Authenticated : AuthState
    data object PasswordRecovery : AuthState
}

interface AuthRepository {
    val teacherId: String
    val authState: Flow<AuthState>
    suspend fun login(email: String, password: String): EmptyResult<SupabaseAuthError>
    suspend fun signUp(email: String, password: String): EmptyResult<SupabaseAuthError>
    suspend fun resetPassword(email: String): EmptyResult<SupabaseAuthError>
    suspend fun updatePassword(newPassword: String): EmptyResult<SupabaseAuthError>
    fun finishPasswordRecovery()
    suspend fun handleDeeplink(intent: Intent)
    suspend fun logout()
}
