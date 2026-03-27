package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.BankVerificationStatus
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.Scenario
import com.wiom.csp.data.api.BankVerifyRequest
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
 * Bank Details Screen (Screen 6) — matches prototype exactly.
 *
 * Fields: Account Number, Re-enter Account Number, IFSC Code
 * No account holder name or bank name fields.
 * Account number masked on blur (type=password).
 * Hint: "Bank details should belong to [Personal Name] or [Business Name]"
 *
 * Verification flow:
 * 1. User fills all 3 fields
 * 2. Clicks "Verify Bank Details" → 2s spinner
 * 3. Outcome (based on scenario or API):
 *    - SUCCESS → delight screen → proceed to ISP Agreement
 *    - PENNY_DROP_FAIL → bottom sheet → change details / upload doc
 *    - NAME_MISMATCH → bottom sheet with name comparison → change / upload
 *    - DEDUP → bottom sheet → change details only
 *
 * Validation:
 *   Account Number: 9-18 digits, numeric only
 *   Re-enter: must exactly match account number
 *   IFSC: [A-Z]{4}0[A-Z0-9]{6} (11 chars)
 */

data class BankUiState(
    val accountNumber: String = "",
    val accountNumberConfirm: String = "",
    val ifsc: String = "",
    val accountNumberError: String? = null,
    val accountNumberConfirmError: String? = null,
    val ifscError: String? = null,
    val isAccountBlurred: Boolean = false,
    val isAccountConfirmBlurred: Boolean = false,
    val isIfscBlurred: Boolean = false,
    val isFormValid: Boolean = false,
    val verificationStatus: BankVerificationStatus = BankVerificationStatus.NONE,
    // Penny drop result data (from API)
    val bankAccountHolderName: String? = null,  // fetched from penny drop
    val bankName: String? = null,               // derived from IFSC
    val personalName: String = "",              // from registration (for comparison)
    val tradeName: String = "",                 // from registration (for comparison)
    val duplicateAccountPhone: String? = null,  // last 4 digits if dedup
    // Supporting doc
    val supportDocUploaded: Boolean = false,
    val supportDocType: String? = null,         // "pennydrop" or "mismatch"
)

@HiltViewModel
class BankViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BankUiState(
        personalName = OnboardingState.personalName,
        tradeName = OnboardingState.tradeName
    ))
    val uiState: StateFlow<BankUiState> = _uiState.asStateFlow()

    companion object {
        private val IFSC_REGEX = Regex("^[A-Z]{4}0[A-Z0-9]{6}$")
        private const val MIN_ACCOUNT_LENGTH = 9
        private const val MAX_ACCOUNT_LENGTH = 18
    }

    fun onAccountNumberChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(MAX_ACCOUNT_LENGTH)
        _uiState.update {
            it.copy(
                accountNumber = filtered,
                isAccountBlurred = false,
                accountNumberError = null,
                verificationStatus = BankVerificationStatus.NONE
            ).recalcFormValid()
        }
        // Sync to OnboardingState
        OnboardingState.bankAccountNumber = filtered
    }

    fun onAccountNumberConfirmChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(MAX_ACCOUNT_LENGTH)
        _uiState.update {
            it.copy(
                accountNumberConfirm = filtered,
                isAccountConfirmBlurred = false,
                accountNumberConfirmError = null,
                verificationStatus = BankVerificationStatus.NONE
            ).recalcFormValid()
        }
        OnboardingState.bankAccountNumberConfirm = filtered
    }

    fun onIfscChanged(value: String) {
        val filtered = value.uppercase().filter { it.isLetterOrDigit() }.take(11)
        _uiState.update {
            it.copy(
                ifsc = filtered,
                isIfscBlurred = false,
                ifscError = null,
                verificationStatus = BankVerificationStatus.NONE
            ).recalcFormValid()
        }
        OnboardingState.bankIfsc = filtered
    }

    fun onAccountNumberBlurred() {
        _uiState.update { state ->
            val error = if (state.accountNumber.isNotEmpty() &&
                state.accountNumber.length < MIN_ACCOUNT_LENGTH)
                t("कृपया सही खाता संख्या दर्ज करें", "Please enter valid account number")
            else null
            state.copy(isAccountBlurred = true, accountNumberError = error)
        }
    }

    fun onAccountNumberConfirmBlurred() {
        _uiState.update { state ->
            val error = if (state.accountNumberConfirm.isNotEmpty() &&
                state.accountNumberConfirm != state.accountNumber)
                t("बैंक खाता संख्या मेल नहीं खाती", "Bank account number mismatch")
            else null
            state.copy(isAccountConfirmBlurred = true, accountNumberConfirmError = error)
        }
    }

    fun onIfscBlurred() {
        _uiState.update { state ->
            val error = if (state.ifsc.isNotEmpty() && !IFSC_REGEX.matches(state.ifsc))
                t("कृपया सही IFSC कोड दर्ज करें", "Please enter valid IFSC code")
            else null
            state.copy(isIfscBlurred = true, ifscError = error)
        }
    }

    /**
     * Verify bank details — shows 2s spinner then resolves based on scenario or API.
     */
    fun verifyBankDetails(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(verificationStatus = BankVerificationStatus.VERIFYING) }
            delay(2000) // Simulate verification time

            // Check active scenario
            val scenario = OnboardingState.activeScenario
            when (scenario) {
                Scenario.BANK_PENNYDROP_FAIL -> {
                    OnboardingState.activeScenario = Scenario.NONE
                    _uiState.update {
                        it.copy(verificationStatus = BankVerificationStatus.PENNY_DROP_FAIL)
                    }
                }
                Scenario.BANK_NAME_MISMATCH -> {
                    OnboardingState.activeScenario = Scenario.NONE
                    _uiState.update {
                        it.copy(
                            verificationStatus = BankVerificationStatus.NAME_MISMATCH,
                            bankAccountHolderName = "Rajesh Kumar Sharma" // mock from penny drop
                        )
                    }
                }
                Scenario.BANK_DEDUP -> {
                    OnboardingState.activeScenario = Scenario.NONE
                    _uiState.update {
                        it.copy(
                            verificationStatus = BankVerificationStatus.DEDUP,
                            duplicateAccountPhone = "4567"
                        )
                    }
                }
                else -> {
                    // Success path
                    _uiState.update {
                        it.copy(
                            verificationStatus = BankVerificationStatus.SUCCESS,
                            bankAccountHolderName = "Rajesh Kumar",
                            bankName = "State Bank of India"
                        )
                    }
                    OnboardingState.bankVerificationStatus = BankVerificationStatus.SUCCESS
                }
            }
        }
    }

    fun onChangeBankDetails() {
        _uiState.update {
            BankUiState(
                personalName = OnboardingState.personalName,
                tradeName = OnboardingState.tradeName
            )
        }
        OnboardingState.bankAccountNumber = ""
        OnboardingState.bankAccountNumberConfirm = ""
        OnboardingState.bankIfsc = ""
        OnboardingState.bankVerificationStatus = BankVerificationStatus.NONE
    }

    fun onUploadSupportingDoc(type: String) {
        _uiState.update {
            it.copy(supportDocType = type, verificationStatus = BankVerificationStatus.NONE)
        }
    }

    fun onSupportDocUploaded() {
        _uiState.update { it.copy(supportDocUploaded = true) }
        OnboardingState.bankSupportDocUploaded = true
    }

    private fun BankUiState.recalcFormValid(): BankUiState {
        val accValid = accountNumber.length >= MIN_ACCOUNT_LENGTH
        val confirmValid = accountNumberConfirm == accountNumber && accountNumberConfirm.isNotEmpty()
        val ifscValid = IFSC_REGEX.matches(ifsc)
        return copy(isFormValid = accValid && confirmValid && ifscValid)
    }
}
