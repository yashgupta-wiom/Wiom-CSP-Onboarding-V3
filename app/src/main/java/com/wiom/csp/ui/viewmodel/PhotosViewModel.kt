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
 * Shop & Equipment Photos Screen (Screen 8) — matches prototype exactly.
 *
 * Stage: Verification (Step 4/5)
 * Header: "Verification", sub "Shop Verification"
 *
 * Shop Front Photo: Single photo upload via camera/gallery
 *   Pro Tips: Complete shop front, business name visible, LCO match
 *   View sample document
 *
 * Equipment Photos: Multi-photo upload (up to 5) via camera/gallery
 *   Mandatory Requirements: Power backup, OLT photo, ISP switch with >1 connectivity
 *   View sample document
 *
 * Update button resets and reopens upload from scratch.
 * Both required before proceeding.
 */

data class PhotosUiState(
    val shopPhotoUploaded: Boolean = false,
    val equipmentPhotoUploaded: Boolean = false,
    val equipmentPhotoCount: Int = 0,
    val maxEquipmentPhotos: Int = 5,
    val errorMessage: String? = null,
) {
    val bothUploaded: Boolean get() = shopPhotoUploaded && equipmentPhotoUploaded
}

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotosUiState(
        shopPhotoUploaded = OnboardingState.shopPhotoUploaded,
        equipmentPhotoUploaded = OnboardingState.equipmentPhotoUploaded,
        equipmentPhotoCount = OnboardingState.equipmentPhotoCount,
    ))
    val uiState: StateFlow<PhotosUiState> = _uiState.asStateFlow()

    // ── Shop Front Photo ─────────────────────────────────────────────────

    fun onShopPhotoUploaded() {
        _uiState.update { it.copy(shopPhotoUploaded = true) }
        OnboardingState.shopPhotoUploaded = true
    }

    fun resetShopPhoto() {
        _uiState.update { it.copy(shopPhotoUploaded = false) }
        OnboardingState.shopPhotoUploaded = false
    }

    // ── Equipment Photos (multi-photo, up to 5) ─────────────────────────

    fun addEquipmentPage() {
        _uiState.update { state ->
            val newCount = state.equipmentPhotoCount + 1
            state.copy(equipmentPhotoCount = newCount)
        }
        OnboardingState.equipmentPhotoCount = _uiState.value.equipmentPhotoCount
    }

    fun confirmEquipmentPage() {
        val state = _uiState.value
        if (state.equipmentPhotoCount >= state.maxEquipmentPhotos) {
            finishEquipmentUpload()
        }
    }

    fun finishEquipmentUpload() {
        _uiState.update { it.copy(equipmentPhotoUploaded = true) }
        OnboardingState.equipmentPhotoUploaded = true
        OnboardingState.equipmentPhotoCount = _uiState.value.equipmentPhotoCount
    }

    fun resetEquipmentPhotos() {
        _uiState.update { it.copy(equipmentPhotoUploaded = false, equipmentPhotoCount = 0) }
        OnboardingState.equipmentPhotoUploaded = false
        OnboardingState.equipmentPhotoCount = 0
    }
}
