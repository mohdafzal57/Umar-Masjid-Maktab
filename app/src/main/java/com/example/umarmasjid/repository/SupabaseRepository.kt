package com.example.umarmasjid.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
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

    suspend fun updateByColumn(
        tableName: String,
        columnName: String,
        value: Any,
        updates: Map<String, Any?>
    ) {
        supabase.from(tableName).update(
            {
                updates.forEach { (name, updatedValue) ->
                    set(name, updatedValue)
                }
            }
        ) {
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
}
