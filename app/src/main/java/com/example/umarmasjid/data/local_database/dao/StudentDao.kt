package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.umarmasjid.data.local_database.model.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    fun getStudentById(studentId: Int) : Flow<StudentEntity>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%'")
    fun searchStudents(query: String): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>

    @Query("SELECT * FROM students ORDER BY enrollment_date DESC LIMIT :limit")
    fun getRecentStudents(limit: Int = 5): Flow<List<StudentEntity>>

    // Students who have NOT paid in current month
    @Query("""
        SELECT s.*
        FROM students s
        LEFT JOIN payments p 
        ON s.id = p.student_id
        AND p.payment_date BETWEEN :startOfMonth AND :endOfMonth 
        WHERE p.id IS NULL
    """)
    fun getStudentsWhoNotPaid(
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<List<StudentEntity>>
}

