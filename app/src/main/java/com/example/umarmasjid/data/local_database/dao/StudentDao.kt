package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.umarmasjid.data.local_database.model.StudentEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("UPDATE students SET deleted_at = :timestamp WHERE id = :studentId")
    suspend fun softDeleteStudent(studentId: String, timestamp: Long)

    @Query("SELECT * FROM students WHERE deleted_at IS NULL ORDER BY name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    fun getStudentById(studentId: String): Flow<StudentEntity>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' AND deleted_at IS NULL")
    fun searchStudents(query: String): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(*) FROM students WHERE deleted_at IS NULL")
    fun getStudentCount(): Flow<Int>

    @Query("SELECT * FROM students WHERE deleted_at IS NULL ORDER BY enrollment_date DESC LIMIT :limit")
    fun getRecentStudents(limit: Int = 5): Flow<List<StudentEntity>>

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

