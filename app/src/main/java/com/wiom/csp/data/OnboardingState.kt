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
    Scenario.PHONE_DUPLICATE to ScenarioMeta(0, "\u0928\u0902\u092C\u0930 \u092A\u0939\u0932\u0947 \u0938\u0947 \u0930\u091C\u093F\u0938\u094D\u091F\u0930\u094D\u0921", "Phone Already Registered", "\uD83D\uDCF1", "registration"),
    Scenario.OTP_WRONG to ScenarioMeta(1, "\u0917\u0932\u0924 OTP", "Wrong OTP Entered", "\u274C", "registration"),
    Scenario.OTP_EXPIRED to ScenarioMeta(1, "OTP Expired", "OTP Expired", "\u23F0", "registration"),
    Scenario.AREA_NOT_SERVICEABLE to ScenarioMeta(3, "\u090F\u0930\u093F\u092F\u093E \u0938\u0930\u094D\u0935\u093F\u0938\u0947\u092C\u0932 \u0928\u0939\u0940\u0902", "Area Not Serviceable", "\uD83D\uDCCD", "verification"),
    Scenario.KYC_PAN_MISMATCH to ScenarioMeta(5, "PAN \u0928\u093E\u092E \u092E\u0947\u0932 \u0928\u0939\u0940\u0902 \u0916\u093E\u0924\u093E", "PAN Name Mismatch", "\uD83E\uDEAA", "verification"),
    Scenario.KYC_AADHAAR_EXPIRED to ScenarioMeta(5, "Aadhaar \u092A\u0924\u093E \u092A\u0941\u0930\u093E\u0928\u093E", "Aadhaar Expired", "\u26A0\uFE0F", "verification"),
    Scenario.KYC_PAN_AADHAAR_UNLINKED to ScenarioMeta(5, "PAN-Aadhaar \u0932\u093F\u0902\u0915 \u0928\u0939\u0940\u0902", "PAN-Aadhaar Not Linked", "\uD83D\uDD17", "verification"),
    Scenario.REGFEE_FAILED to ScenarioMeta(4, "\u20B92K \u092D\u0941\u0917\u0924\u093E\u0928 \u092B\u0947\u0932", "\u20B92K Payment Failed", "\uD83D\uDCB3", "payment"),
    Scenario.REGFEE_TIMEOUT to ScenarioMeta(4, "\u20B92K \u092D\u0941\u0917\u0924\u093E\u0928 Timeout", "\u20B92K Payment Timeout", "\u23F3", "payment"),
    Scenario.BANK_PENNYDROP_FAIL to ScenarioMeta(6, "Penny Drop \u092B\u0947\u0932", "Penny Drop Failed", "\uD83C\uDFE6", "bank"),
    Scenario.BANK_NAME_MISMATCH to ScenarioMeta(6, "Bank \u0928\u093E\u092E \u092E\u0947\u0932 \u0928\u0939\u0940\u0902 \u0916\u093E\u0924\u093E", "Bank Name Mismatch", "\uD83D\uDC64", "bank"),
    Scenario.DEDUP_FOUND to ScenarioMeta(6, "Dedup \u092E\u0948\u091A \u092E\u093F\u0932\u093E", "Dedup Match Found", "\uD83D\uDD0D", "bank"),
    Scenario.ISP_DOC_INVALID to ScenarioMeta(7, "ISP \u0926\u0938\u094D\u0924\u093E\u0935\u0947\u091C\u093C \u0905\u092E\u093E\u0928\u094D\u092F", "ISP Document Invalid", "\uD83D\uDCC4", "documentation"),
    Scenario.VERIFICATION_REJECTED to ScenarioMeta(9, "\u0938\u0924\u094D\u092F\u093E\u092A\u0928 \u0905\u0938\u094D\u0935\u0940\u0915\u0943\u0924", "Verification Rejected", "\u274C", "verification"),
    Scenario.TECH_ASSESSMENT_REJECTED to ScenarioMeta(12, "\u0924\u0915\u0928\u0940\u0915\u0940 \u092E\u0942\u0932\u094D\u092F\u093E\u0902\u0915\u0928 \u0905\u0938\u094D\u0935\u0940\u0915\u0943\u0924", "Tech Assessment Rejected", "\uD83D\uDEE0\uFE0F", "assessment"),
    Scenario.ONBOARDFEE_FAILED to ScenarioMeta(11, "\u20B920K \u092D\u0941\u0917\u0924\u093E\u0928 \u092B\u0947\u0932", "\u20B920K Payment Failed", "\uD83D\uDCB8", "payment"),
    Scenario.TRAINING_QUIZ_FAIL to ScenarioMeta(14, "Quiz \u092B\u0947\u0932", "Quiz Failed", "\uD83D\uDCDD", "training"),
    Scenario.POLICY_QUIZ_FAIL to ScenarioMeta(15, "\u092A\u0949\u0932\u093F\u0938\u0940 Quiz \u092B\u0947\u0932", "Policy Quiz Failed", "\uD83D\uDCCB", "training"),
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

object OnboardingState {
    var pitchDismissed by mutableStateOf(false)
    var currentScreen by mutableIntStateOf(0)
    var verificationRejected by mutableStateOf(false)
    var techAssessmentRejected by mutableStateOf(false)
    const val TOTAL_SCREENS = 17

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
    var panUploaded by mutableStateOf(false)
    var aadhaarFrontUploaded by mutableStateOf(false)
    var aadhaarBackUploaded by mutableStateOf(false)
    var gstUploaded by mutableStateOf(false)

    // Bank screen
    var bankAccountHolder by mutableStateOf("")
    var bankName by mutableStateOf("")
    var bankAccountNumber by mutableStateOf("")
    var bankIfsc by mutableStateOf("")
    var bankVerified by mutableStateOf(false)

    // ISP Agreement
    var ispAgreementUploaded by mutableStateOf(false)

    // Shop & Equipment Photos
    var shopPhotoUploaded by mutableStateOf(false)
    var equipmentPhotoUploaded by mutableStateOf(false)

    // Policy Quiz
    var policyQuizPassed by mutableStateOf(false)

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
                        titleHi = "App \u0915\u0948\u0938\u0947 \u091A\u0932\u093E\u090F\u0902",
                        titleEn = "How to use the App",
                        subtitleHi = "Customer, \u0930\u0940\u091A\u093E\u0930\u094D\u091C, \u0936\u093F\u0915\u093E\u092F\u0924\u0947\u0902",
                        subtitleEn = "Customer, Recharge, Complaints",
                        icon = "\uD83D\uDCF1",
                        videoUrl = "",
                        questions = listOf(
                            QuizQuestion(
                                questionHi = "\u0917\u094D\u0930\u093E\u0939\u0915 \u0915\u093E \u0928\u092F\u093E \u0915\u0928\u0947\u0915\u094D\u0936\u0928 \u0915\u0948\u0938\u0947 \u092C\u0928\u093E\u090F\u0902?",
                                questionEn = "How to create a new customer connection?",
                                options = listOf(
                                    "Settings \u0938\u0947" to "From Settings",
                                    "Home > Add Customer \u0938\u0947" to "From Home > Add Customer",
                                    "Profile \u0938\u0947" to "From Profile",
                                    "Help \u0938\u0947" to "From Help",
                                ),
                                correctIndex = 1,
                                hintHi = "Home screen \u092A\u0930 'Add Customer' \u092C\u091F\u0928 \u0926\u092C\u093E\u090F\u0902",
                                hintEn = "Press 'Add Customer' button on Home screen",
                            ),
                            QuizQuestion(
                                questionHi = "\u0930\u0940\u091A\u093E\u0930\u094D\u091C status \u0915\u0939\u093E\u0902 \u0926\u0947\u0916\u0947\u0902?",
                                questionEn = "Where to check recharge status?",
                                options = listOf(
                                    "Settings" to "Settings",
                                    "Home" to "Home",
                                    "Earnings > Transactions" to "Earnings > Transactions",
                                    "Profile" to "Profile",
                                ),
                                correctIndex = 2,
                                hintHi = "Earnings section \u092E\u0947\u0902 Transactions tab \u092A\u0930 \u091C\u093E\u090F\u0902",
                                hintEn = "Go to Transactions tab in Earnings section",
                            ),
                        ),
                    ),
                    TrainingModule(
                        id = "sla_exposure",
                        titleHi = "SLA \u0914\u0930 Exposure",
                        titleEn = "SLA & Exposure",
                        subtitleHi = "\u0928\u093F\u092F\u092E, \u0938\u094D\u0924\u0930, \u092A\u094D\u0930\u092D\u093E\u0935",
                        subtitleEn = "Rules, Levels, Impact",
                        icon = "\uD83D\uDCCA",
                        videoUrl = "",
                        questions = listOf(
                            QuizQuestion(
                                questionHi = "\u0917\u094D\u0930\u093E\u0939\u0915 \u0936\u093F\u0915\u093E\u092F\u0924 \u0915\u093E resolution time \u0915\u094D\u092F\u093E \u0939\u0948?",
                                questionEn = "What is customer complaint resolution time?",
                                options = listOf(
                                    "24 \u0918\u0902\u091F\u0947" to "24 hours",
                                    "4 \u0918\u0902\u091F\u0947" to "4 hours",
                                    "48 \u0918\u0902\u091F\u0947" to "48 hours",
                                    "1 \u0939\u095E\u094D\u0924\u093E" to "1 week",
                                ),
                                correctIndex = 1,
                                hintHi = "SLA \u0915\u0947 \u0905\u0928\u0941\u0938\u093E\u0930 4 \u0918\u0902\u091F\u0947 \u092E\u0947\u0902 \u0938\u092E\u093E\u0927\u093E\u0928 \u0915\u0930\u0928\u093E \u091C\u093C\u0930\u0942\u0930\u0940 \u0939\u0948",
                                hintEn = "Resolution within 4 hours is required as per SLA",
                            ),
                            QuizQuestion(
                                questionHi = "Minimum uptime requirement \u0915\u094D\u092F\u093E \u0939\u0948?",
                                questionEn = "What is minimum uptime requirement?",
                                options = listOf(
                                    "90%" to "90%",
                                    "95%" to "95%",
                                    "99%" to "99%",
                                    "80%" to "80%",
                                ),
                                correctIndex = 1,
                                hintHi = "Connection 95%+ \u091A\u093E\u0932\u0942 \u0930\u0939\u0928\u093E \u091A\u093E\u0939\u093F\u090F",
                                hintEn = "Connection should be running 95%+",
                            ),
                        ),
                    ),
                    TrainingModule(
                        id = "money_matters",
                        titleHi = "\u092A\u0948\u0938\u094B\u0902 \u0915\u0940 \u092C\u093E\u0924",
                        titleEn = "Money Matters",
                        subtitleHi = "Commission, TDS, TCS",
                        subtitleEn = "Commission, TDS, TCS",
                        icon = "\uD83D\uDCB0",
                        videoUrl = "",
                        questions = listOf(
                            QuizQuestion(
                                questionHi = "\u0928\u090F \u0915\u0928\u0947\u0915\u094D\u0936\u0928 \u092A\u0930 \u0915\u092E\u0940\u0936\u0928 \u0915\u093F\u0924\u0928\u093E \u0939\u0948?",
                                questionEn = "Commission on new connection?",
                                options = listOf(
                                    "\u20B9150" to "\u20B9150",
                                    "\u20B9200" to "\u20B9200",
                                    "\u20B9300" to "\u20B9300",
                                    "\u20B9500" to "\u20B9500",
                                ),
                                correctIndex = 2,
                                hintHi = "\u0939\u0930 \u0928\u090F \u0915\u0928\u0947\u0915\u094D\u0936\u0928 \u092A\u0930 \u20B9300 \u092E\u093F\u0932\u0924\u093E \u0939\u0948",
                                hintEn = "You get \u20B9300 for every new connection",
                            ),
                            QuizQuestion(
                                questionHi = "Commission payout \u0915\u092C \u0939\u094B\u0924\u093E \u0939\u0948?",
                                questionEn = "When is commission payout?",
                                options = listOf(
                                    "Daily" to "Daily",
                                    "Weekly (Monday)" to "Weekly (Monday)",
                                    "Monthly" to "Monthly",
                                    "Quarterly" to "Quarterly",
                                ),
                                correctIndex = 1,
                                hintHi = "\u0939\u0930 Monday \u0915\u094B bank account \u092E\u0947\u0902 payout \u0939\u094B\u0924\u093E \u0939\u0948",
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
        phoneNumber = "9876543210"
        otpDigits = listOf("4", "7", "2", "9")
        tncAccepted = true
        personalName = "\u0930\u093E\u091C\u0947\u0936 \u0915\u0941\u092E\u093E\u0930"
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
        bankAccountHolder = "\u0930\u093E\u091C\u0947\u0936 \u0915\u0941\u092E\u093E\u0930"
        bankName = "State Bank of India"
        bankAccountNumber = "XXXX XXXX 4521"
        bankIfsc = "SBIN0001234"
        bankVerified = true
        ispAgreementUploaded = true
        shopPhotoUploaded = true
        equipmentPhotoUploaded = true
        policyQuizPassed = true
        completedModuleIds.clear()
        completedModuleIds.addAll(trainingModules.map { it.id })
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
        panUploaded = false
        aadhaarFrontUploaded = false
        aadhaarBackUploaded = false
        gstUploaded = false
        bankAccountHolder = ""
        bankName = ""
        bankAccountNumber = ""
        bankIfsc = ""
        bankVerified = false
        ispAgreementUploaded = false
        shopPhotoUploaded = false
        equipmentPhotoUploaded = false
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
        ScreenMeta("Stage 1", "\u092E\u094B\u092C\u093E\u0907\u0932 \u0928\u0902\u092C\u0930", "Mobile Number"),
        ScreenMeta("Stage 1", "OTP + T&C", "OTP + T&C"),
        ScreenMeta("Stage 1", "\u0935\u094D\u092F\u0915\u094D\u0924\u093F\u0917\u0924 \u091C\u093E\u0928\u0915\u093E\u0930\u0940", "Personal Info"),
        ScreenMeta("Stage 1", "\u0932\u094B\u0915\u0947\u0936\u0928", "Location"),
        ScreenMeta("Stage 1", "\u0930\u091C\u093F\u0938\u094D\u091F\u094D\u0930\u0947\u0936\u0928 \u092B\u093C\u0940\u0938", "Registration Fee"),
        ScreenMeta("Stage 2", "KYC \u0926\u0938\u094D\u0924\u093E\u0935\u0947\u091C\u093C", "KYC Documents"),
        ScreenMeta("Stage 2", "Bank + Dedup Check", "Bank + Dedup"),
        ScreenMeta("Stage 2", "ISP \u090F\u0917\u094D\u0930\u0940\u092E\u0947\u0902\u091F", "ISP Agreement"),
        ScreenMeta("Stage 2", "\u0936\u0949\u092A \u0914\u0930 \u0909\u092A\u0915\u0930\u0923 \u092B\u093C\u094B\u091F\u094B", "Shop & Equipment Photos"),
        ScreenMeta("Stage 2", "\u0938\u0924\u094D\u092F\u093E\u092A\u0928", "Verification"),
        ScreenMeta("Stage 3", "\u0928\u0940\u0924\u093F, Payout \u0914\u0930 SLA", "Policy, Payout & SLA"),
        ScreenMeta("Stage 3", "\u0911\u0928\u092C\u094B\u0930\u094D\u0921\u093F\u0902\u0917 \u092B\u093C\u0940\u0938", "Onboarding Fee"),
        ScreenMeta("Stage 3", "\u0924\u0915\u0928\u0940\u0915\u0940 \u092E\u0942\u0932\u094D\u092F\u093E\u0902\u0915\u0928", "Technical Assessment"),
        ScreenMeta("Stage 3", "CSP \u0916\u093E\u0924\u093E \u0938\u0947\u091F\u0905\u092A", "CSP Account Setup"),
        ScreenMeta("Stage 3", "\u091F\u094D\u0930\u0947\u0928\u093F\u0902\u0917", "Training"),
        ScreenMeta("Stage 3", "\u092A\u0949\u0932\u093F\u0938\u0940 Quiz", "Policy Quiz"),
        ScreenMeta("Stage 3", "Go Live!", "Go Live!"),
    )
}
