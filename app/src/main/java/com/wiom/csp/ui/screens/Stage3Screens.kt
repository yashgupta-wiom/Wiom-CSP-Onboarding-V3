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
import com.wiom.csp.domain.model.VerificationStatus
import com.wiom.csp.ui.components.*
import com.wiom.csp.ui.theme.*
import com.wiom.csp.ui.viewmodel.*
import com.wiom.csp.util.t
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════════
// Screen 10: Policy & SLA
// ═══════════════════════════════════════════════════════════════════
@Composable
fun PolicySlaScreen(onNext: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("नीतियां और SLA", "Policy & SLA"),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                t("नीतियां और कमीशन", "Policies & Commission"),
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("Verification Approved ✓ — अब policies समझें", "Verification Approved ✓ — now review policies"),
                fontSize = 14.sp, color = WiomTextSec,
            )
            Spacer(Modifier.height(16.dp))

            // Commission Structure
            WiomCard {
                Text(
                    t("कमीशन संरचना", "COMMISSION STRUCTURE"),
                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomTextSec,
                    letterSpacing = 0.5.sp,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(t("नया कनेक्शन", "New Connection"), fontSize = 14.sp, color = WiomTextSec)
                    Text("₹300/${t("कनेक्शन", "conn")}", fontWeight = FontWeight.Bold, color = WiomPositive, fontSize = 14.sp)
                }
                HorizontalDivider(color = WiomBorder)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(t("रिचार्ज कमीशन", "Recharge Commission"), fontSize = 14.sp, color = WiomTextSec)
                    Text("₹300", fontWeight = FontWeight.Bold, color = WiomPositive, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(8.dp))

            // SLA Terms
            WiomCard {
                Text(
                    t("SLA शर्तें", "SLA TERMS"),
                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomTextSec,
                    letterSpacing = 0.5.sp,
                )
                Spacer(Modifier.height(8.dp))
                val slaItems = listOf(
                    t("ग्राहक शिकायत: 4 घंटे में समाधान", "Customer complaints: 4hr resolution"),
                    t("कनेक्शन 95%+ चालू रहना चाहिए", "Connection 95%+ to be up and running"),
                    t("उपकरण देखभाल की ज़िम्मेदारी", "Equipment care responsibility"),
                    t("Wiom ब्रांड गाइडलाइन का पालन", "Wiom brand guidelines compliance"),
                )
                slaItems.forEach { item ->
                    Text(
                        "• $item",
                        fontSize = 14.sp, color = WiomTextSec,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(vertical = 1.dp),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            // Payout Terms
            WiomCard {
                Text(
                    t("पेआउट शर्तें", "PAYOUT TERMS"),
                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomTextSec,
                    letterSpacing = 0.5.sp,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    t("हर सोमवार, सीधे आपके बैंक खाते में RazorpayX के माध्यम से", "Every Monday, directly to your bank account via RazorpayX"),
                    fontSize = 14.sp, color = WiomTextSec, lineHeight = 22.sp,
                )
            }
            Spacer(Modifier.height(8.dp))

            InfoBox("💰", t("Commission payouts हर Monday, सीधे आपके बैंक में", "Commission payouts every Monday, directly to your bank"), type = InfoBoxType.SUCCESS)
        }
        BottomBar {
            WiomButton(t("समझ गया, आगे बढ़ें", "Understood, proceed"), onClick = onNext)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Screen 11: Onboarding Fee ₹20,000
// ═══════════════════════════════════════════════════════════════════
@Composable
fun OnboardingFeeScreen(viewModel: PaymentViewModel, onNext: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setAmount(20000)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(title = t("ऑनबोर्डिंग फ़ीस", "Onboarding Fee"))

            when (uiState.result) {
                PaymentResult.FAILED -> {
                    // ─── Payment failed error screen ───
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
                                "✅ ${t("पैसा कटा नहीं है", "No money deducted")}",
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
                                Text("₹20,000", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomText)
                            }
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Error", fontSize = 13.sp, color = WiomTextSec)
                                Text(uiState.errorMessage ?: "UPI_LIMIT_EXCEEDED", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomNegative)
                            }
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Time", fontSize = 13.sp, color = WiomTextSec)
                                Text("just now", fontSize = 13.sp, color = WiomText)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoBox("\uD83D\uDCA1", t("UPI limit ₹1L/day — NEFT/RTGS या कार्ड से भुगतान करें", "UPI limit ₹1L/day — try NEFT/RTGS or card"), type = InfoBoxType.WARNING)
                        Spacer(Modifier.height(16.dp))
                        WiomButton(t("दोबारा भुगतान करें", "Retry Payment"), onClick = {
                            viewModel.retry()
                            viewModel.initiatePayment { onNext() }
                        })
                        Spacer(Modifier.height(8.dp))
                        WiomButton(t("बाद में करें", "Pay Later"), onClick = { viewModel.retry() }, isSecondary = true)
                    }
                }

                PaymentResult.TIMEOUT -> {
                    // ─── Payment timeout error screen ───
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(24.dp))
                        Text("⏳", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("भुगतान समय सीमा पार हो गई", "Payment timed out"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomWarning700,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                            Text(
                                "✅ ${t("पैसा कटा नहीं है", "No money deducted")}",
                                fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomPositive,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                t("चिंता न करें — आपके अकाउंट से कोई पैसा नहीं कटा है।", "Don't worry — no money has been deducted from your account."),
                                fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoBox("\uD83D\uDCA1", uiState.errorMessage ?: t("भुगतान समय सीमा पार हो गई, कृपया पुनः प्रयास करें", "Payment timed out, please try again"), type = InfoBoxType.WARNING)
                        Spacer(Modifier.height(16.dp))
                        WiomButton(t("दोबारा भुगतान करें", "Retry Payment"), onClick = {
                            viewModel.retry()
                            viewModel.initiatePayment { onNext() }
                        })
                        Spacer(Modifier.height(8.dp))
                        WiomButton(t("बाद में करें", "Pay Later"), onClick = { viewModel.retry() }, isSecondary = true)
                    }
                }

                else -> {
                    // ─── Normal happy path (NONE or SUCCESS) ───
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
                        AmountBox("₹20,000", t("ऑनबोर्डिंग फ़ीस (GST सहित)", "Onboarding Fee (incl. GST)"))
                        Spacer(Modifier.height(12.dp))
                        WiomCard {
                            Text("✓ ${t("विवरण", "Details")}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomText)
                            Spacer(Modifier.height(8.dp))
                            FeeRow(t("रजिस्ट्रेशन फ़ीस (भुगतान हुआ)", "Registration Fee (paid)"), "₹2,000")
                            HorizontalDivider(color = WiomBorder)
                            FeeRow(t("ऑनबोर्डिंग फ़ीस", "Onboarding Fee"), "₹20,000")
                            HorizontalDivider(color = WiomBorder)
                            FeeRow(t("कुल Investment", "Total Investment"), "₹22,000", isBold = true)
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoBox("✓", t("भुगतान के बाद तकनीकी मूल्यांकन शुरू होगा", "Technical assessment will begin after payment"), type = InfoBoxType.SUCCESS)
                    }
                    BottomBar {
                        WiomButton(
                            "₹20,000 ${t("भुगतान करें", "Pay Now")}",
                            onClick = { viewModel.initiatePayment { onNext() } },
                            enabled = !uiState.isProcessing,
                        )
                    }
                }
            }
        }

        // Loading overlay
        LoadingOverlay(
            messageHi = "भुगतान प्रोसेस हो रहा है...",
            messageEn = "Processing payment...",
            isVisible = uiState.isProcessing,
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

// ═══════════════════════════════════════════════════════════════════
// Screen 12: Technical Assessment (BRANCH POINT 2)
// ═══════════════════════════════════════════════════════════════════
@Composable
fun TechAssessmentScreen(viewModel: TechAssessmentViewModel, onNext: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("तकनीकी मूल्यांकन", "Technical Assessment")
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            when (uiState.status) {
                VerificationStatus.REJECTED -> {
                    // ─── Rejected state ───
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("\uD83D\uDE14", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("तकनीकी मूल्यांकन पास नहीं हुआ", "Technical Assessment not passed"),
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
                                uiState.reason.ifEmpty {
                                    t(
                                        "Location पर network infrastructure उपलब्ध नहीं है। Area में FTTH connectivity तैयार होने पर दोबारा apply कर सकते हैं।",
                                        "Network infrastructure is not available at the location. You can re-apply once FTTH connectivity is ready in the area."
                                    )
                                },
                                fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        WiomCard(borderColor = WiomPositive, backgroundColor = WiomPositive100) {
                            Text("\uD83D\uDD12 ${t("Refund शुरू हो गया", "Refund initiated")}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPositive)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                t("₹20,000 आपके bank account में 5-7 working days में आ जाएगा।", "₹20,000 will be credited to your bank account in 5-7 working days."),
                                fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                            )
                            Text("Ref: RFD-2026-0098", fontSize = 12.sp, color = WiomHint)
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoBox("\uD83D\uDD14", t("जब area तैयार हो, तो दोबारा apply कर सकते हैं।", "You can re-apply when the area is ready."))
                        Spacer(Modifier.height(12.dp))
                        WiomButton(
                            "← ${t("Approved Path देखें", "View Approved Path")}",
                            onClick = {
                                viewModel.resetForReassessment()
                            },
                            isSecondary = true,
                        )
                    }
                }

                VerificationStatus.APPROVED -> {
                    // ─── Approved state ───
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("✅", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("तकनीकी मूल्यांकन स्वीकृत!", "Technical Assessment Approved!"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t("आपका सेटअप तैयार है — अगले चरण पर जाएं", "Your setup is ready — proceed to the next step"),
                            fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        ChecklistItem(
                            t("Infrastructure Review", "Infrastructure Review"),
                            subtitle = t("✓ पास", "✓ Passed"),
                            isDone = true,
                        )
                        ChecklistItem(
                            t("Network Readiness", "Network Readiness"),
                            subtitle = t("✓ पास", "✓ Passed"),
                            isDone = true,
                        )
                        ChecklistItem(
                            t("Location Feasibility", "Location Feasibility"),
                            subtitle = t("✓ पास", "✓ Passed"),
                            isDone = true,
                        )
                        Spacer(Modifier.height(16.dp))
                        InfoBox("✓", t("सभी जांच पास — अब CSP Account Setup होगा", "All checks passed — CSP Account Setup will begin now"), type = InfoBoxType.SUCCESS)
                    }
                    BottomBar {
                        WiomButton(t("आगे बढ़ें", "Continue"), onClick = onNext)
                    }
                }

                VerificationStatus.PENDING -> {
                    // ─── Pending state (waiting for dashboard decision) ───
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("\uD83D\uDEE0\uFE0F", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("तकनीकी मूल्यांकन जारी है", "Technical Assessment in progress"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t("हमारी तकनीकी टीम आपके सेटअप की जांच कर रही है", "Our technical team is reviewing your setup"),
                            fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    ChecklistItem(
                        t("Infrastructure Review", "Infrastructure Review"),
                        subtitle = t("जांच जारी है...", "Under review..."),
                        isDone = false,
                        isWaiting = true,
                    )
                    ChecklistItem(
                        t("Network Readiness", "Network Readiness"),
                        subtitle = t("जांच जारी है...", "Under review..."),
                        isDone = false,
                        isWaiting = true,
                    )
                    ChecklistItem(
                        t("Location Feasibility", "Location Feasibility"),
                        subtitle = t("जांच जारी है...", "Under review..."),
                        isDone = false,
                        isWaiting = true,
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoBox("⏳", t("मूल्यांकन में 2-3 कार्य दिवस लग सकते हैं", "Assessment may take 2-3 business days"), type = InfoBoxType.WARNING)
                        Spacer(Modifier.height(12.dp))
                        // Demo toggle button
                        WiomButton(
                            "\uD83D\uDD04 ${t("Rejected Path देखें", "View Rejected Path")}",
                            onClick = {
                                viewModel.setDecision(
                                    VerificationStatus.REJECTED,
                                    t(
                                        "Location पर network infrastructure उपलब्ध नहीं है। Area में FTTH connectivity तैयार होने पर दोबारा apply कर सकते हैं।",
                                        "Network infrastructure is not available at the location. You can re-apply once FTTH connectivity is ready in the area."
                                    )
                                )
                            },
                            isSecondary = true,
                        )
                        Spacer(Modifier.height(8.dp))
                        // Dev skip button
                        OutlinedButton(
                            onClick = {
                                viewModel.setDecision(VerificationStatus.APPROVED)
                                onNext()
                            },
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                        ) {
                            Text("\uD83D\uDD27 Dev: Skip to Account Setup →", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Screen 13: CSP Account Setup (auto-progression)
// ═══════════════════════════════════════════════════════════════════
@Composable
fun CspAccountSetupScreen(viewModel: AccountSetupViewModel, onNext: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Start the auto-progress setup sequence
    LaunchedEffect(Unit) {
        viewModel.startSetup(onComplete = {})
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("CSP खाता सेटअप", "CSP Account Setup"),
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
                Text("⚙\uFE0F", fontSize = 40.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (uiState.isComplete) t("सेटअप पूरा!", "Setup Complete!")
                    else t("Backend Setup हो रहा है", "Backend Setup in progress"),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    color = if (uiState.isComplete) WiomPositive else WiomText,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    t("System आपका CSP account तैयार कर रहा है", "System is preparing your CSP account"),
                    fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                )
            }

            uiState.items.forEachIndexed { index, item ->
                val isDone = item.status == SetupItemStatus.COMPLETED
                val isProcessing = item.status == SetupItemStatus.IN_PROGRESS

                AnimatedVisibility(
                    visible = item.status != SetupItemStatus.PENDING || index <= (uiState.currentIndex + 1),
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
                                    if (isDone) "✓" else "⋯",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) Color.White else WiomHint,
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(t(item.titleHi, item.titleEn), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = WiomText)
                            Text(
                                if (isProcessing) t("प्रोसेस हो रहा है...", "Processing...")
                                else t(item.titleHi, item.titleEn),
                                fontSize = 12.sp,
                                color = if (isProcessing) WiomWarning700 else WiomTextSec,
                            )
                        }
                    }
                }
            }

            if (uiState.isComplete) {
                Column(modifier = Modifier.padding(16.dp)) {
                    WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                        Text("✓ ${t("सब तैयार है!", "All set!")}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomPositive)
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
            WiomButton(t("Training शुरू करें", "Start Training"), onClick = onNext, enabled = uiState.isComplete)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Screen 14: Training Modules
// ═══════════════════════════════════════════════════════════════════
@Composable
fun TrainingScreen(viewModel: TrainingViewModel, onNext: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.activeModuleId != null) {
        TrainingDetailScreen(
            viewModel = viewModel,
            moduleId = uiState.activeModuleId!!,
            onBack = { viewModel.closeModule() },
            onComplete = {
                viewModel.completeModule(uiState.activeModuleId!!)
            },
        )
    } else {
        TrainingListScreen(viewModel = viewModel, onNext = onNext)
    }
}

@Composable
private fun TrainingListScreen(viewModel: TrainingViewModel, onNext: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val modules = uiState.modules
    val doneCount = uiState.completedModuleIds.size
    val totalCount = modules.size
    val progress = if (totalCount > 0) doneCount.toFloat() / totalCount else 0f

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(title = "\uD83C\uDF93 ${t("ट्रेनिंग", "Training")}", rightText = "$doneCount/$totalCount")

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
                val isDone = viewModel.isModuleCompleted(module.id)
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
                        viewModel.openModule(module.id)
                    },
                )
                Spacer(Modifier.height(8.dp))
            }

            if (!uiState.allModulesCompleted) {
                InfoBox("\uD83D\uDCDD", t("सभी modules पूरे करें, फिर Quiz दें", "Complete all modules, then take the Quiz"))
            }
        }
        BottomBar {
            WiomButton(
                t("Wiom नीति प्रश्नोत्तरी शुरू करें", "Start Wiom Policy Quiz"),
                onClick = onNext,
                enabled = uiState.allModulesCompleted,
            )
        }
    }
}

// Training Detail Screen — video + quiz
@Composable
fun TrainingDetailScreen(viewModel: TrainingViewModel, moduleId: String, onBack: () -> Unit, onComplete: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val module = uiState.modules.find { it.id == moduleId } ?: run {
        onBack()
        return
    }

    val videoWatched = viewModel.isVideoWatched(moduleId)
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
            viewModel.markVideoWatched(moduleId)
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
                    t("Quiz — ${module.questions.size} सवाल", "Quiz — ${module.questions.size} Questions"),
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

// ═══════════════════════════════════════════════════════════════════
// Screen 15: Wiom Policy Quiz
// ═══════════════════════════════════════════════════════════════════
@Composable
fun PolicyQuizScreen(viewModel: PolicyQuizViewModel, onNext: () -> Unit, onBackToTraining: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Load questions on first composition if empty
    LaunchedEffect(Unit) {
        if (uiState.questions.isEmpty()) {
            viewModel.loadQuestions()
        }
    }

    // Auto-advance after correct answer
    if (!uiState.isFinished && uiState.currentAnswer != null) {
        val isCorrect = uiState.currentQuestion?.let { q ->
            uiState.currentAnswer == q.correctIndex
        } == true
        if (isCorrect) {
            LaunchedEffect(uiState.currentQuestionIndex, uiState.currentAnswer) {
                delay(800)
                viewModel.nextQuestion()
            }
        }
    }

    if (uiState.isFinished) {
        // ─── Quiz finished: pass or fail ───
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(title = t("Wiom नीति प्रश्नोत्तरी", "Wiom Policy Quiz"))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(24.dp))
                if (uiState.passed) {
                    // ─── Pass: celebration ───
                    Text("\uD83C\uDF89", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("बधाई हो! प्रश्नोत्तरी पास!", "Congratulations! Quiz Passed!"),
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                    WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                        Text(t("आपका स्कोर", "Your Score"), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomPositive)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("${uiState.score}/${uiState.totalQuestions}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = WiomPositive)
                            Text(t("पास ✓", "Passed ✓"), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WiomPositive)
                        }
                        Spacer(Modifier.height(8.dp))
                        WiomProgressBar(uiState.score.toFloat() / uiState.totalQuestions)
                    }
                    Spacer(Modifier.height(12.dp))
                    InfoBox("✓", t("आप Wiom Partner बनने के लिए तैयार हैं!", "You are ready to become a Wiom Partner!"), type = InfoBoxType.SUCCESS)
                } else {
                    // ─── Fail: score card ───
                    Text("\uD83D\uDCCB", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("प्रश्नोत्तरी पास नहीं हुई", "Quiz not passed"),
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = WiomNegative,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                    val passThreshold = (uiState.totalQuestions * 80) / 100
                    WiomCard(borderColor = WiomNegative, backgroundColor = WiomNegative100) {
                        Text(t("आपका स्कोर", "Your Score"), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomNegative)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("${uiState.score}/${uiState.totalQuestions}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = WiomNegative)
                            Text(t("पास होने के लिए $passThreshold/${uiState.totalQuestions} ज़रूरी", "Need $passThreshold/${uiState.totalQuestions} to pass"), fontSize = 13.sp, color = WiomTextSec)
                        }
                        Spacer(Modifier.height(8.dp))
                        WiomProgressBar(uiState.score.toFloat() / uiState.totalQuestions)
                    }
                    Spacer(Modifier.height(12.dp))
                    ErrorCard(
                        icon = "\uD83D\uDCDD",
                        titleHi = "चिंता न करें",
                        titleEn = "Don't worry",
                        messageHi = "Training modules दोबारा review करें और फिर quiz दें",
                        messageEn = "Review training modules again and retake the quiz",
                        type = "warning",
                    )
                }
            }
            BottomBar {
                if (uiState.passed) {
                    WiomButton(t("Go Live!", "Go Live!"), onClick = onNext, enabled = uiState.passed)
                } else {
                    WiomButton(t("Modules Review करें", "Review Modules"), onClick = onBackToTraining)
                    Spacer(Modifier.height(8.dp))
                    WiomButton(t("Quiz दोबारा दें", "Retake Quiz"), onClick = {
                        viewModel.retryQuiz()
                    }, isSecondary = true)
                }
            }
        }
    } else if (uiState.isLoading) {
        // Loading state
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(title = t("Wiom नीति प्रश्नोत्तरी", "Wiom Policy Quiz"))
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = WiomPrimary)
            }
        }
    } else {
        // ─── Normal quiz flow ───
        val question = uiState.currentQuestion ?: return
        val selectedOptionIndex = uiState.currentAnswer
        val answerState = when {
            selectedOptionIndex == null -> "none"
            selectedOptionIndex == question.correctIndex -> "correct"
            else -> "wrong"
        }

        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(title = t("Wiom नीति प्रश्नोत्तरी", "Wiom Policy Quiz"))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Info card
                if (uiState.currentQuestionIndex == 0 && selectedOptionIndex == null) {
                    WiomCard(borderColor = WiomInfo, backgroundColor = WiomInfo100) {
                        Text(
                            t("यह प्रश्नोत्तरी सभी प्रशिक्षण विषयों पर आधारित है। पास होने के लिए 80% अंक चाहिए।",
                                "This quiz covers all training module topics. 80% score required to pass."),
                            fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Progress
                Text(
                    t("सवाल ${uiState.currentQuestionIndex + 1} / ${uiState.totalQuestions}", "Question ${uiState.currentQuestionIndex + 1} of ${uiState.totalQuestions}"),
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomTextSec,
                )
                Spacer(Modifier.height(4.dp))
                WiomProgressBar(uiState.progressFraction)
                Spacer(Modifier.height(16.dp))

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
                        answerState == "wrong" && isCorrectAnswer -> WiomPositive100
                        isSelected -> WiomPrimaryLight
                        else -> Color.White
                    }
                    val optionBorderColor = when {
                        answerState == "correct" && isCorrectAnswer -> WiomPositive
                        answerState == "wrong" && isSelected -> WiomNegative
                        answerState == "wrong" && isCorrectAnswer -> WiomPositive
                        isSelected -> WiomPrimary
                        else -> WiomBorder
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(1.5.dp, optionBorderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = answerState == "none") {
                                viewModel.answerQuestion(index)
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
                                        answerState == "wrong" && isCorrectAnswer -> WiomPositive
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
                            } else if (answerState == "wrong" && isCorrectAnswer) {
                                Text("\u2713", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                            fontWeight = if (isSelected || (answerState == "wrong" && isCorrectAnswer)) FontWeight.SemiBold else FontWeight.Normal,
                            color = WiomText,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Wrong answer: show correct + next button
                if (answerState == "wrong") {
                    Spacer(Modifier.height(8.dp))
                    ErrorCard(
                        icon = "\uD83D\uDCA1",
                        titleHi = "सही जवाब ऊपर हरे रंग में दिखाया गया है",
                        titleEn = "Correct answer is highlighted in green above",
                        messageHi = question.hintHi,
                        messageEn = question.hintEn,
                        type = "warning",
                    )
                    Spacer(Modifier.height(8.dp))
                    WiomButton(
                        if (!uiState.isLastQuestion) t("अगला सवाल", "Next Question")
                        else t("नतीजे देखें", "View Results"),
                        onClick = {
                            viewModel.nextQuestion()
                        },
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Screen 16: Go Live!
// ═══════════════════════════════════════════════════════════════════
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
            AppHeader(title = t("पार्टनर ऐप होम", "Partner App Home"))
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
                        t("बधाई हो, राजेश!", "Congratulations, Rajesh!"),
                        fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WiomText,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        t("आप अब Wiom Partner हैं", "You are now a Wiom Partner"),
                        fontSize = 14.sp, color = WiomTextSec,
                    )
                    Spacer(Modifier.height(16.dp))
                    // 9 Status chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        WiomChip("✓ ${t("रजिस्टर्ड", "Registered")}")
                        WiomChip("✓ ${t("KYC वेरीफाइड", "KYC Verified")}")
                        WiomChip("✓ ${t("Bank वेरीफाइड", "Bank Verified")}")
                        WiomChip("✓ ${t("ISP एग्रीमेंट", "ISP Agreement")}")
                        WiomChip("✓ ${t("फ़ोटो सबमिट", "Photos Submitted")}")
                        WiomChip("✓ ${t("सत्यापन Approved", "Verification Approved")}")
                        WiomChip("✓ ${t("Tech Assessment पास", "Tech Assessment Passed")}")
                        WiomChip("✓ ${t("अकाउंट सेटअप", "Account Setup")}")
                        WiomChip("✓ ${t("ट्रेनिंग और सर्टिफाइड", "Trained & Certified")}")
                    }
                }
                SectionHeader(t("क्विक एक्शन", "QUICK ACTIONS"))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    QuickActionCard("\uD83D\uDC64", t("Customer जोड़ें", "Add Customer"), t("नया कनेक्शन", "New connection"), onClick = { activeAction = "customer" })
                    Spacer(Modifier.height(8.dp))
                    QuickActionCard("\uD83D\uDCB0", t("कमाई देखें", "View Earnings"), "Commission, TDS", onClick = { activeAction = "earnings" })
                    Spacer(Modifier.height(8.dp))
                    QuickActionCard("\uD83D\uDCDD", t("टास्क", "Tasks"), t("रिस्टोर, शिकायतें", "Restore, complaints"), onClick = { activeAction = "tasks" })
                    Spacer(Modifier.height(8.dp))
                    QuickActionCard("\uD83C\uDF93", t("ट्रेनिंग", "Training"), t("Module दोबारा देखें", "Revisit modules"), onClick = { activeAction = "training" })
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
                                    "\uD83D\uDE80 ${t("Wiom CSP App डाउनलोड करें", "Download Wiom CSP App")}",
                                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WiomText,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    t("नए कनेक्शन बनाने के लिए Wiom CSP App इस्तेमाल करें", "Use Wiom CSP App to create new connections"),
                                    fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                                )
                                Spacer(Modifier.height(8.dp))
                                WiomButton(
                                    t("App डाउनलोड करें", "Download App"),
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
            "Customer जोड़ें", "Add Customer",
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
            "टास्क", "Tasks",
            t("Restore और शिकायतें — Tutorial", "Restore and complaints — Tutorial"),
            t("Customer complaints, restore requests यहां मैनेज करें", "Manage customer complaints, restore requests here"),
            "Wiom CSP App खोलें", "Open Wiom CSP App",
        )
        "training" -> ActionDetail(
            "ट्रेनिंग", "Training",
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
                    Text("▶", fontSize = 40.sp, color = Color.White)
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
