package com.example.umarmasjid.data.repository

import com.example.umarmasjid.domain.repository.AuthRepository
import com.example.umarmasjid.domain.repository.AuthState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseAuthRepository @Inject constructor(
    private val supabase: SupabaseClient
) : AuthRepository {

    override val authState: Flow<AuthState> = supabase.auth.sessionStatus
        .map { status ->
            when (status) {
                is SessionStatus.Authenticated -> AuthState.AUTHENTICATED
                is SessionStatus.NotAuthenticated -> AuthState.UNAUTHENTICATED
                is SessionStatus.Initializing -> AuthState.LOADING
                else -> AuthState.UNAUTHENTICATED
            }
        }

    override suspend fun logout() {
        supabase.auth.signOut()
    }
}
