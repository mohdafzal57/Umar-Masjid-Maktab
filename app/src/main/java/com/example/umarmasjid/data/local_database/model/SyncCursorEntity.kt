package com.example.umarmasjid.data.local_database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_cursor")
data class SyncCursorEntity(
    @PrimaryKey
    val id: Int = 1,

    val lastVersion: Long = 0
)