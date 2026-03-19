package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.data.local_database.model.DailyAttendanceCount
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

    // For 7-day trend — per day present count
    @Query(
        """
            SELECT attendance_date, COUNT(*) AS presentCount
            FROM attendances
            WHERE present = 1 AND attendance_date BETWEEN :start AND :end
            GROUP BY attendance_date
        """
    )
    fun getDailyPresentCounts(start: Long, end: Long): Flow<List<DailyAttendanceCount>>

    // For total students on a given day (to calculate %)
    @Query(
        value = """
            SELECT COUNT(DISTINCT student_id) FROM attendances
            WHERE attendance_date BETWEEN :start AND :end
        """
    )
    fun getTotalMarkedStudents(start: Long, end: Long): Flow<Int>

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

    @Query("SELECT * FROM attendances WHERE student_id = :studentId AND attendance_date BETWEEN :startOfDay AND :endOfDay")
    suspend fun getAttendanceByDateRange(
        studentId: Int,
        startOfDay: Long,
        endOfDay: Long
    ): AttendanceEntity?

    @Query(
       value = """
            SELECT s.id AS studentId, s.name AS name, a.present
            FROM students s
            LEFT JOIN attendances a 
            ON s.id = a.student_id AND a.attendance_date = :date
        """
    )
    fun getStudentsWithAttendance(date: Long): Flow<List<StudentWithAttendance>>
}

