package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.domain.model.VerificationStatus
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VerificationUiState(
    val status: VerificationStatus = VerificationStatus.PENDING,
    val reason: String = "",
    val reuploadCount: Int = 0,
    val isLoading: Boolean = false,
)

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    fun refreshStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // In production, poll backend for status
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /** Called by dashboard/admin to set verification decision */
    fun setDecision(status: VerificationStatus, reason: String = "") {
        _uiState.update {
            it.copy(
                status = status,
                reason = reason,
                reuploadCount = if (status == VerificationStatus.REJECTED)
                    it.reuploadCount + 1 else it.reuploadCount
            )
        }
    }

    fun resetForReupload() {
        _uiState.update {
            it.copy(status = VerificationStatus.PENDING, reason = "")
        }
    }

    val statusLabel: String
        get() = when (_uiState.value.status) {
            VerificationStatus.PENDING -> t("सत्यापन लंबित है", "Verification Pending")
            VerificationStatus.APPROVED -> t("सत्यापन स्वीकृत", "Verification Approved")
            VerificationStatus.REJECTED -> t("सत्यापन अस्वीकृत", "Verification Rejected")
        }
}
