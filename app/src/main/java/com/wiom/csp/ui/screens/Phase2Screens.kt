package com.wiom.csp.ui.screens

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

// ─── Screen 9: Verification (was QA Investigation) ────────────────────────────
@Composable
fun VerificationScreen(onNext: () -> Unit) {
    val rejected = OnboardingState.verificationRejected

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("सत्यापन", "Verification"),
            rightText = t("स्टेप 5/5", "Step 5/5"),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (rejected) {
                // Rejected state — show reason + resolution CTA
                val reasonId = OnboardingState.verificationRejectReasonId
                val reason = com.wiom.csp.data.REJECTION_REASONS.find { it.id == reasonId }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83D\uDE14", fontSize = 40.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("सत्यापन अस्वीकृत", "Verification Rejected"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("चिंता न करें — आपका पैसा सुरक्षित है", "Don't worry — your money is safe"),
                        fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))

                    // Rejection reason card
                    WiomCard(borderColor = WiomNegative, backgroundColor = WiomNegative100) {
                        Text("❌ ${t("अस्वीकृति का कारण", "Reason for Rejection")}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomNegative)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (reason != null) t(reason.labelHi, reason.labelEn)
                            else t("दस्तावेज़ सही नहीं हैं।", "Documents are not correct."),
                            fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = WiomText, lineHeight = 22.sp,
                        )
                    }
                    Spacer(Modifier.height(10.dp))

                    // Resolution CTA — if resolvable
                    if (reason != null && reason.resolvable) {
                        WiomCard(borderColor = WiomPrimary, backgroundColor = Color(0xFFFFF0F6)) {
                            Text("🔧 ${t("समाधान", "Resolution")}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPrimary)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                t(reason.ctaHi, reason.ctaEn),
                                fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                            )
                            Spacer(Modifier.height(12.dp))
                            WiomButton(
                                t(reason.ctaHi, reason.ctaEn),
                                onClick = {
                                    OnboardingState.verificationRejected = false
                                    OnboardingState.verificationRejectReasonId = null
                                    OnboardingState.goTo(reason.resolveScreen)
                                },
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoBox("\uD83D\uDD14", t("दस्तावेज़ सही करें (2 मौके बाकी)", "Fix documents (2 chances remaining)"))
                    } else {
                        // Not resolvable — show refund
                        WiomCard(borderColor = WiomPositive, backgroundColor = WiomPositive100) {
                            Text("\uD83D\uDD12 ${t("Refund शुरू हो गया", "Refund initiated")}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPositive)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                t("₹2,000 आपके bank account में 5-7 working days में आ जाएगा।", "₹2,000 will be credited to your bank account in 5-7 working days."),
                                fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                            )
                            Text("Ref: RFD-2026-0042", fontSize = 12.sp, color = WiomHint)
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoBox("ℹ️", t("यह समस्या ऐप से हल नहीं हो सकती। रिफंड प्रक्रिया शुरू हो गई है।", "This issue cannot be resolved from the app. Refund has been initiated."))
                    }
                }
            } else {
                // Pending/Approved state
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83D\uDD0D", fontSize = 40.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("सत्यापन चल रहा है", "Verification in progress"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("Business/QA team आपकी profile चेक कर रही है", "Business/QA team is reviewing your profile"),
                        fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(16.dp))
                ChecklistItem(t("KYC दस्तावेज़ वेरीफाइड", "KYC Documents Verified"))
                ChecklistItem(t("Bank वेरीफाइड", "Bank Verified"))
                ChecklistItem(t("ISP अनुबंध", "ISP Agreement"))
                ChecklistItem(t("फ़ोटो जमा", "Photos Submitted"))
                ChecklistItem(
                    t("सत्यापन", "Verification"),
                    subtitle = t("Business/QA team review कर रही है...", "Business/QA team is reviewing..."),
                    isDone = false,
                    isWaiting = true,
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoBox("\u23F3", t("Review में 2-3 business days लग सकते हैं। Notification मिलेगा।", "Review may take 2-3 business days. You will be notified."), type = InfoBoxType.WARNING)
                }
            }
        }
    }
}

// ─── Screen 10: Policy + Rate Card ────────────────────────────────────────────
@Composable
fun PolicyRateCardScreen(onNext: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("नीतियां और रेट कार्ड", "Policy & Rate Card"),
            onBack = onBack,
            rightText = t("स्टेप 1/7", "Step 1/7")
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
                t("सत्यापन Approved \u2713 — अब policies समझें", "Verification Approved \u2713 — now review policies"),
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
                    Text("\u20B9300/${t("कनेक्शन", "conn")}", fontWeight = FontWeight.Bold, color = WiomPositive, fontSize = 14.sp)
                }
                HorizontalDivider(color = WiomBorder)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(t("रिचार्ज कमीशन", "Recharge Commission"), fontSize = 14.sp, color = WiomTextSec)
                    Text("\u20B9300", fontWeight = FontWeight.Bold, color = WiomPositive, fontSize = 14.sp)
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
                        "\u2022 $item",
                        fontSize = 14.sp, color = WiomTextSec,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(vertical = 1.dp),
                    )
                }
            }
        }
        BottomBar {
            WiomButton(t("समझ गया, आगे बढ़ें", "Understood, proceed"), onClick = onNext)
        }
    }
}

// ─── Screen 6: Bank + Dedup Check ─────────────────────────────────────────────
@Composable
fun BankDedupScreen(onNext: () -> Unit, onBack: () -> Unit) {
    val scenario = OnboardingState.activeScenario
    var isVerifying by remember { mutableStateOf(false) }
    val bankVerified = OnboardingState.bankVerified

    // Bank verification loading simulation
    if (isVerifying) {
        LaunchedEffect(Unit) {
            delay(2000)
            OnboardingState.bankVerified = true
            isVerifying = false
        }
    }

    val allFieldsFilled = OnboardingState.bankAccountHolder.isNotBlank() &&
            OnboardingState.bankName.isNotBlank() &&
            OnboardingState.bankAccountNumber.isNotBlank() &&
            OnboardingState.bankIfsc.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(
                title = t("Bank वेरिफिकेशन", "Bank Verification"),
                onBack = onBack,
                rightText = t("स्टेप 2/5", "Step 2/5")
            )

            when (scenario) {
                Scenario.BANK_PENNYDROP_FAIL -> {
                    // ─── BANK_PENNYDROP_FAIL ───
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            t("बैंक खाता जानकारी", "Bank Account Details"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t("Commission payment आपके बैंक अकाउंट में आएगा", "Commission payments will be sent to your bank account"),
                            fontSize = 14.sp, color = WiomTextSec,
                        )
                        Spacer(Modifier.height(16.dp))

                        FieldLabel(t("अकाउंट होल्डर नाम", "Account Holder Name"))
                        WiomTextField(value = "Rajesh Kumar", onValueChange = {})

                        FieldLabel(t("बैंक नाम", "Bank Name"))
                        WiomTextField(value = "State Bank of India", onValueChange = {}, readOnly = true)

                        FieldLabel(t("अकाउंट नंबर", "Account Number"))
                        WiomTextField(
                            value = "XXXX XXXX 4521",
                            onValueChange = {},
                            isError = true,
                            errorMessage = t("अकाउंट नंबर गलत हो सकता है", "Account number may be incorrect"),
                        )

                        FieldLabel("IFSC Code")
                        WiomTextField(value = "SBIN0001234", onValueChange = {})

                        Spacer(Modifier.height(8.dp))
                        ErrorCard(
                            icon = "\uD83C\uDFE6",
                            titleHi = "\u20B91 credit नहीं हो पाया",
                            titleEn = "Penny drop failed",
                            messageHi = "अकाउंट नंबर गलत हो सकता है या बैंक सर्वर डाउन है।",
                            messageEn = "Account number may be wrong or bank server is down.",
                            type = "error",
                        )
                        Spacer(Modifier.height(12.dp))
                        WiomButton(t("अकाउंट नंबर ठीक करें", "Fix Account Number"), onClick = {})
                    }
                }
                Scenario.BANK_NAME_MISMATCH -> {
                    // ─── BANK_NAME_MISMATCH ───
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            t("बैंक खाता जानकारी", "Bank Account Details"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        )
                        Spacer(Modifier.height(16.dp))

                        FieldLabel(t("अकाउंट होल्डर नाम", "Account Holder Name"))
                        WiomTextField(value = "Rajesh Kumar", onValueChange = {})

                        FieldLabel(t("बैंक नाम", "Bank Name"))
                        WiomTextField(value = "State Bank of India", onValueChange = {}, readOnly = true)

                        FieldLabel(t("अकाउंट नंबर", "Account Number"))
                        WiomTextField(value = "XXXX XXXX 4521", onValueChange = {}, isVerified = true)

                        FieldLabel("IFSC Code")
                        WiomTextField(value = "SBIN0001234", onValueChange = {}, isVerified = true)

                        Spacer(Modifier.height(8.dp))
                        WiomCard(borderColor = WiomWarning, backgroundColor = WiomWarning200) {
                            Text(
                                t("Bank में नाम: Rajesh Kumar Sharma\nआपने डाला: Rajesh Kumar",
                                  "Name in Bank: Rajesh Kumar Sharma\nYou entered: Rajesh Kumar"),
                                fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        ErrorCard(
                            icon = "\uD83D\uDC64",
                            titleHi = "Penny Drop — नाम मेल नहीं खाता",
                            titleEn = "Penny Drop — Name Mismatch",
                            messageHi = "Bank account का नाम और आपका नाम अलग है।",
                            messageEn = "Bank account name and your name are different.",
                            type = "warning",
                        )
                        Spacer(Modifier.height(12.dp))
                        WiomButton(t("नाम ठीक करें और Retry", "Fix Name and Retry"), onClick = {})
                    }
                }
                Scenario.DEDUP_FOUND -> {
                    // ─── DEDUP_FOUND ───
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            t("बैंक खाता जानकारी", "Bank Account Details"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        )
                        Spacer(Modifier.height(16.dp))

                        // Penny drop verified
                        WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                            Text(
                                "\u2713 ${t("पेनी ड्रॉप वेरीफाइड", "PENNY DROP VERIFIED")}",
                                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                t("\u20B91 क्रेडिट और वेरीफाइड — नाम मैच कन्फ़र्म", "\u20B91 credited & verified — Name match confirmed"),
                                fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        ErrorCard(
                            icon = "\uD83D\uDD0D",
                            titleHi = "DEDUP CHECK — Match Found!",
                            titleEn = "DEDUP CHECK — Match Found!",
                            messageHi = "इस PAN और Bank Account से पहले से एक पार्टनर रजिस्टर्ड है।",
                            messageEn = "A partner is already registered with this PAN and Bank Account.",
                            type = "error",
                        )
                        Spacer(Modifier.height(8.dp))
                        WiomCard {
                            Text(t("मैच विवरण", "MATCH DETAILS"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomTextSec, letterSpacing = 0.5.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Partner ID", fontSize = 13.sp, color = WiomTextSec)
                                Text("CSP-0031", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomText)
                            }
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Name", fontSize = 13.sp, color = WiomTextSec)
                                Text("Rajesh K.", fontSize = 13.sp, color = WiomText)
                            }
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("City", fontSize = 13.sp, color = WiomTextSec)
                                Text("Indore", fontSize = 13.sp, color = WiomText)
                            }
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Match On", fontSize = 13.sp, color = WiomTextSec)
                                Text("PAN + Bank A/C", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomNegative)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoBox("\uD83D\uDCDE", t("Wiom सपोर्ट से बात करें", "Talk to Wiom support"))
                        Spacer(Modifier.height(12.dp))
                        WiomButton(t("हमसे बात करें", "Talk to us"), onClick = {})
                    }
                }
                else -> {
                    // ─── Normal happy path (interactive) ───
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            t("बैंक खाता जानकारी", "Bank Account Details"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t("Commission payment आपके बैंक अकाउंट में आएगा", "Commission payments will be sent to your bank account"),
                            fontSize = 14.sp, color = WiomTextSec,
                        )
                        Spacer(Modifier.height(16.dp))

                        FieldLabel(t("अकाउंट होल्डर नाम", "Account Holder Name"))
                        WiomTextField(
                            value = OnboardingState.bankAccountHolder,
                            onValueChange = { OnboardingState.bankAccountHolder = it },
                            placeholder = t("नाम डालें", "Enter name"),
                            isVerified = bankVerified,
                        )

                        FieldLabel(t("बैंक नाम", "Bank Name"))
                        WiomTextField(
                            value = OnboardingState.bankName,
                            onValueChange = { OnboardingState.bankName = it },
                            placeholder = t("बैंक नाम डालें", "Enter bank name"),
                            isVerified = bankVerified,
                        )

                        FieldLabel(t("अकाउंट नंबर", "Account Number"))
                        WiomTextField(
                            value = OnboardingState.bankAccountNumber,
                            onValueChange = { OnboardingState.bankAccountNumber = it },
                            placeholder = t("अकाउंट नंबर डालें", "Enter account number"),
                            isVerified = bankVerified,
                        )

                        FieldLabel("IFSC Code")
                        WiomTextField(
                            value = OnboardingState.bankIfsc,
                            onValueChange = { OnboardingState.bankIfsc = it },
                            placeholder = t("IFSC कोड डालें", "Enter IFSC code"),
                            isVerified = bankVerified,
                        )

                        if (bankVerified) {
                            WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                                Text(
                                    "\u2713 ${t("पेनी ड्रॉप वेरीफाइड", "PENNY DROP VERIFIED")}",
                                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    t("\u20B91 क्रेडिट और वेरीफाइड — नाम मैच कन्फ़र्म", "\u20B91 credited & verified — Name match confirmed"),
                                    fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                                Text(
                                    "\u2713 ${t("डीडप चेक पास", "DEDUP CHECK PASSED")}",
                                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    t("PAN, आधार, GST, Bank — कोई डुप्लिकेट नहीं मिला", "PAN, Aadhaar, GST, Bank — No duplicates found"),
                                    fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                                )
                            }
                        } else if (!allFieldsFilled) {
                            InfoBox("\uD83D\uDCDD", t("सभी बैंक डिटेल्स भरें", "Fill all bank details to proceed"))
                        }
                    }
                    BottomBar {
                        if (bankVerified) {
                            WiomButton(t("अब ISP अनुबंध अपलोड करें", "Next: ISP Agreement"), onClick = onNext)
                        } else {
                            WiomButton(
                                t("अब ISP अनुबंध अपलोड करें", "Next: ISP Agreement"),
                                onClick = { isVerifying = true },
                                enabled = allFieldsFilled,
                            )
                        }
                    }
                }
            }
        }

        // Loading overlay
        LoadingOverlay(
            messageHi = "Penny Drop वेरीफाई हो रहा है...",
            messageEn = "Verifying via Penny Drop...",
            isVisible = isVerifying,
        )
    }
}

// ─── Screen 7: ISP Agreement ──────────────────────────────────────────────────
@Composable
fun IspAgreementScreen(onNext: () -> Unit, onBack: () -> Unit) {
    var ispUploaded by remember { mutableStateOf(OnboardingState.ispAgreementUploaded) }
    var isUploading by remember { mutableStateOf(false) }
    val isIspInvalid = OnboardingState.activeScenario == Scenario.ISP_DOC_INVALID

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("ISP अनुबंध", "ISP Agreement"),
            onBack = onBack,
            rightText = t("स्टेप 3/5", "Step 3/5")
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ISP_DOC_INVALID error screen
            if (isIspInvalid) {
                WiomCard(borderColor = WiomNegative, backgroundColor = WiomNegative100) {
                    Text(
                        "⚠️ ${t("ISP दस्तावेज़ अमान्य", "ISP Document Invalid")}",
                        fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WiomNegative,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        t("अपलोड किया गया दस्तावेज़ अमान्य है। कृपया सही ISP अनुबंध अपलोड करें।",
                           "The uploaded document is invalid. Please upload a valid ISP agreement."),
                        fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                    )
                    Spacer(Modifier.height(12.dp))
                    WiomButton(
                        t("दोबारा अपलोड करें", "Re-upload"),
                        onClick = {
                            OnboardingState.clearScenario()
                            OnboardingState.ispAgreementUploaded = false
                            ispUploaded = false
                        },
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            Text(
                t("ISP अनुबंध अपलोड", "ISP Agreement Upload"),
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(12.dp))

            // DOT compliance info card
            WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                Text(
                    "\u2139\uFE0F ${t("DOT अनुपालन के लिए अनिवार्य", "Mandatory for DOT Compliance")}",
                    fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomPositive,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    t("ISP अनुबंध दूरसंचार विभाग की जांच के लिए आवश्यक है।",
                       "ISP Agreement is required for Department of Telecom verification."),
                    fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                )
            }
            Spacer(Modifier.height(12.dp))

            // Trust badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TrustBadge("\uD83D\uDD12", t("DOT अनुपालन", "DOT Compliance"))
                TrustBadge("\u2713", t("TRAI दिशानिर्देश", "TRAI Guidelines"))
            }
            Spacer(Modifier.height(16.dp))

            // ISP Agreement upload card
            WiomCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("\uD83D\uDCC4", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                t("ISP अनुबंध", "ISP Agreement"),
                                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomText,
                            )
                            Text(
                                if (ispUploaded) t("अपलोड हो गया \u2705", "Uploaded \u2705")
                                else t("अपलोड करें", "Upload"),
                                fontSize = 12.sp,
                                color = if (ispUploaded) WiomPositive else WiomTextSec,
                            )
                        }
                    }
                    if (!ispUploaded) {
                        Button(
                            onClick = {
                                isUploading = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = WiomPrimary,
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WiomPrimary),
                        ) {
                            Text(t("अपलोड", "Upload"), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    } else {
                        Text("\u2705", fontSize = 24.sp)
                    }
                }
            }

            // Simulate upload
            if (isUploading) {
                LaunchedEffect(Unit) {
                    delay(1500)
                    ispUploaded = true
                    OnboardingState.ispAgreementUploaded = true
                    isUploading = false
                }
                Spacer(Modifier.height(12.dp))
                WiomCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = WiomPrimary,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            t("अपलोड हो रहा है...", "Uploading..."),
                            fontSize = 14.sp, color = WiomTextSec,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            InfoBox("\uD83D\uDCA1", t("साफ़ फ़ोटो लें — सारा टेक्स्ट दिखना चाहिए", "Take a clear photo — all text should be visible"), type = InfoBoxType.WARNING)
        }

        BottomBar {
            WiomButton(
                if (ispUploaded) t("आगे बढ़ें", "Proceed")
                else t("ISP अनुबंध अपलोड करें", "Upload ISP Agreement"),
                onClick = if (ispUploaded) onNext else ({}),
                enabled = ispUploaded,
            )
        }
    }
}

// ─── Screen 8: Shop Photos ────────────────────────────────────────────────────
@Composable
fun ShopPhotosScreen(onNext: () -> Unit, onBack: () -> Unit) {
    var shopUploaded by remember { mutableStateOf(OnboardingState.shopFrontPhotoUploaded) }
    var routerUploaded by remember { mutableStateOf(OnboardingState.routerPhotoUploaded) }
    var isUploadingShop by remember { mutableStateOf(false) }
    var isUploadingRouter by remember { mutableStateOf(false) }

    val bothUploaded = shopUploaded && routerUploaded

    // Simulate shop upload
    if (isUploadingShop) {
        LaunchedEffect(Unit) {
            delay(1500)
            shopUploaded = true
            OnboardingState.shopFrontPhotoUploaded = true
            isUploadingShop = false
        }
    }
    // Simulate router upload
    if (isUploadingRouter) {
        LaunchedEffect(Unit) {
            delay(1500)
            routerUploaded = true
            OnboardingState.routerPhotoUploaded = true
            isUploadingRouter = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("फ़ोटो", "Photos"),
            onBack = onBack,
            rightText = t("स्टेप 4/5", "Step 4/5"),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                t("फ़ोटो अपलोड करें", "Upload Photos"),
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("दुकान और उपकरण की फ़ोटो अपलोड करें", "Upload shop and equipment photos"),
                fontSize = 14.sp, color = WiomTextSec,
            )
            Spacer(Modifier.height(16.dp))

            // Shop Front Photo card
            WiomCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("\uD83D\uDCF7", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                t("दुकान की फ़ोटो", "Shop Front Photo"),
                                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomText,
                            )
                            Text(
                                if (shopUploaded) t("अपलोड हो गया \u2705", "Uploaded \u2705")
                                else if (isUploadingShop) t("अपलोड हो रहा है...", "Uploading...")
                                else t("दुकान के सामने से साफ़ फ़ोटो — बोर्ड दिखना चाहिए", "Clear front photo — signboard must be visible"),
                                fontSize = 12.sp,
                                color = if (shopUploaded) WiomPositive else WiomTextSec,
                            )
                        }
                    }
                    if (isUploadingShop) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = WiomPrimary,
                            strokeWidth = 2.dp,
                        )
                    } else if (!shopUploaded) {
                        Button(
                            onClick = { isUploadingShop = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = WiomPrimary,
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WiomPrimary),
                        ) {
                            Text(t("अपलोड", "Upload"), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    } else {
                        Text("\u2705", fontSize = 24.sp)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // Router/Equipment Photo card
            WiomCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("\uD83D\uDCF7", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                t("राउटर/उपकरण फ़ोटो", "Router/Equipment Photo"),
                                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomText,
                            )
                            Text(
                                if (routerUploaded) t("अपलोड हो गया \u2705", "Uploaded \u2705")
                                else if (isUploadingRouter) t("अपलोड हो रहा है...", "Uploading...")
                                else t("इंटरनेट उपकरण (राउटर, केबल) की फ़ोटो", "Internet equipment (router, cables) photo"),
                                fontSize = 12.sp,
                                color = if (routerUploaded) WiomPositive else WiomTextSec,
                            )
                        }
                    }
                    if (isUploadingRouter) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = WiomPrimary,
                            strokeWidth = 2.dp,
                        )
                    } else if (!routerUploaded) {
                        Button(
                            onClick = { isUploadingRouter = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = WiomPrimary,
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WiomPrimary),
                        ) {
                            Text(t("अपलोड", "Upload"), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    } else {
                        Text("\u2705", fontSize = 24.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (bothUploaded) {
                InfoBox("\u2713", t("दोनों फ़ोटो अपलोड हो गई!", "Both photos uploaded!"), type = InfoBoxType.SUCCESS)
            } else {
                InfoBox("\uD83D\uDCF7", t("दोनों फ़ोटो अपलोड करें", "Upload both photos to proceed"))
            }
        }

        BottomBar {
            WiomButton(
                t("सत्यापन के लिए जमा करें", "Submit for Verification"),
                onClick = onNext,
                enabled = bothUploaded,
            )
        }
    }
}
