package com.example.umarmasjid.repository

import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.data.local_database.model.StudentEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(private val studentDao: StudentDao) {
    fun getAllStudents(): Flow<List<StudentEntity>> = studentDao.getAllStudents()
    suspend fun addStudent(student: StudentEntity) = studentDao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = studentDao.updateStudent(student)
    suspend fun deleteStudent(studentId: String, timestamp: Long) = studentDao.softDeleteStudent(studentId, timestamp)

    fun getStudentById(studentId: String) : Flow<StudentEntity> {
        return studentDao.getStudentById(studentId)
    }

    fun getStudentsWhoNotPaid(
        startOfMonth: LocalDate,
        startOfNextMonth: LocalDate
    ): Flow<List<StudentEntity>> =
        studentDao.getStudentsWhoNotPaid(startOfMonth, startOfNextMonth)

    fun searchStudents(query: String): Flow<List<StudentEntity>> = studentDao.searchStudents(query)

}