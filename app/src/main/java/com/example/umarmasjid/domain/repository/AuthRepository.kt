package com.example.umarmasjid.domain.repository

import kotlinx.coroutines.flow.Flow

enum class AuthState {
    AUTHENTICATED,
    UNAUTHENTICATED,
    LOADING
}

interface AuthRepository {
    val teacherId: String
    val authState: Flow<AuthState>
    suspend fun login(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun resetPassword(email: String)
    suspend fun logout()
}
