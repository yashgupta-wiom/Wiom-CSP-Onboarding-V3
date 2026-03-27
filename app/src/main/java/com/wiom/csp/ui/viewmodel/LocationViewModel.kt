package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.util.Validation
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationUiState(
    val state: String = "Madhya Pradesh",
    val city: String = "",
    val cityError: String? = null,
    val pincode: String = "",
    val pincodeError: String? = null,
    val address: String = "",
    val addressError: String? = null,
    val isLoading: Boolean = false,
    val isServiceable: Boolean? = null,
    val isFormValid: Boolean = false,
)

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    fun onCityChanged(value: String) {
        val filtered = value.filter { !it.isDigit() }
        _uiState.update {
            it.copy(
                city = filtered,
                cityError = if (filtered.isBlank()) t("शहर डालें", "Enter city") else null
            ).withFormValidity()
        }
    }

    fun onPincodeChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(6)
        _uiState.update {
            it.copy(
                pincode = filtered,
                pincodeError = Validation.validatePincode(filtered),
                isServiceable = null // Reset serviceability on change
            ).withFormValidity()
        }
    }

    fun onAddressChanged(value: String) {
        _uiState.update {
            it.copy(
                address = value,
                addressError = if (value.isBlank()) t("पता डालें", "Enter address") else null
            ).withFormValidity()
        }
    }

    fun checkServiceability() {
        // Serviceability check removed in Phase 1 (no AREA_NOT_SERVICEABLE scenario)
        // In production, add back via API call if needed
        val current = _uiState.value
        val pincodeErr = Validation.validatePincode(current.pincode)
        if (pincodeErr != null) {
            _uiState.update { it.copy(pincodeError = pincodeErr) }
            return
        }
        _uiState.update { it.copy(isServiceable = true).withFormValidity() }
    }

    fun validateAll(): Boolean {
        val current = _uiState.value
        val cityErr = if (current.city.isBlank()) t("शहर डालें", "Enter city") else null
        val pincodeErr = Validation.validatePincode(current.pincode)
        val addressErr = if (current.address.isBlank()) t("पता डालें", "Enter address") else null

        _uiState.update {
            it.copy(
                cityError = cityErr,
                pincodeError = pincodeErr,
                addressError = addressErr
            ).withFormValidity()
        }

        return cityErr == null && pincodeErr == null && addressErr == null
    }

    private fun LocationUiState.withFormValidity(): LocationUiState {
        val valid = cityError == null && city.isNotBlank()
                && pincodeError == null && pincode.length == 6
                && addressError == null && address.isNotBlank()
                && isServiceable != false
        return copy(isFormValid = valid)
    }
}
