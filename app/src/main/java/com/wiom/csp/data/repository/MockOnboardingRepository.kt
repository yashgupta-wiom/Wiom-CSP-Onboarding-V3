package com.wiom.csp.data.repository

import com.wiom.csp.domain.model.*
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockOnboardingRepository @Inject constructor() : OnboardingRepository {
    // Each method simulates a 1-2 second API call delay, then returns mock data.
    // This is the ONLY file that needs to be replaced with real API calls for production.

    override suspend fun sendOtp(phone: String): Result<Boolean> {
        delay(1000)
        return Result.success(true)
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<Boolean> {
        delay(1000)
        return if (otp == "4729") Result.success(true)
        else Result.failure(Exception("Invalid OTP"))
    }

    override suspend fun checkPhoneDuplicate(phone: String): Result<Boolean> {
        delay(500)
        return Result.success(false) // false = not duplicate
    }

    override suspend fun checkServiceability(pincode: String): Result<Boolean> {
        delay(500)
        return Result.success(true) // true = serviceable
    }

    override suspend fun processPayment(amount: Int): Result<String> {
        delay(2000)
        return Result.success("TXN-${System.currentTimeMillis()}")
    }

    override suspend fun verifyBankAccount(
        holder: String,
        bank: String,
        account: String,
        ifsc: String,
    ): Result<BankVerificationResult> {
        delay(2000)
        return Result.success(
            BankVerificationResult(
                pennyDropVerified = true,
                dedupPassed = true,
                nameMatch = true,
            )
        )
    }

    override suspend fun getTrainingModules(): Result<List<TrainingModule>> {
        delay(500)
        return Result.success(defaultTrainingModules())
    }

    override suspend fun getPolicyQuizQuestions(): Result<List<QuizQuestion>> {
        delay(500)
        return Result.success(defaultQuizQuestions())
    }

    private fun defaultTrainingModules(): List<TrainingModule> = listOf(
        TrainingModule(
            id = "app_usage",
            titleHi = "App कैसे चलाएं",
            titleEn = "How to use the App",
            subtitleHi = "Customer, रीचार्ज, शिकायतें",
            subtitleEn = "Customer, Recharge, Complaints",
            icon = "\uD83D\uDCF1",
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

    private fun defaultQuizQuestions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            questionHi = "ग्राहक शिकायत का समाधान कितने घंटे में करना ज़रूरी है?",
            questionEn = "Within how many hours must a customer complaint be resolved?",
            options = listOf(
                "2 घंटे" to "2 hours",
                "4 घंटे" to "4 hours",
                "8 घंटे" to "8 hours",
                "24 घंटे" to "24 hours",
            ),
            correctIndex = 1,
            hintHi = "SLA के अनुसार 4 घंटे में समाधान ज़रूरी है",
            hintEn = "SLA requires resolution within 4 hours",
        ),
        QuizQuestion(
            questionHi = "नए कनेक्शन पर कमीशन कितना मिलता है?",
            questionEn = "What is the commission for a new connection?",
            options = listOf(
                "₹100" to "₹100",
                "₹200" to "₹200",
                "₹300" to "₹300",
                "₹500" to "₹500",
            ),
            correctIndex = 2,
            hintHi = "हर नए कनेक्शन पर ₹300 कमीशन मिलता है",
            hintEn = "You earn ₹300 commission for every new connection",
        ),
        QuizQuestion(
            questionHi = "Commission payouts कब होते हैं?",
            questionEn = "When are commission payouts made?",
            options = listOf(
                "हर दिन" to "Every day",
                "हर सोमवार" to "Every Monday",
                "हर महीने" to "Every month",
                "हर 15 दिन" to "Every 15 days",
            ),
            correctIndex = 1,
            hintHi = "Payouts हर सोमवार RazorpayX के माध्यम से होते हैं",
            hintEn = "Payouts are made every Monday via RazorpayX",
        ),
        QuizQuestion(
            questionHi = "Network uptime कितना प्रतिशत बनाए रखना ज़रूरी है?",
            questionEn = "What percentage of network uptime must be maintained?",
            options = listOf(
                "80%+" to "80%+",
                "90%+" to "90%+",
                "95%+" to "95%+",
                "99%+" to "99%+",
            ),
            correctIndex = 2,
            hintHi = "SLA के अनुसार 95%+ uptime ज़रूरी है",
            hintEn = "SLA requires 95%+ uptime",
        ),
        QuizQuestion(
            questionHi = "रिचार्ज कमीशन कितना मिलता है?",
            questionEn = "What is the recharge commission?",
            options = listOf(
                "₹100" to "₹100",
                "₹200" to "₹200",
                "₹300" to "₹300",
                "₹500" to "₹500",
            ),
            correctIndex = 2,
            hintHi = "हर रिचार्ज पर ₹300 कमीशन मिलता है",
            hintEn = "You earn ₹300 commission for every recharge",
        ),
    )
}
