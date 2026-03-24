package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.util.Validation
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PersonalInfoUiState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val entityType: String = "",
    val entityTypeError: String? = null,
    val tradeName: String = "",
    val tradeNameError: String? = null,
    val isFormValid: Boolean = false,
)

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalInfoUiState())
    val uiState: StateFlow<PersonalInfoUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.update {
            it.copy(
                name = value,
                nameError = Validation.validateName(value)
            ).withFormValidity()
        }
    }

    fun onEmailChanged(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                emailError = Validation.validateEmail(value)
            ).withFormValidity()
        }
    }

    fun onEntityTypeChanged(value: String) {
        _uiState.update {
            it.copy(
                entityType = value,
                entityTypeError = if (value.isBlank())
                    t("व्यापार प्रकार चुनें", "Select entity type")
                else null
            ).withFormValidity()
        }
    }

    fun onTradeNameChanged(value: String) {
        _uiState.update {
            it.copy(
                tradeName = value,
                tradeNameError = if (value.isBlank())
                    t("व्यापार का नाम डालें", "Enter trade name")
                else null
            ).withFormValidity()
        }
    }

    fun validateAll(): Boolean {
        val current = _uiState.value
        val nameErr = Validation.validateName(current.name)
        val emailErr = Validation.validateEmail(current.email)
        val entityErr = if (current.entityType.isBlank())
            t("व्यापार प्रकार चुनें", "Select entity type") else null
        val tradeErr = if (current.tradeName.isBlank())
            t("व्यापार का नाम डालें", "Enter trade name") else null

        _uiState.update {
            it.copy(
                nameError = nameErr,
                emailError = emailErr,
                entityTypeError = entityErr,
                tradeNameError = tradeErr
            ).withFormValidity()
        }

        return nameErr == null && emailErr == null && entityErr == null && tradeErr == null
    }

    private fun PersonalInfoUiState.withFormValidity(): PersonalInfoUiState {
        val valid = nameError == null && name.isNotBlank()
                && emailError == null && email.isNotBlank()
                && entityTypeError == null && entityType.isNotBlank()
                && tradeNameError == null && tradeName.isNotBlank()
        return copy(isFormValid = valid)
    }
}
