package com.example.umarmasjid.data.local_database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey
    val userId: String,

    val lastSyncedAt: Long
)