package com.example.umarmasjid.core.presentation.util

import com.example.umarmasjid.core.domain.util.SupabaseAuthError
import io.github.jan.supabase.auth.exception.AuthErrorCode

fun SupabaseAuthError.toMessage(): String {
    return when (this) {
        SupabaseAuthError.InvalidCredentials ->
            "Invalid email or password"

        SupabaseAuthError.EmailAlreadyExists ->
            "Email is already registered"

        SupabaseAuthError.InvalidEmail ->
            "Invalid email address"

        SupabaseAuthError.WeakPassword ->
            "Password is too weak"

        SupabaseAuthError.Network ->
            "No internet connection"

        SupabaseAuthError.Timeout ->
            "Request timed out"

        SupabaseAuthError.Unknown ->
            "Something went wrong"

        SupabaseAuthError.InvalidSession -> "Password recovery session is not active."
    }
}