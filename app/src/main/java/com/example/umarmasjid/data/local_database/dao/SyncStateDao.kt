package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.umarmasjid.data.local_database.model.SyncStateEntity

@Dao
interface SyncStateDao {

    @Query("""
        SELECT lastSyncedAt
        FROM sync_state
        WHERE userId = :userId
    """)
    suspend fun getLastSyncedAt(userId: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(syncState: SyncStateEntity)
}