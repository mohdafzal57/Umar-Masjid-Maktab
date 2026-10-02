package com.example.umarmasjid.data.networking.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncChangeDto(
    val version: Long,

    @SerialName("entity_type")
    val entityType: String,

    @SerialName("entity_id")
    val entityId: String,

    val operation: String,

    @SerialName("changed_at")
    val changedAt: String
)