package com.example.umarmasjid.feature.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.repository.OfflineStudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudentProfileState(
    val students: List<StudentEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSearching: Boolean = false,
    val searchQuery: String = ""
)

@HiltViewModel
class StudentProfileViewModel @Inject constructor(
    private val offlineStudentRepository: OfflineStudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentProfileState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            offlineStudentRepository.getAllStudents()
                .collect { students ->
                    _uiState.update { it.copy(students = students, isLoading = false) }
                }
        }
    }

    fun onEvent(event: StudentEvent) {
        when (event) {
            is StudentEvent.AddStudent -> {
                if (event.name.isBlank() || event.phoneNumber.isBlank()) return
                viewModelScope.launch {
                    offlineStudentRepository.addStudent(event.name, event.phoneNumber)
                }
            }
            is StudentEvent.UpdateStudent -> {
                viewModelScope.launch {
                    offlineStudentRepository.updateStudent(
                        name = event.name,
                        phoneNumber = event.phoneNumber,
                        student = event.student
                    )
                }
            }
            is StudentEvent.DeleteStudent -> {
                viewModelScope.launch {
                    offlineStudentRepository.deleteStudent(event.student.id, System.currentTimeMillis())
                }
            }
        }
    }
}
