package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

enum class SetupItemStatus {
    PENDING, IN_PROGRESS, COMPLETED
}

data class SetupItem(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val icon: String,
    val status: SetupItemStatus = SetupItemStatus.PENDING,
)

data class AccountSetupUiState(
    val items: List<SetupItem> = defaultSetupItems(),
    val currentIndex: Int = -1,
    val isRunning: Boolean = false,
    val isComplete: Boolean = false,
)

private fun defaultSetupItems() = listOf(
    SetupItem("csp_id", "CSP ID बनाना", "Creating CSP ID", "🆔"),
    SetupItem("wallet", "Wallet सेटअप", "Wallet Setup", "💳"),
    SetupItem("portal", "Portal एक्सेस", "Portal Access", "🌐"),
    SetupItem("inventory", "Inventory सेटअप", "Inventory Setup", "📦"),
    SetupItem("go_live", "Go Live तैयारी", "Go Live Prep", "🚀"),
)

@HiltViewModel
class AccountSetupViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountSetupUiState())
    val uiState: StateFlow<AccountSetupUiState> = _uiState.asStateFlow()

    fun startSetup(onComplete: () -> Unit) {
        if (_uiState.value.isRunning) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true, isComplete = false) }

            val items = _uiState.value.items
            for (i in items.indices) {
                // Mark current as in-progress
                _uiState.update { state ->
                    state.copy(
                        currentIndex = i,
                        items = state.items.mapIndexed { idx, item ->
                            when {
                                idx == i -> item.copy(status = SetupItemStatus.IN_PROGRESS)
                                idx < i -> item.copy(status = SetupItemStatus.COMPLETED)
                                else -> item
                            }
                        }
                    )
                }

                // Simulate processing time (1.5-3s per item)
                delay(1500L + (i * 300L))

                // Mark as completed
                _uiState.update { state ->
                    state.copy(
                        items = state.items.mapIndexed { idx, item ->
                            if (idx == i) item.copy(status = SetupItemStatus.COMPLETED)
                            else item
                        }
                    )
                }
            }

            _uiState.update { it.copy(isRunning = false, isComplete = true) }
            onComplete()
        }
    }

    fun reset() {
        _uiState.update { AccountSetupUiState() }
    }
}
