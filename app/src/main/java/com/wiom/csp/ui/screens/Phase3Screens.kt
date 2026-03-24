package com.wiom.csp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.Scenario
import com.wiom.csp.ui.components.*
import com.wiom.csp.ui.theme.*
import com.wiom.csp.util.t
import kotlinx.coroutines.delay

// ─── Screen 11: Onboarding Fee ₹20,000 ───────────────────────────────────────
@Composable
fun OnboardingFeeScreen(onNext: () -> Unit) {
    val scenario = OnboardingState.activeScenario
    var isPaying by remember { mutableStateOf(false) }

    // Payment loading simulation
    if (isPaying) {
        LaunchedEffect(Unit) {
            delay(2000)
            isPaying = false
            onNext()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(
                title = t("ऑनबोर्डिंग फ़ीस", "Onboarding Fee"),
                rightText = t("स्टेप 2/7", "Step 2/7"),
            )

            if (scenario == Scenario.ONBOARDFEE_FAILED) {
                // ─── ONBOARDFEE_FAILED error screen ───
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(24.dp))
                    Text("\uD83D\uDE1F", fontSize = 40.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("भुगतान नहीं हो पाया", "Payment could not be processed"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                    WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                        Text(
                            "\u2705 ${t("पैसा कटा नहीं है", "No money deducted")}",
                            fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomPositive,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t("चिंता न करें — आपके अकाउंट से कोई पैसा नहीं कटा है।", "Don't worry — no money has been deducted from your account."),
                            fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    WiomCard {
                        Text(t("ट्रांज़ैक्शन विवरण", "TRANSACTION DETAILS"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomTextSec, letterSpacing = 0.5.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Amount", fontSize = 13.sp, color = WiomTextSec)
                            Text("\u20B920,000", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomText)
                        }
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Error", fontSize = 13.sp, color = WiomTextSec)
                            Text("UPI_LIMIT_EXCEEDED", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomNegative)
                        }
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Time", fontSize = 13.sp, color = WiomTextSec)
                            Text("just now", fontSize = 13.sp, color = WiomText)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    InfoBox("\uD83D\uDCA1", t("UPI limit \u20B91L/day — NEFT/RTGS या कार्ड से भुगतान करें", "UPI limit \u20B91L/day — try NEFT/RTGS or card"), type = InfoBoxType.WARNING)
                    Spacer(Modifier.height(16.dp))
                    WiomButton(t("दोबारा भुगतान करें", "Retry Payment"), onClick = {})
                    Spacer(Modifier.height(8.dp))
                    WiomButton(t("बाद में करें", "Pay Later"), onClick = {}, isSecondary = true)
                }
            } else {
                // ─── Normal happy path ───
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        t("ऑनबोर्डिंग फ़ीस", "Onboarding Fee"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                    )
                    Spacer(Modifier.height(12.dp))
                    AmountBox("\u20B920,000", t("ऑनबोर्डिंग फ़ीस (GST सहित)", "Onboarding Fee (incl. GST)"))
                    Spacer(Modifier.height(12.dp))
                    WiomCard {
                        Text("\u2713 ${t("विवरण", "Details")}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomText)
                        Spacer(Modifier.height(8.dp))
                        FeeRow(t("रजिस्ट्रेशन फ़ीस (भुगतान हुआ)", "Registration Fee (paid)"), "\u20B92,000")
                        HorizontalDivider(color = WiomBorder)
                        FeeRow(t("ऑनबोर्डिंग फ़ीस", "Onboarding Fee"), "\u20B920,000")
                        HorizontalDivider(color = WiomBorder)
                        FeeRow(t("कुल Investment", "Total Investment"), "\u20B922,000", isBold = true)
                    }
                    Spacer(Modifier.height(8.dp))
                    InfoBox("\u2713", t("भुगतान के बाद तकनीकी मूल्यांकन शुरू होगा", "Technical assessment will begin after payment"), type = InfoBoxType.SUCCESS)
                }
                BottomBar {
                    WiomButton("\u20B920,000 ${t("भुगतान करें", "Pay Now")}", onClick = { isPaying = true }, enabled = !isPaying)
                }
            }
        }

        // Loading overlay
        LoadingOverlay(
            messageHi = "भुगतान प्रोसेस हो रहा है...",
            messageEn = "Processing payment...",
            isVisible = isPaying,
        )
    }
}

@Composable
private fun FeeRow(label: String, amount: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = if (isBold) WiomText else WiomTextSec,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        )
        Text(
            amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = WiomText,
        )
    }
}

// ─── Screen 12: Technical Assessment (Branch Point 2) ─────────────────────────
@Composable
fun TechAssessmentScreen(onNext: () -> Unit) {
    val rejected = OnboardingState.techAssessmentRejected

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("तकनीकी मूल्यांकन", "Technical Assessment"),
            rightText = t("स्टेप 3/7", "Step 3/7"),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (rejected) {
                // Rejected state
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83D\uDE14", fontSize = 40.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("तकनीकी मूल्यांकन अस्वीकृत", "Technical Assessment Rejected"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("चिंता न करें — आपका पैसा सुरक्षित है", "Don't worry — your money is safe"),
                        fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))
                    WiomCard(borderColor = WiomNegative, backgroundColor = WiomNegative100) {
                        Text(t("कारण", "Reason"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomNegative)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t(
                                "लोकेशन पर इंफ्रास्ट्रक्चर उपलब्ध नहीं है। नेटवर्क तैयार होने पर दोबारा apply कर सकते हैं।",
                                "Infrastructure not available at this location. You can re-apply once network is ready."
                            ),
                            fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    WiomCard(borderColor = WiomPositive, backgroundColor = WiomPositive100) {
                        Text("\uD83D\uDD12 ${t("Refund शुरू हो गया", "Refund initiated")}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPositive)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t("\u20B920,000 आपके bank account में 5-7 working days में आ जाएगा।", "\u20B920,000 will be credited to your bank account in 5-7 working days."),
                            fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                        )
                        Text("Ref: RFD-2026-0098", fontSize = 12.sp, color = WiomHint)
                    }
                    Spacer(Modifier.height(12.dp))
                    WiomButton(
                        "\u2190 ${t("Approved Path देखें", "View Approved Path")}",
                        onClick = {
                            OnboardingState.techAssessmentRejected = false
                        },
                        isSecondary = true,
                    )
                }
            } else {
                // Pending/Approved state
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83D\uDEE0\uFE0F", fontSize = 40.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("तकनीकी मूल्यांकन चल रहा है", "Technical Assessment in progress"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("Wiom team आपकी लोकेशन और इंफ्रास्ट्रक्चर चेक कर रही है", "Wiom team is checking your location and infrastructure"),
                        fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(16.dp))
                ChecklistItem(
                    t("इंफ्रास्ट्रक्चर Review", "Infrastructure Review"),
                    subtitle = t("चल रहा है...", "In progress..."),
                    isDone = false,
                    isWaiting = true,
                )
                ChecklistItem(
                    t("नेटवर्क Readiness", "Network Readiness"),
                    subtitle = t("चल रहा है...", "In progress..."),
                    isDone = false,
                    isWaiting = true,
                )
                ChecklistItem(
                    t("स्थान Feasibility", "Location Feasibility"),
                    subtitle = t("चल रहा है...", "In progress..."),
                    isDone = false,
                    isWaiting = true,
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoBox("\u23F3", t("2-3 कार्य दिवस लग सकते हैं। Notification मिलेगा।", "May take 2-3 working days. You will be notified."), type = InfoBoxType.WARNING)
                }
            }
        }
    }
}

// ─── Screen 13: CSP Account Setup (was Financial Setup) ───────────────────────
@Composable
fun AccountSetupScreen(onNext: () -> Unit) {
    val checklistItems = listOf(
        Pair(t("पार्टनर लेजर", "Partner Ledger"), t("Commission tracking चालू", "Commission tracking active")),
        Pair(t("RazorpayX", "RazorpayX"), t("SBI A/C XXXX4521 payouts के लिए लिंक", "SBI A/C XXXX4521 linked for payouts")),
        Pair(t("Zoho Invoice", "Zoho Invoice"), t("हर settlement के लिए auto-invoice", "Auto-invoice for every settlement")),
        Pair(t("व्यापार नाम लॉक", "Trade Name Locked"), t("\"Rajesh Telecom\" — आधिकारिक नाम", "\"Rajesh Telecom\" — official name")),
        Pair(t("TDS/TCS कॉन्फ़िगरेशन", "TDS/TCS Configuration"), t("PAN ABCDE1234F — auto deduction सेटअप", "PAN ABCDE1234F — auto deduction setup")),
    )

    // Sequential animation: track how many items are "done"
    var completedCount by remember { mutableStateOf(0) }
    val allDone = completedCount >= checklistItems.size

    // Sequentially complete items with 800ms delays
    LaunchedEffect(Unit) {
        for (i in checklistItems.indices) {
            delay(800)
            completedCount = i + 1
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("CSP खाता सेटअप", "CSP Account Setup"),
            rightText = t("स्टेप 4/7", "Step 4/7"),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("\u2699\uFE0F", fontSize = 40.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (allDone) t("सेटअप पूरा!", "Setup Complete!")
                    else t("Backend Setup हो रहा है", "Backend Setup in progress"),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    color = if (allDone) WiomPositive else WiomText,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    t("System आपका CSP account तैयार कर रहा है", "System is preparing your CSP account"),
                    fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                )
            }

            checklistItems.forEachIndexed { index, (title, subtitle) ->
                val isDone = index < completedCount
                val isProcessing = index == completedCount

                AnimatedVisibility(
                    visible = index <= completedCount,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 }),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (isProcessing) {
                            // Loading spinner for currently-processing item
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = WiomPrimary,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) WiomPositive else WiomBgSec),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    if (isDone) "\u2713" else "\u22EF",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) Color.White else WiomHint,
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = WiomText)
                            Text(
                                if (isProcessing) t("प्रोसेस हो रहा है...", "Processing...")
                                else subtitle,
                                fontSize = 12.sp,
                                color = if (isProcessing) WiomWarning700 else WiomTextSec,
                            )
                        }
                    }
                }
            }

            if (allDone) {
                Column(modifier = Modifier.padding(16.dp)) {
                    WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                        Text("\u2713 ${t("सब तैयार है!", "All set!")}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomPositive)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t("Ledger, payouts, invoicing, और tax setup पूरा। अब training शुरू!", "Ledger, payouts, invoicing, and tax setup complete. Now start training!"),
                            fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    InfoBox("\uD83D\uDCB0", t("Commission payouts हर Monday, सीधे आपके बैंक में", "Commission payouts every Monday, directly to your bank"), type = InfoBoxType.SUCCESS)
                }
            }
        }
        BottomBar {
            WiomButton(t("Training शुरू करें", "Start Training"), onClick = onNext, enabled = allDone)
        }
    }
}

// ─── Screen 14: Training Modules ──────────────────────────────────────────────
@Composable
fun TrainingScreen(onNext: () -> Unit) {
    val activeModuleId = OnboardingState.activeTrainingModuleId

    if (activeModuleId != null) {
        TrainingDetailScreen(
            moduleId = activeModuleId,
            onBack = {
                OnboardingState.activeTrainingModuleId = null
            },
            onComplete = {
                OnboardingState.completeModule(activeModuleId)
                OnboardingState.activeTrainingModuleId = null
            },
        )
    } else {
        TrainingListScreen(onNext = onNext)
    }
}

@Composable
private fun TrainingListScreen(onNext: () -> Unit) {
    val scenario = OnboardingState.activeScenario
    val modules = OnboardingState.trainingModules
    val doneCount = OnboardingState.completedModuleIds.size
    val totalCount = modules.size
    val progress = if (totalCount > 0) doneCount.toFloat() / totalCount else 0f

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = "\uD83C\uDF93 ${t("ट्रेनिंग", "Training")}",
            rightText = t("स्टेप 5/7", "Step 5/7"),
        )

        if (scenario == Scenario.TRAINING_QUIZ_FAIL) {
            // --- TRAINING_QUIZ_FAIL error screen ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(24.dp))
                Text("\uD83D\uDCDD", fontSize = 40.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    t("Quiz पास नहीं हुआ", "Quiz not passed"),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomWarning700,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                WiomCard {
                    Text(t("आपका स्कोर", "Your Score"), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomText)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("2/5", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = WiomNegative)
                        Text(t("पास होने के लिए 4/5 ज़रूरी", "Need 4/5 to pass"), fontSize = 13.sp, color = WiomTextSec)
                    }
                    Spacer(Modifier.height(8.dp))
                    WiomProgressBar(0.4f)
                }
                Spacer(Modifier.height(12.dp))
                ErrorCard(
                    icon = "\uD83D\uDCDD",
                    titleHi = "चिंता न करें",
                    titleEn = "Don't worry",
                    messageHi = "Modules दोबारा review करें और फिर quiz दें",
                    messageEn = "Review modules again and retake quiz",
                    type = "warning",
                )
                Spacer(Modifier.height(16.dp))
                WiomButton(t("Modules Review करें", "Review Modules"), onClick = {})
                Spacer(Modifier.height(8.dp))
                WiomButton(t("Quiz दोबारा दें", "Retake Quiz"), onClick = {}, isSecondary = true)
            }
        } else {
            // --- Normal happy path (interactive) ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    t("पार्टनर ट्रेनिंग", "Partner Training"),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    t("पूरा करें, फिर काम शुरू!", "Complete to start working!"),
                    fontSize = 14.sp, color = WiomTextSec,
                )
                Spacer(Modifier.height(4.dp))
                WiomProgressBar(progress)
                Spacer(Modifier.height(12.dp))

                // Dynamic module cards
                var foundFirstIncomplete = false
                modules.forEach { module ->
                    val isDone = OnboardingState.isModuleCompleted(module.id)
                    val isCurrent = !isDone && !foundFirstIncomplete
                    if (isCurrent) foundFirstIncomplete = true

                    ModuleCard(
                        icon = module.icon,
                        title = t(module.titleHi, module.titleEn),
                        subtitle = t(module.subtitleHi, module.subtitleEn),
                        isDone = isDone,
                        isCurrent = isCurrent,
                        badgeText = t("शुरू करें", "Start"),
                        onClick = {
                            OnboardingState.activeTrainingModuleId = module.id
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (!OnboardingState.allModulesCompleted()) {
                    InfoBox("\uD83D\uDCDD", t("सभी modules पूरे करें, फिर Quiz दें", "Complete all modules, then take the Quiz"))
                }
            }
            BottomBar {
                WiomButton(
                    t("Wiom नीति प्रश्नोत्तरी शुरू करें", "Start Policy Quiz"),
                    onClick = onNext,
                    enabled = OnboardingState.allModulesCompleted(),
                )
            }
        }
    }
}

// Training Detail Screen — video + quiz
@Composable
fun TrainingDetailScreen(moduleId: String, onBack: () -> Unit, onComplete: () -> Unit) {
    val module = OnboardingState.trainingModules.find { it.id == moduleId } ?: run {
        onBack()
        return
    }

    var videoWatched by remember { mutableStateOf(false) }
    var videoPlaying by remember { mutableStateOf(false) }
    var videoProgress by remember { mutableStateOf(0f) }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableIntStateOf(-1) }
    var answerState by remember { mutableStateOf("none") } // none, correct, wrong
    var allQuizDone by remember { mutableStateOf(false) }

    // Video progress animation
    if (videoPlaying && !videoWatched) {
        LaunchedEffect(Unit) {
            val steps = 100
            for (i in 1..steps) {
                delay(100)
                videoProgress = i / steps.toFloat()
            }
            videoWatched = true
            videoPlaying = false
        }
    }

    // Auto-advance after correct answer
    if (answerState == "correct") {
        LaunchedEffect(currentQuestionIndex) {
            delay(800)
            if (currentQuestionIndex < module.questions.size - 1) {
                currentQuestionIndex++
                selectedOptionIndex = -1
                answerState = "none"
            } else {
                allQuizDone = true
            }
        }
    }

    // Auto-complete after quiz done
    if (allQuizDone) {
        LaunchedEffect(Unit) {
            delay(1000)
            onComplete()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t(module.titleHi, module.titleEn),
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- Video Section ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1025)),
                contentAlignment = Alignment.Center,
            ) {
                if (videoWatched) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\u2705", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t("Video देखा गया", "Video watched"),
                            fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                        )
                    }
                } else if (videoPlaying) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp),
                    ) {
                        Text("\uD83C\uDFAC", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t("चल रहा है...", "Playing..."),
                            fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f),
                        )
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { videoProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = WiomPrimary,
                            trackColor = Color.White.copy(alpha = 0.2f),
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\u25B6\uFE0F", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t(module.titleHi, module.titleEn),
                            fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (!videoWatched && !videoPlaying) {
                WiomButton(
                    t("Video देखें", "Watch Video"),
                    onClick = { videoPlaying = true },
                )
            }

            if (videoWatched && !videoPlaying) {
                // Green badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiomPositive100)
                        .border(1.dp, WiomPositive300, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("\u2713", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WiomPositive)
                    Text(
                        t("Video देखा गया", "Video watched"),
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomPositive,
                    )
                }
            }

            // --- Quiz Section ---
            if (videoWatched && !allQuizDone) {
                Spacer(Modifier.height(20.dp))
                Text(
                    t("Quiz \u2014 ${module.questions.size} सवाल", "Quiz \u2014 ${module.questions.size} Questions"),
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText,
                )
                Spacer(Modifier.height(12.dp))

                val question = module.questions[currentQuestionIndex]

                // Question text
                WiomCard {
                    Text(
                        t(question.questionHi, question.questionEn),
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = WiomText,
                        lineHeight = 22.sp,
                    )
                }
                Spacer(Modifier.height(12.dp))

                // Options
                question.options.forEachIndexed { index, option ->
                    val isSelected = selectedOptionIndex == index
                    val isCorrectAnswer = index == question.correctIndex

                    val bgColor = when {
                        answerState == "correct" && isCorrectAnswer -> WiomPositive100
                        answerState == "wrong" && isSelected -> WiomNegative100
                        isSelected -> WiomPrimaryLight
                        else -> Color.White
                    }
                    val borderColor = when {
                        answerState == "correct" && isCorrectAnswer -> WiomPositive
                        answerState == "wrong" && isSelected -> WiomNegative
                        isSelected -> WiomPrimary
                        else -> WiomBorder
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = answerState == "none") {
                                selectedOptionIndex = index
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Radio indicator
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        answerState == "correct" && isCorrectAnswer -> WiomPositive
                                        answerState == "wrong" && isSelected -> WiomNegative
                                        isSelected -> WiomPrimary
                                        else -> WiomBgSec
                                    }
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (answerState == "correct" && isCorrectAnswer) {
                                Text("\u2713", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            } else if (answerState == "wrong" && isSelected) {
                                Text("\u2717", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            } else if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }

                        Text(
                            t(option.first, option.second),
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = WiomText,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Check Answer / Try Again button
                if (answerState == "none" && selectedOptionIndex >= 0) {
                    Spacer(Modifier.height(4.dp))
                    WiomButton(
                        t("जवाब चेक करें", "Check Answer"),
                        onClick = {
                            answerState = if (selectedOptionIndex == question.correctIndex) "correct" else "wrong"
                        },
                    )
                }

                // Wrong answer hint
                if (answerState == "wrong") {
                    Spacer(Modifier.height(8.dp))
                    ErrorCard(
                        icon = "\uD83D\uDCA1",
                        titleHi = "सही नहीं",
                        titleEn = "Not correct",
                        messageHi = question.hintHi,
                        messageEn = question.hintEn,
                        type = "warning",
                    )
                    Spacer(Modifier.height(8.dp))
                    WiomButton(
                        t("फिर से कोशिश करें", "Try Again"),
                        onClick = {
                            selectedOptionIndex = -1
                            answerState = "none"
                        },
                        isSecondary = true,
                    )
                }
            }

            // Quiz completed
            if (allQuizDone) {
                Spacer(Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(WiomPositive100)
                        .border(1.dp, WiomPositive300, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83C\uDF89", fontSize = 36.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("Quiz पास!", "Quiz Passed!"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        t("सभी जवाब सही हैं!", "All answers correct!"),
                        fontSize = 14.sp, color = WiomTextSec,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // Bottom bar with progress
        BottomBar {
            if (videoWatched && !allQuizDone) {
                Text(
                    t("सवाल ${currentQuestionIndex + 1}/${module.questions.size}", "Question ${currentQuestionIndex + 1}/${module.questions.size}"),
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomTextSec,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else if (!videoWatched) {
                Text(
                    t("Video देखें", "Watch the video"),
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomTextSec,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Text(
                    t("पूरा हो गया!", "Completed!"),
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomPositive,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// ─── Screen 15: Policy Quiz ──────────────────────────────────────────────────
@Composable
fun PolicyQuizScreen(onNext: () -> Unit) {
    data class PolicyQuestion(
        val questionHi: String,
        val questionEn: String,
        val options: List<Pair<String, String>>,
        val correctIndex: Int,
    )

    val questions = remember {
        listOf(
            PolicyQuestion(
                "नए कनेक्शन पर कमीशन कितना है?",
                "New connection commission?",
                listOf("\u20B9150" to "\u20B9150", "\u20B9200" to "\u20B9200", "\u20B9300" to "\u20B9300", "\u20B9500" to "\u20B9500"),
                2,
            ),
            PolicyQuestion(
                "ग्राहक शिकायत का resolution time?",
                "Complaint resolution time?",
                listOf("2 घंटे" to "2 hrs", "4 घंटे" to "4 hrs", "12 घंटे" to "12 hrs", "24 घंटे" to "24 hrs"),
                1,
            ),
            PolicyQuestion(
                "Minimum uptime requirement?",
                "Min uptime?",
                listOf("85%" to "85%", "90%" to "90%", "95%" to "95%", "99%" to "99%"),
                2,
            ),
            PolicyQuestion(
                "Payout कब होता है?",
                "Payout when?",
                listOf("Daily" to "Daily", "Weekly Mon" to "Weekly Mon", "Monthly" to "Monthly", "Bi-weekly" to "Bi-weekly"),
                1,
            ),
            PolicyQuestion(
                "रिचार्ज कमीशन कितना है?",
                "Recharge commission?",
                listOf("\u20B9100" to "\u20B9100", "\u20B9200" to "\u20B9200", "\u20B9300" to "\u20B9300", "\u20B9400" to "\u20B9400"),
                2,
            ),
        )
    }

    var currentQ by remember { mutableIntStateOf(0) }
    var selectedIdx by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var answered by remember { mutableStateOf(false) }
    var quizFinished by remember { mutableStateOf(OnboardingState.policyQuizPassed) }
    var showResult by remember { mutableStateOf(false) }

    if (quizFinished && OnboardingState.policyQuizPassed) {
        // Already passed — show pass screen
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(
                title = t("Wiom नीति प्रश्नोत्तरी", "Policy Quiz"),
                rightText = t("स्टेप 6/7", "Step 6/7"),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(32.dp))
                Text("\uD83C\uDF89", fontSize = 48.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    t("बधाई हो! प्रश्नोत्तरी पास!", "Congratulations! Quiz Passed!"),
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                WiomCard {
                    Text(t("आपका स्कोर", "Your Score"), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomText)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${OnboardingState.policyQuizScore}/5",
                        fontSize = 32.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                    )
                    Spacer(Modifier.height(8.dp))
                    WiomProgressBar(OnboardingState.policyQuizScore / 5f)
                }
            }
            BottomBar {
                WiomButton(
                    t("लाइव हो जाएं!", "Go Live!"),
                    onClick = onNext,
                )
            }
        }
        return
    }

    if (showResult) {
        // Show result screen
        val passed = score >= 4
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(
                title = t("Wiom नीति प्रश्नोत्तरी", "Policy Quiz"),
                rightText = t("स्टेप 6/7", "Step 6/7"),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(32.dp))
                Text(if (passed) "\uD83C\uDF89" else "\uD83D\uDE14", fontSize = 48.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    if (passed) t("बधाई हो! प्रश्नोत्तरी पास!", "Congratulations! Quiz Passed!")
                    else t("प्रश्नोत्तरी पास नहीं हुई", "Quiz not passed"),
                    fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = if (passed) WiomPositive else WiomNegative,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                WiomCard {
                    Text(t("आपका स्कोर", "Your Score"), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomText)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "$score/5",
                        fontSize = 32.sp, fontWeight = FontWeight.Bold,
                        color = if (passed) WiomPositive else WiomNegative,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        t("पास होने के लिए 4/5 ज़रूरी (80%)", "Need 4/5 to pass (80%)"),
                        fontSize = 13.sp, color = WiomTextSec,
                    )
                    Spacer(Modifier.height(8.dp))
                    WiomProgressBar(score / 5f)
                }
            }
            BottomBar {
                if (passed) {
                    WiomButton(
                        t("लाइव हो जाएं!", "Go Live!"),
                        onClick = {
                            OnboardingState.policyQuizScore = score
                            OnboardingState.policyQuizPassed = true
                            onNext()
                        },
                    )
                } else {
                    WiomButton(
                        t("दोबारा कोशिश करें", "Try Again"),
                        onClick = {
                            currentQ = 0
                            selectedIdx = -1
                            score = 0
                            answered = false
                            showResult = false
                        },
                    )
                }
            }
        }
        return
    }

    // Quiz in progress
    val question = questions[currentQ]

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("Wiom नीति प्रश्नोत्तरी", "Policy Quiz"),
            rightText = t("स्टेप 6/7", "Step 6/7"),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Progress bar
            Text(
                t("सवाल ${currentQ + 1}/5", "Question ${currentQ + 1}/5"),
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomTextSec,
            )
            Spacer(Modifier.height(4.dp))
            WiomProgressBar((currentQ + 1) / 5f)
            Spacer(Modifier.height(20.dp))

            // Question
            WiomCard {
                Text(
                    t(question.questionHi, question.questionEn),
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText,
                    lineHeight = 24.sp,
                )
            }
            Spacer(Modifier.height(16.dp))

            // Options
            question.options.forEachIndexed { index, option ->
                val isSelected = selectedIdx == index
                val bgColor = if (isSelected) WiomPrimaryLight else Color.White
                val borderClr = if (isSelected) WiomPrimary else WiomBorder

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .border(1.5.dp, borderClr, RoundedCornerShape(12.dp))
                        .clickable(enabled = !answered) { selectedIdx = index }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) WiomPrimary else WiomBgSec),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                    Text(
                        t(option.first, option.second),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = WiomText,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        BottomBar {
            WiomButton(
                if (currentQ < 4) t("अगला सवाल", "Next Question")
                else t("परिणाम देखें", "See Results"),
                onClick = {
                    if (selectedIdx == question.correctIndex) score++
                    if (currentQ < 4) {
                        currentQ++
                        selectedIdx = -1
                    } else {
                        // Add the final score
                        val finalScore = score
                        OnboardingState.policyQuizScore = finalScore
                        showResult = true
                    }
                },
                enabled = selectedIdx >= 0,
            )
        }
    }
}

// ─── Screen 16: Go Live! ─────────────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoLiveScreen() {
    var activeAction by remember { mutableStateOf<String?>(null) }

    if (activeAction != null) {
        QuickActionDetailScreen(
            actionType = activeAction!!,
            onBack = { activeAction = null },
        )
    } else {
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(
                title = t("पार्टनर ऐप होम", "Partner App Home"),
                rightText = t("स्टेप 7/7", "Step 7/7"),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83C\uDF89", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("बधाई हो!", "Congratulations!"),
                        fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WiomText,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        t("आप अब Wiom Partner हैं", "You are now a Wiom Partner"),
                        fontSize = 14.sp, color = WiomTextSec,
                    )
                    Spacer(Modifier.height(16.dp))
                    // Status chips (9 completion chips)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        WiomChip("\u2713 ${t("पंजीकृत", "Registered")}")
                        WiomChip("\u2713 ${t("KYC सत्यापित", "KYC Verified")}")
                        WiomChip("\u2713 ${t("बैंक सत्यापित", "Bank Verified")}")
                        WiomChip("\u2713 ${t("ISP अनुबंध", "ISP Agreement")}")
                        WiomChip("\u2713 ${t("फ़ोटो जमा", "Photos Submitted")}")
                        WiomChip("\u2713 ${t("सत्यापन स्वीकृत", "Verification Approved")}")
                        WiomChip("\u2713 ${t("तकनीकी मूल्यांकन", "Tech Assessment")}")
                        WiomChip("\u2713 ${t("खाता सेटअप", "Account Setup")}")
                        WiomChip("\u2713 ${t("प्रशिक्षित", "Trained")}")
                    }
                }
                SectionHeader(t("क्विक एक्शन", "QUICK ACTIONS"))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    QuickActionCard("\uD83D\uDC64", t("ग्राहक जोड़ें", "Add Customer"), t("नया कनेक्शन", "New connection"), onClick = { activeAction = "customer" })
                    Spacer(Modifier.height(8.dp))
                    QuickActionCard("\uD83D\uDCB0", t("कमाई देखें", "View Earnings"), "Commission, TDS", onClick = { activeAction = "earnings" })
                    Spacer(Modifier.height(8.dp))
                    QuickActionCard("\uD83D\uDCDD", t("कार्य", "Tasks"), t("रिस्टोर, शिकायतें", "Restore, complaints"), onClick = { activeAction = "tasks" })
                    Spacer(Modifier.height(8.dp))
                    QuickActionCard("\uD83C\uDF93", t("प्रशिक्षण", "Training"), t("Module दोबारा देखें", "Revisit modules"), onClick = { activeAction = "training" })
                    Spacer(Modifier.height(24.dp))

                    // Download Wiom CSP App card
                    WiomCard(
                        borderColor = WiomPrimary,
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "\uD83D\uDE80 ${t("Wiom CSP ऐप डाउनलोड करें", "Download Wiom CSP App")}",
                                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WiomText,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    t("नए कनेक्शन बनाने के लिए Wiom CSP App इस्तेमाल करें", "Use Wiom CSP App to create new connections"),
                                    fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                                )
                                Spacer(Modifier.height(8.dp))
                                WiomButton(
                                    t("Wiom CSP ऐप डाउनलोड करें", "Download Wiom CSP App"),
                                    onClick = {},
                                    modifier = Modifier.wrapContentWidth(),
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickActionDetailScreen(actionType: String, onBack: () -> Unit) {
    val (headerHi, headerEn, videoTitle, infoText, buttonHi, buttonEn) = when (actionType) {
        "customer" -> ActionDetail(
            "ग्राहक जोड़ें", "Add Customer",
            t("Customer कैसे जोड़ें — Tutorial", "How to add Customer — Tutorial"),
            t("यहां से नया कनेक्शन बनाएं", "Create new connection from here"),
            "Wiom CSP App खोलें", "Open Wiom CSP App",
        )
        "earnings" -> ActionDetail(
            "कमाई देखें", "View Earnings",
            t("Commission और TDS — Tutorial", "Commission and TDS — Tutorial"),
            t("अपनी कमाई, TDS certificate, settlements यहां देखें", "View your earnings, TDS certificate, settlements here"),
            "Wiom CSP App खोलें", "Open Wiom CSP App",
        )
        "tasks" -> ActionDetail(
            "कार्य", "Tasks",
            t("Restore और शिकायतें — Tutorial", "Restore and complaints — Tutorial"),
            t("Customer complaints, restore requests यहां मैनेज करें", "Manage customer complaints, restore requests here"),
            "Wiom CSP App खोलें", "Open Wiom CSP App",
        )
        "training" -> ActionDetail(
            "प्रशिक्षण", "Training",
            t("Training Modules — Revisit", "Training Modules — Revisit"),
            t("सभी training modules दोबारा देख सकते हैं", "You can revisit all training modules"),
            "Modules देखें", "View Modules",
        )
        else -> return
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t(headerHi, headerEn),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Video placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(WiomDark),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("\u25B6", fontSize = 40.sp, color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        videoTitle,
                        fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            InfoBox("\uD83D\uDCA1", infoText)
        }
    }
}

private data class ActionDetail(
    val headerHi: String,
    val headerEn: String,
    val videoTitle: String,
    val infoText: String,
    val buttonHi: String,
    val buttonEn: String,
)
