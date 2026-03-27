package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.Scenario
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Account Setup Screen (Screen 13) — matches prototype exactly.
 *
 * Stage: Activation (Step 4/5)
 * Header: "Activation"
 *
 * Shows: Loading spinner with "Account Setup in Progress for [Business Name]"
 * Auto-progresses to Successfully Onboarded screen after 3 seconds.
 * No CTA — fully automatic.
 *
 * Error scenarios (from simulator):
 *   - ACCOUNT_SETUP_FAILED: "Technical issue" → Retry + Talk to Us
 *   - ACCOUNT_SETUP_PENDING: "Being processed" → Refresh Status + Talk to Us
 */

enum class AccountSetupState { LOADING, COMPLETED, FAILED, PENDING }

data class AccountSetupUiState(
    val state: AccountSetupState = AccountSetupState.LOADING,
    val businessName: String = "",
    val helpNumber: String = "7836811111",
)

@HiltViewModel
class AccountSetupViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountSetupUiState(
        businessName = OnboardingState.tradeName.ifEmpty { "Kumar Electronics" }
    ))
    val uiState: StateFlow<AccountSetupUiState> = _uiState.asStateFlow()

    /**
     * Start auto-setup. After 3 seconds, either:
     * - Calls onComplete (success path)
     * - Shows failed/pending state (if scenario active)
     */
    fun startSetup(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(state = AccountSetupState.LOADING) }
            delay(3000)

            when (OnboardingState.activeScenario) {
                Scenario.ACCOUNT_SETUP_FAILED -> {
                    OnboardingState.activeScenario = Scenario.NONE
                    _uiState.update { it.copy(state = AccountSetupState.FAILED) }
                }
                Scenario.ACCOUNT_SETUP_PENDING -> {
                    OnboardingState.activeScenario = Scenario.NONE
                    _uiState.update { it.copy(state = AccountSetupState.PENDING) }
                }
                else -> {
                    _uiState.update { it.copy(state = AccountSetupState.COMPLETED) }
                    onComplete()
                }
            }
        }
    }

    fun retry(onComplete: () -> Unit) {
        startSetup(onComplete)
    }

    fun refreshStatus(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(state = AccountSetupState.LOADING) }
            delay(2000)
            _uiState.update { it.copy(state = AccountSetupState.COMPLETED) }
            onComplete()
        }
    }
}
