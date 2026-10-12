package com.example.umarmasjid.data.repository

import android.content.Intent
import android.net.Uri
import com.example.umarmasjid.core.data.networking.safeAuthCall
import com.example.umarmasjid.core.domain.util.EmptyResult
import com.example.umarmasjid.core.domain.util.SupabaseAuthError
import com.example.umarmasjid.core.domain.util.onSuccess
import com.example.umarmasjid.data.local_database.AppDatabase
import com.example.umarmasjid.data.sync.PrayerTimesSyncScheduler
import com.example.umarmasjid.data.sync.SyncManager
import com.example.umarmasjid.domain.repository.AuthRepository
import com.example.umarmasjid.domain.repository.AuthState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class SupabaseAuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val syncManager: SyncManager,
    private val appDatabase: AppDatabase,
    private val prayerTimesSyncScheduler: PrayerTimesSyncScheduler
) : AuthRepository {

    override val teacherId: String
        get() = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User not logged in")

    private val _isPasswordRecovery = MutableStateFlow(false)

    override val authState: Flow<AuthState> =
        combine(
            supabase.auth.sessionStatus,
            _isPasswordRecovery
        ) { sessionStatus, isPasswordRecovery ->
            when {
                sessionStatus is SessionStatus.Initializing ->
                    AuthState.Loading

                isPasswordRecovery ->
                    AuthState.PasswordRecovery

                sessionStatus is SessionStatus.Authenticated ->
                    AuthState.Authenticated

                else -> AuthState.Unauthenticated
            }
        }.distinctUntilChanged()

    override suspend fun login(email: String, password: String): EmptyResult<SupabaseAuthError> {
        return safeAuthCall {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            syncManager.requestSync()
            prayerTimesSyncScheduler.requestInitialSync()
            prayerTimesSyncScheduler.ensurePeriodicSyncScheduled()
        }
    }

    override suspend fun signUp(email: String, password: String): EmptyResult<SupabaseAuthError> {
        return safeAuthCall {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    override suspend fun resetPassword(email: String): EmptyResult<SupabaseAuthError> {
        return safeAuthCall {
            supabase.auth.resetPasswordForEmail(
                email = email,
                redirectUrl = "umarmasjid://auth/callback"
            )
        }
    }

    override suspend fun updatePassword(newPassword: String): EmptyResult<SupabaseAuthError> {
        check(_isPasswordRecovery.value) {
            "Password recovery session is not active."
        }
        return safeAuthCall {
            supabase.auth.updateUser {
                password = newPassword
            }
        }.onSuccess {
            // finished Password recovery
            _isPasswordRecovery.value = false
            // Need to find a better way to call these things,
            // Is this a right approach to call these again here?
            syncManager.requestSync()
            prayerTimesSyncScheduler.requestInitialSync()
            prayerTimesSyncScheduler.ensurePeriodicSyncScheduled()
        }
    }

    override suspend fun handleDeeplink(intent: Intent) {
        val uri = intent.data ?: return
        val isRecoveryLink = uri.isRecoveryLink()

        try {
            supabase.handleDeeplinks(
                intent = intent,
                onSessionSuccess = {}
            )
            if (isRecoveryLink) {
                _isPasswordRecovery.value = true
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _isPasswordRecovery.value = false
            throw e
        }
    }

    private fun Uri.isRecoveryLink(): Boolean {
        val type = getQueryParameter("type")
        if (type == "recovery") return true

        val fragmentStr = fragment ?: return false
        return fragmentStr.split("&").any { param ->
            param == "type=recovery" || param.startsWith("type=recovery")
        }
    }

    override suspend fun logout() {
        syncManager.cancelAllSyncs()
        prayerTimesSyncScheduler.cancelPrayerTimesSync()

        try {
            supabase.auth.signOut()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Log the remote sign-out failure.
        } finally {
            try {
                supabase.auth.clearSession()
            } finally {
                withContext(Dispatchers.IO) {
                    appDatabase.clearAllTables()
                }
            }
        }
    }
}
