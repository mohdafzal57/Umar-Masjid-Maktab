package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.umarmasjid.data.local_database.model.SyncOperationEntity

object SyncEntityType {
    const val STUDENT = "students"
    const val ATTENDANCE = "attendances"
    const val PAYMENT = "payments"
}

object SyncOperationType {
    const val INSERT = "INSERT"
    const val UPDATE = "UPDATE"
    const val DELETE = "DELETE"
}

@Dao
interface SyncOperationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: SyncOperationEntity)

    // Process oldest operations first to preserve causality
    @Query("SELECT * FROM sync_operations ORDER BY createdAt ASC")
    suspend fun getAllPending(): List<SyncOperationEntity>

    @Query("DELETE FROM sync_operations WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM sync_operations WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query("UPDATE sync_operations SET retryCount = retryCount + 1 WHERE id = :id")
    suspend fun incrementRetry(id: String)

    // Clear stale operations if a DELETE replaces earlier INSERT/UPDATE
    @Query("DELETE FROM sync_operations WHERE entityId = :entityId AND id != :currentOpId")
    suspend fun clearPrecedingForEntity(entityId: String, currentOpId: String)
}