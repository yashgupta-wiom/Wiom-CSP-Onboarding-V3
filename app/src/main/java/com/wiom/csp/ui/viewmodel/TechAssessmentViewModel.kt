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

data class TechAssessmentUiState(
    val status: VerificationStatus = VerificationStatus.PENDING,
    val reason: String = "",
    val isLoading: Boolean = false,
)

@HiltViewModel
class TechAssessmentViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TechAssessmentUiState())
    val uiState: StateFlow<TechAssessmentUiState> = _uiState.asStateFlow()

    fun refreshStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // In production, poll backend for tech assessment status
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /** Called by dashboard/admin to set tech assessment decision */
    fun setDecision(status: VerificationStatus, reason: String = "") {
        _uiState.update {
            it.copy(status = status, reason = reason)
        }
    }

    fun resetForReassessment() {
        _uiState.update {
            it.copy(status = VerificationStatus.PENDING, reason = "")
        }
    }

    val statusLabel: String
        get() = when (_uiState.value.status) {
            VerificationStatus.PENDING ->
                t("तकनीकी मूल्यांकन लंबित है", "Technical Assessment Pending")
            VerificationStatus.APPROVED ->
                t("तकनीकी मूल्यांकन स्वीकृत", "Technical Assessment Approved")
            VerificationStatus.REJECTED ->
                t("तकनीकी मूल्यांकन अस्वीकृत", "Technical Assessment Rejected")
        }
}
