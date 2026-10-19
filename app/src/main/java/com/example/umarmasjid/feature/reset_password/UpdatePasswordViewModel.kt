package com.example.umarmasjid.feature.reset_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umarmasjid.core.domain.util.onError
import com.example.umarmasjid.core.domain.util.onSuccess
import com.example.umarmasjid.core.presentation.util.toMessage
import com.example.umarmasjid.domain.repository.AuthRepository
import com.example.umarmasjid.domain.usecase.OnAuthenticatedSessionReady
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UpdatePasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val onAuthenticatedSessionReady: OnAuthenticatedSessionReady
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdatePasswordUiState())
    val uiState: StateFlow<UpdatePasswordUiState> = _uiState.asStateFlow()

    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password, errorMessage = null) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }

    fun onUpdatePassword() {
        val newPass = _uiState.value.newPassword
        val confirmPass = _uiState.value.confirmPassword

        if (newPass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Password cannot be empty.") }
            return
        }

        if (newPass.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
            return
        }

        if (newPass != confirmPass) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.updatePassword(newPass)
                .onSuccess {
                    onAuthenticatedSessionReady()
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toMessage()
                        )
                    }
                }
        }
    }
}
