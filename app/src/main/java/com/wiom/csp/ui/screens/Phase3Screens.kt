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
import com.wiom.csp.ui.viewmodel.*
import com.wiom.csp.util.t
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

/**
 * Phase 3 Screens — Activation (Screens 10-14)
 * Matches prototype exactly.
 *
 * Screen 10: Policy & SLA (Step 1/7, header "Important Terms")
 * Screen 11: Technical Assessment (Step 2/7, BEFORE onboarding fee)
 * Screen 12: Onboarding Fee ₹20,000 (Step 3/7, header "Activation")
 * Screen 13: Account Setup (Step 4/5, auto-progress, no CTA)
 * Screen 14: Successfully Onboarded (Step 5/5)
 */

// ═══════════════════════════════════════════════════════════════════════════
// Screen 10: Policy & SLA
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun PolicySlaScreen(onNext: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("महत्वपूर्ण शर्तें", "Important Terms"),
            onBack = onBack,
            rightText = t("स्टेप 1/7", "Step 1/7"),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Subheading
            Text(
                "\uD83D\uDCCB ${t("Wiom नीति और सेवा स्तर अनुबंध", "Wiom's Policy and Service Level Agreement")}",
                fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("कृपया नीचे दी गई शर्तें ध्यान से पढ़ें", "Please read the terms below carefully"),
                fontSize = 13.sp, color = WiomTextSec, lineHeight = 18.sp,
            )
            Spacer(Modifier.height(14.dp))

            // Commission card
            WiomCard {
                Text(t("कमीशन", "COMMISSION"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomTextSec, letterSpacing = 0.5.sp)
                Spacer(Modifier.height(8.dp))
                FeeRow(t("नया कनेक्शन", "New Connection"), "₹300", valueColor = WiomPositive)
                HorizontalDivider(color = WiomBorder)
                FeeRow(t("रीचार्ज", "Recharge"), "₹300", valueColor = WiomPositive)
            }
            Spacer(Modifier.height(8.dp))

            // Payout card
            WiomCard {
                Text(t("भुगतान", "PAYOUT"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomTextSec, letterSpacing = 0.5.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    t("हर सोमवार, सुबह 10 बजे तक", "Every Monday by 10 AM"),
                    fontSize = 14.sp, color = WiomTextSec,
                )
            }
            Spacer(Modifier.height(8.dp))

            // Service Levels card
            WiomCard {
                Text(t("सेवा स्तर बनाए रखें", "SERVICE LEVELS TO BE MAINTAINED"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomTextSec, letterSpacing = 0.5.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "• ${t("शिकायत: 4 घंटे", "Complaints: 4hr")}\n" +
                    "• ${t("अपटाइम: 95%+", "Uptime: 95%+")}\n" +
                    "• ${t("उपकरण देखभाल", "Equipment care")}\n" +
                    "• ${t("ब्रांड अनुपालन", "Brand compliance")}",
                    fontSize = 14.sp, color = WiomTextSec, lineHeight = 22.sp,
                )
            }
        }
        BottomBar {
            WiomButton(t("समझ गया, आगे बढ़ें", "Understood, proceed"), onClick = onNext)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Screen 11: Technical Assessment (now BEFORE Onboarding Fee)
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun TechAssessmentScreen(viewModel: TechAssessmentViewModel, onNext: () -> Unit) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("तकनीकी मूल्यांकन", "Technical Assessment"),
            rightText = t("स्टेप 2/7", "Step 2/7"),
        )

        if (state.status == TechAssessmentStatus.REJECTED) {
            // ─── Rejected state ───
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))
                Text("\uD83D\uDE14", fontSize = 40.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    t("प्रोफ़ाइल अभी स्वीकृत नहीं हुई", "Profile not accepted yet"),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    t("तकनीकी मूल्यांकन पास नहीं हो पाया", "Technical assessment could not be passed"),
                    fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center, lineHeight = 20.sp,
                )
                Spacer(Modifier.height(16.dp))
                // Reason card
                WiomCard(borderColor = WiomNegative, backgroundColor = WiomNegative100) {
                    Text(t("कारण", "Reason"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomNegative)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        state.rejectionReason.ifEmpty { t("इंफ्रास्ट्रक्चर तैयार नहीं", "Infrastructure not ready") },
                        fontSize = 14.sp, color = WiomText,
                    )
                }
                Spacer(Modifier.height(8.dp))
                // No refund notice
                WiomCard(borderColor = WiomWarning, backgroundColor = WiomWarning200) {
                    Text(
                        t("इस समय कोई रिफंड नहीं किया जाएगा", "No refund will be done at this moment"),
                        fontSize = 13.sp, color = WiomText, lineHeight = 18.sp,
                    )
                }
            }
            BottomBar {
                WiomButton(t("हमसे बात करें", "Talk to Us"), onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:7836811111"))
                    context.startActivity(intent)
                })
            }
        } else {
            // ─── Pending/In progress state ───
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(16.dp))
                    Text("\uD83D\uDD27", fontSize = 40.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("तकनीकी मूल्यांकन जारी है", "Technical Assessment in progress"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("हमारी तकनीकी टीम जांच कर रही है", "Our technical team is reviewing"),
                        fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(16.dp))
                // Checklist
                ChecklistItem(t("इंफ्रास्ट्रक्चर रिव्यू", "Infrastructure Review"), isWaiting = true)
                ChecklistItem(t("नेटवर्क रेडीनेस", "Network Readiness"), isWaiting = true)
                ChecklistItem(t("स्थान व्यवहार्यता", "Location Feasibility"), isWaiting = true, isLast = true)

                Column(modifier = Modifier.padding(16.dp)) {
                    InfoBox("⏳", t("4-5 कार्य दिवस", "May take 4-5 business days"), type = InfoBoxType.WARNING)
                    Spacer(Modifier.height(8.dp))
                    InfoBox("📞", t("अगले चरणों के लिए हमारी नेटवर्क क्वालिटी टीम आपको कॉल भी करेगी", "You will also receive a call from our Network Quality team for next steps"))

                    // ─── Prototype test buttons (hidden in production) ───
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("⚙️ प्रोटोटाइप कंट्रोल", "⚙️ Prototype Controls"),
                        fontSize = 11.sp, color = WiomHint, fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    WiomButton(
                        "✓ ${t("मूल्यांकन पास", "Assessment Passed")}",
                        onClick = { viewModel.setApproved(); onNext() },
                        backgroundColor = WiomPositive,
                    )
                    Spacer(Modifier.height(8.dp))
                    WiomButton(
                        "✗ ${t("मूल्यांकन अस्वीकृत", "Assessment Rejected")}",
                        onClick = { viewModel.setRejected() },
                        isSecondary = true,
                        textColor = WiomNegative,
                        backgroundColor = WiomNegative100,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Screen 12: Onboarding Fee ₹20,000 (now AFTER Technical Assessment)
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun OnboardingFeeScreen(viewModel: PaymentViewModel, onNext: () -> Unit) {
    val scenario = OnboardingState.activeScenario
    var showSuccess by remember { mutableStateOf(false) }

    // Auto-progress after success
    if (showSuccess) {
        LaunchedEffect(Unit) {
            delay(3000)
            showSuccess = false
            onNext()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(
                title = t("एक्टिवेशन", "Activation"),
                rightText = t("स्टेप 3/7", "Step 3/7"),
            )

            when {
                scenario == Scenario.ONBOARDFEE_FAILED -> {
                    // ─── Payment Failed ───
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text("\uD83D\uDE1F", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("भुगतान नहीं हो पाया", "Payment failed"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t("चिंता न करें — आपका पैसा सुरक्षित है", "Don't worry — your money is safe"),
                            fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                            Text(
                                "\uD83D\uDEE1\uFE0F ${t("पैसा कटा नहीं है", "No money was deducted")}",
                                fontWeight = FontWeight.Bold, fontSize = 12.sp, color = WiomPositive,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                t("बैंक या UPI ऐप से कनेक्शन में दिक़्क़त हुई — आपकी कोई गलती नहीं है", "There was a connection issue with the bank or UPI app — this is not your fault"),
                                fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                            )
                        }
                    }
                    BottomBar {
                        WiomButton(t("दोबारा भुगतान करें", "Retry Payment"), onClick = { OnboardingState.clearScenario() })
                        Spacer(Modifier.height(8.dp))
                        WiomButton(t("हमसे बात करें", "Talk to Us"), onClick = {}, isSecondary = true)
                    }
                }

                scenario == Scenario.ONBOARDFEE_TIMEOUT -> {
                    // ─── Payment Timeout ───
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text("⏳", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("भुगतान लंबित है", "Payment is pending"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomWarning700,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t("बैंक से जवाब देर से आ रहा है। कभी-कभी 2-5 मिनट लग सकते हैं।", "Bank response is delayed. Sometimes it can take 2-5 minutes."),
                            fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center, lineHeight = 20.sp,
                        )
                        Spacer(Modifier.height(16.dp))
                        WiomCard(borderColor = WiomBorderInput, backgroundColor = Color.White) {
                            Text(t("लेन-देन विवरण", "Transaction Details"), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomText)
                            Spacer(Modifier.height(10.dp))
                            FeeRow(t("राशि", "Amount"), "₹20,000")
                            HorizontalDivider(color = WiomBorder)
                            FeeRow("UPI Ref", "UPI83746251")
                        }
                    }
                    BottomBar {
                        WiomButton(t("Status Refresh करें", "Refresh Status"), onClick = { OnboardingState.clearScenario() }, backgroundColor = WiomInfo)
                        Spacer(Modifier.height(8.dp))
                        WiomButton(t("हमसे बात करें", "Talk to Us"), onClick = {}, isSecondary = true)
                    }
                }

                else -> {
                    // ─── Normal: Pay ₹20,000 ───
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)
                    ) {
                        Text(
                            "\uD83D\uDCB3 ${t("ऑनबोर्डिंग शुल्क", "Onboarding Fee")}",
                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t("यह राशि आपके व्यापार स्थान पर WiFi उपकरण भेजने के लिए आवश्यक है", "This amount is required to send WiFi devices to your business location"),
                            fontSize = 13.sp, color = WiomTextSec, lineHeight = 18.sp,
                        )
                        Spacer(Modifier.height(14.dp))
                        AmountBox("₹20,000", t("अभी भुगतान करें", "Pay Now"))
                        Spacer(Modifier.height(12.dp))

                        // Investment Summary
                        WiomCard {
                            Text(t("निवेश विवरण", "INVESTMENT SUMMARY"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomTextSec, letterSpacing = 0.5.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(t("पंजीकरण शुल्क", "Reg Fee"), fontSize = 13.sp, color = WiomTextSec)
                                Text("₹2,000 ✓ ${t("भुगतान हुआ", "Paid")}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPositive)
                            }
                            HorizontalDivider(color = WiomBorder)
                            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(t("ऑनबोर्डिंग शुल्क", "Onboarding Fee"), fontSize = 13.sp, color = WiomTextSec)
                                Text("₹20,000 ${t("बकाया", "Due")}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary)
                            }
                            HorizontalDivider(color = WiomBorder)
                            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(t("कुल निवेश", "Total Investment"), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomText)
                                Text("₹22,000", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomText)
                            }
                        }
                    }
                    BottomBar {
                        WiomButton(t("₹20,000 अभी भुगतान करें", "Pay ₹20,000 Now"), onClick = { showSuccess = true })
                    }
                }
            }
        }

        // Payment success overlay (shown on top of the normal content)
        if (showSuccess) {
            PaymentSuccessOverlay("₹20,000")
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Screen 13: Account Setup (auto-progress, no CTA)
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun AccountSetupScreen(viewModel: AccountSetupViewModel, onNext: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    // Auto-start setup on first composition
    LaunchedEffect(Unit) {
        viewModel.startSetup(onNext)
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("एक्टिवेशन", "Activation"),
            rightText = t("स्टेप 4/5", "Step 4/5"),
        )

        when (state.state) {
            AccountSetupState.LOADING -> {
                // Loading spinner
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("⚙\uFE0F", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("खाता सेटअप जारी है", "Account Setup in Progress"),
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${t("${state.businessName} के लिए खाता तैयार किया जा रहा है", "Setting up account for ${state.businessName}")}",
                        fontSize = 14.sp, color = WiomTextSec,
                    )
                    Spacer(Modifier.height(24.dp))
                    CircularProgressIndicator(color = WiomPrimary, modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                }
            }

            AccountSetupState.FAILED -> {
                Column(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("❌", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("खाता सेटअप विफल", "Account Setup Failed"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("तकनीकी समस्या के कारण खाता सेटअप नहीं हो पाया", "Account setup failed due to a technical issue"),
                        fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center, lineHeight = 20.sp,
                    )
                }
                BottomBar {
                    WiomButton(t("दोबारा कोशिश करें", "Retry"), onClick = { viewModel.retry(onNext) })
                    Spacer(Modifier.height(8.dp))
                    WiomButton(t("हमसे बात करें", "Talk to Us"), onClick = {}, isSecondary = true)
                }
            }

            AccountSetupState.PENDING -> {
                Column(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("⏳", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("खाता सेटअप लंबित है", "Account Setup Pending"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomWarning,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("खाता सेटअप अभी प्रोसेस हो रहा है", "Account setup is being processed"),
                        fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center, lineHeight = 20.sp,
                    )
                }
                BottomBar {
                    WiomButton(t("स्टेटस रिफ्रेश करें", "Refresh Status"), onClick = { viewModel.refreshStatus(onNext) }, backgroundColor = WiomInfo)
                    Spacer(Modifier.height(8.dp))
                    WiomButton(t("हमसे बात करें", "Talk to Us"), onClick = {}, isSecondary = true)
                }
            }

            AccountSetupState.COMPLETED -> {
                // Brief success before auto-navigate
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("✅", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("खाता सेटअप पूरा!", "Account Setup Complete!"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Screen 14: Successfully Onboarded
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun SuccessfullyOnboardedScreen() {
    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("एक्टिवेशन", "Activation"),
            rightText = t("स्टेप 5/5", "Step 5/5"),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Celebration
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))
                Text("\uD83C\uDF89", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    t("बधाई हो, राजेश!", "Congratulations, Rajesh!"),
                    fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WiomText,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    t("आप अब Wiom कनेक्शन सर्विस प्रोवाइडर हैं", "You are now a Wiom Connection Service Provider"),
                    fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center, lineHeight = 20.sp,
                )
            }

            Spacer(Modifier.height(20.dp))

            // Next Steps
            Text(
                t("अगले कदम", "Next Steps"),
                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(8.dp))
            WiomCard(borderColor = WiomPrimary, backgroundColor = WiomPrimaryLight) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83D\uDCF2", fontSize = 32.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("Wiom Partner Plus ऐप डाउनलोड करें", "Download Wiom Partner Plus App"),
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { /* Open Play Store */ },
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WiomPrimary),
                    ) {
                        Text(
                            t("अभी इंस्टॉल करें", "Install Now"),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Important Instructions
            Text(
                t("महत्वपूर्ण निर्देश", "Important Instructions"),
                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = WiomBgSec,
            ) {
                Column(modifier = Modifier.padding(12.dp, 14.dp)) {
                    Text(
                        "2. ${t("Wiom Partner Plus ऐप में लॉगिन करें", "Login to Wiom Partner Plus app")}",
                        fontSize = 13.sp, color = WiomText, lineHeight = 22.sp,
                    )
                    Text(
                        "3. ${t("सभी आवश्यक अनुमतियां दें", "Allow all required permissions")}",
                        fontSize = 13.sp, color = WiomText, lineHeight = 22.sp,
                    )
                    Text(
                        "4. ${t("अनिवार्य Wiom प्रशिक्षण पूरा करें", "Complete Mandatory Wiom Training")}",
                        fontSize = 13.sp, color = WiomText, lineHeight = 22.sp,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Helper: Fee Row
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun FeeRow(label: String, amount: String, isBold: Boolean = false, valueColor: Color = WiomText) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, fontSize = 14.sp, color = if (isBold) WiomText else WiomTextSec, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
