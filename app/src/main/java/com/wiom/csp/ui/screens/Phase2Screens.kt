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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiom.csp.data.BankVerificationStatus
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.Scenario
import com.wiom.csp.ui.components.*
import com.wiom.csp.ui.theme.*
import com.wiom.csp.ui.viewmodel.*
import com.wiom.csp.util.t
import kotlinx.coroutines.delay

/**
 * Phase 2 Screens — Verification (Screens 6-9)
 * Note: Screen 5 (KYC) is in Phase1Screens.kt (3 sub-stages)
 *
 * Screen 6: Bank Details (Step 2/5, header "Verification")
 * Screen 7: ISP Agreement (Step 3/5, header "Verification")
 * Screen 8: Shop & Equipment Photos (Step 4/5, header "Verification")
 * Screen 9: Verification Status (Step 5/5, header "Verification")
 */

// ═══════════════════════════════════════════════════════════════════════════
// Screen 6: Bank Details
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun BankDetailsScreen(viewModel: BankViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("सत्यापन", "Verification"),
            onBack = onBack,
            rightText = t("स्टेप 2/5", "Step 2/5"),
        )

        when (state.verificationStatus) {
            BankVerificationStatus.VERIFYING -> {
                // Spinner
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("⏳", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(t("बैंक विवरण सत्यापित हो रहे हैं...", "Verifying bank details..."), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText)
                    Spacer(Modifier.height(8.dp))
                    Text(t("पेनी ड्रॉप सत्यापन जारी है", "Penny drop verification in progress"), fontSize = 13.sp, color = WiomTextSec)
                    Spacer(Modifier.height(24.dp))
                    CircularProgressIndicator(color = WiomPrimary, modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                }
            }

            BankVerificationStatus.SUCCESS -> {
                // Verified delight
                Column(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("✅", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(t("बैंक विवरण सत्यापित!", "Bank Details Verified!"), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomPositive)
                    Spacer(Modifier.height(8.dp))
                    Text(t("पेनी ड्रॉप सफल — नाम मैच कन्फ़र्म", "Penny drop successful — name match confirmed"), fontSize = 13.sp, color = WiomTextSec)
                    Spacer(Modifier.height(20.dp))
                    WiomCard(borderColor = WiomPositive, backgroundColor = WiomPositive100) {
                        FeeDetailRow(t("खाताधारक", "Account Holder"), state.bankAccountHolderName ?: "Rajesh Kumar")
                        HorizontalDivider(color = WiomPositive200)
                        FeeDetailRow(t("बैंक", "Bank"), state.bankName ?: "State Bank of India")
                        HorizontalDivider(color = WiomPositive200)
                        FeeDetailRow(t("स्टेटस", "Status"), "✓ ${t("सत्यापित", "Verified")}", valueColor = WiomPositive)
                    }
                }
                BottomBar {
                    WiomButton(t("ISP अनुबंध जोड़ें", "Add ISP Agreement"), onClick = onNext)
                }
            }

            else -> {
                // Default entry form (also handles supporting doc upload when supportDocType is set)
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)
                ) {
                    // Supporting document upload UI (shown after penny-drop fail or name mismatch)
                    if (state.supportDocType != null) {
                        Text(
                            "\uD83D\uDCC4 ${t("बैंक सहायक दस्तावेज़ अपलोड करें", "Upload Bank Supporting Document")}",
                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t(
                                "कृपया बैंक पासबुक, चेक बुक, या बैंक स्टेटमेंट अपलोड करें",
                                "Please upload bank passbook, cheque book, or bank statement"
                            ),
                            fontSize = 13.sp, color = WiomTextSec, lineHeight = 18.sp,
                        )
                        Spacer(Modifier.height(14.dp))
                        UploadRow(
                            icon = "\uD83C\uDFE6",
                            label = t("बैंक दस्तावेज़", "Bank Document"),
                            isUploaded = state.supportDocUploaded,
                            onUpload = { viewModel.onSupportDocUploaded() },
                        )
                        Spacer(Modifier.height(12.dp))
                        if (state.supportDocUploaded) {
                            InfoBox(
                                "✓",
                                t("दस्तावेज़ अपलोड हो गया। आप आगे बढ़ सकते हैं।", "Document uploaded. You can proceed."),
                                type = InfoBoxType.SUCCESS,
                            )
                        } else {
                            InfoBox(
                                "ℹ️",
                                t(
                                    "दस्तावेज़ में बैंक खाता संख्या और नाम दिखना चाहिए",
                                    "Document should show bank account number and name"
                                ),
                            )
                        }
                    } else {

                    Text("\uD83C\uDFE6 ${t("बैंक विवरण", "Bank Details")}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText)
                    Spacer(Modifier.height(4.dp))
                    Text(t("अपने बैंक खाते की जानकारी दर्ज करें", "Enter your bank account details"), fontSize = 13.sp, color = WiomTextSec, lineHeight = 18.sp)
                    Spacer(Modifier.height(8.dp))

                    // Ownership hint
                    Surface(shape = RoundedCornerShape(8.dp), color = WiomBgSec, modifier = Modifier.fillMaxWidth().border(1.dp, WiomBorder, RoundedCornerShape(8.dp))) {
                        Text(
                            "ℹ️ ${t("बैंक विवरण", "Bank details should belong to")} ${state.personalName.ifEmpty { "Rajesh Kumar" }} ${t("या", "or")} ${state.tradeName.ifEmpty { "Kumar Electronics" }} ${t("के नाम पर होने चाहिए", "")}",
                            modifier = Modifier.padding(8.dp, 10.dp), fontSize = 12.sp, color = WiomTextSec, lineHeight = 16.sp,
                        )
                    }
                    Spacer(Modifier.height(14.dp))

                    // Account Number
                    FieldLabel(t("बैंक खाता संख्या", "Bank Account Number"))
                    WiomTextField(
                        value = state.accountNumber,
                        onValueChange = { viewModel.onAccountNumberChanged(it) },
                        placeholder = t("बैंक खाता संख्या दर्ज करें", "Enter bank account number"),
                        isVerified = state.accountNumber.length >= 9,
                        isError = state.accountNumberError != null,
                        errorMessage = state.accountNumberError,
                        visualTransformation = if (state.isAccountBlurred && state.accountNumber.length >= 9) PasswordVisualTransformation() else VisualTransformation.None,
                        onFocusChanged = { if (!it) viewModel.onAccountNumberBlurred() },
                    )

                    // Re-enter Account Number
                    FieldLabel(t("बैंक खाता संख्या दोबारा दर्ज करें", "Re-enter Bank Account Number"))
                    WiomTextField(
                        value = state.accountNumberConfirm,
                        onValueChange = { viewModel.onAccountNumberConfirmChanged(it) },
                        placeholder = t("बैंक खाता संख्या दोबारा दर्ज करें", "Re-enter bank account number"),
                        isVerified = state.accountNumberConfirm.isNotEmpty() && state.accountNumberConfirm == state.accountNumber,
                        isError = state.accountNumberConfirmError != null,
                        errorMessage = state.accountNumberConfirmError,
                        onFocusChanged = { if (!it) viewModel.onAccountNumberConfirmBlurred() },
                    )

                    // IFSC Code
                    FieldLabel(t("IFSC कोड", "IFSC Code"))
                    WiomTextField(
                        value = state.ifsc,
                        onValueChange = { viewModel.onIfscChanged(it) },
                        placeholder = t("उदा. SBIN0001234", "e.g. SBIN0001234"),
                        isVerified = state.ifsc.length == 11 && state.ifscError == null,
                        isError = state.ifscError != null,
                        errorMessage = state.ifscError,
                        onFocusChanged = { if (!it) viewModel.onIfscBlurred() },
                    )
                    } // end else (normal form vs support doc upload)
                }
                BottomBar {
                    if (state.supportDocType != null) {
                        // Support doc flow: proceed when uploaded
                        WiomButton(
                            t("आगे बढ़ें", "Proceed"),
                            onClick = onNext,
                            enabled = state.supportDocUploaded,
                        )
                    } else {
                        WiomButton(
                            t("बैंक विवरण सत्यापित करें", "Verify Bank Details"),
                            onClick = { viewModel.verifyBankDetails(onNext) },
                            enabled = state.isFormValid,
                        )
                    }
                }
            }
        }

        // Bottom sheet overlays for failure states
        if (state.verificationStatus == BankVerificationStatus.PENNY_DROP_FAIL) {
            BankFailBottomSheet(
                title = t("बैंक खाता सत्यापन विफल", "Bank Account Verification Failed"),
                subtitle = t("हम आपके बैंक विवरण सत्यापित नहीं कर पाए", "We could not verify your bank details"),
                icon = "✗",
                iconColor = WiomNegative,
                iconBg = WiomNegative100,
                onChangeBankDetails = { viewModel.onChangeBankDetails() },
                onUploadDoc = { viewModel.onUploadSupportingDoc("pennydrop") },
                showUploadOption = true,
            )
        }

        if (state.verificationStatus == BankVerificationStatus.NAME_MISMATCH) {
            BankMismatchBottomSheet(
                bankName = state.bankAccountHolderName ?: "Rajesh Kumar Sharma",
                personalName = state.personalName.ifEmpty { "Rajesh Kumar" },
                tradeName = state.tradeName.ifEmpty { "Kumar Electronics" },
                onChangeBankDetails = { viewModel.onChangeBankDetails() },
                onUploadDoc = { viewModel.onUploadSupportingDoc("mismatch") },
            )
        }

        if (state.verificationStatus == BankVerificationStatus.DEDUP) {
            BankFailBottomSheet(
                title = t("बैंक खाता पहले से जुड़ा है", "Bank Account Already Linked"),
                subtitle = t("यह बैंक खाता संख्या एक और Wiom अकाउंट से जुड़ा है जिसका मोबाइल नंबर ****${state.duplicateAccountPhone ?: "4567"} है", "This bank account number is linked with another Wiom account number ending with ${state.duplicateAccountPhone ?: "4567"}"),
                icon = "\uD83D\uDD0D",
                iconColor = WiomNegative,
                iconBg = WiomNegative100,
                onChangeBankDetails = { viewModel.onChangeBankDetails() },
                onUploadDoc = null,
                showUploadOption = false,
            )
        }
    }
}

@Composable
private fun BankFailBottomSheet(
    title: String, subtitle: String, icon: String,
    iconColor: Color, iconBg: Color,
    onChangeBankDetails: () -> Unit,
    onUploadDoc: (() -> Unit)?,
    showUploadOption: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.5f),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = Color.White,
            ) {
                Column(modifier = Modifier.padding(28.dp, 28.dp, 28.dp, 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    // Drag handle
                    Box(Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(WiomBorderInput))
                    Spacer(Modifier.height(20.dp))
                    // Icon circle
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(iconBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(icon, fontSize = 28.sp, color = iconColor)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = WiomText, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(subtitle, fontSize = 13.sp, color = WiomTextSec, textAlign = TextAlign.Center, lineHeight = 18.sp)
                    Spacer(Modifier.height(24.dp))
                    WiomButton(t("बैंक विवरण बदलें", "Change Bank Details"), onClick = onChangeBankDetails)
                    if (showUploadOption && onUploadDoc != null) {
                        Spacer(Modifier.height(10.dp))
                        WiomButton(t("बैंक दस्तावेज़ अपलोड करें", "Upload Bank Document"), onClick = onUploadDoc, isSecondary = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun BankMismatchBottomSheet(
    bankName: String, personalName: String, tradeName: String,
    onChangeBankDetails: () -> Unit, onUploadDoc: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.5f),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = Color.White,
            ) {
                Column(modifier = Modifier.padding(28.dp, 28.dp, 28.dp, 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(WiomBorderInput))
                    Spacer(Modifier.height(20.dp))
                    Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(WiomWarning200), contentAlignment = Alignment.Center) {
                        Text("⚠", fontSize = 28.sp, color = WiomWarning700)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(t("बैंक खाता नाम मेल नहीं खाता", "Bank Account Name Mismatch"), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = WiomText, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    // Name comparison
                    Surface(shape = RoundedCornerShape(8.dp), color = WiomBgSec, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(10.dp, 12.dp)) {
                            FeeDetailRow(t("बैंक में नाम", "Name in Bank"), bankName)
                            HorizontalDivider(color = WiomBorder)
                            FeeDetailRow(t("व्यक्तिगत नाम", "Personal Name"), personalName)
                            HorizontalDivider(color = WiomBorder)
                            FeeDetailRow(t("व्यापार नाम", "Business Name"), tradeName)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    WiomButton(t("बैंक विवरण बदलें", "Change Bank Details"), onClick = onChangeBankDetails)
                    Spacer(Modifier.height(10.dp))
                    WiomButton(t("बैंक दस्तावेज़ अपलोड करें", "Upload Bank Document"), onClick = onUploadDoc, isSecondary = true)
                }
            }
        }
    }
}

@Composable
private fun FeeDetailRow(label: String, value: String, valueColor: Color = WiomText) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = WiomTextSec)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Screen 7: ISP Agreement Upload
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun IspAgreementScreen(viewModel: IspAgreementViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("सत्यापन", "Verification"),
            onBack = onBack,
            rightText = t("स्टेप 3/5", "Step 3/5"),
        )
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text("\uD83D\uDCC4 ${t("ISP अनुबंध अपलोड करें", "Upload ISP Agreement")}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText)
            Spacer(Modifier.height(4.dp))
            Text(
                t("दूरसंचार विभाग (DOT) अनुपालन जांच के लिए हमें आपके इंटरनेट सर्विस प्रोवाइडर के साथ आपके कानूनी अनुबंध की आवश्यकता है", "We need your legal agreement with your Internet Service Provider for Department of Telecommunication compliance check"),
                fontSize = 13.sp, color = WiomTextSec, lineHeight = 18.sp,
            )
            Spacer(Modifier.height(14.dp))

            // Upload options (3 methods per prototype)
            var showUploadOptions by remember { mutableStateOf(false) }

            if (state.isUploaded) {
                UploadRow(
                    icon = "\uD83D\uDCC4",
                    label = t("ISP अनुबंध", "ISP Agreement"),
                    isUploaded = true,
                    onUpload = { viewModel.resetUpload(); showUploadOptions = false },
                )
            } else if (showUploadOptions) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WiomBorder),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            t("अपलोड का तरीका चुनें", "Choose upload method"),
                            fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        )
                        Spacer(Modifier.height(8.dp))
                        // Option 1: PDF
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = WiomBgSec,
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.uploadPdf(); showUploadOptions = false },
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("\uD83D\uDCC4", fontSize = 20.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(t("PDF अपलोड", "PDF Upload"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomText)
                                    Text(t("PDF फ़ाइल चुनें", "Select PDF file"), fontSize = 12.sp, color = WiomTextSec)
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        // Option 2: Camera
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = WiomBgSec,
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.uploadPdf(); showUploadOptions = false },
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("\uD83D\uDCF7", fontSize = 20.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(t("कैमरा", "Camera"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomText)
                                    Text(t("7 पेज तक फ़ोटो लें", "Take photos up to 7 pages"), fontSize = 12.sp, color = WiomTextSec)
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        // Option 3: Gallery
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = WiomBgSec,
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.uploadPdf(); showUploadOptions = false },
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("\uD83D\uDDBC\uFE0F", fontSize = 20.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(t("गैलरी", "Gallery"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomText)
                                    Text(t("7 पेज तक चुनें", "Select up to 7 pages"), fontSize = 12.sp, color = WiomTextSec)
                                }
                            }
                        }
                    }
                }
            } else {
                UploadRow(
                    icon = "\uD83D\uDCC4",
                    label = t("ISP अनुबंध", "ISP Agreement"),
                    isUploaded = false,
                    onUpload = { showUploadOptions = true },
                )
            }
            Spacer(Modifier.height(8.dp))

            // View sample document link
            Text(
                "\uD83D\uDCCB ${t("सैंपल दस्तावेज़ देखें", "View sample document")}",
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary,
                modifier = Modifier.clickable { /* show sample doc */ },
            )
            Spacer(Modifier.height(12.dp))

            // Mandatory details
            Surface(shape = RoundedCornerShape(8.dp), color = WiomBgSec, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp, 14.dp)) {
                    Text(t("ISP अनुबंध में अनिवार्य विवरण:", "Mandatory details required in ISP Agreement:"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomText)
                    Spacer(Modifier.height(6.dp))
                    listOf(
                        t("ISP कंपनी का नाम", "ISP Company Name"),
                        t("LCO / पार्टनर का नाम", "LCO / Partner Name"),
                        t("अनुबंध की तिथि", "Agreement Date"),
                        t("अनुबंध वैध होना चाहिए (समाप्त नहीं)", "Agreement should be Valid (Not Expired)"),
                        t("लाइसेंस नंबर", "License Number"),
                        t("संपर्क / हस्ताक्षरकर्ता का नाम", "Contact / Signatory Names"),
                        t("पार्टनर और ISP की मुहर और हस्ताक्षर", "Partner and ISP stamp and signature"),
                    ).forEach {
                        Text("• $it", fontSize = 12.sp, color = WiomText, lineHeight = 20.sp)
                    }
                }
            }
        }
        BottomBar {
            WiomButton(
                if (state.isUploaded) t("आगे बढ़ें", "Proceed") else t("ISP अनुबंध अपलोड करें", "Upload ISP Agreement"),
                onClick = if (state.isUploaded) onNext else { {} },
                enabled = state.isUploaded,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Screen 8: Shop & Equipment Photos
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun ShopPhotosScreen(viewModel: PhotosViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("सत्यापन", "Verification"),
            onBack = onBack,
            rightText = t("स्टेप 4/5", "Step 4/5"),
        )
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text("\uD83C\uDFEA ${t("दुकान सत्यापन", "Shop Verification")}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText)
            Spacer(Modifier.height(4.dp))
            Text(
                t("दुकान के सामने की फ़ोटो और इंटरनेट उपकरण/राउटर की फ़ोटो अपलोड करें", "Upload shop front photo and Internet equipment/Router photos"),
                fontSize = 13.sp, color = WiomTextSec, lineHeight = 18.sp,
            )
            Spacer(Modifier.height(14.dp))

            // Shop Front Photo
            FieldLabel(t("दुकान की फ़ोटो", "Shop Front Photo"))
            UploadRow(
                icon = "\uD83C\uDFEA",
                label = t("दुकान की फ़ोटो", "Shop Front Photo"),
                isUploaded = state.shopPhotoUploaded,
                onUpload = { if (state.shopPhotoUploaded) viewModel.resetShopPhoto() else viewModel.onShopPhotoUploaded() },
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "\uD83D\uDCCB ${t("सैंपल दस्तावेज़ देखें", "View sample document")}",
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary,
                modifier = Modifier.clickable { /* show sample */ },
            )
            Spacer(Modifier.height(12.dp))

            // Equipment Photos (up to 5)
            FieldLabel(t("राउटर/उपकरण फ़ोटो (${state.equipmentPhotoCount}/5)", "Router/Equipment Photos (${state.equipmentPhotoCount}/5)"))
            if (state.equipmentPhotoCount > 0) {
                // Show count of uploaded photos
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WiomPositive100,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WiomPositive300),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✅", fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                t("${state.equipmentPhotoCount} फ़ोटो अपलोड हुई", "${state.equipmentPhotoCount} photos uploaded"),
                                fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomPositive,
                            )
                        }
                        Text(
                            t("हटाएं", "Remove"),
                            fontSize = 12.sp, color = WiomNegative, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { viewModel.resetEquipmentPhotos() },
                        )
                    }
                }
                // Add more button (if less than 5)
                if (state.equipmentPhotoCount < 5) {
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WiomBgSec,
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.addEquipmentPhoto() },
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("➕ ", fontSize = 14.sp)
                            Text(
                                t("और फ़ोटो जोड़ें", "Add more photos"),
                                fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary,
                            )
                        }
                    }
                }
            } else {
                UploadRow(
                    icon = "\uD83D\uDCE1",
                    label = t("राउटर/उपकरण फ़ोटो", "Router/Equipment Photos"),
                    isUploaded = false,
                    onUpload = { viewModel.addEquipmentPhoto() },
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "\uD83D\uDCCB ${t("सैंपल दस्तावेज़ देखें", "View sample document")}",
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary,
                modifier = Modifier.clickable { /* show sample */ },
            )
        }
        BottomBar {
            WiomButton(
                if (state.bothUploaded) t("सत्यापन के लिए जमा करें", "Submit for Verification") else t("दोनों फ़ोटो अपलोड करें", "Upload both photos"),
                onClick = if (state.bothUploaded) onNext else { {} },
                enabled = state.bothUploaded,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Screen 9: Verification Status
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun VerificationScreen(viewModel: VerificationViewModel, onNext: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val rejected = OnboardingState.verificationRejected

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("सत्यापन", "Verification"),
            rightText = t("स्टेप 5/5", "Step 5/5"),
        )

        if (rejected || state.status == VerificationState.REJECTED) {
            // ─── Rejected: auto refund, no re-upload ───
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))
                Text("\uD83D\uDE14", fontSize = 40.sp)
                Spacer(Modifier.height(16.dp))
                Text(t("सत्यापन अस्वीकृत", "Verification Rejected"), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(t("चिंता न करें — आपका पैसा सुरक्षित है", "Don't worry — your money is safe"), fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                WiomCard(borderColor = WiomPositive, backgroundColor = WiomPositive100) {
                    Text("\uD83D\uDD12 ${t("रिफंड शुरू हो गया", "Refund initiated")}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPositive)
                    Spacer(Modifier.height(4.dp))
                    Text(t("₹2,000 पूरा रिफंड 5-6 कार्य दिवसों में आपके खाते में आ जाएगा", "₹2,000 full refund will be credited to your account in 5-6 working days"), fontSize = 14.sp, color = WiomText, lineHeight = 20.sp)
                }
            }
            BottomBar {
                WiomButton(t("रिफंड स्टेटस देखें", "Check Refund Status"), onClick = { viewModel.checkRefundStatus() })
            }
        } else {
            // ─── Pending / All documents submitted ───
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(16.dp))
                    Text("✅", fontSize = 40.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(t("सभी दस्तावेज़ जमा हो गए", "All Documents Submitted"), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(t("सत्यापन जारी है — सत्यापन टीम आपके दस्तावेज़ों की जांच कर रही है", "Verification in progress — Verification team is reviewing your documents"), fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center, lineHeight = 20.sp)
                }
                Spacer(Modifier.height(16.dp))
                ChecklistItem(t("KYC दस्तावेज़", "KYC Documents"), isDone = true)
                ChecklistItem(t("बैंक विवरण", "Bank Details"), isDone = true)
                ChecklistItem(t("ISP अनुबंध", "ISP Agreement"), isDone = true)
                ChecklistItem(t("दुकान और उपकरण फ़ोटो", "Shop & Equipment Photos"), isDone = true)
                ChecklistItem(t("सत्यापन समीक्षा", "Verification Review"), subtitle = t("समीक्षा जारी...", "Under review..."), isWaiting = true, isLast = true)

                Column(modifier = Modifier.padding(16.dp)) {
                    InfoBox("⏳", t("समीक्षा में 3 कार्य दिवस", "Review may take 3 business days"), type = InfoBoxType.WARNING)
                    Spacer(Modifier.height(16.dp))
                    // ─── Prototype test buttons (hidden in production) ───
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("⚙️ प्रोटोटाइप कंट्रोल", "⚙️ Prototype Controls"),
                        fontSize = 11.sp, color = WiomHint, fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    WiomButton("✓ ${t("सत्यापन स्वीकृत", "Verification Approved")}", onClick = { viewModel.setApproved(); onNext() }, backgroundColor = WiomPositive)
                    Spacer(Modifier.height(8.dp))
                    WiomButton("✗ ${t("सत्यापन अस्वीकृत", "Verification Rejected")}", onClick = { viewModel.setRejected() }, isSecondary = true, textColor = WiomNegative, backgroundColor = WiomNegative100)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Helper: Upload Row
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun UploadRow(icon: String, label: String, isUploaded: Boolean, onUpload: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onUpload),
        shape = RoundedCornerShape(12.dp),
        color = if (isUploaded) WiomPositive100 else Color.White,
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("$icon $label", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomText)
                Text(
                    if (isUploaded) "${t("अपलोड हो गया", "Uploaded")} ✓" else t("टैप करें", "Tap to Upload"),
                    fontSize = 12.sp, color = if (isUploaded) WiomPositive else WiomHint,
                )
            }
            OutlinedButton(
                onClick = onUpload,
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isUploaded) WiomPositive else WiomPrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    if (isUploaded) t("बदलें", "Update") else t("अपलोड", "Upload"),
                    fontSize = 12.sp, fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
