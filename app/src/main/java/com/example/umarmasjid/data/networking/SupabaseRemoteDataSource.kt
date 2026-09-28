package com.example.umarmasjid.data.networking

import com.example.umarmasjid.data.networking.dto.SoftDeleteStudentParamsForRPC
import com.example.umarmasjid.data.networking.dto.SyncChangeDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
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
        teacherId: String,
        version: Long
    ): List<SyncChangeDto> {
        return supabase
            .from(SYNC_CHANGES)
            .select {
                filter {
//                    eq(TEACHER_ID, teacherId)
                    gt(VERSION, version)
                }
                order(
                    column = VERSION,
                    order = Order.ASCENDING
                )
                limit(500)
            }
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

    suspend inline fun <reified T : Any> upsert(
        tableName: String,
        value: T,
        onConflict: String? = null
    ) {
        supabase.from(tableName).upsert(value) {
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

    suspend fun softDeleteStudent(
        studentId: String,
        timestamp: String
    ) {
        val parameters = SoftDeleteStudentParamsForRPC(
            studentId = studentId,
            timestamp = timestamp
        )

        supabase.postgrest.rpc(
            function = RPC_SOFT_DELETE_STUDENT,
            parameters = parameters
        )
    }

    companion object {
        const val SYNC_CHANGES = "sync_changes"
        const val VERSION = "version"
        const val ID = "id"
        const val STUDENT_TABLE = "students"
        const val TEACHER_ID = "teacher_id"
        const val UPDATED_AT = "updated_at"

        const val RPC_SOFT_DELETE_STUDENT = "soft_delete_student"
        const val RPC_GET_CURRENT_SYNC_VERSION = "get_current_sync_version"
    }
}