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

data class IspAgreementUiState(
    val isUploaded: Boolean = false,
    val isUploading: Boolean = false,
    val progress: Float = 0f,
    val errorMessage: String? = null,
)

@HiltViewModel
class IspAgreementViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IspAgreementUiState())
    val uiState: StateFlow<IspAgreementUiState> = _uiState.asStateFlow()

    fun uploadAgreement(uri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, progress = 0f, errorMessage = null) }

            // Simulate upload progress
            for (step in 1..10) {
                delay(200)
                _uiState.update { it.copy(progress = step / 10f) }
            }

            val result = runCatching {
                delay(200)
                true
            }

            result.onSuccess {
                _uiState.update {
                    it.copy(isUploaded = true, isUploading = false, progress = 1f)
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        progress = 0f,
                        errorMessage = t("अपलोड विफल, पुनः प्रयास करें", "Upload failed, try again")
                    )
                }
            }
        }
    }

    fun removeAgreement() {
        _uiState.update { IspAgreementUiState() }
    }
}
