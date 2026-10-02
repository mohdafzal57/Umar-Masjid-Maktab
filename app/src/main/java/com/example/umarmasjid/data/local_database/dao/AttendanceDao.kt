package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.data.local_database.model.DailyAttendanceCount
import com.example.umarmasjid.data.local_database.model.StudentAttendance
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity)

    @Upsert
    suspend fun upsertAllAttendances(attendances: List<AttendanceEntity>)

    @Update
    suspend fun update(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendances WHERE id = :attendanceId LIMIT 1")
    suspend fun getAttendanceById(attendanceId: String): AttendanceEntity?

    @Query("SELECT * FROM attendances WHERE id IN (:ids)")
    suspend fun getAttendanceEntitiesByIds(ids: List<String>): List<AttendanceEntity>

    @Query("UPDATE attendances SET deleted_at = :timestamp WHERE id = :attendanceId")
    suspend fun softDeleteAttendance(attendanceId: String, timestamp: Long)

    @Query("UPDATE attendances SET deleted_at = :timestamp WHERE id IN (:attendanceIds)")
    suspend fun softDeleteAttendances(attendanceIds: List<String>, timestamp: Long)

    @Query("""
        SELECT COUNT(*) 
        FROM attendances 
        WHERE attendance_date >= :date
          AND attendance_status IN ('PRESENT', 'LATE')
          AND deleted_at IS NULL
    """)
    fun getPresentCount(
        date: LocalDate,
    ): Flow<Int>

    @Query("""
        SELECT * FROM attendances
        WHERE student_id = :studentId
          AND attendance_date = :today
          AND deleted_at IS NULL
        LIMIT 1
    """)
    suspend fun getAttendanceForToday(
        studentId: String,
        today: LocalDate
    ): AttendanceEntity?

    // For 7-day trend — per day present count
//    Sep 10 → 2 present
//    Sep 11 → 2 present
//    Sep 12 → 5 present
    //Todo: i need to work on this output.
    @Query("""
        SELECT attendance_date, COUNT(*) AS presentCount
        FROM attendances
        WHERE attendance_status IN ('PRESENT', 'LATE')
          AND attendance_date >= :start
          AND attendance_date < :endExclusive
          AND deleted_at IS NULL
        GROUP BY attendance_date
        ORDER BY attendance_date
    """)
    fun getDailyPresentCounts(
        start: LocalDate,
        endExclusive: LocalDate
    ): Flow<List<DailyAttendanceCount>>

    @Query("""
    SELECT COUNT(DISTINCT student_id)
    FROM attendances
    WHERE attendance_date >= :start AND attendance_date < :endExclusive
    AND deleted_at IS NULL
    """)
    fun getTotalMarkedStudents(
        start: LocalDate,
        endExclusive: LocalDate
    ): Flow<Int>

    // It will return all attendance for the given month.
    @Query("""
    SELECT * FROM attendances
    WHERE student_id = :studentId
      AND attendance_date >= :startDate
      AND attendance_date < :endDateExclusive
      AND deleted_at IS NULL
    """)
    fun getAttendanceForMonth(
        studentId: String,
        startDate: LocalDate,
        endDateExclusive: LocalDate
    ): Flow<List<AttendanceEntity>>

    // It gives all student list with their attendance status for the given date.
    @Query("""
    SELECT 
        s.id AS studentId,
        s.name AS name,
        a.attendance_status AS status
    FROM students s
    LEFT JOIN attendances a
        ON s.id = a.student_id
        AND a.attendance_date = :date
        AND a.deleted_at IS NULL
    WHERE s.deleted_at IS NULL
    """)
    fun getStudentsWithAttendance(
        date: LocalDate
    ): Flow<List<StudentAttendance>>
}
