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

data class DocUploadState(
    val isUploaded: Boolean = false,
    val isUploading: Boolean = false,
    val progress: Float = 0f,
    val errorMessage: String? = null,
)

data class KycUiState(
    val pan: DocUploadState = DocUploadState(),
    val aadhaarFront: DocUploadState = DocUploadState(),
    val aadhaarBack: DocUploadState = DocUploadState(),
    val gst: DocUploadState = DocUploadState(),
    val allUploaded: Boolean = false,
)

@HiltViewModel
class KycViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(KycUiState())
    val uiState: StateFlow<KycUiState> = _uiState.asStateFlow()

    fun uploadDocument(docType: String, uri: String) {
        viewModelScope.launch {
            updateDocState(docType) { it.copy(isUploading = true, progress = 0f, errorMessage = null) }

            // Simulate upload progress
            for (step in 1..10) {
                delay(150)
                updateDocState(docType) { it.copy(progress = step / 10f) }
            }

            val result = runCatching {
                // In production, this would call repo with actual file upload
                delay(200)
                true
            }

            result.onSuccess {
                updateDocState(docType) {
                    it.copy(isUploaded = true, isUploading = false, progress = 1f)
                }
                refreshAllUploaded()
            }.onFailure {
                updateDocState(docType) {
                    it.copy(
                        isUploading = false,
                        progress = 0f,
                        errorMessage = t("अपलोड विफल, पुनः प्रयास करें", "Upload failed, try again")
                    )
                }
            }
        }
    }

    fun removeDocument(docType: String) {
        updateDocState(docType) { DocUploadState() }
        refreshAllUploaded()
    }

    private fun updateDocState(docType: String, transform: (DocUploadState) -> DocUploadState) {
        _uiState.update { state ->
            when (docType) {
                "pan" -> state.copy(pan = transform(state.pan))
                "aadhaarFront" -> state.copy(aadhaarFront = transform(state.aadhaarFront))
                "aadhaarBack" -> state.copy(aadhaarBack = transform(state.aadhaarBack))
                "gst" -> state.copy(gst = transform(state.gst))
                else -> state
            }
        }
    }

    private fun refreshAllUploaded() {
        _uiState.update {
            it.copy(
                allUploaded = it.pan.isUploaded
                        && it.aadhaarFront.isUploaded
                        && it.aadhaarBack.isUploaded
                        && it.gst.isUploaded
            )
        }
    }
}
