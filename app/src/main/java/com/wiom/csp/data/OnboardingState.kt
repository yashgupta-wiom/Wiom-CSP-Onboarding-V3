package com.wiom.csp.data

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

enum class Scenario {
    NONE,
    PHONE_DUPLICATE,
    OTP_WRONG,
    OTP_EXPIRED,
    AREA_NOT_SERVICEABLE,
    KYC_PAN_MISMATCH,
    KYC_AADHAAR_EXPIRED,
    KYC_PAN_AADHAAR_UNLINKED,
    REGFEE_FAILED,
    REGFEE_TIMEOUT,
    BANK_PENNYDROP_FAIL,
    BANK_NAME_MISMATCH,
    DEDUP_FOUND,
    ISP_DOC_INVALID,
    VERIFICATION_REJECTED,
    TECH_ASSESSMENT_REJECTED,
    ONBOARDFEE_FAILED,
    TRAINING_QUIZ_FAIL,
    POLICY_QUIZ_FAIL,
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
    Scenario.OTP_WRONG to ScenarioMeta(1, "गलत OTP", "Wrong OTP Entered", "❌", "registration"),
    Scenario.OTP_EXPIRED to ScenarioMeta(1, "OTP Expired", "OTP Expired", "⏰", "registration"),
    Scenario.AREA_NOT_SERVICEABLE to ScenarioMeta(3, "एरिया सर्विसेबल नहीं", "Area Not Serviceable", "\uD83D\uDCCD", "verification"),
    Scenario.KYC_PAN_MISMATCH to ScenarioMeta(5, "PAN नाम मेल नहीं खाता", "PAN Name Mismatch", "\uD83E\uDEAA", "verification"),
    Scenario.KYC_AADHAAR_EXPIRED to ScenarioMeta(5, "Aadhaar पता पुराना", "Aadhaar Expired", "⚠\uFE0F", "verification"),
    Scenario.KYC_PAN_AADHAAR_UNLINKED to ScenarioMeta(5, "PAN-Aadhaar लिंक नहीं", "PAN-Aadhaar Not Linked", "\uD83D\uDD17", "verification"),
    Scenario.REGFEE_FAILED to ScenarioMeta(4, "₹2K भुगतान फेल", "₹2K Payment Failed", "\uD83D\uDCB3", "payment"),
    Scenario.REGFEE_TIMEOUT to ScenarioMeta(4, "₹2K भुगतान Timeout", "₹2K Payment Timeout", "⏳", "payment"),
    Scenario.BANK_PENNYDROP_FAIL to ScenarioMeta(6, "Penny Drop फेल", "Penny Drop Failed", "\uD83C\uDFE6", "bank"),
    Scenario.BANK_NAME_MISMATCH to ScenarioMeta(6, "Bank नाम मेल नहीं खाता", "Bank Name Mismatch", "\uD83D\uDC64", "bank"),
    Scenario.DEDUP_FOUND to ScenarioMeta(6, "Dedup मैच मिला", "Dedup Match Found", "\uD83D\uDD0D", "bank"),
    Scenario.ISP_DOC_INVALID to ScenarioMeta(7, "ISP दस्तावेज़ अमान्य", "ISP Document Invalid", "\uD83D\uDCC4", "documentation"),
    Scenario.VERIFICATION_REJECTED to ScenarioMeta(9, "सत्यापन अस्वीकृत", "Verification Rejected", "\uD83D\uDE14", "verification"),
    Scenario.TECH_ASSESSMENT_REJECTED to ScenarioMeta(12, "तकनीकी मूल्यांकन अस्वीकृत", "Tech Assessment Rejected", "\uD83D\uDEE0\uFE0F", "documentation"),
    Scenario.ONBOARDFEE_FAILED to ScenarioMeta(11, "₹20K भुगतान फेल", "₹20K Payment Failed", "\uD83D\uDCB8", "payment"),
    Scenario.TRAINING_QUIZ_FAIL to ScenarioMeta(14, "Quiz फेल", "Quiz Failed", "\uD83D\uDCDD", "training"),
    Scenario.POLICY_QUIZ_FAIL to ScenarioMeta(15, "Policy Quiz फेल", "Policy Quiz Failed", "\uD83D\uDCDD", "training"),
)

data class QuizQuestion(
    val questionHi: String,
    val questionEn: String,
    val options: List<Pair<String, String>>, // List of (hindi, english) option pairs
    val correctIndex: Int,
    val hintHi: String,
    val hintEn: String,
)

data class TrainingModule(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val subtitleHi: String,
    val subtitleEn: String,
    val icon: String,
    val videoUrl: String,
    val questions: List<QuizQuestion>,
)

data class RejectionReason(
    val id: String,
    val labelHi: String,
    val labelEn: String,
    val resolvable: Boolean,
    val resolveScreen: Int, // -1 if not resolvable
    val ctaHi: String,
    val ctaEn: String,
)

val REJECTION_REASONS = listOf(
    RejectionReason("kyc_unclear", "KYC दस्तावेज़ अस्पष्ट / अमान्य", "KYC Document Unclear/Invalid", true, 5, "दस्तावेज़ दोबारा अपलोड करें", "Re-upload Documents"),
    RejectionReason("pan_aadhaar_mismatch", "PAN और आधार में नाम मेल नहीं खाता", "PAN-Aadhaar Name Mismatch", true, 5, "सही दस्तावेज़ अपलोड करें", "Upload Correct Documents"),
    RejectionReason("shop_photo_bad", "दुकान की फ़ोटो स्वीकार्य नहीं", "Shop Photo Not Acceptable", true, 8, "फ़ोटो दोबारा अपलोड करें", "Re-upload Photos"),
    RejectionReason("isp_invalid", "ISP अनुबंध अमान्य / अधूरा", "ISP Agreement Invalid/Incomplete", true, 7, "ISP अनुबंध दोबारा अपलोड करें", "Re-upload ISP Agreement"),
    RejectionReason("address_failed", "पता सत्यापन विफल", "Address Verification Failed", true, 3, "पता अपडेट करें", "Update Address"),
    RejectionReason("bank_mismatch", "बैंक विवरण मेल नहीं खाता", "Bank Details Mismatch", true, 6, "बैंक विवरण अपडेट करें", "Update Bank Details"),
    RejectionReason("duplicate_csp", "एरिया में पहले से CSP मौजूद", "Duplicate CSP in Area", false, -1, "₹2,000 रिफंड प्रक्रिया शुरू", "₹2,000 Refund Initiated"),
)

object OnboardingState {
    var currentScreen by mutableIntStateOf(0)
    var pitchDismissed by mutableStateOf(false)
    var verificationRejected by mutableStateOf(false)
    var verificationRejectReasonId by mutableStateOf<String?>(null)
    var techAssessmentRejected by mutableStateOf(false)
    var shopFrontPhotoUploaded by mutableStateOf(false)
    var routerPhotoUploaded by mutableStateOf(false)
    var policyQuizScore by mutableIntStateOf(0)
    var policyQuizPassed by mutableStateOf(false)
    const val TOTAL_SCREENS = 17

    // Scenario simulator
    var activeScenario by mutableStateOf(Scenario.NONE)
    var simulatorExpanded by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

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
    var personalName by mutableStateOf("")
    var personalEmail by mutableStateOf("")
    var entityType by mutableStateOf("")
    var tradeName by mutableStateOf("")
    var selectedState by mutableStateOf("Madhya Pradesh")
    var city by mutableStateOf("")
    var pincode by mutableStateOf("")
    var address by mutableStateOf("")

    // KYC document states
    var panUploaded by mutableStateOf(false)
    var aadhaarFrontUploaded by mutableStateOf(false)
    var aadhaarBackUploaded by mutableStateOf(false)
    var gstUploaded by mutableStateOf(false)

    // ISP Agreement
    var ispAgreementUploaded by mutableStateOf(false)

    // Bank screen
    var bankAccountHolder by mutableStateOf("")
    var bankName by mutableStateOf("")
    var bankAccountNumber by mutableStateOf("")
    var bankIfsc by mutableStateOf("")
    var bankVerified by mutableStateOf(false)

    // Tech review (legacy fields kept for compatibility)
    var shopPhotoUploaded by mutableStateOf(false)
    var equipmentReviewed by mutableStateOf(false)
    var internetSetupType by mutableStateOf("")

    // Training modules
    var trainingModules = mutableStateListOf<TrainingModule>()
    var completedModuleIds = mutableStateListOf<String>()
    var activeTrainingModuleId by mutableStateOf<String?>(null)

    // Mode: empty (prototype starts empty) vs filled (demo with prefilled data)
    var isFilledMode by mutableStateOf(false)

    init {
        initDefaultModules()
    }

    fun initDefaultModules() {
        if (trainingModules.isEmpty()) {
            trainingModules.addAll(
                listOf(
                    TrainingModule(
                        id = "app_usage",
                        titleHi = "App कैसे चलाएं",
                        titleEn = "How to use the App",
                        subtitleHi = "Customer, रीचार्ज, शिकायतें",
                        subtitleEn = "Customer, Recharge, Complaints",
                        icon = "\uD83D\uDCF1",
                        videoUrl = "",
                        questions = listOf(
                            QuizQuestion(
                                questionHi = "ग्राहक का नया कनेक्शन कैसे बनाएं?",
                                questionEn = "How to create a new customer connection?",
                                options = listOf(
                                    "Settings से" to "From Settings",
                                    "Home > Add Customer से" to "From Home > Add Customer",
                                    "Profile से" to "From Profile",
                                    "Help से" to "From Help",
                                ),
                                correctIndex = 1,
                                hintHi = "Home screen पर 'Add Customer' बटन दबाएं",
                                hintEn = "Press 'Add Customer' button on Home screen",
                            ),
                            QuizQuestion(
                                questionHi = "रीचार्ज status कहां देखें?",
                                questionEn = "Where to check recharge status?",
                                options = listOf(
                                    "Settings" to "Settings",
                                    "Home" to "Home",
                                    "Earnings > Transactions" to "Earnings > Transactions",
                                    "Profile" to "Profile",
                                ),
                                correctIndex = 2,
                                hintHi = "Earnings section में Transactions tab पर जाएं",
                                hintEn = "Go to Transactions tab in Earnings section",
                            ),
                        ),
                    ),
                    TrainingModule(
                        id = "sla_exposure",
                        titleHi = "SLA और Exposure",
                        titleEn = "SLA & Exposure",
                        subtitleHi = "नियम, स्तर, प्रभाव",
                        subtitleEn = "Rules, Levels, Impact",
                        icon = "\uD83D\uDCCA",
                        videoUrl = "",
                        questions = listOf(
                            QuizQuestion(
                                questionHi = "ग्राहक शिकायत का resolution time क्या है?",
                                questionEn = "What is customer complaint resolution time?",
                                options = listOf(
                                    "24 घंटे" to "24 hours",
                                    "4 घंटे" to "4 hours",
                                    "48 घंटे" to "48 hours",
                                    "1 हफ़्ता" to "1 week",
                                ),
                                correctIndex = 1,
                                hintHi = "SLA के अनुसार 4 घंटे में समाधान करना ज़रूरी है",
                                hintEn = "Resolution within 4 hours is required as per SLA",
                            ),
                            QuizQuestion(
                                questionHi = "Minimum uptime requirement क्या है?",
                                questionEn = "What is minimum uptime requirement?",
                                options = listOf(
                                    "90%" to "90%",
                                    "95%" to "95%",
                                    "99%" to "99%",
                                    "80%" to "80%",
                                ),
                                correctIndex = 1,
                                hintHi = "Connection 95%+ चालू रहना चाहिए",
                                hintEn = "Connection should be running 95%+",
                            ),
                        ),
                    ),
                    TrainingModule(
                        id = "money_matters",
                        titleHi = "पैसों की बात",
                        titleEn = "Money Matters",
                        subtitleHi = "Commission, TDS, TCS",
                        subtitleEn = "Commission, TDS, TCS",
                        icon = "\uD83D\uDCB0",
                        videoUrl = "",
                        questions = listOf(
                            QuizQuestion(
                                questionHi = "नए कनेक्शन पर कमीशन कितना है?",
                                questionEn = "Commission on new connection?",
                                options = listOf(
                                    "₹150" to "₹150",
                                    "₹200" to "₹200",
                                    "₹300" to "₹300",
                                    "₹500" to "₹500",
                                ),
                                correctIndex = 2,
                                hintHi = "हर नए कनेक्शन पर ₹300 मिलता है",
                                hintEn = "You get ₹300 for every new connection",
                            ),
                            QuizQuestion(
                                questionHi = "Commission payout कब होता है?",
                                questionEn = "When is commission payout?",
                                options = listOf(
                                    "Daily" to "Daily",
                                    "Weekly (Monday)" to "Weekly (Monday)",
                                    "Monthly" to "Monthly",
                                    "Quarterly" to "Quarterly",
                                ),
                                correctIndex = 1,
                                hintHi = "हर Monday को bank account में payout होता है",
                                hintEn = "Payout happens every Monday to bank account",
                            ),
                        ),
                    ),
                )
            )
        }
    }

    fun isModuleCompleted(id: String) = id in completedModuleIds
    fun completeModule(id: String) { if (id !in completedModuleIds) completedModuleIds.add(id) }
    fun allModulesCompleted() = trainingModules.all { it.id in completedModuleIds }

    fun fillAllScreens() {
        isFilledMode = true
        pitchDismissed = true
        phoneNumber = "9876543210"
        otpDigits = listOf("4", "7", "2", "9")
        personalName = "राजेश कुमार"
        personalEmail = "rajesh@email.com"
        entityType = "Individual"
        tradeName = "Rajesh Telecom"
        city = "Indore"
        pincode = "452010"
        address = "123, Vijay Nagar, Indore"
        panUploaded = true
        aadhaarFrontUploaded = true
        aadhaarBackUploaded = true
        gstUploaded = true
        bankAccountHolder = "राजेश कुमार"
        bankName = "State Bank of India"
        bankAccountNumber = "XXXX XXXX 4521"
        bankIfsc = "SBIN0001234"
        bankVerified = true
        shopPhotoUploaded = true
        shopFrontPhotoUploaded = true
        routerPhotoUploaded = true
        equipmentReviewed = true
        internetSetupType = "Fiber (FTTH)"
        ispAgreementUploaded = true
        policyQuizScore = 5
        policyQuizPassed = true
        completedModuleIds.clear()
        completedModuleIds.addAll(trainingModules.map { it.id })
    }

    fun emptyAllScreens() {
        isFilledMode = false
        pitchDismissed = false
        phoneNumber = ""
        otpDigits = listOf("", "", "", "")
        otpTimerSeconds = 30
        otpTimerExpired = false
        personalName = ""
        personalEmail = ""
        entityType = ""
        tradeName = ""
        city = ""
        pincode = ""
        address = ""
        panUploaded = false
        aadhaarFrontUploaded = false
        aadhaarBackUploaded = false
        gstUploaded = false
        bankAccountHolder = ""
        bankName = ""
        bankAccountNumber = ""
        bankIfsc = ""
        bankVerified = false
        shopPhotoUploaded = false
        shopFrontPhotoUploaded = false
        routerPhotoUploaded = false
        equipmentReviewed = false
        internetSetupType = ""
        ispAgreementUploaded = false
        verificationRejected = false
        techAssessmentRejected = false
        policyQuizScore = 0
        policyQuizPassed = false
        completedModuleIds.clear()
        activeTrainingModuleId = null
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
        ScreenMeta("Phase 1", "मोबाइल नंबर", "Mobile Number"),              // 0
        ScreenMeta("Phase 1", "OTP वेरिफिकेशन", "OTP Verification"),       // 1
        ScreenMeta("Phase 1", "व्यक्तिगत जानकारी", "Personal Info"),        // 2
        ScreenMeta("Phase 1", "लोकेशन", "Location"),                        // 3
        ScreenMeta("Phase 1", "रजिस्ट्रेशन फ़ीस", "Registration Fee"),      // 4
        ScreenMeta("Phase 2", "KYC दस्तावेज़", "KYC Documents"),             // 5
        ScreenMeta("Phase 2", "Bank + Dedup Check", "Bank + Dedup"),         // 6
        ScreenMeta("Phase 2", "ISP अनुबंध", "ISP Agreement"),               // 7
        ScreenMeta("Phase 2", "फ़ोटो", "Photos"),                           // 8
        ScreenMeta("Phase 2", "सत्यापन", "Verification"),                   // 9
        ScreenMeta("Phase 2", "नीतियां और रेट कार्ड", "Policy & Rate Card"), // 10
        ScreenMeta("Phase 3", "ऑनबोर्डिंग फ़ीस", "Onboarding Fee"),          // 11
        ScreenMeta("Phase 3", "तकनीकी मूल्यांकन", "Technical Assessment"),   // 12
        ScreenMeta("Phase 3", "CSP खाता सेटअप", "CSP Account Setup"),        // 13
        ScreenMeta("Phase 3", "ट्रेनिंग", "Training"),                      // 14
        ScreenMeta("Phase 3", "Wiom नीति प्रश्नोत्तरी", "Policy Quiz"),      // 15
        ScreenMeta("Phase 3", "Go Live!", "Go Live!"),                       // 16
    )
}
