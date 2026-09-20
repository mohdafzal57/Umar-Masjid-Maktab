package com.example.umarmasjid.feature.settings

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.repository.OfflineStudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val offlineStudentRepository: OfflineStudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _importState = MutableStateFlow<ImportUiState>(ImportUiState.Idle)
    val importState = _importState.asStateFlow()

    fun exportAsCsv() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            try {
                val students = offlineStudentRepository.getAllStudents().first()
                if (students.isEmpty()) {
                    _uiState.value = SettingsUiState.Error("No student records found.")
                    return@launch
                }

                val csvHeader = "ID,Name,Phone,Enrollment Date\n"
                val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val csvContent = students.joinToString("\n", prefix = csvHeader) { student ->
                    "${student.id},${student.name},${student.phoneNumber},${student.enrollmentDate.format(dateFormatter)}"
                }

                _uiState.value = SettingsUiState.Success(csvContent, "text/csv", "Students_Records.csv")
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Export failed: ${e.localizedMessage}")
            }
        }
    }

    fun exportAsVcf() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            try {
                val students = offlineStudentRepository.getAllStudents().first()
                if (students.isEmpty()) {
                    _uiState.value = SettingsUiState.Error("No student records found.")
                    return@launch
                }

                val vcfContent = StringBuilder()
                students.forEach { student ->
                    vcfContent.append("BEGIN:VCARD\n")
                    vcfContent.append("VERSION:3.0\n")
                    vcfContent.append("FN:${student.name}\n")
                    vcfContent.append("TEL;TYPE=CELL:${student.phoneNumber}\n")
                    vcfContent.append("END:VCARD\n")
                }

                _uiState.value = SettingsUiState.Success(vcfContent.toString(), "text/vcard", "Students_Contacts.vcf")
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Export failed: ${e.localizedMessage}")
            }
        }
    }

    fun importStudentsFromContacts(context: Context) {
        viewModelScope.launch {
            _importState.value = ImportUiState.Loading
            try {
                val contacts = withContext(Dispatchers.IO) {
                    fetchContacts(context)
                }
                _importState.value = ImportUiState.ShowContactPicker(contacts)
            } catch (e: Exception) {
                _importState.value = ImportUiState.Error("Failed to load contacts: ${e.localizedMessage}")
            }
        }
    }

    private fun fetchContacts(context: Context): List<ContactInfo> {
        val contactsList = mutableListOf<ContactInfo>()
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex).replace("\\s".toRegex(), "")
                if (name != null) {
                    contactsList.add(ContactInfo(name, number))
                }
            }
        }
        return contactsList.distinctBy { it.phoneNumber }
    }

    fun addSelectedStudents(selectedContacts: List<ContactInfo>) {
        viewModelScope.launch {
            _importState.value = ImportUiState.Loading
            try {
                selectedContacts.forEach { contact ->
                    offlineStudentRepository.addStudent(
                        name = contact.name,
                        phoneNumber = contact.phoneNumber,
                    )
                }
                _importState.value = ImportUiState.Success("${selectedContacts.size} students imported.")
            } catch (e: Exception) {
                _importState.value = ImportUiState.Error("Import failed: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _uiState.value = SettingsUiState.Idle
    }

    fun resetImportState() {
        _importState.value = ImportUiState.Idle
    }
}

data class ContactInfo(
    val name: String,
    val phoneNumber: String,
    var isSelected: Boolean = false
)

sealed class SettingsUiState {
    object Idle : SettingsUiState()
    object Loading : SettingsUiState()
    data class Success(val data: String, val mimeType: String, val fileName: String) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}

sealed class ImportUiState {
    object Idle : ImportUiState()
    object Loading : ImportUiState()
    data class ShowContactPicker(val contacts: List<ContactInfo>) : ImportUiState()
    data class Success(val message: String) : ImportUiState()
    data class Error(val message: String) : ImportUiState()
}
