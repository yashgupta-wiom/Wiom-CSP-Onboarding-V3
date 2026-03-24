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
import com.wiom.csp.data.Scenario
import com.wiom.csp.domain.model.VerificationStatus
import com.wiom.csp.ui.components.*
import com.wiom.csp.ui.theme.*
import com.wiom.csp.ui.viewmodel.*
import com.wiom.csp.util.t
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════════
// Screen 5: KYC Documents (moved from Stage 1 to Stage 2)
// ═══════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(viewModel: KycViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Bottom sheet state: null, "choose_pan"/"choose_aadhaar_front"/etc, "preview_pan"/etc, "uploading_pan"/etc
    var sheetTarget by remember { mutableStateOf<String?>(null) }
    var sheetStep by remember { mutableStateOf("choose") } // "choose", "preview", "uploading", "done"
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Determine doc label for bottom sheet title
    val sheetDocLabel = when {
        sheetTarget?.contains("pan") == true -> t("PAN Card", "PAN Card")
        sheetTarget?.contains("aadhaar_front") == true -> t("आधार कार्ड — सामने", "Aadhaar Card — Front")
        sheetTarget?.contains("aadhaar_back") == true -> t("आधार कार्ड — पीछे", "Aadhaar Card — Back")
        sheetTarget?.contains("gst") == true -> t("GST प्रमाणपत्र", "GST Certificate")
        else -> ""
    }

    // Upload simulation
    fun simulateUpload() {
        sheetStep = "uploading"
    }

    fun markUploaded() {
        when (sheetTarget) {
            "pan" -> viewModel.uploadDocument("pan", "simulated_uri")
            "aadhaar_front" -> viewModel.uploadDocument("aadhaarFront", "simulated_uri")
            "aadhaar_back" -> viewModel.uploadDocument("aadhaarBack", "simulated_uri")
            "gst" -> viewModel.uploadDocument("gst", "simulated_uri")
        }
        sheetTarget = null
        sheetStep = "choose"
    }

    // Bottom Sheet
    if (sheetTarget != null) {
        ModalBottomSheet(
            onDismissRequest = { sheetTarget = null; sheetStep = "choose" },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
            ) {
                when (sheetStep) {
                    "choose" -> {
                        // Step 1: Choose source
                        Text(
                            t("$sheetDocLabel अपलोड करें", "Upload $sheetDocLabel"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiomText,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t("फ़ोटो कैसे लेना चाहेंगे?", "How would you like to capture?"),
                            fontSize = 14.sp,
                            color = WiomTextSec,
                        )
                        Spacer(Modifier.height(16.dp))

                        // Camera option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(WiomBgSec)
                                .clickable { sheetStep = "preview" }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WiomPrimaryLight),
                                contentAlignment = Alignment.Center,
                            ) { Text("\uD83D\uDCF7", fontSize = 24.sp) }
                            Column {
                                Text(t("कैमरा से फ़ोटो लें", "Take Photo"), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WiomText)
                                Text(t("सीधे कैमरा खोलें और फ़ोटो क्लिक करें", "Open camera and click photo"), fontSize = 13.sp, color = WiomTextSec)
                            }
                        }
                        Spacer(Modifier.height(10.dp))

                        // Gallery option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(WiomBgSec)
                                .clickable { sheetStep = "preview" }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WiomInfo100),
                                contentAlignment = Alignment.Center,
                            ) { Text("\uD83D\uDDBC\uFE0F", fontSize = 24.sp) }
                            Column {
                                Text(t("गैलरी से चुनें", "Choose from Gallery"), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WiomText)
                                Text(t("फ़ोन में सेव फ़ोटो में से चुनें", "Pick from saved photos"), fontSize = 13.sp, color = WiomTextSec)
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        // Tips
                        InfoBox(
                            "\uD83D\uDCA1",
                            t("साफ़ फ़ोटो लें — सारे अक्षर दिखने चाहिए", "Take a clear photo — all text must be visible"),
                            type = InfoBoxType.INFO,
                        )
                    }

                    "preview" -> {
                        // Step 2: Preview photo
                        Text(
                            t("फ़ोटो रिव्यू करें", "Review Photo"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiomText,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(sheetDocLabel, fontSize = 14.sp, color = WiomTextSec)
                        Spacer(Modifier.height(16.dp))

                        // Simulated photo preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(WiomBgSec)
                                .border(2.dp, WiomBorder, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    when {
                                        sheetTarget?.contains("pan") == true -> "\uD83E\uDEAA"
                                        sheetTarget?.contains("gst") == true -> "\uD83D\uDCCB"
                                        else -> "\uD83D\uDCC4"
                                    },
                                    fontSize = 48.sp,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    sheetDocLabel,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = WiomTextSec,
                                )
                                Text(
                                    t("फ़ोटो कैप्चर हुई", "Photo captured"),
                                    fontSize = 12.sp,
                                    color = WiomPositive,
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))

                        // Quality check badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            TrustBadge("✓", t("साफ़ दिख रहा है", "Clear"))
                            TrustBadge("✓", t("पूरा दिख रहा है", "Complete"))
                        }
                        Spacer(Modifier.height(16.dp))

                        // Action buttons
                        WiomButton(
                            t("यह फ़ोटो सेव करें", "Save this photo"),
                            onClick = { simulateUpload() },
                        )
                        Spacer(Modifier.height(8.dp))
                        WiomButton(
                            t("दोबारा फ़ोटो लें", "Retake photo"),
                            onClick = { sheetStep = "choose" },
                            isSecondary = true,
                        )
                    }

                    "uploading" -> {
                        // Step 3: Uploading
                        var uploadProgress by remember { mutableStateOf(0f) }
                        LaunchedEffect(Unit) {
                            while (uploadProgress < 1f) {
                                delay(80)
                                uploadProgress = (uploadProgress + 0.05f).coerceAtMost(1f)
                            }
                            delay(300)
                            markUploaded()
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Spacer(Modifier.height(16.dp))
                            if (uploadProgress < 1f) {
                                CircularProgressIndicator(
                                    progress = { uploadProgress },
                                    color = WiomPrimary,
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(56.dp),
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(WiomPositive100),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("✓", fontSize = 28.sp, color = WiomPositive, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                if (uploadProgress < 1f) t("अपलोड हो रहा है...", "Uploading...")
                                else t("अपलोड हो गया!", "Upload complete!"),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uploadProgress < 1f) WiomText else WiomPositive,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                sheetDocLabel,
                                fontSize = 14.sp,
                                color = WiomTextSec,
                            )
                            Spacer(Modifier.height(12.dp))
                            if (uploadProgress < 1f) {
                                LinearProgressIndicator(
                                    progress = { uploadProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = WiomPrimary,
                                    trackColor = WiomBgSec,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${(uploadProgress * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    color = WiomTextSec,
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("KYC दस्तावेज़", "KYC Documents"),
            onBack = onBack,
            rightText = t("स्टेप 1/5", "Step 1/5")
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                t("KYC दस्तावेज़", "KYC Documents"),
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("अपलोड करें — सिस्टम तुरंत वेरीफाई करेगा", "Upload — system will verify instantly"),
                fontSize = 14.sp, color = WiomTextSec,
            )
            Spacer(Modifier.height(16.dp))

            // Normal happy path: interactive upload cards using ViewModel state

            // PAN Card
            KycUploadCard(
                emoji = "\uD83E\uDEAA",
                labelHi = "PAN Card अपलोड करें",
                labelEn = "Upload PAN Card",
                nameHi = "PAN Card",
                nameEn = "PAN Card",
                isUploaded = uiState.pan.isUploaded,
                onUploadClick = { sheetTarget = "pan" },
                onRemoveClick = { viewModel.removeDocument("pan") },
            )
            Spacer(Modifier.height(8.dp))

            // Aadhaar Front
            KycUploadCard(
                emoji = "\uD83D\uDCC4",
                labelHi = "आधार कार्ड — सामने अपलोड करें",
                labelEn = "Upload Aadhaar Card — Front",
                nameHi = "आधार कार्ड — सामने",
                nameEn = "Aadhaar Card — Front",
                isUploaded = uiState.aadhaarFront.isUploaded,
                onUploadClick = { sheetTarget = "aadhaar_front" },
                onRemoveClick = { viewModel.removeDocument("aadhaarFront") },
            )
            Spacer(Modifier.height(8.dp))

            // Aadhaar Back
            KycUploadCard(
                emoji = "\uD83D\uDCC4",
                labelHi = "आधार कार्ड — पीछे अपलोड करें",
                labelEn = "Upload Aadhaar Card — Back",
                nameHi = "आधार कार्ड — पीछे",
                nameEn = "Aadhaar Card — Back",
                isUploaded = uiState.aadhaarBack.isUploaded,
                onUploadClick = { sheetTarget = "aadhaar_back" },
                onRemoveClick = { viewModel.removeDocument("aadhaarBack") },
            )
            Spacer(Modifier.height(8.dp))

            // GST Certificate
            KycUploadCard(
                emoji = "\uD83D\uDCCB",
                labelHi = "GST प्रमाणपत्र अपलोड करें",
                labelEn = "Upload GST Certificate",
                nameHi = "GST प्रमाणपत्र",
                nameEn = "GST Certificate",
                isUploaded = uiState.gst.isUploaded,
                onUploadClick = { sheetTarget = "gst" },
                onRemoveClick = { viewModel.removeDocument("gst") },
            )
        }
        BottomBar {
            WiomButton(
                t("अब बैंक विवरण दें", "Next: Bank Details"),
                onClick = onNext,
                enabled = uiState.allUploaded,
            )
        }
    }
}

// KYC Upload Card (reusable composable)
@Composable
private fun KycUploadCard(
    emoji: String,
    labelHi: String,
    labelEn: String,
    nameHi: String,
    nameEn: String,
    isUploaded: Boolean,
    onUploadClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    if (isUploaded) {
        // Uploaded state
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(WiomPositive100)
                .border(1.dp, WiomPositive300, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Preview placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WiomBgSec),
                    contentAlignment = Alignment.Center,
                ) { Text(emoji, fontSize = 16.sp) }
                Column {
                    Text(
                        "$emoji ${t(nameHi, nameEn)}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = WiomText,
                    )
                    Text(
                        t("अपलोड \u2713", "Uploaded \u2713"),
                        fontSize = 12.sp,
                        color = WiomPositive,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            // Remove button
            Text(
                t("हटाएं", "Remove"),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = WiomNegative,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(WiomNegative100)
                    .clickable { onRemoveClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
    } else {
        // Not uploaded state
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, WiomBorderInput, RoundedCornerShape(12.dp))
                .clickable { onUploadClick() }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("\uD83D\uDCE4", fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                "$emoji ${t(labelHi, labelEn)}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Text(
                t("टैप करें", "Tap to upload"),
                fontSize = 12.sp,
                color = WiomHint,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Screen 6: Bank + Dedup Check (moved from V2 Phase 2 to Stage 2)
// ═══════════════════════════════════════════════════════════════════
@Composable
fun BankDedupScreen(viewModel: BankViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(
                title = t("Bank वेरिफिकेशन", "Bank Verification"),
                onBack = onBack,
                rightText = t("स्टेप 2/5", "Step 2/5")
            )

            // Normal happy path (interactive)
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
                    value = uiState.accountHolder,
                    onValueChange = { viewModel.onAccountHolderChanged(it) },
                    placeholder = t("नाम डालें", "Enter name"),
                    isVerified = uiState.isVerified,
                    isError = uiState.accountHolderError != null,
                    errorMessage = uiState.accountHolderError,
                )

                FieldLabel(t("बैंक नाम", "Bank Name"))
                WiomTextField(
                    value = uiState.bankName,
                    onValueChange = { viewModel.onBankNameChanged(it) },
                    placeholder = t("बैंक नाम डालें", "Enter bank name"),
                    isVerified = uiState.isVerified,
                    isError = uiState.bankNameError != null,
                    errorMessage = uiState.bankNameError,
                )

                FieldLabel(t("अकाउंट नंबर", "Account Number"))
                WiomTextField(
                    value = uiState.accountNumber,
                    onValueChange = { viewModel.onAccountNumberChanged(it) },
                    placeholder = t("अकाउंट नंबर डालें", "Enter account number"),
                    isVerified = uiState.isVerified,
                    isError = uiState.accountNumberError != null,
                    errorMessage = uiState.accountNumberError,
                )

                FieldLabel("IFSC Code")
                WiomTextField(
                    value = uiState.ifsc,
                    onValueChange = { viewModel.onIfscChanged(it) },
                    placeholder = t("IFSC कोड डालें", "Enter IFSC code"),
                    isVerified = uiState.isVerified,
                    isError = uiState.ifscError != null,
                    errorMessage = uiState.ifscError,
                )

                // Show verification result cards
                if (uiState.isVerified && uiState.verificationResult != null) {
                    WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                        Text(
                            "✓ ${t("पेनी ड्रॉप वेरीफाइड", "PENNY DROP VERIFIED")}",
                            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t("₹1 क्रेडिट और वेरीफाइड — नाम मैच कन्फ़र्म", "₹1 credited & verified — Name match confirmed"),
                            fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    WiomCard(borderColor = WiomPositive300, backgroundColor = WiomPositive100) {
                        Text(
                            "✓ ${t("डीडप चेक पास", "DEDUP CHECK PASSED")}",
                            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t("PAN, आधार, GST, Bank — कोई डुप्लिकेट नहीं मिला", "PAN, Aadhaar, GST, Bank — No duplicates found"),
                            fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                        )
                    }
                } else if (uiState.verificationError != null) {
                    Spacer(Modifier.height(8.dp))
                    ErrorCard(
                        icon = "\uD83C\uDFE6",
                        titleHi = "सत्यापन विफल",
                        titleEn = "Verification Failed",
                        messageHi = uiState.verificationError ?: "",
                        messageEn = uiState.verificationError ?: "",
                        type = "error",
                    )
                } else if (!uiState.isFormValid) {
                    InfoBox("\uD83D\uDCDD", t("सभी बैंक डिटेल्स भरें", "Fill all bank details to proceed"))
                }
            }
            BottomBar {
                if (uiState.isVerified) {
                    WiomButton(t("अब ISP अनुबंध अपलोड करें", "Next: ISP Agreement"), onClick = onNext)
                } else {
                    WiomButton(
                        t("Penny Drop Verify करें", "Verify via Penny Drop"),
                        onClick = { viewModel.verifyBankAccount { onNext() } },
                        enabled = uiState.isFormValid,
                    )
                }
            }
        }

        // Loading overlay
        LoadingOverlay(
            messageHi = "Penny Drop वेरीफाई हो रहा है...",
            messageEn = "Verifying via Penny Drop...",
            isVisible = uiState.isLoading,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// Screen 7: ISP Agreement Upload (NEW)
// ═══════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IspAgreementScreen(viewModel: IspAgreementViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Bottom sheet state
    var sheetTarget by remember { mutableStateOf<String?>(null) }
    var sheetStep by remember { mutableStateOf("choose") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sheetDocLabel = t("ISP अनुबंध", "ISP Agreement")

    fun simulateUpload() {
        sheetStep = "uploading"
    }

    fun markUploaded() {
        viewModel.uploadAgreement("simulated_uri")
        sheetTarget = null
        sheetStep = "choose"
    }

    // Bottom Sheet
    if (sheetTarget != null) {
        ModalBottomSheet(
            onDismissRequest = { sheetTarget = null; sheetStep = "choose" },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
            ) {
                when (sheetStep) {
                    "choose" -> {
                        Text(
                            t("$sheetDocLabel अपलोड करें", "Upload $sheetDocLabel"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiomText,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t("फ़ोटो कैसे लेना चाहेंगे?", "How would you like to capture?"),
                            fontSize = 14.sp,
                            color = WiomTextSec,
                        )
                        Spacer(Modifier.height(16.dp))

                        // Camera option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(WiomBgSec)
                                .clickable { sheetStep = "preview" }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WiomPrimaryLight),
                                contentAlignment = Alignment.Center,
                            ) { Text("\uD83D\uDCF7", fontSize = 24.sp) }
                            Column {
                                Text(t("कैमरा से फ़ोटो लें", "Take Photo"), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WiomText)
                                Text(t("सीधे कैमरा खोलें और फ़ोटो क्लिक करें", "Open camera and click photo"), fontSize = 13.sp, color = WiomTextSec)
                            }
                        }
                        Spacer(Modifier.height(10.dp))

                        // Gallery option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(WiomBgSec)
                                .clickable { sheetStep = "preview" }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WiomInfo100),
                                contentAlignment = Alignment.Center,
                            ) { Text("\uD83D\uDDBC\uFE0F", fontSize = 24.sp) }
                            Column {
                                Text(t("गैलरी से चुनें", "Choose from Gallery"), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WiomText)
                                Text(t("फ़ोन में सेव फ़ोटो में से चुनें", "Pick from saved photos"), fontSize = 13.sp, color = WiomTextSec)
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        InfoBox(
                            "\uD83D\uDCA1",
                            t("साफ़ फ़ोटो लें — सारे अक्षर दिखने चाहिए", "Take a clear photo — all text must be visible"),
                            type = InfoBoxType.INFO,
                        )
                    }

                    "preview" -> {
                        Text(
                            t("फ़ोटो रिव्यू करें", "Review Photo"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiomText,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(sheetDocLabel, fontSize = 14.sp, color = WiomTextSec)
                        Spacer(Modifier.height(16.dp))

                        // Simulated photo preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(WiomBgSec)
                                .border(2.dp, WiomBorder, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("\uD83D\uDCDC", fontSize = 48.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    sheetDocLabel,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = WiomTextSec,
                                )
                                Text(
                                    t("फ़ोटो कैप्चर हुई", "Photo captured"),
                                    fontSize = 12.sp,
                                    color = WiomPositive,
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            TrustBadge("✓", t("साफ़ दिख रहा है", "Clear"))
                            TrustBadge("✓", t("पूरा दिख रहा है", "Complete"))
                        }
                        Spacer(Modifier.height(16.dp))

                        WiomButton(
                            t("यह फ़ोटो सेव करें", "Save this photo"),
                            onClick = { simulateUpload() },
                        )
                        Spacer(Modifier.height(8.dp))
                        WiomButton(
                            t("दोबारा फ़ोटो लें", "Retake photo"),
                            onClick = { sheetStep = "choose" },
                            isSecondary = true,
                        )
                    }

                    "uploading" -> {
                        var uploadProgress by remember { mutableStateOf(0f) }
                        LaunchedEffect(Unit) {
                            while (uploadProgress < 1f) {
                                delay(80)
                                uploadProgress = (uploadProgress + 0.05f).coerceAtMost(1f)
                            }
                            delay(300)
                            markUploaded()
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Spacer(Modifier.height(16.dp))
                            if (uploadProgress < 1f) {
                                CircularProgressIndicator(
                                    progress = { uploadProgress },
                                    color = WiomPrimary,
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(56.dp),
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(WiomPositive100),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("✓", fontSize = 28.sp, color = WiomPositive, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                if (uploadProgress < 1f) t("अपलोड हो रहा है...", "Uploading...")
                                else t("अपलोड हो गया!", "Upload complete!"),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uploadProgress < 1f) WiomText else WiomPositive,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                sheetDocLabel,
                                fontSize = 14.sp,
                                color = WiomTextSec,
                            )
                            Spacer(Modifier.height(12.dp))
                            if (uploadProgress < 1f) {
                                LinearProgressIndicator(
                                    progress = { uploadProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = WiomPrimary,
                                    trackColor = WiomBgSec,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${(uploadProgress * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    color = WiomTextSec,
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("ISP अनुबंध अपलोड", "ISP Agreement Upload"),
            onBack = onBack,
            rightText = t("स्टेप 3/5", "Step 3/5")
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                t("ISP अनुबंध अपलोड", "ISP Agreement Upload"),
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("DOT अनुपालन के लिए अनिवार्य दस्तावेज़", "Mandatory document for DOT compliance"),
                fontSize = 14.sp, color = WiomTextSec,
            )
            Spacer(Modifier.height(16.dp))

            // Info card explaining DOT compliance
            WiomCard(borderColor = WiomInfo, backgroundColor = WiomInfo100) {
                Text(
                    "\uD83D\uDCCB ${t("DOT अनुपालन", "DOT Compliance")}",
                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomInfo,
                    letterSpacing = 0.5.sp,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    t("DOT अनुपालन के लिए ISP अनुबंध अनिवार्य है। यह दस्तावेज़ दूरसंचार विभाग की जांच के लिए आवश्यक है।",
                      "ISP Agreement is mandatory for DOT compliance. This document is required for Department of Telecom verification."),
                    fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                )
            }
            Spacer(Modifier.height(12.dp))

            // Compliance badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TrustBadge("\uD83D\uDD12", "DOT Compliance")
                TrustBadge("✓", "TRAI Guidelines")
            }
            Spacer(Modifier.height(16.dp))

            // Normal happy path using ViewModel
            if (uiState.isUploaded) {
                // Uploaded state (green with DOT Ready badge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiomPositive100)
                        .border(1.dp, WiomPositive300, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(WiomBgSec),
                            contentAlignment = Alignment.Center,
                        ) { Text("\uD83D\uDCDC", fontSize = 16.sp) }
                        Column {
                            Text(
                                "\uD83D\uDCDC ${t("ISP अनुबंध", "ISP Agreement")}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = WiomText,
                            )
                            Text(
                                t("DOT Ready ✓", "DOT Ready ✓"),
                                fontSize = 12.sp,
                                color = WiomPositive,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                    // Remove button
                    Text(
                        t("हटाएं", "Remove"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WiomNegative,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WiomNegative100)
                            .clickable { viewModel.removeAgreement() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            } else {
                // Not uploaded state (gray)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, WiomBorderInput, RoundedCornerShape(12.dp))
                        .clickable { sheetTarget = "isp_agreement" }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83D\uDCE4", fontSize = 24.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "\uD83D\uDCDC ${t("ISP अनुबंध अपलोड करें", "Upload ISP Agreement")}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                    Text(
                        t("टैप करें", "Tap to upload"),
                        fontSize = 12.sp,
                        color = WiomHint,
                    )
                }
            }
        }
        BottomBar {
            WiomButton(
                t("आगे बढ़ें", "Proceed"),
                onClick = onNext,
                enabled = uiState.isUploaded,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Screen 8: Shop & Equipment Photos (NEW)
// ═══════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopPhotosScreen(viewModel: PhotosViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Bottom sheet state
    var sheetTarget by remember { mutableStateOf<String?>(null) }
    var sheetStep by remember { mutableStateOf("choose") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sheetDocLabel = when (sheetTarget) {
        "shop" -> t("दुकान की फ़ोटो", "Shop Front Photo")
        "equipment" -> t("राउटर/उपकरण फ़ोटो", "Router/Equipment Photo")
        else -> ""
    }

    fun simulateUpload() {
        sheetStep = "uploading"
    }

    fun markUploaded() {
        when (sheetTarget) {
            "shop" -> viewModel.uploadShopPhoto("simulated_uri")
            "equipment" -> viewModel.uploadEquipmentPhoto("simulated_uri")
        }
        sheetTarget = null
        sheetStep = "choose"
    }

    // Bottom Sheet
    if (sheetTarget != null) {
        ModalBottomSheet(
            onDismissRequest = { sheetTarget = null; sheetStep = "choose" },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
            ) {
                when (sheetStep) {
                    "choose" -> {
                        Text(
                            t("$sheetDocLabel अपलोड करें", "Upload $sheetDocLabel"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiomText,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t("फ़ोटो कैसे लेना चाहेंगे?", "How would you like to capture?"),
                            fontSize = 14.sp,
                            color = WiomTextSec,
                        )
                        Spacer(Modifier.height(16.dp))

                        // Camera option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(WiomBgSec)
                                .clickable { sheetStep = "preview" }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WiomPrimaryLight),
                                contentAlignment = Alignment.Center,
                            ) { Text("\uD83D\uDCF7", fontSize = 24.sp) }
                            Column {
                                Text(t("कैमरा से फ़ोटो लें", "Take Photo"), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WiomText)
                                Text(t("सीधे कैमरा खोलें और फ़ोटो क्लिक करें", "Open camera and click photo"), fontSize = 13.sp, color = WiomTextSec)
                            }
                        }
                        Spacer(Modifier.height(10.dp))

                        // Gallery option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(WiomBgSec)
                                .clickable { sheetStep = "preview" }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WiomInfo100),
                                contentAlignment = Alignment.Center,
                            ) { Text("\uD83D\uDDBC\uFE0F", fontSize = 24.sp) }
                            Column {
                                Text(t("गैलरी से चुनें", "Choose from Gallery"), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WiomText)
                                Text(t("फ़ोन में सेव फ़ोटो में से चुनें", "Pick from saved photos"), fontSize = 13.sp, color = WiomTextSec)
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        InfoBox(
                            "\uD83D\uDCA1",
                            if (sheetTarget == "shop")
                                t("दुकान का नाम बोर्ड और पूरा दुकान दिखना चाहिए", "Shop name board and full shop should be visible")
                            else
                                t("राउटर और उपकरण का मॉडल नंबर दिखना चाहिए", "Router and equipment model number should be visible"),
                            type = InfoBoxType.INFO,
                        )
                    }

                    "preview" -> {
                        Text(
                            t("फ़ोटो रिव्यू करें", "Review Photo"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiomText,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(sheetDocLabel, fontSize = 14.sp, color = WiomTextSec)
                        Spacer(Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(WiomBgSec)
                                .border(2.dp, WiomBorder, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    if (sheetTarget == "shop") "\uD83C\uDFEA" else "\uD83D\uDCE1",
                                    fontSize = 48.sp,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    sheetDocLabel,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = WiomTextSec,
                                )
                                Text(
                                    t("फ़ोटो कैप्चर हुई", "Photo captured"),
                                    fontSize = 12.sp,
                                    color = WiomPositive,
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            TrustBadge("✓", t("साफ़ दिख रहा है", "Clear"))
                            TrustBadge("✓", t("पूरा दिख रहा है", "Complete"))
                        }
                        Spacer(Modifier.height(16.dp))

                        WiomButton(
                            t("यह फ़ोटो सेव करें", "Save this photo"),
                            onClick = { simulateUpload() },
                        )
                        Spacer(Modifier.height(8.dp))
                        WiomButton(
                            t("दोबारा फ़ोटो लें", "Retake photo"),
                            onClick = { sheetStep = "choose" },
                            isSecondary = true,
                        )
                    }

                    "uploading" -> {
                        var uploadProgress by remember { mutableStateOf(0f) }
                        LaunchedEffect(Unit) {
                            while (uploadProgress < 1f) {
                                delay(80)
                                uploadProgress = (uploadProgress + 0.05f).coerceAtMost(1f)
                            }
                            delay(300)
                            markUploaded()
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Spacer(Modifier.height(16.dp))
                            if (uploadProgress < 1f) {
                                CircularProgressIndicator(
                                    progress = { uploadProgress },
                                    color = WiomPrimary,
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(56.dp),
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(WiomPositive100),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("✓", fontSize = 28.sp, color = WiomPositive, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                if (uploadProgress < 1f) t("अपलोड हो रहा है...", "Uploading...")
                                else t("अपलोड हो गया!", "Upload complete!"),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uploadProgress < 1f) WiomText else WiomPositive,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                sheetDocLabel,
                                fontSize = 14.sp,
                                color = WiomTextSec,
                            )
                            Spacer(Modifier.height(12.dp))
                            if (uploadProgress < 1f) {
                                LinearProgressIndicator(
                                    progress = { uploadProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = WiomPrimary,
                                    trackColor = WiomBgSec,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${(uploadProgress * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    color = WiomTextSec,
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("दुकान और उपकरण फ़ोटो", "Shop & Equipment Photos"),
            onBack = onBack,
            rightText = t("स्टेप 4/5", "Step 4/5")
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                t("दुकान और उपकरण फ़ोटो", "Shop & Equipment Photos"),
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("सत्यापन के लिए दोनों फ़ोटो अपलोड करें", "Upload both photos for verification"),
                fontSize = 14.sp, color = WiomTextSec,
            )
            Spacer(Modifier.height(16.dp))

            // Shop Front Photo
            Text(
                t("1. दुकान की फ़ोटो", "1. Shop Front Photo"),
                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("दुकान का नाम बोर्ड और पूरा दुकान दिखना चाहिए", "Shop name board and full shop should be visible"),
                fontSize = 12.sp, color = WiomTextSec,
            )
            Spacer(Modifier.height(8.dp))

            if (uiState.shopPhotoUploaded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiomPositive100)
                        .border(1.dp, WiomPositive300, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(WiomBgSec),
                            contentAlignment = Alignment.Center,
                        ) { Text("\uD83C\uDFEA", fontSize = 16.sp) }
                        Column {
                            Text(
                                "\uD83D\uDCF7 ${t("दुकान की फ़ोटो", "Shop Front Photo")}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = WiomText,
                            )
                            Text(
                                t("अपलोड \u2713", "Uploaded \u2713"),
                                fontSize = 12.sp,
                                color = WiomPositive,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                    Text(
                        t("हटाएं", "Remove"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WiomNegative,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WiomNegative100)
                            .clickable { viewModel.removeShopPhoto() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, WiomBorderInput, RoundedCornerShape(12.dp))
                        .clickable { sheetTarget = "shop" }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83D\uDCE4", fontSize = 24.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "\uD83D\uDCF7 ${t("दुकान की फ़ोटो अपलोड करें", "Upload Shop Front Photo")}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                    Text(
                        t("टैप करें", "Tap to upload"),
                        fontSize = 12.sp,
                        color = WiomHint,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Equipment Photo
            Text(
                t("2. राउटर/उपकरण फ़ोटो", "2. Router/Equipment Photo"),
                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("राउटर और उपकरण का मॉडल नंबर दिखना चाहिए", "Router and equipment model number should be visible"),
                fontSize = 12.sp, color = WiomTextSec,
            )
            Spacer(Modifier.height(8.dp))

            if (uiState.equipmentPhotoUploaded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiomPositive100)
                        .border(1.dp, WiomPositive300, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(WiomBgSec),
                            contentAlignment = Alignment.Center,
                        ) { Text("\uD83D\uDCE1", fontSize = 16.sp) }
                        Column {
                            Text(
                                "\uD83D\uDCF7 ${t("राउटर/उपकरण फ़ोटो", "Router/Equipment Photo")}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = WiomText,
                            )
                            Text(
                                t("अपलोड \u2713", "Uploaded \u2713"),
                                fontSize = 12.sp,
                                color = WiomPositive,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                    Text(
                        t("हटाएं", "Remove"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WiomNegative,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WiomNegative100)
                            .clickable { viewModel.removeEquipmentPhoto() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, WiomBorderInput, RoundedCornerShape(12.dp))
                        .clickable { sheetTarget = "equipment" }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83D\uDCE4", fontSize = 24.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "\uD83D\uDCF7 ${t("राउटर/उपकरण फ़ोटो अपलोड करें", "Upload Router/Equipment Photo")}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                    Text(
                        t("टैप करें", "Tap to upload"),
                        fontSize = 12.sp,
                        color = WiomHint,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            if (uiState.bothUploaded) {
                InfoBox("✓", t("दोनों फ़ोटो अपलोड हो गई! सत्यापन के लिए जमा करें।", "Both photos uploaded! Submit for verification."), type = InfoBoxType.SUCCESS)
            } else {
                InfoBox("\uD83D\uDCDD", t("दोनों फ़ोटो अपलोड करें", "Upload both photos to proceed"))
            }
        }
        BottomBar {
            WiomButton(
                t("सत्यापन के लिए जमा करें", "Submit for Verification"),
                onClick = onNext,
                enabled = uiState.bothUploaded,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Screen 9: Verification (Branch Point 1)
// ═══════════════════════════════════════════════════════════════════
@Composable
fun VerificationScreen(viewModel: VerificationViewModel, onNext: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("Application स्टेटस", "Application Status")
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
                            t("प्रोफ़ाइल अभी स्वीकृत नहीं हुई", "Profile not accepted yet"),
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
                                uiState.reason.ifBlank {
                                    t(
                                        "Location इस समय service area में नहीं है। Area infrastructure तैयार होने पर दोबारा apply कर सकते हैं।",
                                        "Location is not in the service area currently. You can re-apply once area infrastructure is ready."
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
                                t("₹2,000 आपके bank account में 5-7 working days में आ जाएगा।", "₹2,000 will be credited to your bank account in 5-7 working days."),
                                fontSize = 14.sp, color = WiomText, lineHeight = 20.sp,
                            )
                            Text("Ref: RFD-2026-0042", fontSize = 12.sp, color = WiomHint)
                        }
                        if (uiState.reuploadCount > 0) {
                            Spacer(Modifier.height(8.dp))
                            InfoBox(
                                "\uD83D\uDD04",
                                t("पुनः अपलोड प्रयास: ${uiState.reuploadCount}", "Re-upload attempts: ${uiState.reuploadCount}")
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoBox("\uD83D\uDD14", t("जब area तैयार हो, तो दोबारा apply कर सकते हैं।", "You can re-apply when the area is ready."))
                        Spacer(Modifier.height(12.dp))
                        WiomButton(
                            "\u2190 ${t("Approved Path देखें", "View Approved Path")}",
                            onClick = {
                                viewModel.setDecision(VerificationStatus.PENDING)
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
                        Text("✓", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("सत्यापन स्वीकृत!", "Verification Approved!"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomPositive,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t("बधाई हो! आप अगले चरण पर जा सकते हैं।", "Congratulations! You can proceed to the next stage."),
                            fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    ChecklistItem(t("KYC दस्तावेज़ वेरीफाइड", "KYC Documents Verified"))
                    ChecklistItem(t("बैंक वेरिफिकेशन पूरा", "Bank Verification Complete"))
                    ChecklistItem(t("ISP अनुबंध अपलोड", "ISP Agreement Uploaded"))
                    ChecklistItem(t("दुकान और उपकरण फ़ोटो", "Shop & Equipment Photos"))
                    ChecklistItem(t("सत्यापन स्वीकृत", "Verification Approved"))
                }

                VerificationStatus.PENDING -> {
                    // ─── Pending state ───
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("\uD83D\uDD0D", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("सत्यापन जारी है", "Verification in progress"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t("Wiom team आपकी profile चेक कर रही है", "Wiom team is reviewing your profile"),
                            fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    ChecklistItem(t("KYC दस्तावेज़ वेरीफाइड", "KYC Documents Verified"))
                    ChecklistItem(t("बैंक वेरिफिकेशन पूरा", "Bank Verification Complete"))
                    ChecklistItem(t("ISP अनुबंध अपलोड", "ISP Agreement Uploaded"))
                    ChecklistItem(t("दुकान और उपकरण फ़ोटो", "Shop & Equipment Photos"))
                    ChecklistItem(
                        t("सत्यापन", "Verification"),
                        subtitle = t("Wiom team review कर रही है...", "Wiom team is reviewing..."),
                        isDone = false,
                        isWaiting = true,
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoBox("\u23F3", t("Review में 2-3 business days लग सकते हैं। Notification मिलेगा।", "Review may take 2-3 business days. You will be notified."), type = InfoBoxType.WARNING)
                        Spacer(Modifier.height(12.dp))
                        // Demo toggle button
                        WiomButton(
                            "\uD83D\uDD04 ${t("Rejected Path देखें", "View Rejected Path")}",
                            onClick = {
                                viewModel.setDecision(
                                    VerificationStatus.REJECTED,
                                    t(
                                        "Location इस समय service area में नहीं है। Area infrastructure तैयार होने पर दोबारा apply कर सकते हैं।",
                                        "Location is not in the service area currently. You can re-apply once area infrastructure is ready."
                                    )
                                )
                            },
                            isSecondary = true,
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onNext,
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                        ) {
                            Text("\uD83D\uDD27 Dev: Skip to Stage 3 →", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
        if (uiState.status != VerificationStatus.REJECTED) {
            BottomBar {
                WiomButton(
                    t("आगे बढ़ें (Approved)", "Proceed (Approved)"),
                    onClick = onNext,
                )
            }
        }
    }
}
