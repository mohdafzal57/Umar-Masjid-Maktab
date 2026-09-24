package com.example.umarmasjid.data.networking

import com.example.umarmasjid.data.networking.dto.SoftDeleteStudentParamsForRPC
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

    suspend inline fun <reified T : Any> getUpdatedAfter(
        tableName: String,
        updatedAt: String,
        columns: Columns = Columns.ALL
    ): List<T> {
        return supabase
            .from(tableName)
            .select(columns = columns) {
                filter {
                    gt(UPDATED_AT, updatedAt)
                }
            }
            .decodeList<T>()
    }

    suspend inline fun <reified T : Any> getStudentsUpdatedAfter(
        teacherId: String,
        updatedAt: String
    ): List<T> {
        return supabase
            .from(STUDENT_TABLE)
            .select {
                filter {
                    eq(TEACHER_ID, teacherId)
                    gt(UPDATED_AT, updatedAt)
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
        const val STUDENT_TABLE = "students"
        const val TEACHER_ID = "teacher_id"
        const val UPDATED_AT = "updated_at"

        const val RPC_SOFT_DELETE_STUDENT = "soft_delete_student"
    }
}