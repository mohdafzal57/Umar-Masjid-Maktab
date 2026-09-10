package com.example.umarmasjid.domain.repository

import kotlinx.coroutines.flow.Flow

enum class AuthState {
    AUTHENTICATED,
    UNAUTHENTICATED,
    LOADING
}

interface AuthRepository {
    val authState: Flow<AuthState>
    suspend fun logout()
}
