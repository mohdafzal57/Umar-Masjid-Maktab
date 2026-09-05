package com.example.umarmasjid.data.local_database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "sync_operations")
data class SyncOperationEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val entityId: String,

    val entityType: String,

    val operation: String,

    val createdAt: Long,

    val retryCount: Int = 0
)