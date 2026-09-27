package com.example.umarmasjid.core.data.networking

import com.example.umarmasjid.core.domain.util.EmptyResult
import com.example.umarmasjid.core.domain.util.Result
import com.example.umarmasjid.core.domain.util.SupabaseAuthError
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun safeAuthCall(
    crossinline execute: suspend () -> Unit
): EmptyResult<SupabaseAuthError> {

    try {
        execute()

    } catch (e: AuthWeakPasswordException) {
        return Result.Error(SupabaseAuthError.WeakPassword)

    } catch (e: AuthRestException) {
        return Result.Error(
            mapSupabaseAuthError(
                AuthErrorCode.fromValue(e.error)
            )
        )

    } catch (e: HttpRequestTimeoutException) {
        return Result.Error(SupabaseAuthError.Timeout)

    } catch (e: HttpRequestException) {
        return Result.Error(SupabaseAuthError.Network)

    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        return Result.Error(SupabaseAuthError.Unknown)
    }

    return Result.Success(Unit)
}

fun mapSupabaseAuthError(
    errorCode: AuthErrorCode?
): SupabaseAuthError {
    return when (errorCode) {
        AuthErrorCode.EmailExists ->
            SupabaseAuthError.EmailAlreadyExists

        AuthErrorCode.InvalidCredentials ->
            SupabaseAuthError.InvalidCredentials

        AuthErrorCode.EmailAddressInvalid ->
            SupabaseAuthError.InvalidEmail

        AuthErrorCode.WeakPassword ->
            SupabaseAuthError.WeakPassword

        else ->
            SupabaseAuthError.Unknown
    }
}