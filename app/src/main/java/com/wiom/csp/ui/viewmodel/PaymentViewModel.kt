package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PaymentResult {
    NONE, SUCCESS, FAILED, TIMEOUT
}

data class PaymentUiState(
    val amount: Int = 0,
    val isProcessing: Boolean = false,
    val result: PaymentResult = PaymentResult.NONE,
    val transactionId: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun setAmount(amount: Int) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun initiatePayment(onSuccess: () -> Unit) {
        val amount = _uiState.value.amount
        if (amount <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, result = PaymentResult.NONE, errorMessage = null) }

            repo.processPayment(amount)
                .onSuccess { txnId ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            result = PaymentResult.SUCCESS,
                            transactionId = txnId
                        )
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    val isTimeout = error.message?.contains("timeout", ignoreCase = true) == true
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            result = if (isTimeout) PaymentResult.TIMEOUT else PaymentResult.FAILED,
                            errorMessage = if (isTimeout)
                                t("भुगतान समय सीमा पार हो गई, कृपया पुनः प्रयास करें",
                                    "Payment timed out, please try again")
                            else
                                t("भुगतान विफल हुआ, कृपया पुनः प्रयास करें",
                                    "Payment failed, please try again")
                        )
                    }
                }
        }
    }

    fun retry() {
        _uiState.update {
            it.copy(
                result = PaymentResult.NONE,
                errorMessage = null,
                transactionId = null
            )
        }
    }

    fun reset() {
        _uiState.update { PaymentUiState() }
    }
}
