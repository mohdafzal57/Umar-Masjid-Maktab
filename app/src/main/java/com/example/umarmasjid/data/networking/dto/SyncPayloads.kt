package com.example.umarmasjid.data.networking.dto

import kotlinx.serialization.Serializable

@Serializable
data class SoftDeletePayload(
    val deleted_at: String
)