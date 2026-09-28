package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.umarmasjid.data.local_database.model.SyncCursorEntity

@Dao
interface SyncCursorDao {

    @Query("""
        SELECT lastVersion
        FROM sync_cursor
        WHERE id = 1
    """)
    suspend fun getLastVersion(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cursor: SyncCursorEntity)
}