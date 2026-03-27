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
 * Technical Assessment Screen (Screen 11) — matches prototype exactly.
 *
 * Stage: Activation (Step 2/5)
 * Header: "Technical Assessment"
 * TAT: 4-5 business days
 * Info: "You will also receive a call from our Network Quality team for next steps"
 *
 * Checklist (all pending):
 *   - Infrastructure Review
 *   - Network Readiness
 *   - Location Feasibility
 *
 * If REJECTED:
 *   - "Profile not accepted yet"
 *   - "No refund will be done at this moment"
 *   - CTA: "Talk to Us" → dials 7836811111
 *   - NO refund of ₹2,000 reg fee
 *
 * If APPROVED: Proceed to Onboarding Fee (Screen 12)
 */

enum class TechAssessmentStatus { PENDING, APPROVED, REJECTED }

data class TechAssessmentUiState(
    val status: TechAssessmentStatus = TechAssessmentStatus.PENDING,
    val rejectionReason: String = "",
    val isLoading: Boolean = false,
    val estimatedDays: Int = 5,
    val helpNumber: String = "7836811111",
)

@HiltViewModel
class TechAssessmentViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TechAssessmentUiState())
    val uiState: StateFlow<TechAssessmentUiState> = _uiState.asStateFlow()

    init {
        // Check if scenario is active
        if (OnboardingState.activeScenario == Scenario.TECH_ASSESSMENT_REJECTED) {
            OnboardingState.activeScenario = Scenario.NONE
            _uiState.update {
                it.copy(
                    status = TechAssessmentStatus.REJECTED,
                    rejectionReason = t("इंफ्रास्ट्रक्चर तैयार नहीं", "Infrastructure not ready")
                )
            }
            OnboardingState.techAssessmentRejected = true
        }
    }

    fun refreshStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setApproved() {
        _uiState.update { it.copy(status = TechAssessmentStatus.APPROVED) }
        OnboardingState.techAssessmentRejected = false
    }

    fun setRejected() {
        _uiState.update {
            it.copy(
                status = TechAssessmentStatus.REJECTED,
                rejectionReason = t("इंफ्रास्ट्रक्चर तैयार नहीं", "Infrastructure not ready")
            )
        }
        OnboardingState.techAssessmentRejected = true
    }
}
