package com.wiom.csp.data

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

enum class BankVerificationStatus {
    NONE, VERIFYING, SUCCESS, PENNY_DROP_FAIL, NAME_MISMATCH, DEDUP
}

enum class Scenario {
    NONE,
    // Global errors (any screen)
    NO_INTERNET,
    SERVER_ERROR,
    // Registration & OTP
    PHONE_DUPLICATE,
    OTP_WRONG,
    OTP_EXPIRED,
    // Registration Fee
    REGFEE_FAILED,
    REGFEE_TIMEOUT,
    // KYC reminders & dedup
    KYC_DAY1_REMINDER,
    KYC_DAY2_REMINDER,
    KYC_DAY3_REMINDER,
    KYC_DAY4_AUTOREJECT,
    KYC_PAN_DEDUP,
    KYC_AADHAAR_DEDUP,
    KYC_GST_DEDUP,
    // Bank
    BANK_PENNYDROP_FAIL,
    BANK_NAME_MISMATCH,
    BANK_DEDUP,
    // Refund tracking
    REFUND_SUCCESS,
    REFUND_IN_PROGRESS,
    REFUND_FAILED,
    REFUND_IN_PROGRESS_VR,
    // Verification & Documentation
    VERIFICATION_PENDING,
    VERIFICATION_REJECTED,
    POLICY_SLA,
    POLICY_QUIZ_FAIL,
    // Tech Assessment
    TECH_ASSESSMENT_REJECTED,
    // Onboarding Fee
    ONBOARDFEE_SUCCESS,
    ONBOARDFEE_FAILED,
    ONBOARDFEE_TIMEOUT,
    // Account Setup
    ACCOUNT_SETUP_FAILED,
    ACCOUNT_SETUP_PENDING,
}

data class ScenarioMeta(
    val screen: Int,
    val labelHi: String,
    val labelEn: String,
    val icon: String,
    val category: String
)

val scenarioMeta = mapOf(
    Scenario.PHONE_DUPLICATE to ScenarioMeta(0, "नंबर पहले से रजिस्टर्ड", "Phone Already Registered", "\uD83D\uDCF1", "registration"),
    Scenario.OTP_WRONG to ScenarioMeta(1, "गलत OTP", "Wrong OTP Entered", "\u274C", "registration"),
    Scenario.OTP_EXPIRED to ScenarioMeta(1, "OTP Expired", "OTP Expired", "\u23F0", "registration"),
    Scenario.REGFEE_FAILED to ScenarioMeta(4, "₹2K भुगतान फेल", "₹2K Payment Failed", "\uD83D\uDCB3", "payment"),
    Scenario.REGFEE_TIMEOUT to ScenarioMeta(4, "₹2K भुगतान Timeout", "₹2K Payment Timeout", "\u23F3", "payment"),
    Scenario.KYC_PAN_DEDUP to ScenarioMeta(5, "PAN Dedup मिला", "PAN Dedup Found", "\uD83D\uDD0D", "verification"),
    Scenario.KYC_AADHAAR_DEDUP to ScenarioMeta(5, "Aadhaar Dedup मिला", "Aadhaar Dedup Found", "\uD83D\uDD0D", "verification"),
    Scenario.KYC_GST_DEDUP to ScenarioMeta(5, "GST Dedup मिला", "GST Dedup Found", "\uD83D\uDD0D", "verification"),
    Scenario.KYC_DAY1_REMINDER to ScenarioMeta(5, "KYC Day 1 रिमाइंडर", "KYC Day 1 Reminder", "\uD83D\uDD14", "verification"),
    Scenario.KYC_DAY2_REMINDER to ScenarioMeta(5, "KYC Day 2 रिमाइंडर", "KYC Day 2 Reminder", "\uD83D\uDD14", "verification"),
    Scenario.KYC_DAY3_REMINDER to ScenarioMeta(5, "KYC Day 3 रिमाइंडर", "KYC Day 3 Reminder", "\uD83D\uDD14", "verification"),
    Scenario.KYC_DAY4_AUTOREJECT to ScenarioMeta(5, "KYC Day 4 ऑटो-रिजेक्ट", "KYC Day 4 Auto-Reject", "\u274C", "verification"),
    Scenario.REFUND_SUCCESS to ScenarioMeta(5, "रिफंड सफल", "Refund Success", "\u2705", "payment"),
    Scenario.REFUND_IN_PROGRESS to ScenarioMeta(5, "रिफंड प्रक्रिया में", "Refund In Progress", "\u23F3", "payment"),
    Scenario.REFUND_FAILED to ScenarioMeta(5, "रिफंड फेल", "Refund Failed", "\u274C", "payment"),
    Scenario.BANK_PENNYDROP_FAIL to ScenarioMeta(6, "Penny Drop फेल", "Penny Drop Failed", "\uD83C\uDFE6", "bank"),
    Scenario.BANK_NAME_MISMATCH to ScenarioMeta(6, "Bank नाम मेल नहीं खाता", "Bank Name Mismatch", "\uD83D\uDC64", "bank"),
    Scenario.BANK_DEDUP to ScenarioMeta(6, "Bank Dedup मिला", "Bank Dedup Found", "\uD83D\uDD0D", "bank"),
    Scenario.VERIFICATION_PENDING to ScenarioMeta(9, "सत्यापन लंबित", "Verification Pending", "\u23F3", "verification"),
    Scenario.VERIFICATION_REJECTED to ScenarioMeta(9, "सत्यापन अस्वीकृत", "Verification Rejected", "\u274C", "verification"),
    Scenario.POLICY_SLA to ScenarioMeta(10, "नीति और SLA", "Policy & SLA", "\uD83D\uDCCB", "policy"),
    Scenario.TECH_ASSESSMENT_REJECTED to ScenarioMeta(11, "तकनीकी मूल्यांकन अस्वीकृत", "Tech Assessment Rejected", "\uD83D\uDEE0\uFE0F", "assessment"),
    Scenario.ONBOARDFEE_SUCCESS to ScenarioMeta(12, "₹20K भुगतान सफल", "₹20K Payment Success", "\u2705", "payment"),
    Scenario.ONBOARDFEE_FAILED to ScenarioMeta(12, "₹20K भुगतान फेल", "₹20K Payment Failed", "\uD83D\uDCB8", "payment"),
    Scenario.ONBOARDFEE_TIMEOUT to ScenarioMeta(12, "₹20K भुगतान Timeout", "₹20K Payment Timeout", "\u23F3", "payment"),
    Scenario.ACCOUNT_SETUP_FAILED to ScenarioMeta(13, "खाता सेटअप फेल", "Account Setup Failed", "\u274C", "activation"),
    Scenario.ACCOUNT_SETUP_PENDING to ScenarioMeta(13, "खाता सेटअप लंबित", "Account Setup Pending", "\u23F3", "activation"),
)

object OnboardingState {
    var pitchDismissed by mutableStateOf(false)
    var currentScreen by mutableIntStateOf(0)
    var verificationRejected by mutableStateOf(false)
    var techAssessmentRejected by mutableStateOf(false)
    const val TOTAL_SCREENS = 15

    // Scenario simulator
    var activeScenario by mutableStateOf(Scenario.NONE)
    var simulatorExpanded by mutableStateOf(true)
    var isLoading by mutableStateOf(false)
    var isProcessing by mutableStateOf(false)
    var processingMessage by mutableStateOf("")

    // Validation state
    var phoneError by mutableStateOf<String?>(null)
    var otpError by mutableStateOf<String?>(null)
    var nameError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var pincodeError by mutableStateOf<String?>(null)

    // Form data states (empty by default for empty state)
    var phoneNumber by mutableStateOf("")
    var otpDigits by mutableStateOf(listOf("", "", "", ""))
    var otpTimerSeconds by mutableIntStateOf(30)
    var otpTimerExpired by mutableStateOf(false)
    var tncAccepted by mutableStateOf(false)
    var personalName by mutableStateOf("")
    var personalEmail by mutableStateOf("")
    var entityType by mutableStateOf("")
    var tradeName by mutableStateOf("")
    var city by mutableStateOf("")
    var pincode by mutableStateOf("")
    var address by mutableStateOf("")

    // KYC document states
    var kycSubStage by mutableIntStateOf(0) // 0=PAN, 1=Aadhaar, 2=GST
    var panNumber by mutableStateOf("")
    var panNumberValid by mutableStateOf(false)
    var panUploaded by mutableStateOf(false)
    var aadhaarNumber by mutableStateOf("")
    var aadhaarNumberValid by mutableStateOf(false)
    var aadhaarFrontUploaded by mutableStateOf(false)
    var aadhaarBackUploaded by mutableStateOf(false)
    var gstNumber by mutableStateOf("")
    var gstNumberValid by mutableStateOf(false)
    var gstUploaded by mutableStateOf(false)

    // Bank screen
    var bankAccountNumber by mutableStateOf("")
    var bankAccountNumberConfirm by mutableStateOf("")
    var bankIfsc by mutableStateOf("")
    var bankVerificationStatus by mutableStateOf(BankVerificationStatus.NONE)
    var bankSupportDocUploaded by mutableStateOf(false)

    // ISP Agreement
    var ispAgreementUploaded by mutableStateOf(false)
    var ispPageCount by mutableIntStateOf(0) // multi-page, up to 7
    var ispUploadType by mutableStateOf("") // PDF or PHOTOS

    // Shop & Equipment Photos
    var shopPhotoUploaded by mutableStateOf(false)
    var equipmentPhotoUploaded by mutableStateOf(false)
    var equipmentPhotoCount by mutableIntStateOf(0) // multi-photo, up to 5

    // Mode: empty (prototype starts empty) vs filled (demo with prefilled data)
    var isFilledMode by mutableStateOf(false)

    fun fillAllScreens() {
        isFilledMode = true
        phoneNumber = "9876543210"
        otpDigits = listOf("4", "7", "2", "9")
        tncAccepted = true
        personalName = "राजेश कुमार"
        personalEmail = "rajesh@email.com"
        entityType = "Individual"
        tradeName = "Rajesh Telecom"
        city = "Indore"
        pincode = "452010"
        address = "123, Vijay Nagar, Indore"
        kycSubStage = 2
        panNumber = "ABCDE1234F"
        panNumberValid = true
        panUploaded = true
        aadhaarNumber = "1234 5678 9012"
        aadhaarNumberValid = true
        aadhaarFrontUploaded = true
        aadhaarBackUploaded = true
        gstNumber = "22ABCDE1234F1Z5"
        gstNumberValid = true
        gstUploaded = true
        bankAccountNumber = "XXXX XXXX 4521"
        bankAccountNumberConfirm = "XXXX XXXX 4521"
        bankIfsc = "SBIN0001234"
        bankVerificationStatus = BankVerificationStatus.SUCCESS
        bankSupportDocUploaded = false
        ispAgreementUploaded = true
        ispPageCount = 3
        ispUploadType = "PDF"
        shopPhotoUploaded = true
        equipmentPhotoUploaded = true
        equipmentPhotoCount = 2
    }

    fun emptyAllScreens() {
        isFilledMode = false
        phoneNumber = ""
        otpDigits = listOf("", "", "", "")
        otpTimerSeconds = 30
        otpTimerExpired = false
        tncAccepted = false
        personalName = ""
        personalEmail = ""
        entityType = ""
        tradeName = ""
        city = ""
        pincode = ""
        address = ""
        kycSubStage = 0
        panNumber = ""
        panNumberValid = false
        panUploaded = false
        aadhaarNumber = ""
        aadhaarNumberValid = false
        aadhaarFrontUploaded = false
        aadhaarBackUploaded = false
        gstNumber = ""
        gstNumberValid = false
        gstUploaded = false
        bankAccountNumber = ""
        bankAccountNumberConfirm = ""
        bankIfsc = ""
        bankVerificationStatus = BankVerificationStatus.NONE
        bankSupportDocUploaded = false
        ispAgreementUploaded = false
        ispPageCount = 0
        ispUploadType = ""
        shopPhotoUploaded = false
        equipmentPhotoUploaded = false
        equipmentPhotoCount = 0
        currentScreen = 0
    }

    fun triggerScenario(scenario: Scenario) {
        val meta = scenarioMeta[scenario]
        if (meta != null) {
            activeScenario = scenario
            goTo(meta.screen)
        }
    }

    fun clearScenario() {
        activeScenario = Scenario.NONE
        phoneError = null
        otpError = null
        nameError = null
        emailError = null
        pincodeError = null
        isLoading = false
    }

    fun scenariosForScreen(screen: Int): List<Scenario> {
        return Scenario.entries.filter { it != Scenario.NONE && scenarioMeta[it]?.screen == screen }
    }

    fun next() {
        if (currentScreen < TOTAL_SCREENS - 1) currentScreen++
    }

    fun prev() {
        if (currentScreen > 0) currentScreen--
    }

    fun goTo(index: Int) {
        if (index in 0 until TOTAL_SCREENS) currentScreen = index
    }

    data class ScreenMeta(
        val phase: String,
        val titleHi: String,
        val titleEn: String
    )

    val screenMetas = listOf(
        ScreenMeta("Stage 1", "मोबाइल नंबर", "Mobile Number"),
        ScreenMeta("Stage 1", "OTP सत्यापन", "OTP Verification"),
        ScreenMeta("Stage 1", "व्यक्तिगत और व्यापार जानकारी", "Personal & Business Info"),
        ScreenMeta("Stage 1", "व्यापार स्थान", "Business Location"),
        ScreenMeta("Stage 1", "रजिस्ट्रेशन फ़ीस ₹2,000", "Registration Fee ₹2,000"),
        ScreenMeta("Stage 2 - Verification", "KYC दस्तावेज़", "KYC Documents"),
        ScreenMeta("Stage 2 - Verification", "Bank विवरण", "Bank Details"),
        ScreenMeta("Stage 2 - Verification", "ISP एग्रीमेंट", "ISP Agreement"),
        ScreenMeta("Stage 2 - Verification", "शॉप और उपकरण फ़ोटो", "Shop & Equipment Photos"),
        ScreenMeta("Stage 2 - Verification", "सत्यापन स्थिति", "Verification Status"),
        ScreenMeta("Stage 3 - Important Terms", "नीति और SLA", "Policy & SLA"),
        ScreenMeta("Stage 3 - Activation", "तकनीकी मूल्यांकन", "Technical Assessment"),
        ScreenMeta("Stage 3 - Activation", "ऑनबोर्डिंग फ़ीस ₹20,000", "Onboarding Fee ₹20,000"),
        ScreenMeta("Stage 3 - Activation", "CSP खाता सेटअप", "Account Setup"),
        ScreenMeta("Stage 3 - Activation", "सफलतापूर्वक ऑनबोर्ड", "Successfully Onboarded"),
    )
}
