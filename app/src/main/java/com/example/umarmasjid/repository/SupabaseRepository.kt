package com.example.umarmasjid.repository

import com.example.umarmasjid.data.networking.dto.SoftDeleteStudentParamsForRPC
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseRepository @Inject constructor(
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
        value: Any,
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

    suspend inline fun <reified T : Any> getAttendancesUpdatedAfter(
        teacherId: String,
        updatedAt: String
    ): List<T> {
        return supabase
            .from(ATTENDANCE_TABLE)
            .select {
                filter {
                    gt(UPDATED_AT, updatedAt)
                }
            }
            .decodeList<T>()
    }

    suspend inline fun <reified T : Any> getPaymentsUpdatedAfter(
        updatedAt: String
    ): List<T> {
        return supabase
            .from(PAYMENT_TABLE)
            .select {
                filter {
                    gt(UPDATED_AT, updatedAt)
                }
            }
            .decodeList<T>()
    }

    suspend inline fun <reified T : Any> getSingleByColumn(
        tableName: String,
        columnName: String,
        value: Any,
        columns: Columns = Columns.ALL
    ): T {
        return supabase.from(tableName)
            .select(columns = columns) {
                filter {
                    eq(columnName, value)
                }
            }
            .decodeSingle<T>()
    }

    suspend inline fun <reified T : Any> insert(
        tableName: String,
        value: T
    ) {
        supabase.from(tableName).insert(value)
    }

    suspend inline fun <reified T : Any> insertAndReturn(
        tableName: String,
        value: T
    ): T {
        return supabase.from(tableName)
            .insert(value) {
                select()
            }
            .decodeSingle<T>()
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

    suspend inline fun <reified T : Any> upsertAndReturn(
        tableName: String,
        value: T,
        onConflict: String? = null
    ): T {
        return supabase.from(tableName)
            .upsert(value) {
                select()
                onConflict?.let { this.onConflict = it }
            }
            .decodeSingle<T>()
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

    suspend inline fun <reified T : Any> updateByColumnAndReturn(
        tableName: String,
        columnName: String,
        value: Any,
        updates: Map<String, Any?>
    ): T {
        return supabase.from(tableName)
            .update(
                {
                    updates.forEach { (name, updatedValue) ->
                        set(name, updatedValue)
                    }
                }
            ) {
                select()
                filter {
                    eq(columnName, value)
                }
            }
            .decodeSingle<T>()
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

    suspend inline fun <reified T : Any> deleteByColumnAndReturn(
        tableName: String,
        columnName: String,
        value: Any
    ): T {
        return supabase.from(tableName)
            .delete {
                select()
                filter {
                    eq(columnName, value)
                }
            }
            .decodeSingle<T>()
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
        const val ATTENDANCE_TABLE = "attendances"
        const val PAYMENT_TABLE = "payments"

        const val TEACHER_ID = "teacher_id"
        const val UPDATED_AT = "updated_at"

        const val RPC_SOFT_DELETE_STUDENT = "soft_delete_student"
    }
}
