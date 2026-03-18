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
    ESIGN_FAILED,
    TECH_DEVICE_INCOMPATIBLE,
    ONBOARDFEE_FAILED,
    TRAINING_QUIZ_FAIL,
}

data class ScenarioMeta(
    val screen: Int,
    val labelHi: String,
    val labelEn: String,
    val icon: String,
    val category: String
)

val scenarioMeta = mapOf(
    Scenario.PHONE_DUPLICATE to ScenarioMeta(0, "नंबर पहले से रजिस्टर्ड", "Phone Already Registered", "📱", "registration"),
    Scenario.OTP_WRONG to ScenarioMeta(1, "गलत OTP", "Wrong OTP Entered", "❌", "registration"),
    Scenario.OTP_EXPIRED to ScenarioMeta(1, "OTP Expired", "OTP Expired", "⏰", "registration"),
    Scenario.AREA_NOT_SERVICEABLE to ScenarioMeta(3, "एरिया सर्विसेबल नहीं", "Area Not Serviceable", "📍", "verification"),
    Scenario.KYC_PAN_MISMATCH to ScenarioMeta(4, "PAN नाम मेल नहीं खाता", "PAN Name Mismatch", "🪪", "verification"),
    Scenario.KYC_AADHAAR_EXPIRED to ScenarioMeta(4, "Aadhaar पता पुराना", "Aadhaar Expired", "⚠\uFE0F", "verification"),
    Scenario.KYC_PAN_AADHAAR_UNLINKED to ScenarioMeta(4, "PAN-Aadhaar लिंक नहीं", "PAN-Aadhaar Not Linked", "🔗", "verification"),
    Scenario.REGFEE_FAILED to ScenarioMeta(5, "₹2K भुगतान फेल", "₹2K Payment Failed", "💳", "payment"),
    Scenario.REGFEE_TIMEOUT to ScenarioMeta(5, "₹2K भुगतान Timeout", "₹2K Payment Timeout", "⏳", "payment"),
    Scenario.BANK_PENNYDROP_FAIL to ScenarioMeta(8, "Penny Drop फेल", "Penny Drop Failed", "🏦", "bank"),
    Scenario.BANK_NAME_MISMATCH to ScenarioMeta(8, "Bank नाम मेल नहीं खाता", "Bank Name Mismatch", "👤", "bank"),
    Scenario.DEDUP_FOUND to ScenarioMeta(8, "Dedup मैच मिला", "Dedup Match Found", "🔍", "bank"),
    Scenario.ESIGN_FAILED to ScenarioMeta(9, "e-Sign फेल", "e-Sign Failed", "✍\uFE0F", "documentation"),
    Scenario.TECH_DEVICE_INCOMPATIBLE to ScenarioMeta(10, "Device Compatible नहीं", "Device Incompatible", "📵", "documentation"),
    Scenario.ONBOARDFEE_FAILED to ScenarioMeta(11, "₹20K भुगतान फेल", "₹20K Payment Failed", "💸", "payment"),
    Scenario.TRAINING_QUIZ_FAIL to ScenarioMeta(13, "Quiz फेल", "Quiz Failed", "📝", "training"),
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
    var currentScreen by mutableIntStateOf(0)
    var qaRejected by mutableStateOf(false)
    const val TOTAL_SCREENS = 15

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

    // Tech review
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
        shopPhotoUploaded = true
        equipmentReviewed = true
        internetSetupType = "Fiber (FTTH)"
        completedModuleIds.clear()
        completedModuleIds.addAll(trainingModules.map { it.id })
    }

    fun emptyAllScreens() {
        isFilledMode = false
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
        equipmentReviewed = false
        internetSetupType = ""
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
        ScreenMeta("Phase 1", "मोबाइल नंबर", "Mobile Number"),
        ScreenMeta("Phase 1", "OTP वेरिफिकेशन", "OTP Verification"),
        ScreenMeta("Phase 1", "व्यक्तिगत जानकारी", "Personal Info"),
        ScreenMeta("Phase 1", "लोकेशन", "Location"),
        ScreenMeta("Phase 1", "KYC दस्तावेज़", "KYC Documents"),
        ScreenMeta("Phase 1", "रजिस्ट्रेशन फ़ीस", "Registration Fee"),
        ScreenMeta("Phase 2", "QA Investigation", "QA Investigation"),
        ScreenMeta("Phase 2", "नीतियां और रेट कार्ड", "Policy & Rate Card"),
        ScreenMeta("Phase 2", "Bank + Dedup Check", "Bank + Dedup"),
        ScreenMeta("Phase 2", "एग्रीमेंट", "Agreement"),
        ScreenMeta("Phase 2", "तकनीकी समीक्षा", "Technical Review"),
        ScreenMeta("Phase 3", "ऑनबोर्डिंग फ़ीस", "Onboarding Fee"),
        ScreenMeta("Phase 3", "फ़ाइनेंशियल सेटअप", "Financial Setup"),
        ScreenMeta("Phase 3", "ट्रेनिंग", "Training"),
        ScreenMeta("Phase 3", "Go Live!", "Go Live!"),
    )
}
