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

data class PhotosUiState(
    val shopPhotoUploaded: Boolean = false,
    val shopPhotoUploading: Boolean = false,
    val equipmentPhotoUploaded: Boolean = false,
    val equipmentPhotoUploading: Boolean = false,
    val errorMessage: String? = null,
    val bothUploaded: Boolean = false,
)

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotosUiState())
    val uiState: StateFlow<PhotosUiState> = _uiState.asStateFlow()

    fun uploadShopPhoto(uri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(shopPhotoUploading = true, errorMessage = null) }
            delay(1500) // Simulate upload
            _uiState.update {
                it.copy(
                    shopPhotoUploaded = true,
                    shopPhotoUploading = false,
                    bothUploaded = true && it.equipmentPhotoUploaded
                )
            }
        }
    }

    fun uploadEquipmentPhoto(uri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(equipmentPhotoUploading = true, errorMessage = null) }
            delay(1500) // Simulate upload
            _uiState.update {
                it.copy(
                    equipmentPhotoUploaded = true,
                    equipmentPhotoUploading = false,
                    bothUploaded = it.shopPhotoUploaded && true
                )
            }
        }
    }

    fun removeShopPhoto() {
        _uiState.update {
            it.copy(shopPhotoUploaded = false, bothUploaded = false)
        }
    }

    fun removeEquipmentPhoto() {
        _uiState.update {
            it.copy(equipmentPhotoUploaded = false, bothUploaded = false)
        }
    }
}
