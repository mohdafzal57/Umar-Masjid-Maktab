package com.example.umarmasjid.data.networking

import com.example.umarmasjid.data.networking.dto.GetChangesAfterVersionParamsForRPC
import com.example.umarmasjid.data.networking.dto.SoftDeleteStudentsParamsForRPC
import com.example.umarmasjid.data.networking.dto.SyncChangeDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseRemoteDataSource @Inject constructor(
    @PublishedApi internal val supabase: SupabaseClient
) {

    suspend inline fun <reified T : Any> getAll(
        tableName: String,
        columns: Columns = Columns.ALL
    ): List<T> {
        return supabase.from(tableName)
            .select(columns = columns)
            .decodeList<T>()
    }

    suspend inline fun <reified T : Any> getByColumn(
        tableName: String,
        columnName: String,
        value: String,
        columns: Columns = Columns.ALL
    ): List<T> {
        return supabase.from(tableName)
            .select(columns = columns) {
                filter {
                    eq(columnName, value)
                }
            }
            .decodeList<T>()
    }

    suspend fun getCurrentSyncVersion(): Long {
        val currentSyncVersion = supabase.postgrest
            .rpc(RPC_GET_CURRENT_SYNC_VERSION)
            .decodeAs<Long>()
        println("SYNC_REPO: getCurrentSyncVersion(): $currentSyncVersion")
        return currentSyncVersion
    }

    suspend fun getChangesAfterVersion(
        version: Long
    ): List<SyncChangeDto> {

        val parameters = GetChangesAfterVersionParamsForRPC(
            version = version,
            limit = SYNC_BATCH_SIZE
        )
        return supabase
            .postgrest
            .rpc(
                RPC_GET_CHANGES_AFTER_VERSION,
                parameters
            )
            .decodeList<SyncChangeDto>()
    }

    suspend inline fun <reified T: Any> getById(
        tableName: String,
        id: String
    ): T? {
        return supabase
            .from(tableName)
            .select {
                filter {
                    eq(ID, id)
                }
            }
            .decodeList<T>()
            .firstOrNull()
    }

    suspend inline fun <reified T: Any> getByIds(
        tableName: String,
        ids: List<String>
    ): List<T> {
        return supabase
            .from(tableName)
            .select {
                filter {
                    isIn(ID, ids)
                }
            }
            .decodeList<T>()
    }

    suspend inline fun <reified T : Any> upsert(
        tableName: String,
        value: T,
        onConflict: String? = null
    ) {
        supabase.from(tableName).upsert(value) {
            onConflict?.let { this.onConflict = it }
        }
    }

    suspend inline fun <reified T : Any> upsertAll(
        tableName: String,
        values: List<T>,
        onConflict: String? = null
    ) {
        supabase.from(tableName).upsert(values) {
            onConflict?.let { this.onConflict = it }
        }
    }


    suspend inline fun <reified T : Any, reified V : Any> updateByColumn(
        tableName: String,
        columnName: String,
        value: V,
        updateData: T
    ) {
        supabase.from(tableName).update(updateData) {
            filter {
                eq(columnName, value)
            }
        }
    }

    suspend inline fun <reified T : Any> updateByColumnIn(
        tableName: String,
        columnName: String,
        values: List<String>,
        updateData: T
    ) {
        if (values.isEmpty()) return

        supabase.from(tableName).update(updateData) {
            filter {
                isIn(columnName, values)
            }
        }
    }

    suspend fun deleteByColumn(
        tableName: String,
        columnName: String,
        value: Any
    ) {
        supabase.from(tableName).delete {
            filter {
                eq(columnName, value)
            }
        }
    }

    suspend fun softDeleteStudents(
        studentIds: List<String>,
        timestamp: String
    ) {
        val parameters = SoftDeleteStudentsParamsForRPC(
            studentIds = studentIds,
            timestamp = timestamp
        )

        supabase.postgrest.rpc(
            function = RPC_SOFT_DELETE_STUDENTS,
            parameters = parameters
        )
    }

    companion object {
        const val ID = "id"

        private const val SYNC_BATCH_SIZE = 100

        const val RPC_SOFT_DELETE_STUDENTS = "soft_delete_students"
        const val RPC_GET_CURRENT_SYNC_VERSION = "get_current_sync_version"
        const val RPC_GET_CHANGES_AFTER_VERSION = "get_changes_after_version"
    }
}