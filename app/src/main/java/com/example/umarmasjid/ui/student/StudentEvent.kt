package com.example.umarmasjid.ui.student

import com.example.umarmasjid.data.local_database.model.StudentEntity

sealed class StudentEvent {
    data class AddStudent(val name: String, val phoneNumber: String) : StudentEvent()
    data class UpdateStudent(
        val student: StudentEntity,
        val name: String,
        val phoneNumber: String
    ) : StudentEvent()
    data class DeleteStudent(val student: StudentEntity) : StudentEvent()
//    data class SearchStudents(val searchQuery: String) : StudentEvent()
}
