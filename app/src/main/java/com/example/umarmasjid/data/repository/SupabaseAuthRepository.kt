package com.example.umarmasjid.data.repository

import android.content.Intent
import android.util.Log
import com.example.umarmasjid.core.data.networking.safeAuthCall
import com.example.umarmasjid.core.data.preferences.SystemSettingsPreferences
import com.example.umarmasjid.core.domain.util.EmptyResult
import com.example.umarmasjid.core.domain.util.Result
import com.example.umarmasjid.core.domain.util.SupabaseAuthError
import com.example.umarmasjid.core.domain.util.onSuccess
import com.example.umarmasjid.data.local_database.AppDatabase
import com.example.umarmasjid.data.sync.PrayerTimesSyncScheduler
import com.example.umarmasjid.data.sync.SyncManager
import com.example.umarmasjid.domain.repository.AuthRepository
import com.example.umarmasjid.domain.repository.AuthState
import com.example.umarmasjid.domain.repository.AuthUiEvent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.event.AuthEvent
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.RefreshFailureCause
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

private const val logTag = "AuthenticationRepository"

@Singleton
class SupabaseAuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val syncManager: SyncManager,
    private val appDatabase: AppDatabase,
    private val prayerTimesSyncScheduler: PrayerTimesSyncScheduler,
    private val settingsPreferences: SystemSettingsPreferences
) : AuthRepository {

    private val auth: Auth get() = supabase.auth

    @OptIn(SupabaseExperimental::class)
    override val authEvents: Flow<AuthUiEvent> =
        auth.events
            .map { event ->
                when (event) {
                    is AuthEvent.OtpError ->
                        AuthUiEvent.DeepLinkError(event.error)

                    is AuthEvent.RefreshFailure ->
                        AuthUiEvent.RefreshError(
                            when (val cause = event.cause) {
                                is RefreshFailureCause.NetworkError ->
                                    cause.exception.message
                                        ?: "Network error while refreshing session"

                                is RefreshFailureCause.InternalServerError ->
                                    cause.exception.message
                                        ?: "Server error while refreshing session"
                            }
                        )
                }
            }

    override val teacherId: String
        get() = auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User not logged in")

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            auth.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    status.session.user?.id?.let { teacherId ->
                        settingsPreferences.setTeacherId(teacherId)
                    }
                }
            }
        }
    }

    override val authState: Flow<AuthState> =
        combine(
            settingsPreferences.settings,
            auth.sessionStatus,
        ) { settings, sessionStatus ->
            val localTeacherId = settings.teacherId
            when (sessionStatus) {
                is SessionStatus.Authenticated ->
                    AuthState.Authenticated

                SessionStatus.Initializing ->
                    if (localTeacherId != "") {
                        AuthState.Authenticated
                    } else {
                        AuthState.Initializing
                    }

                is SessionStatus.RefreshFailure ->
                    if (localTeacherId != "") {
                        AuthState.Authenticated
                    } else {
                        AuthState.Unauthenticated
                    }

                is SessionStatus.NotAuthenticated ->
                    AuthState.Unauthenticated
            }
        }

    private val _isPasswordRecovery = MutableStateFlow(false)

    override val passwordRecovery = _isPasswordRecovery.asStateFlow()


    override suspend fun login(email: String, password: String): EmptyResult<SupabaseAuthError> {
        return safeAuthCall {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    override suspend fun signUp(email: String, password: String): EmptyResult<SupabaseAuthError> {
        return safeAuthCall {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    override suspend fun resetPassword(email: String): EmptyResult<SupabaseAuthError> {
        return safeAuthCall {
            auth.resetPasswordForEmail(
                email = email,
                redirectUrl = "umarmasjid://auth/callback"
            )
        }
    }

    override suspend fun updatePassword(newPassword: String): EmptyResult<SupabaseAuthError> {
        if (!_isPasswordRecovery.value) {
            return Result.Error(SupabaseAuthError.InvalidSession)
        }
        return safeAuthCall {
            auth.updateUser {
                password = newPassword
            }
        }.onSuccess {
            // finished Password recovery
            _isPasswordRecovery.value = false
        }
    }

    override suspend fun handleDeeplink(intent: Intent) {
        val uri = intent.data ?: return
        val fragment = uri.fragment ?: ""
        val query = uri.query ?: ""
        val isRecovery = fragment.contains("type=recovery") ||
                query.contains("type=recovery") ||
                uri.getQueryParameter("type") == "recovery"

        try {
            supabase.handleDeeplinks(
                intent = intent,
                onSessionSuccess = {
                    _isPasswordRecovery.value = isRecovery
                }
            )
        } catch (e: CancellationException) {
            throw e
        }
    }

    override suspend fun logout() {
        syncManager.cancelAllSyncs()
        prayerTimesSyncScheduler.cancelPrayerTimesSync()
        _isPasswordRecovery.value = false

        try {
            auth.signOut()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Log the remote sign-out failure.
        } finally {
            try {
                auth.clearSession()
            } finally {
                settingsPreferences.setTeacherId("")
                withContext(Dispatchers.IO) {
                    appDatabase.clearAllTables()
                }
            }
        }
    }

    private suspend fun localTeacherSessionExists(): Boolean =
        settingsPreferences.settings.first().teacherId.isNotEmpty()
}
