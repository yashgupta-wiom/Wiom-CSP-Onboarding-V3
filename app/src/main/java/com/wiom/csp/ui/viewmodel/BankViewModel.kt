package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.repository.BankVerificationResult
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BankUiState(
    val accountHolder: String = "",
    val accountHolderError: String? = null,
    val bankName: String = "",
    val bankNameError: String? = null,
    val accountNumber: String = "",
    val accountNumberError: String? = null,
    val ifsc: String = "",
    val ifscError: String? = null,
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val verificationResult: BankVerificationResult? = null,
    val verificationError: String? = null,
    val isFormValid: Boolean = false,
)

@HiltViewModel
class BankViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BankUiState())
    val uiState: StateFlow<BankUiState> = _uiState.asStateFlow()

    fun onAccountHolderChanged(value: String) {
        _uiState.update {
            it.copy(
                accountHolder = value,
                accountHolderError = if (value.isBlank())
                    t("खाताधारक का नाम डालें", "Enter account holder name") else null,
                isVerified = false,
                verificationResult = null
            ).withFormValidity()
        }
    }

    fun onBankNameChanged(value: String) {
        _uiState.update {
            it.copy(
                bankName = value,
                bankNameError = if (value.isBlank())
                    t("बैंक का नाम डालें", "Enter bank name") else null,
                isVerified = false,
                verificationResult = null
            ).withFormValidity()
        }
    }

    fun onAccountNumberChanged(value: String) {
        val filtered = value.filter { it.isDigit() }
        _uiState.update {
            it.copy(
                accountNumber = filtered,
                accountNumberError = if (filtered.isBlank())
                    t("खाता नंबर डालें", "Enter account number")
                else if (filtered.length < 8)
                    t("सही खाता नंबर डालें", "Enter valid account number")
                else null,
                isVerified = false,
                verificationResult = null
            ).withFormValidity()
        }
    }

    fun onIfscChanged(value: String) {
        val upper = value.uppercase().take(11)
        _uiState.update {
            it.copy(
                ifsc = upper,
                ifscError = if (upper.isBlank())
                    t("IFSC कोड डालें", "Enter IFSC code")
                else if (upper.length != 11)
                    t("11 अंकों का IFSC डालें", "Enter 11-character IFSC")
                else null,
                isVerified = false,
                verificationResult = null
            ).withFormValidity()
        }
    }

    fun verifyBankAccount(onSuccess: () -> Unit) {
        if (!validateAll()) return

        val current = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, verificationError = null) }

            repo.verifyBankAccount(
                holder = current.accountHolder,
                bank = current.bankName,
                account = current.accountNumber,
                ifsc = current.ifsc
            ).onSuccess { result ->
                val hasIssue = !result.pennyDropVerified || !result.nameMatch || !result.dedupPassed
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isVerified = !hasIssue,
                        verificationResult = result,
                        verificationError = when {
                            !result.pennyDropVerified ->
                                t("पैनी ड्रॉप विफल, खाता विवरण जांचें",
                                    "Penny drop failed, check account details")
                            !result.nameMatch ->
                                t("बैंक में नाम मेल नहीं खाता",
                                    "Name does not match bank records")
                            !result.dedupPassed ->
                                t("यह खाता पहले से जुड़ा है",
                                    "This account is already linked to another CSP")
                            else -> null
                        }
                    )
                }
                if (!hasIssue) onSuccess()
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        verificationError = t("सत्यापन में समस्या हुई, पुनः प्रयास करें",
                            "Verification failed, please try again")
                    )
                }
            }
        }
    }

    private fun validateAll(): Boolean {
        val s = _uiState.value
        val holderErr = if (s.accountHolder.isBlank())
            t("खाताधारक का नाम डालें", "Enter account holder name") else null
        val bankErr = if (s.bankName.isBlank())
            t("बैंक का नाम डालें", "Enter bank name") else null
        val accErr = if (s.accountNumber.isBlank())
            t("खाता नंबर डालें", "Enter account number")
        else if (s.accountNumber.length < 8)
            t("सही खाता नंबर डालें", "Enter valid account number")
        else null
        val ifscErr = if (s.ifsc.isBlank())
            t("IFSC कोड डालें", "Enter IFSC code")
        else if (s.ifsc.length != 11)
            t("11 अंकों का IFSC डालें", "Enter 11-character IFSC")
        else null

        _uiState.update {
            it.copy(
                accountHolderError = holderErr,
                bankNameError = bankErr,
                accountNumberError = accErr,
                ifscError = ifscErr
            ).withFormValidity()
        }

        return holderErr == null && bankErr == null && accErr == null && ifscErr == null
    }

    private fun BankUiState.withFormValidity(): BankUiState {
        val valid = accountHolderError == null && accountHolder.isNotBlank()
                && bankNameError == null && bankName.isNotBlank()
                && accountNumberError == null && accountNumber.length >= 8
                && ifscError == null && ifsc.length == 11
        return copy(isFormValid = valid)
    }
}
