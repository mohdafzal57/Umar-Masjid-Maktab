package com.example.umarmasjid.core.domain.util

sealed interface SupabaseAuthError : Error {

    data object InvalidCredentials : SupabaseAuthError
    data object InvalidSession : SupabaseAuthError
    data object EmailAlreadyExists : SupabaseAuthError
    data object InvalidEmail : SupabaseAuthError
    data object WeakPassword : SupabaseAuthError
    data object Network : SupabaseAuthError
    data object Timeout : SupabaseAuthError
    data object Unknown : SupabaseAuthError
}