package com.example.umarmasjid.repository

import androidx.room.withTransaction
import com.example.umarmasjid.data.local_database.AppDatabase
import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.data.local_database.dao.SyncEntityType
import com.example.umarmasjid.data.local_database.dao.SyncOperationDao
import com.example.umarmasjid.data.local_database.dao.SyncOperationType
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.data.local_database.model.SyncOperationEntity
import com.example.umarmasjid.data.repository.SupabaseAuthRepository
import com.example.umarmasjid.data.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineFirstStudentRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val studentDao: StudentDao,
    private val syncOperationDao: SyncOperationDao,
    private val supabaseAuthRepository: SupabaseAuthRepository,
    private val syncManager: SyncManager
) {

    fun getAllStudents(): Flow<List<StudentEntity>> =
        studentDao.getAllStudents()

    suspend fun addStudent(name: String, phoneNumber: String) {
        val now = System.currentTimeMillis()
        val studentId = UUID.randomUUID().toString()

        val student = StudentEntity(
            id = studentId,
            name = name,
            phoneNumber = phoneNumber,
            enrollmentDate = LocalDate.now(),
            teacherId = supabaseAuthRepository.teacherId,
            createdAt = now,
            updatedAt = now,
        )

        val syncOp = SyncOperationEntity(
            entityId = studentId,
            entityType = SyncEntityType.STUDENT,
            operation = SyncOperationType.INSERT,
            createdAt = now
        )

        appDatabase.withTransaction {
            studentDao.insertStudent(student)
            syncOperationDao.insert(syncOp)
        }

        syncManager.requestSync()
    }

    suspend fun updateStudent(name: String, phoneNumber: String, student: StudentEntity) {
        val now = System.currentTimeMillis()
        val updatedStudent = student.copy(
            name = name,
            phoneNumber = phoneNumber,
            updatedAt = now
        )
        val syncOp = SyncOperationEntity(
            entityId = updatedStudent.id,
            entityType = SyncEntityType.STUDENT,
            operation = SyncOperationType.UPDATE,
            createdAt = now
        )

        appDatabase.withTransaction {
            studentDao.updateStudent(updatedStudent)
            syncOperationDao.insert(syncOp)
        }

        syncManager.requestSync()
    }

    suspend fun deleteStudent(
        studentId: String,
        timestamp: Long
    ) {
        val syncOp = SyncOperationEntity(
            entityId = studentId,
            entityType = SyncEntityType.STUDENT,
            operation = SyncOperationType.DELETE,
            createdAt = timestamp
        )

        appDatabase.withTransaction {
            studentDao.deleteStudent(studentId, timestamp)
            syncOperationDao.insert(syncOp)
        }

        syncManager.requestSync()
    }

    fun getStudentById(studentId: String): Flow<StudentEntity> =
        studentDao.getStudentById(studentId)

    fun getStudentsWhoNotPaid(
        startOfMonth: LocalDate,
        startOfNextMonth: LocalDate
    ): Flow<List<StudentEntity>> =
        studentDao.getStudentsWhoNotPaid(
            startOfMonth,
            startOfNextMonth
        )

    fun searchStudents(query: String): Flow<List<StudentEntity>> =
        studentDao.searchStudents(query)
}