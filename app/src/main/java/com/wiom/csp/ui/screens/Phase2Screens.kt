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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiom.csp.R
import com.wiom.csp.data.BankVerificationStatus
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.Scenario
import com.wiom.csp.ui.components.*
import com.wiom.csp.ui.theme.*
import com.wiom.csp.ui.viewmodel.*
import com.wiom.csp.util.t
import kotlinx.coroutines.delay
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

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
                            "\uD83D\uDCC4 ${t("बैंक विवरण दस्तावेज़", "Bank Details Document")}",
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
                        Spacer(Modifier.height(4.dp))
                        var showSampleBank by remember { mutableStateOf(false) }
                        Text(
                            "\uD83D\uDCCB ${t("सैंपल दस्तावेज़ देखें", "View sample document")}",
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { showSampleBank = true },
                        )
                        if (showSampleBank) {
                            SampleDocViewer(title = t("सैंपल बैंक दस्तावेज़", "Sample Bank Document"), imageResId = R.drawable.sample_bank_doc, onDismiss = { showSampleBank = false })
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
                            if (state.supportDocUploaded) t("ISP अनुबंध जोड़ें", "Add ISP Agreement") else t("दस्तावेज़ अपलोड करें", "Upload document"),
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
    var showSampleIsp by remember { mutableStateOf(false) }

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

            // Upload row + bottom sheet (matching prototype upload flow)
            var showIspSheet by remember { mutableStateOf(false) }
            var showIspPreview by remember { mutableStateOf(false) }
            var ispPreviewSource by remember { mutableStateOf("") }

            UploadRow(
                icon = "\uD83D\uDCC4",
                label = t("ISP अनुबंध", "ISP Agreement"),
                isUploaded = state.isUploaded,
                onUpload = { if (state.isUploaded) viewModel.resetUpload() else showIspSheet = true },
            )

            // ISP Upload bottom sheet (with PDF option)
            if (showIspSheet) {
                UploadBottomSheet(
                    title = t("ISP अनुबंध अपलोड करें", "Upload ISP Agreement"),
                    onPdf = { ispPreviewSource = t("PDF फ़ाइल", "PDF File"); showIspPreview = true },
                    onCamera = { ispPreviewSource = t("कैमरा से ली गई फ़ोटो", "Photo from Camera"); showIspPreview = true },
                    onGallery = { ispPreviewSource = t("गैलरी से चुनी गई फ़ोटो", "Photo from Gallery"); showIspPreview = true },
                    onDismiss = { showIspSheet = false },
                    proTips = listOf(
                        t("दस्तावेज़ साफ़ दिखना चाहिए", "Document should be clearly visible"),
                        t("सभी व्यापार विवरण मेल खाने चाहिए", "All business details should match"),
                        t("दस्तावेज़ की वैधता समाप्त नहीं होनी चाहिए", "Document should not be expired"),
                    ),
                )
            }
            if (showIspPreview) {
                UploadPreviewOverlay(
                    docName = t("ISP अनुबंध", "ISP Agreement"),
                    docIcon = "📄",
                    sourceLabel = ispPreviewSource,
                    onRetake = { showIspPreview = false; showIspSheet = true },
                    onConfirm = { showIspPreview = false; viewModel.uploadPdf() },
                )
            }
            Spacer(Modifier.height(8.dp))

            // View sample document link
            Text(
                "\uD83D\uDCCB ${t("सैंपल दस्तावेज़ देखें", "View sample document")}",
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { showSampleIsp = true },
            )
            if (showSampleIsp) {
                SampleDocViewer(title = t("सैंपल ISP अनुबंध", "Sample ISP Agreement"), imageResId = R.drawable.sample_isp, onDismiss = { showSampleIsp = false })
            }
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

            // Shop Front Photo — with upload sheet + shop-specific pro tips
            var showShopSheet by remember { mutableStateOf(false) }
            var showShopPreview by remember { mutableStateOf(false) }
            var shopPreviewSource by remember { mutableStateOf("") }

            FieldLabel(t("दुकान की फ़ोटो", "Shop Front Photo"))
            UploadRow(
                icon = "\uD83C\uDFEA",
                label = t("दुकान की फ़ोटो", "Shop Front Photo"),
                isUploaded = state.shopPhotoUploaded,
                onUpload = { if (state.shopPhotoUploaded) viewModel.resetShopPhoto() else showShopSheet = true },
            )
            Spacer(Modifier.height(4.dp))
            var showSampleShop by remember { mutableStateOf(false) }
            Text(
                "\uD83D\uDCCB ${t("सैंपल दस्तावेज़ देखें", "View sample document")}",
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { showSampleShop = true },
            )
            if (showSampleShop) {
                SampleDocViewer(title = t("सैंपल दुकान फ़ोटो", "Sample Shop Front Photo"), imageResId = R.drawable.sample_shop, onDismiss = { showSampleShop = false })
            }
            Spacer(Modifier.height(12.dp))

            if (showShopSheet) {
                UploadBottomSheet(
                    title = t("दुकान की फ़ोटो अपलोड करें", "Upload Shop Front Photo"),
                    onCamera = { shopPreviewSource = t("कैमरा से ली गई फ़ोटो", "Photo from Camera"); showShopPreview = true },
                    onGallery = { shopPreviewSource = t("गैलरी से चुनी गई फ़ोटो", "Photo from Gallery"); showShopPreview = true },
                    onDismiss = { showShopSheet = false },
                    proTips = listOf(
                        t("पूरी दुकान के सामने की फ़ोटो आवश्यक", "Complete Shop Front photo required"),
                        t("व्यापार का नाम स्पष्ट दिखना चाहिए", "Business name should be clearly visible"),
                        t("व्यापार LCO के रूप में मेल खाना चाहिए", "Line of business should match as an LCO"),
                    ),
                )
            }
            if (showShopPreview) {
                UploadPreviewOverlay(
                    docName = t("दुकान की फ़ोटो", "Shop Front Photo"),
                    docIcon = "🏪",
                    sourceLabel = shopPreviewSource,
                    onRetake = { showShopPreview = false; showShopSheet = true },
                    onConfirm = { showShopPreview = false; viewModel.onShopPhotoUploaded() },
                )
            }

            // Equipment Photos (up to 5) — with upload sheet + mandatory requirements
            var showEquipSheet by remember { mutableStateOf(false) }
            var showEquipPreview by remember { mutableStateOf(false) }
            var equipPreviewSource by remember { mutableStateOf("") }

            FieldLabel(t("राउटर/उपकरण फ़ोटो", "Router/Equipment Photos"))
            UploadRow(
                icon = "\uD83D\uDCE1",
                label = t("राउटर/उपकरण फ़ोटो", "Router/Equipment Photos"),
                isUploaded = state.equipmentPhotoUploaded,
                onUpload = { if (state.equipmentPhotoUploaded) viewModel.resetEquipmentPhotos() else showEquipSheet = true },
            )
            Spacer(Modifier.height(4.dp))
            var showSampleEquip by remember { mutableStateOf(false) }
            Text(
                "\uD83D\uDCCB ${t("सैंपल दस्तावेज़ देखें", "View sample document")}",
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { showSampleEquip = true },
            )
            if (showSampleEquip) {
                SampleDocViewer(title = t("सैंपल उपकरण फ़ोटो", "Sample Equipment Photo"), imageResId = R.drawable.sample_equip, onDismiss = { showSampleEquip = false })
            }

            if (showEquipSheet) {
                UploadBottomSheet(
                    title = t("राउटर/उपकरण फ़ोटो", "Router/Equipment Photos"),
                    onCamera = { equipPreviewSource = t("कैमरा से ली गई फ़ोटो", "Photo from Camera"); showEquipPreview = true },
                    onGallery = { equipPreviewSource = t("गैलरी से चुनी गई फ़ोटो", "Photo from Gallery"); showEquipPreview = true },
                    onDismiss = { showEquipSheet = false },
                    proTips = listOf(
                        t("पावर बैकअप उपलब्धता", "Power Backup availability"),
                        t("OLT फ़ोटो आवश्यक", "OLT Photo required"),
                        t("ISP स्विच फ़ोटो — 1 से अधिक कनेक्टिविटी", "ISP Switch Photo with more than 1 connectivity"),
                    ),
                )
            }
            if (showEquipPreview) {
                UploadPreviewOverlay(
                    docName = t("उपकरण फ़ोटो", "Equipment Photo"),
                    docIcon = "📡",
                    sourceLabel = equipPreviewSource,
                    onRetake = { showEquipPreview = false; showEquipSheet = true },
                    onConfirm = { showEquipPreview = false; viewModel.finishEquipmentUpload() },
                )
            }
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
        if (rejected || state.status == VerificationState.REJECTED) {
            val context = LocalContext.current
            AppHeader(
                title = t("सत्यापन", "Verification"),
            )

            when (state.refundState) {
                RefundState.IN_PROGRESS -> {
                    // ─── Refund In Progress ───
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text("⏳", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("रिफंड प्रक्रिया में है", "Refund In Progress"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText, textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(20.dp))
                        WiomCard {
                            FeeDetailRow(t("रिफंड राशि", "Refund Amount"), "₹2,000", valueColor = WiomPositive)
                            HorizontalDivider(color = WiomBorder)
                            FeeDetailRow(t("अनुमानित समय", "Estimated Time"), t("5-7 कार्य दिवस", "5-7 working days"))
                        }
                        Spacer(Modifier.height(12.dp))
                        InfoBox("\uD83D\uDD12", t("चिंता न करें — आपका पैसा सुरक्षित है", "Don't worry — your money is safe"))
                    }
                    BottomBar {
                        WiomButton(t("ठीक है", "OK"), onClick = { viewModel.backToRejected() })
                    }
                }

                RefundState.SUCCESS -> {
                    // ─── Refund Successful ───
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text("✅", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("रिफंड सफल!", "Refund Successful!"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomPositive, textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(20.dp))
                        WiomCard(borderColor = WiomPositive, backgroundColor = WiomPositive100) {
                            FeeDetailRow(t("रिफंड राशि", "Refund Amount"), "₹2,000", valueColor = WiomPositive)
                            HorizontalDivider(color = WiomPositive200)
                            FeeDetailRow("UTR", "UTR928374610")
                            HorizontalDivider(color = WiomPositive200)
                            FeeDetailRow(t("तारीख", "Date"), "27 Mar 2026")
                        }
                    }
                    BottomBar {
                        WiomButton(t("ठीक है", "OK"), onClick = { viewModel.backToRejected() })
                    }
                }

                RefundState.FAILED -> {
                    // ─── Refund Failed ───
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text("\uD83D\uDE1F", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            t("रिफंड विफल", "Refund Failed"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative, textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(20.dp))
                        InfoBox("📞", t("कृपया हमारी हेल्पलाइन पर कॉल करें: 7836811111", "Please call our helpline: 7836811111"))
                    }
                    BottomBar {
                        WiomButton(t("हमसे बात करें", "Talk to Us"), onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:7836811111"))
                            context.startActivity(intent)
                        })
                    }
                }

                else -> {
                    // ─── Rejected: default view with refund initiated ───
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text("\uD83D\uDE14", fontSize = 40.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(t("प्रोफ़ाइल अभी स्वीकृत नहीं हुई", "Profile not accepted yet"), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(8.dp))
                        Text(t("चिंता न करें — आपका पैसा सुरक्षित है", "Don't worry — your money is safe"), fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(20.dp))
                        WiomCard(borderColor = WiomPositive, backgroundColor = WiomPositive100) {
                            Text("\uD83D\uDD12 ${t("रिफंड शुरू हो गया", "Refund initiated")}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomPositive)
                            Spacer(Modifier.height(4.dp))
                            Text(t("₹2,000 पूरा रिफंड 5-7 कार्य दिवसों में आपके खाते में आ जाएगा", "₹2,000 full refund will be credited to your account in 5-7 working days"), fontSize = 14.sp, color = WiomText, lineHeight = 20.sp)
                        }
                    }
                    BottomBar {
                        WiomButton(t("रिफंड स्टेटस देखें", "Check Refund Status"), onClick = { viewModel.checkRefundStatus() })
                    }
                }
            }
        } else {
            AppHeader(
                title = t("सत्यापन", "Verification"),
                rightText = t("स्टेप 5/5", "Step 5/5"),
            )
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
