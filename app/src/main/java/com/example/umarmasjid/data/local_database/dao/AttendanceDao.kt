package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.data.local_database.model.StudentWithAttendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity)

    @Update
    suspend fun update(attendance: AttendanceEntity)

    @Query(
        """
        SELECT COUNT(*) FROM attendances 
        WHERE attendance_date BETWEEN :startOfMonth AND :endOfMonth AND present = 1
    """
    )
    fun getPresentCount(
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<Int>

    //    @Query(
//        """
//            SELECT * FROM attendances
//            WHERE student_id = :studentId AND attendance_date BETWEEN :startOfDay AND :endOfDay
//        """
//    )
//    suspend fun deleteByStudentIdAndDate(studentId: Int, startOfDay: Long, endOfDay: Long)
    @Query(
        """
            SELECT * FROM attendances 
            WHERE student_id = :studentId 
            AND attendance_date BETWEEN :startOfDay AND :endOfDay
            ORDER BY attendance_date ASC
        """
    )
    fun getAttendanceByStudentId(
        studentId: Int,
        startOfDay: Long,
        endOfDay: Long
    ): Flow<List<AttendanceEntity>>

    //
    @Delete
    suspend fun delete(attendance: AttendanceEntity)


    @Query("SELECT * FROM attendances WHERE student_id = :studentId AND attendance_date BETWEEN :startOfDay AND :endOfDay")
    fun getAttendanceByDateRange(
        studentId: Int,
        startOfDay: Long,
        endOfDay: Long
    ): AttendanceEntity?

    @Transaction
    @Query(
        """
        SELECT s.id AS studentId, s.name AS name, a.present
        FROM students s
        LEFT JOIN attendances a 
        ON s.id = a.student_id AND a.attendance_date = :date
    """
    )
    fun getStudentsWithAttendance(date: Long): Flow<List<StudentWithAttendance>>
}

