package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.util.Validation
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhoneUiState(
    val phone: String = "",
    val phoneError: String? = null,
    val isLoading: Boolean = false,
    val isDuplicate: Boolean = false,
)

@HiltViewModel
class PhoneViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneUiState())
    val uiState: StateFlow<PhoneUiState> = _uiState.asStateFlow()

    fun onPhoneChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(10)
        _uiState.update {
            it.copy(
                phone = filtered,
                phoneError = Validation.validatePhone(filtered),
                isDuplicate = false
            )
        }
    }

    fun sendOtp(onSuccess: () -> Unit) {
        val current = _uiState.value
        val error = Validation.validatePhone(current.phone)
        if (error != null) {
            _uiState.update { it.copy(phoneError = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val dupResult = repo.checkPhoneDuplicate(current.phone)
            if (dupResult.getOrNull() == true) {
                _uiState.update { it.copy(isDuplicate = true, isLoading = false) }
                return@launch
            }

            repo.sendOtp(current.phone).onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        phoneError = t("OTP भेजने में समस्या हुई", "Failed to send OTP")
                    )
                }
            }
        }
    }

    fun dismissDuplicate() {
        _uiState.update { it.copy(isDuplicate = false) }
    }
}
