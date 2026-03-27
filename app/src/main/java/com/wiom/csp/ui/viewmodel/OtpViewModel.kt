package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.util.Validation
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OtpUiState(
    val phone: String = "",
    val digits: List<String> = listOf("", "", "", ""),
    val otpError: String? = null,
    val isLoading: Boolean = false,
    val timerSeconds: Int = 28,
    val timerExpired: Boolean = false,
    val tncAccepted: Boolean = false,
    val tncError: String? = null,
)

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun setPhone(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerSeconds = 28, timerExpired = false) }
        timerJob = viewModelScope.launch {
            for (i in 27 downTo 0) {
                delay(1000)
                _uiState.update { it.copy(timerSeconds = i) }
            }
            _uiState.update { it.copy(timerExpired = true) }
        }
    }

    fun onDigitChanged(index: Int, value: String) {
        val digit = value.filter { it.isDigit() }.take(1)
        val newDigits = _uiState.value.digits.toMutableList()
        newDigits[index] = digit
        _uiState.update {
            it.copy(
                digits = newDigits,
                otpError = null
            )
        }
    }

    fun onTncChanged(accepted: Boolean) {
        _uiState.update { it.copy(tncAccepted = accepted, tncError = null) }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        val current = _uiState.value

        val otpError = Validation.validateOtp(current.digits)
        if (otpError != null) {
            _uiState.update { it.copy(otpError = otpError) }
            return
        }

        if (!current.tncAccepted) {
            _uiState.update {
                it.copy(tncError = t("नियम व शर्तें स्वीकार करें", "Accept Terms & Conditions"))
            }
            return
        }

        if (current.timerExpired) {
            _uiState.update {
                it.copy(otpError = t("OTP समाप्त हो गया, नया OTP भेजें", "OTP expired, resend OTP"))
            }
            return
        }

        val otpString = current.digits.joinToString("")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.verifyOtp(current.phone, otpString)
                .onSuccess { verified ->
                    _uiState.update { it.copy(isLoading = false) }
                    if (verified) {
                        onSuccess()
                    } else {
                        _uiState.update {
                            it.copy(otpError = t("OTP मेल नहीं खाता", "OTP does not match"))
                        }
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            otpError = t("सत्यापन में समस्या हुई", "Verification failed")
                        )
                    }
                }
        }
    }

    fun resendOtp() {
        val phone = _uiState.value.phone
        _uiState.update {
            it.copy(
                digits = listOf("", "", "", ""),
                otpError = null,
                timerExpired = false
            )
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.sendOtp(phone)
            _uiState.update { it.copy(isLoading = false) }
            startTimer()
        }
    }

    fun changeNumber() {
        timerJob?.cancel()
        _uiState.update { OtpUiState() }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
