package com.example.umarmasjid.repository

import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.data.local_database.model.StudentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentRepository @Inject constructor(private val studentDao: StudentDao) {
    fun getAllStudents(): Flow<List<StudentEntity>> = studentDao.getAllStudents()
    suspend fun addStudent(student: StudentEntity) = studentDao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: StudentEntity) = studentDao.deleteStudent(student)

    fun getStudentById(studentId: Int) : Flow<StudentEntity> {
        return studentDao.getStudentById(studentId)
    }

    fun getStudentsWhoNotPaid(startOfMonth: Long, endOfMonth: Long): Flow<List<StudentEntity>> =
        studentDao.getStudentsWhoNotPaid(startOfMonth, endOfMonth)

    fun searchStudents(query: String): Flow<List<StudentEntity>> = studentDao.searchStudents(query)

}