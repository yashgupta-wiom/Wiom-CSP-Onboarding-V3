package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.Scenario
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Verification Screen (Screen 9) — matches prototype exactly.
 *
 * Stage: Verification (Step 5/5)
 * Header: "Verification"
 *
 * Shows "All Documents Submitted" with checklist:
 *   KYC Documents ✓, Bank Details ✓, ISP Agreement ✓, Shop & Equipment Photos ✓
 *   Verification Review ⋯ (Under review)
 *
 * TAT: 3 business days (review); refund: 5-6 working days
 *
 * If REJECTED: Auto refund ₹2,000, no re-upload in Phase 1
 *   - "Verification Rejected" + refund card
 *   - CTA: "Check Refund Status" → refund in progress screen
 *
 * If APPROVED: Proceed to Policy & SLA (Screen 10)
 *
 * Refund states: SUCCESS / IN_PROGRESS / FAILED
 */

enum class VerificationState { PENDING, APPROVED, REJECTED }
enum class RefundState { NONE, IN_PROGRESS, SUCCESS, FAILED }

data class VerificationUiState(
    val status: VerificationState = VerificationState.PENDING,
    val isLoading: Boolean = false,
    val estimatedDays: Int = 3,
    val refundState: RefundState = RefundState.NONE,
    val refundAmount: Int = 2000,
)

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    init {
        when (OnboardingState.activeScenario) {
            Scenario.VERIFICATION_REJECTED -> {
                OnboardingState.activeScenario = Scenario.NONE
                _uiState.update { it.copy(status = VerificationState.REJECTED) }
                OnboardingState.verificationRejected = true
            }
            Scenario.VERIFICATION_PENDING -> {
                OnboardingState.activeScenario = Scenario.NONE
                _uiState.update { it.copy(status = VerificationState.PENDING) }
            }
            else -> {}
        }
    }

    fun setApproved() {
        _uiState.update { it.copy(status = VerificationState.APPROVED) }
        OnboardingState.verificationRejected = false
    }

    fun setRejected() {
        _uiState.update { it.copy(status = VerificationState.REJECTED) }
        OnboardingState.verificationRejected = true
    }

    fun checkRefundStatus() {
        _uiState.update { it.copy(refundState = RefundState.IN_PROGRESS) }
    }

    fun setRefundSuccess() {
        _uiState.update { it.copy(refundState = RefundState.SUCCESS) }
    }

    fun setRefundFailed() {
        _uiState.update { it.copy(refundState = RefundState.FAILED) }
    }

    fun backToRejected() {
        _uiState.update { it.copy(refundState = RefundState.NONE) }
    }
}
