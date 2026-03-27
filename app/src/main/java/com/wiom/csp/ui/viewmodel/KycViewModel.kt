package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.Scenario
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * KYC Documents Screen (Screen 5) — 3 sub-stages with progress bar.
 *
 * Sub-stage 0: PAN — number entry + card upload + view sample + dedup check
 * Sub-stage 1: Aadhaar — number entry + front & back upload + view sample + dedup
 * Sub-stage 2: GST — number entry + certificate upload + view sample + dedup
 *
 * Validation (on blur only, not while typing):
 *   PAN: [A-Z]{5}[0-9]{4}[A-Z] (10 chars)
 *   Aadhaar: [0-9]{12} (displayed as 4-4-4 with spaces)
 *   GST: [0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][0-9A-Z]{3} (15 chars)
 *        Characters 3-12 must match PAN exactly
 *
 * Dedup: Triggered on blur when number is valid AND dedup scenario is active.
 *   Shows "This [document] is linked with another Wiom account ending with 4567"
 *   CTA: "Enter Different [document]" → clears and stays on same sub-stage
 */

data class KycUiState(
    val subStage: Int = 0,  // 0=PAN, 1=Aadhaar, 2=GST

    // PAN
    val panNumber: String = "",
    val panError: String? = null,
    val panBlurred: Boolean = false,
    val panUploaded: Boolean = false,
    val panDedup: Boolean = false,

    // Aadhaar
    val aadhaarNumber: String = "",  // raw 12 digits (no spaces)
    val aadhaarError: String? = null,
    val aadhaarBlurred: Boolean = false,
    val aadhaarFrontUploaded: Boolean = false,
    val aadhaarBackUploaded: Boolean = false,
    val aadhaarDedup: Boolean = false,

    // GST
    val gstNumber: String = "",
    val gstError: String? = null,
    val gstBlurred: Boolean = false,
    val gstUploaded: Boolean = false,
    val gstDedup: Boolean = false,

    // Dedup info
    val dedupPhone: String = "4567",  // last 4 digits of existing account
) {
    val isPanValid: Boolean get() = PAN_REGEX.matches(panNumber)
    val isPanStageComplete: Boolean get() = isPanValid && panUploaded

    val isAadhaarValid: Boolean get() = AADHAAR_REGEX.matches(aadhaarNumber)
    val isAadhaarStageComplete: Boolean get() = isAadhaarValid && aadhaarFrontUploaded && aadhaarBackUploaded

    val isGstValid: Boolean get() {
        if (!GST_REGEX.matches(gstNumber)) return false
        if (panNumber.isEmpty()) return false
        return gstNumber.substring(2, 12) == panNumber
    }
    val isGstPanMatch: Boolean get() {
        if (gstNumber.length < 12 || panNumber.isEmpty()) return true  // don't show mismatch error yet
        return gstNumber.substring(2, 12) == panNumber
    }
    val isGstStageComplete: Boolean get() = isGstValid && gstUploaded

    /** Formatted aadhaar for display (4-4-4 spaces) */
    val aadhaarFormatted: String get() = aadhaarNumber.chunked(4).joinToString(" ")

    /** GST placeholder using PAN (falls back to example if PAN empty) */
    val gstPlaceholder: String get() {
        val pan = panNumber.ifEmpty { "CEVPG6375L" }
        return "09${pan}1Z4"
    }

    companion object {
        val PAN_REGEX = Regex("^[A-Z]{5}[0-9]{4}[A-Z]$")
        val AADHAAR_REGEX = Regex("^[0-9]{12}$")
        val GST_REGEX = Regex("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][0-9A-Z]{3}$")
    }
}

@HiltViewModel
class KycViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(KycUiState(
        subStage = OnboardingState.kycSubStage,
        panNumber = OnboardingState.panNumber,
        panUploaded = OnboardingState.panUploaded,
        aadhaarNumber = OnboardingState.aadhaarNumber,
        aadhaarFrontUploaded = OnboardingState.aadhaarFrontUploaded,
        aadhaarBackUploaded = OnboardingState.aadhaarBackUploaded,
        gstNumber = OnboardingState.gstNumber,
        gstUploaded = OnboardingState.gstUploaded,
    ))
    val uiState: StateFlow<KycUiState> = _uiState.asStateFlow()

    // ── PAN ──────────────────────────────────────────────────────────────

    fun onPanNumberChanged(value: String) {
        val filtered = value.uppercase().filter { it.isLetterOrDigit() }.take(10)
        _uiState.update { it.copy(panNumber = filtered, panBlurred = false, panError = null, panDedup = false) }
        OnboardingState.panNumber = filtered
    }

    fun onPanBlurred() {
        _uiState.update { state ->
            val error = if (state.panNumber.isNotEmpty() && !state.isPanValid)
                t("कृपया सही पैन नंबर दर्ज करें", "Please enter valid PAN number")
            else null

            // Check dedup scenario
            val dedup = state.isPanValid && OnboardingState.activeScenario == Scenario.KYC_PAN_DEDUP
            if (dedup) OnboardingState.activeScenario = Scenario.NONE

            state.copy(panBlurred = true, panError = error, panDedup = dedup)
        }
    }

    fun onPanUploaded() {
        _uiState.update { it.copy(panUploaded = true) }
        OnboardingState.panUploaded = true
    }

    fun clearPanDedup() {
        _uiState.update { it.copy(panNumber = "", panUploaded = false, panBlurred = false, panError = null, panDedup = false) }
        OnboardingState.panNumber = ""
        OnboardingState.panUploaded = false
    }

    // ── Aadhaar ──────────────────────────────────────────────────────────

    fun onAadhaarNumberChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(12)
        _uiState.update { it.copy(aadhaarNumber = filtered, aadhaarBlurred = false, aadhaarError = null, aadhaarDedup = false) }
        OnboardingState.aadhaarNumber = filtered
    }

    fun onAadhaarBlurred() {
        _uiState.update { state ->
            val error = if (state.aadhaarNumber.isNotEmpty() && !state.isAadhaarValid)
                t("कृपया सही आधार नंबर दर्ज करें", "Please enter valid Aadhaar number")
            else null

            val dedup = state.isAadhaarValid && OnboardingState.activeScenario == Scenario.KYC_AADHAAR_DEDUP
            if (dedup) OnboardingState.activeScenario = Scenario.NONE

            state.copy(aadhaarBlurred = true, aadhaarError = error, aadhaarDedup = dedup)
        }
    }

    fun onAadhaarFrontUploaded() {
        _uiState.update { it.copy(aadhaarFrontUploaded = true) }
        OnboardingState.aadhaarFrontUploaded = true
    }

    fun onAadhaarBackUploaded() {
        _uiState.update { it.copy(aadhaarBackUploaded = true) }
        OnboardingState.aadhaarBackUploaded = true
    }

    fun clearAadhaarDedup() {
        _uiState.update { it.copy(aadhaarNumber = "", aadhaarFrontUploaded = false, aadhaarBackUploaded = false, aadhaarBlurred = false, aadhaarError = null, aadhaarDedup = false) }
        OnboardingState.aadhaarNumber = ""
        OnboardingState.aadhaarFrontUploaded = false
        OnboardingState.aadhaarBackUploaded = false
    }

    // ── GST ──────────────────────────────────────────────────────────────

    fun onGstNumberChanged(value: String) {
        val filtered = value.uppercase().filter { it.isLetterOrDigit() }.take(15)
        _uiState.update { it.copy(gstNumber = filtered, gstBlurred = false, gstError = null, gstDedup = false) }
        OnboardingState.gstNumber = filtered
    }

    fun onGstBlurred() {
        _uiState.update { state ->
            val error = when {
                state.gstNumber.isEmpty() -> null
                state.gstNumber.length >= 5 && !state.isGstPanMatch ->
                    t("जीएसटी नंबर पैन कार्ड से मेल नहीं खाता", "GST number mismatch with PAN Card")
                !state.isGstValid ->
                    t("कृपया सही जीएसटी नंबर दर्ज करें", "Please enter valid GST number")
                else -> null
            }

            val dedup = state.isGstValid && OnboardingState.activeScenario == Scenario.KYC_GST_DEDUP
            if (dedup) OnboardingState.activeScenario = Scenario.NONE

            state.copy(gstBlurred = true, gstError = error, gstDedup = dedup)
        }
    }

    fun onGstUploaded() {
        _uiState.update { it.copy(gstUploaded = true) }
        OnboardingState.gstUploaded = true
    }

    fun clearGstDedup() {
        _uiState.update { it.copy(gstNumber = "", gstUploaded = false, gstBlurred = false, gstError = null, gstDedup = false) }
        OnboardingState.gstNumber = ""
        OnboardingState.gstUploaded = false
    }

    // ── Sub-stage navigation ─────────────────────────────────────────────

    fun moveToAadhaar() {
        _uiState.update { it.copy(subStage = 1) }
        OnboardingState.kycSubStage = 1
    }

    fun moveToGst() {
        _uiState.update { it.copy(subStage = 2) }
        OnboardingState.kycSubStage = 2
    }

    fun moveToPan() {
        _uiState.update { it.copy(subStage = 0) }
        OnboardingState.kycSubStage = 0
    }

    fun moveBackFromAadhaar() {
        _uiState.update { it.copy(subStage = 0) }
        OnboardingState.kycSubStage = 0
    }

    fun moveBackFromGst() {
        _uiState.update { it.copy(subStage = 1) }
        OnboardingState.kycSubStage = 1
    }

    // ── Document re-upload (update) ──────────────────────────────────────

    fun resetPanUpload() {
        _uiState.update { it.copy(panUploaded = false) }
        OnboardingState.panUploaded = false
    }

    fun resetAadhaarFrontUpload() {
        _uiState.update { it.copy(aadhaarFrontUploaded = false) }
        OnboardingState.aadhaarFrontUploaded = false
    }

    fun resetAadhaarBackUpload() {
        _uiState.update { it.copy(aadhaarBackUploaded = false) }
        OnboardingState.aadhaarBackUploaded = false
    }

    fun resetGstUpload() {
        _uiState.update { it.copy(gstUploaded = false) }
        OnboardingState.gstUploaded = false
    }
}
