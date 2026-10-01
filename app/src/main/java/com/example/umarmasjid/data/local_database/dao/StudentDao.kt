package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.example.umarmasjid.data.local_database.model.StudentEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Upsert
    suspend fun upsertAllStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("SELECT * FROM students WHERE deleted_at IS NULL ORDER BY name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    fun getStudentById(studentId: String): Flow<StudentEntity>

    @Query("SELECT * FROM students WHERE id = :studentId LIMIT 1")
    suspend fun getStudentEntityById(studentId: String): StudentEntity?

    @Query("SELECT * FROM students WHERE id IN (:ids)")
    suspend fun getStudentEntitiesByIds(ids: List<String>): List<StudentEntity>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' AND deleted_at IS NULL")
    fun searchStudents(query: String): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(*) FROM students WHERE deleted_at IS NULL")
    fun getStudentCount(): Flow<Int>

    @Query("SELECT * FROM students WHERE deleted_at IS NULL ORDER BY enrollment_date DESC LIMIT :limit")
    fun getRecentStudents(limit: Int = 5): Flow<List<StudentEntity>>

    //TODO: i missed updating UPDATED_AT field, probably many other places too.
    @Query("UPDATE students SET deleted_at = :timestamp, updated_at = :timestamp WHERE id = :studentId")
    suspend fun softDeleteStudentInternal(studentId: String, timestamp: Long)

    @Query("UPDATE attendances SET deleted_at = :timestamp, updated_at = :timestamp WHERE student_id = :studentId")
    suspend fun softDeleteAttendanceInternal(studentId: String, timestamp: Long)

    @Query("UPDATE payments SET deleted_at = :timestamp, updated_at = :timestamp WHERE student_id = :studentId")
    suspend fun softDeletePaymentsInternal(studentId: String, timestamp: Long)

    @Transaction
    suspend fun deleteStudent(studentId: String, timestamp: Long) {
        softDeleteStudentInternal(studentId, timestamp)
        softDeleteAttendanceInternal(studentId, timestamp)
        softDeletePaymentsInternal(studentId, timestamp)
    }

    // This query is a little complex to understand because of deleted_at property.
    // Students who have NOT paid in current month
    @Query(
        """
        SELECT s.*
        FROM students s
        LEFT JOIN payments p
            ON s.id = p.student_id
            AND p.payment_date >= :startOfMonth
            AND p.payment_date < :startOfNextMonth
            AND p.deleted_at IS NULL
        WHERE s.deleted_at IS NULL
          AND p.id IS NULL
    """)
    fun getStudentsWhoNotPaid(
        startOfMonth: LocalDate,
        startOfNextMonth: LocalDate
    ): Flow<List<StudentEntity>>
}

