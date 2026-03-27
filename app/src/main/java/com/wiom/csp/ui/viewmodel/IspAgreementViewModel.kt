package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.OnboardingState
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
 * ISP Agreement Screen (Screen 7) — matches prototype exactly.
 *
 * Stage: Verification (Step 3/5)
 * Header: "Verification"
 *
 * Upload options:
 *   - PDF upload (single file)
 *   - Camera photos (up to 7 pages)
 *   - Gallery photos (up to 7 pages)
 *
 * Multi-page flow: After each page confirm, asks "Add more pages?" or "Finish Upload"
 * Auto-finishes at 7 pages.
 *
 * Mandatory details checklist shown on screen:
 *   ISP Company Name, LCO/Partner Name, Agreement Date,
 *   Valid (Not Expired), License Number, Signatory Names,
 *   Partner and ISP stamp and signature
 *
 * View sample document link with actual ISP agreement image.
 * Update button resets all and reopens upload from scratch.
 */

data class IspAgreementUiState(
    val isUploaded: Boolean = false,
    val isUploading: Boolean = false,
    val pageCount: Int = 0,
    val uploadType: String = "",  // "PDF" or "PHOTOS"
    val maxPages: Int = 7,
    val errorMessage: String? = null,
)

@HiltViewModel
class IspAgreementViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IspAgreementUiState(
        isUploaded = OnboardingState.ispAgreementUploaded,
        pageCount = OnboardingState.ispPageCount,
        uploadType = OnboardingState.ispUploadType,
    ))
    val uiState: StateFlow<IspAgreementUiState> = _uiState.asStateFlow()

    fun uploadPdf() {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, errorMessage = null) }
            delay(1500) // simulate upload
            _uiState.update { it.copy(isUploaded = true, isUploading = false, uploadType = "PDF", pageCount = 1) }
            syncToState()
        }
    }

    fun addPage() {
        _uiState.update { state ->
            val newCount = state.pageCount + 1
            state.copy(pageCount = newCount)
        }
    }

    fun confirmPage() {
        _uiState.update { state ->
            val finished = state.pageCount >= state.maxPages
            state.copy(
                isUploaded = finished,
                uploadType = "PHOTOS"
            )
        }
        syncToState()
    }

    fun finishUpload() {
        _uiState.update { it.copy(isUploaded = true, uploadType = "PHOTOS") }
        syncToState()
    }

    fun resetUpload() {
        _uiState.update { IspAgreementUiState() }
        OnboardingState.ispAgreementUploaded = false
        OnboardingState.ispPageCount = 0
        OnboardingState.ispUploadType = ""
    }

    private fun syncToState() {
        val current = _uiState.value
        OnboardingState.ispAgreementUploaded = current.isUploaded
        OnboardingState.ispPageCount = current.pageCount
        OnboardingState.ispUploadType = current.uploadType
    }
}
