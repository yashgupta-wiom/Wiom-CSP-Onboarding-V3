package com.wiom.csp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.Scenario
import com.wiom.csp.ui.components.*
import com.wiom.csp.ui.theme.*
import com.wiom.csp.util.t
import kotlinx.coroutines.delay
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextDecoration
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.wiom.csp.ui.viewmodel.*

// Screen 0: Phone Entry
@Composable
fun PhoneEntryScreen(viewModel: PhoneViewModel, onNext: () -> Unit) {
    val scenario = OnboardingState.activeScenario
    var tncAccepted by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(title = "Wiom Partner+")

        if (scenario == Scenario.PHONE_DUPLICATE) {
            // ─── PHONE_DUPLICATE error screen ───
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(32.dp))
                Text("\uD83D\uDCF1", fontSize = 40.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    t("यह नंबर पहले से रजिस्टर्ड है", "This number is already registered"),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                ErrorCard(
                    icon = "ℹ️",
                    titleHi = "अकाउंट मौजूद है",
                    titleEn = "Account Exists",
                    messageHi = "इस नंबर से पहले से एक अकाउंट बना हुआ है। आप लॉगिन कर सकते हैं या नए नंबर से रजिस्टर कर सकते हैं।",
                    messageEn = "An account already exists with this number. You can login or register with a new number.",
                    type = "info",
                )
                Spacer(Modifier.height(12.dp))
                InfoBox("\uD83D\uDD12", t("आपका पुराना डेटा सुरक्षित है", "Your existing data is safe"))
                Spacer(Modifier.height(16.dp))
                WiomButton(t("नए नंबर से OTP भेजें", "Send OTP with new number"), onClick = {})
                Spacer(Modifier.height(8.dp))
                WiomButton(t("लॉगिन करें", "Login"), onClick = {}, isSecondary = true)
            }
        } else {
            // ─── Normal happy path ───
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\uD83E\uDD1D", fontSize = 32.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t("Wiom पार्टनर बनें", "Become a Wiom Partner"),
                        fontSize = 24.sp, fontWeight = FontWeight.Bold,
                        color = WiomText, textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        t("Wiom के साथ अपना बिज़नेस शुरू करें", "Start your business with Wiom"),
                        fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(24.dp))
                FieldLabel(t("मोबाइल नंबर", "Mobile Number"))
                // Phone input with +91 prefix
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                            .background(WiomBgSec)
                            .border(1.dp, WiomBorderInput, RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("+91", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomTextSec)
                    }
                    val isPhoneError = OnboardingState.phoneNumber.length > 10
                    OutlinedTextField(
                        value = OnboardingState.phoneNumber,
                        onValueChange = { newValue ->
                            // Filter to digits only, allow typing beyond 10 to show error
                            val filtered = newValue.filter { it.isDigit() }
                            OnboardingState.phoneNumber = filtered
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = if (isPhoneError) WiomNegative else WiomBorderInput,
                            focusedBorderColor = if (isPhoneError) WiomNegative else WiomBorderFocus,
                            unfocusedContainerColor = Color.White,
                        ),
                        isError = isPhoneError,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text(t("10 अंकों का नंबर", "10 digit number"), color = WiomHint) }
                    )
                }
                // Character count hint or error
                if (OnboardingState.phoneNumber.length > 10) {
                    Text(
                        t("⚠️ नंबर 10 अंक से ज़्यादा नहीं होना चाहिए", "⚠️ Number must not exceed 10 digits"),
                        fontSize = 12.sp,
                        color = WiomNegative,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                    )
                } else if (OnboardingState.phoneNumber.isNotEmpty() && OnboardingState.phoneNumber.length < 10) {
                    Text(
                        t("${OnboardingState.phoneNumber.length}/10 अंक", "${OnboardingState.phoneNumber.length}/10 digits"),
                        fontSize = 12.sp,
                        color = WiomHint,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                    )
                } else {
                    Spacer(Modifier.height(8.dp))
                }
                InfoBox("\uD83D\uDD12", t("OTP आपके नंबर पर भेजा जाएगा", "OTP will be sent to your number"))

                Spacer(Modifier.height(16.dp))

                // ─── T&C Checkbox Section ───
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiomBgSec)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = tncAccepted,
                        onCheckedChange = { tncAccepted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = WiomPrimary,
                            uncheckedColor = WiomBorderInput,
                        ),
                    )
                    Spacer(Modifier.width(4.dp))
                    Column {
                        Text(
                            t("आगे बढ़कर, मैं नियम व शर्तें स्वीकार करता/करती हूँ", "By Continuing, I accept the Terms and Conditions"),
                            fontSize = 14.sp,
                            color = WiomText,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            t("नियम व शर्तें पढ़ें", "Read T&C"),
                            fontSize = 12.sp,
                            color = WiomPrimary,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wiom.in/terms"))
                                context.startActivity(intent)
                            },
                        )
                    }
                }
            }
            BottomBar {
                WiomButton(
                    t("OTP भेजें", "Send OTP"),
                    onClick = onNext,
                    enabled = OnboardingState.phoneNumber.length == 10 && tncAccepted,
                )
            }
        }
    }
}

// Screen 1: OTP Verification
@Composable
fun OtpTncScreen(viewModel: OtpViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val scenario = OnboardingState.activeScenario

    // Timer state
    var timerSeconds by remember { mutableIntStateOf(28) }
    var timerExpired by remember { mutableStateOf(false) }

    // Countdown timer
    LaunchedEffect(timerExpired) {
        if (!timerExpired) {
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds--
            }
            timerExpired = true
        }
    }

    // Format phone display
    val phoneDisplay = if (OnboardingState.phoneNumber.length == 10) {
        val p = OnboardingState.phoneNumber
        "+91 ${p.substring(0, 5)} ${p.substring(5)}"
    } else {
        "+91 98765 43210"
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(title = t("OTP सत्यापन", "Verify OTP"), onBack = onBack)

        if (scenario == Scenario.OTP_WRONG) {
            // ─── OTP_WRONG error screen ───
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    t("OTP डालें", "Enter OTP"),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    t("$phoneDisplay पर भेजा गया", "Sent to $phoneDisplay"),
                    fontSize = 14.sp, color = WiomTextSec,
                )
                OtpRow(isError = true)
                ErrorCard(
                    icon = "❌",
                    titleHi = "गलत OTP",
                    titleEn = "Wrong OTP",
                    messageHi = "कृपया दोबारा कोशिश करें — 2 प्रयास बाकी हैं",
                    messageEn = "Please try again — 2 attempts remaining",
                    type = "error",
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    t("OTP दोबारा भेजें", "Resend OTP"),
                    fontSize = 14.sp, color = WiomPrimary, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { },
                )
            }
            BottomBar {
                WiomButton(t("सत्यापित करें", "Verify"), onClick = {})
            }
        } else if (scenario == Scenario.OTP_EXPIRED) {
            // ─── OTP_EXPIRED error screen ───
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    t("OTP डालें", "Enter OTP"),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    t("$phoneDisplay पर भेजा गया", "Sent to $phoneDisplay"),
                    fontSize = 14.sp, color = WiomTextSec,
                )
                OtpRow(isExpired = true)
                ErrorCard(
                    icon = "⏰",
                    titleHi = "OTP expired हो गया",
                    titleEn = "OTP has expired",
                    messageHi = "चिंता न करें — नया OTP भेजें",
                    messageEn = "Don't worry — send a new OTP",
                    type = "warning",
                )
                Spacer(Modifier.height(16.dp))
                WiomButton(t("नया OTP भेजें", "Send New OTP"), onClick = {})
                Spacer(Modifier.height(8.dp))
                WiomButton(t("नंबर बदलें", "Change Number"), onClick = {}, isSecondary = true)
            }
        } else {
            // ─── Normal happy path ───
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    t("OTP डालें", "Enter OTP"),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    t("$phoneDisplay पर भेजा गया", "Sent to $phoneDisplay"),
                    fontSize = 14.sp, color = WiomTextSec,
                )
                // Interactive OTP input
                OtpInputRow(
                    digits = OnboardingState.otpDigits,
                    onDigitsChange = { OnboardingState.otpDigits = it },
                )

                // Timer / Resend / Change Number
                if (!timerExpired) {
                    Text(
                        t("OTP दोबारा भेजें", "Resend OTP") + " ${timerSeconds}s",
                        fontSize = 12.sp, color = WiomHint,
                    )
                } else {
                    // Timer expired — show resend + change number
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            t("OTP दोबारा भेजें", "Resend OTP"),
                            fontSize = 14.sp,
                            color = WiomPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                timerSeconds = 28
                                timerExpired = false
                            },
                        )
                        Text("|", fontSize = 14.sp, color = WiomHint)
                        Text(
                            t("नंबर बदलें", "Change Number"),
                            fontSize = 14.sp,
                            color = WiomTextSec,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onBack() },
                        )
                    }
                }
            }
            BottomBar {
                val allFilled = OnboardingState.otpDigits.all { it.isNotEmpty() }
                WiomButton(
                    t("सत्यापित करें", "Verify"),
                    onClick = onNext,
                    enabled = allFilled,
                )
            }
        }
    }
}

// Screen 2: Personal & Business Info
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(viewModel: PersonalInfoViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    // Entity type dropdown state
    var dropdownExpanded by remember { mutableStateOf(false) }
    val entityOptions = listOf(t("प्रोप्राइटरशिप (Proprietorship)", "Proprietorship"))

    // Email validation state
    var emailTouched by remember { mutableStateOf(false) }
    val emailError = if (emailTouched && OnboardingState.personalEmail.isNotEmpty() &&
        !(OnboardingState.personalEmail.contains("@") && OnboardingState.personalEmail.contains(".")))
        t("कृपया सही ईमेल आईडी डालें", "Please enter a valid Email ID") else null

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("पंजीकरण", "Registration"),
            onBack = onBack,
            rightText = t("स्टेप 1/3", "Step 1/3")
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                t("व्यक्तिगत और व्यापार जानकारी", "Personal & Business Info"),
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("आपका Wiom अकाउंट बनाने के लिए हमें इन विवरणों की आवश्यकता है", "We require these details for creating your Wiom Account"),
                fontSize = 14.sp, color = WiomTextSec,
            )
            Spacer(Modifier.height(16.dp))

            FieldLabel(t("पूरा नाम (आधार अनुसार)", "Full Name (as per Aadhaar)"))
            WiomTextField(
                value = OnboardingState.personalName,
                onValueChange = { OnboardingState.personalName = it },
                placeholder = t("उदाहरण: राजेश कुमार", "Example: Rajesh Kumar"),
            )

            FieldLabel(t("ईमेल आईडी", "Email ID"))
            WiomTextField(
                value = OnboardingState.personalEmail,
                onValueChange = { OnboardingState.personalEmail = it },
                placeholder = t("उदाहरण: rajesh@email.com", "Example: rajesh@email.com"),
                isError = emailError != null,
                errorMessage = emailError,
                onFocusChanged = { if (!it) emailTouched = true },
            )

            FieldLabel(t("व्यापार इकाई प्रकार", "Business Entity Type"))
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it },
            ) {
                OutlinedTextField(
                    value = OnboardingState.entityType,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text(t("व्यापार इकाई प्रकार चुनें", "Select entity type"), color = WiomHint) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = WiomBorderInput,
                        focusedBorderColor = WiomBorderFocus,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                    singleLine = true,
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                ) {
                    entityOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                OnboardingState.entityType = option
                                dropdownExpanded = false
                            },
                        )
                    }
                }
            }

            FieldLabel(t("व्यापार का नाम", "Business Name"))
            WiomTextField(
                value = OnboardingState.tradeName,
                onValueChange = { OnboardingState.tradeName = it },
                placeholder = t("उदाहरण: राजेश टेलीकॉम", "Example: Rajesh Telecom"),
            )
        }
        BottomBar {
            val allFilled = OnboardingState.personalName.isNotBlank() &&
                    OnboardingState.personalEmail.isNotBlank() &&
                    OnboardingState.personalEmail.contains("@") &&
                    OnboardingState.personalEmail.contains(".") &&
                    OnboardingState.entityType.isNotBlank() &&
                    OnboardingState.tradeName.isNotBlank()
            WiomButton(
                t("व्यापार स्थान जोड़ें", "Add Business Location"),
                onClick = onNext,
                enabled = allFilled,
            )
        }
    }
}

// Screen 3: Location
@Composable
fun LocationScreen(viewModel: LocationViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val scenario = OnboardingState.activeScenario

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("पंजीकरण", "Registration"),
            onBack = onBack,
            rightText = t("स्टेप 2/3", "Step 2/3")
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                t("व्यापार स्थान", "Business Location"),
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("आपकी पूरी पता जानकारी चाहिए ताकि हम आपकी लोकैलिटी जान सकें और Wiom इंटरनेट ग्राहक दे सकें", "We need your complete address details to know your locality and provide Wiom Internet customers"),
                fontSize = 14.sp, color = WiomTextSec,
            )
            Spacer(Modifier.height(16.dp))

            FieldLabel(t("राज्य", "State"))
            var stateExpanded by remember { mutableStateOf(false) }
            val indianStates = listOf(
                "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
                "Chhattisgarh", "Goa", "Gujarat", "Haryana",
                "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
                "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya",
                "Mizoram", "Nagaland", "Odisha", "Punjab",
                "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana",
                "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal",
                "Delhi", "Jammu & Kashmir", "Ladakh", "Chandigarh", "Puducherry",
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = OnboardingState.selectedState,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { stateExpanded = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = WiomBorderInput,
                        focusedBorderColor = WiomBorderFocus,
                        unfocusedContainerColor = Color.White,
                        disabledBorderColor = WiomBorderInput,
                        disabledTextColor = WiomText,
                        disabledContainerColor = Color.White,
                    ),
                    enabled = false,
                    trailingIcon = { Text("▼", fontSize = 12.sp, color = WiomHint) },
                    placeholder = { Text(t("राज्य चुनें", "Select State"), color = WiomHint) },
                )
                DropdownMenu(
                    expanded = stateExpanded,
                    onDismissRequest = { stateExpanded = false },
                    modifier = Modifier.heightIn(max = 300.dp),
                ) {
                    indianStates.forEach { stateName ->
                        DropdownMenuItem(
                            text = { Text(stateName, fontSize = 14.sp) },
                            onClick = {
                                OnboardingState.selectedState = stateName
                                stateExpanded = false
                            },
                        )
                    }
                }
            }

            FieldLabel(t("शहर", "City"))
            WiomTextField(value = OnboardingState.city, onValueChange = { OnboardingState.city = it })

            FieldLabel(t("पिनकोड", "Pincode"))
            WiomTextField(value = OnboardingState.pincode, onValueChange = { OnboardingState.pincode = it.filter { c -> c.isDigit() }.take(6) })

            FieldLabel(t("पूरा पता", "Full Address"))
            OutlinedTextField(
                value = OnboardingState.address,
                onValueChange = { OnboardingState.address = it },
                placeholder = { Text(t("उदा: 123, विजय नगर", "For ex: 123, Vijay Nagar"), color = WiomHint) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = WiomBorderInput,
                    focusedBorderColor = WiomBorderFocus,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
                singleLine = false,
                minLines = 3,
            )

            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TrustBadge("\uD83D\uDCCD", t("GPS कैप्चर हुआ", "GPS Captured"))
                Text("22.71° N, 75.85° E", fontSize = 12.sp, color = WiomHint)
            }

        }
        BottomBar {
            WiomButton(t("पंजीकरण शुल्क भरें", "Pay Registration Fee"), onClick = onNext)
        }
    }
}

// Screen 5: KYC Documents — 3 sub-stages (PAN → Aadhaar → GST) with progress bar
@Composable
fun KycScreen(viewModel: KycViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    // Dedup screens
    if (state.panDedup) {
        KycDedupScreen(
            docType = t("पैन", "PAN"),
            phone = state.dedupPhone,
            onEnterDifferent = { viewModel.clearPanDedup() }
        )
        return
    }
    if (state.aadhaarDedup) {
        KycDedupScreen(
            docType = t("आधार", "Aadhaar"),
            phone = state.dedupPhone,
            onEnterDifferent = { viewModel.clearAadhaarDedup() }
        )
        return
    }
    if (state.gstDedup) {
        KycDedupScreen(
            docType = t("जीएसटी", "GST"),
            phone = state.dedupPhone,
            onEnterDifferent = { viewModel.clearGstDedup() }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("सत्यापन", "Verification"),
            onBack = when (state.subStage) {
                0 -> onBack
                1 -> {{ viewModel.moveBackFromAadhaar() }}
                else -> {{ viewModel.moveBackFromGst() }}
            },
            rightText = t("स्टेप 1/5", "Step 1/5"),
        )

        // Progress bar: PAN → Aadhaar → GST
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            listOf(
                Triple(t("पैन", "PAN"), "🪪", 0),
                Triple(t("आधार", "Aadhaar"), "📄", 1),
                Triple("GST", "📋", 2),
            ).forEachIndexed { idx, (label, icon, stage) ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    stage < state.subStage -> WiomPositive
                                    stage == state.subStage -> WiomPrimary
                                    else -> WiomBgSec
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (stage < state.subStage) "✓" else icon,
                            fontSize = 13.sp, fontWeight = FontWeight.Bold,
                            color = if (stage <= state.subStage) Color.White else WiomHint,
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        color = when {
                            stage < state.subStage -> WiomPositive
                            stage == state.subStage -> WiomPrimary
                            else -> WiomHint
                        }
                    )
                }
                if (idx < 2) {
                    Box(
                        modifier = Modifier.weight(0.5f).height(2.dp).padding(top = 15.dp)
                            .background(if (idx < state.subStage) WiomPositive else WiomBorder)
                    )
                }
            }
        }

        when (state.subStage) {
            0 -> KycPanSubStage(state, viewModel, onMoveToAadhaar = { viewModel.moveToAadhaar() })
            1 -> KycAadhaarSubStage(state, viewModel, onMoveToGst = { viewModel.moveToGst() })
            2 -> KycGstSubStage(state, viewModel, onComplete = onNext)
        }
    }
}

@Composable
private fun ColumnScope.KycPanSubStage(state: KycUiState, viewModel: KycViewModel, onMoveToAadhaar: () -> Unit) {
    val canProceed = state.isPanStageComplete
    Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("🪪 ${t("पैन विवरण", "PAN Details")}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText)
        Spacer(Modifier.height(4.dp))
        Text(t("पैन नंबर दर्ज करें और पैन कार्ड अपलोड करें", "Enter PAN number and upload PAN card"), fontSize = 13.sp, color = WiomTextSec, lineHeight = 18.sp)
        Spacer(Modifier.height(14.dp))
        FieldLabel(t("पैन नंबर", "PAN Number"))
        WiomTextField(
            value = state.panNumber,
            onValueChange = { viewModel.onPanNumberChanged(it) },
            placeholder = t("उदा. CEVPG6375L", "e.g. CEVPG6375L"),
            isVerified = state.isPanValid,
            isError = state.panBlurred && state.panError != null,
            errorMessage = state.panError,
            onFocusChanged = { if (!it) viewModel.onPanBlurred() },
        )
        FieldLabel(t("पैन कार्ड अपलोड", "Upload PAN Card"))
        KycUploadRow("🪪", t("पैन कार्ड", "PAN Card"), state.panUploaded,
            onUpload = { viewModel.onPanUploaded() }, onReset = { viewModel.resetPanUpload() })
        Spacer(Modifier.height(8.dp))
        Text("📋 ${t("सैंपल दस्तावेज़ देखें", "View sample document")}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary, modifier = Modifier.clickable { })
    }
    BottomBar {
        WiomButton(
            if (canProceed) t("आधार जोड़ें", "Add Aadhaar") else t("पैन विवरण भरें", "Complete PAN details"),
            onClick = onMoveToAadhaar, enabled = canProceed,
        )
    }
}

@Composable
private fun ColumnScope.KycAadhaarSubStage(state: KycUiState, viewModel: KycViewModel, onMoveToGst: () -> Unit) {
    val canProceed = state.isAadhaarStageComplete
    Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("📄 ${t("आधार विवरण", "Aadhaar Details")}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText)
        Spacer(Modifier.height(4.dp))
        Text(t("आधार नंबर दर्ज करें और आधार कार्ड अपलोड करें", "Enter Aadhaar number and upload Aadhaar card"), fontSize = 13.sp, color = WiomTextSec, lineHeight = 18.sp)
        Spacer(Modifier.height(14.dp))
        FieldLabel(t("आधार नंबर", "Aadhaar Number"))
        WiomTextField(
            value = state.aadhaarFormatted,
            onValueChange = { viewModel.onAadhaarNumberChanged(it) },
            placeholder = t("उदा. 3696 8916 4553", "e.g. 3696 8916 4553"),
            isVerified = state.isAadhaarValid,
            isError = state.aadhaarBlurred && state.aadhaarError != null,
            errorMessage = state.aadhaarError,
            onFocusChanged = { if (!it) viewModel.onAadhaarBlurred() },
        )
        FieldLabel(t("आधार कार्ड अपलोड", "Upload Aadhaar Card"))
        KycUploadRow("📄", t("आधार — सामने", "Aadhaar — Front"), state.aadhaarFrontUploaded,
            onUpload = { viewModel.onAadhaarFrontUploaded() }, onReset = { viewModel.resetAadhaarFrontUpload() })
        KycUploadRow("📄", t("आधार — पीछे", "Aadhaar — Back"), state.aadhaarBackUploaded,
            onUpload = { viewModel.onAadhaarBackUploaded() }, onReset = { viewModel.resetAadhaarBackUpload() })
        Spacer(Modifier.height(8.dp))
        Text("📋 ${t("सैंपल दस्तावेज़ देखें", "View sample document")}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary, modifier = Modifier.clickable { })
    }
    BottomBar {
        WiomButton(
            if (canProceed) t("जीएसटी जोड़ें", "Add GST") else t("आधार विवरण भरें", "Complete Aadhaar details"),
            onClick = onMoveToGst, enabled = canProceed,
        )
    }
}

@Composable
private fun ColumnScope.KycGstSubStage(state: KycUiState, viewModel: KycViewModel, onComplete: () -> Unit) {
    val canProceed = state.isGstStageComplete
    Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("📋 ${t("जीएसटी विवरण", "GST Details")}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WiomText)
        Spacer(Modifier.height(4.dp))
        Text(t("जीएसटी नंबर दर्ज करें और जीएसटी प्रमाणपत्र अपलोड करें", "Enter GST number and upload GST certificate"), fontSize = 13.sp, color = WiomTextSec, lineHeight = 18.sp)
        Spacer(Modifier.height(14.dp))
        FieldLabel(t("जीएसटी नंबर", "GST Number"))
        WiomTextField(
            value = state.gstNumber,
            onValueChange = { viewModel.onGstNumberChanged(it) },
            placeholder = t("उदा. 09${state.gstPlaceholder.drop(2)}", "e.g. ${state.gstPlaceholder}"),
            isVerified = state.isGstValid,
            isError = state.gstBlurred && state.gstError != null,
            errorMessage = state.gstError,
            onFocusChanged = { if (!it) viewModel.onGstBlurred() },
        )
        FieldLabel(t("जीएसटी प्रमाणपत्र अपलोड", "Upload GST Certificate"))
        KycUploadRow("📋", t("जीएसटी प्रमाणपत्र", "GST Certificate"), state.gstUploaded,
            onUpload = { viewModel.onGstUploaded() }, onReset = { viewModel.resetGstUpload() })
        Spacer(Modifier.height(8.dp))
        Text("📋 ${t("सैंपल दस्तावेज़ देखें", "View sample document")}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WiomPrimary, modifier = Modifier.clickable { })
    }
    BottomBar {
        WiomButton(
            if (canProceed) t("बैंक विवरण जोड़ें", "Add Bank Details") else t("जीएसटी विवरण भरें", "Complete GST details"),
            onClick = onComplete, enabled = canProceed,
        )
    }
}

@Composable
private fun KycDedupScreen(docType: String, phone: String, onEnterDifferent: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(title = t("सत्यापन", "Verification"), onBack = onEnterDifferent)
        Column(
            modifier = Modifier.weight(1f).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            Text("🔍", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text("$docType ${t("पहले से जुड़ा हुआ है", "already linked")}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomNegative, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                "${t("यह", "This")} $docType ${t("एक और Wiom अकाउंट से जुड़ा है जिसका मोबाइल नंबर ****$phone है", "is linked with another Wiom account number ending with $phone")}",
                fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center, lineHeight = 20.sp,
            )
            Spacer(Modifier.height(16.dp))
            WiomCard(borderColor = WiomInfo, backgroundColor = WiomInfo100) {
                Text("💡 ${t("क्या करें?", "What to do?")}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomInfo)
                Spacer(Modifier.height(6.dp))
                Text(t("अगर यह आपका नंबर है, तो कृपया Wiom Partner Plus ऐप में लॉगिन करें।", "If this is your number, please login in Wiom Partner Plus app."), fontSize = 13.sp, color = WiomText, lineHeight = 20.sp)
                Spacer(Modifier.height(6.dp))
                Text(t("अगर आप Wiom से नया जुड़ना चाहते हैं, तो कृपया अलग KYC दस्तावेज़ का उपयोग करें।", "If you wish to get onboarded with Wiom, use different KYC details."), fontSize = 13.sp, color = WiomText, lineHeight = 20.sp)
            }
        }
        BottomBar {
            WiomButton("${t("अलग", "Enter Different")} $docType ${t("डालें", "")}", onClick = onEnterDifferent)
        }
    }
}

@Composable
private fun KycUploadRow(icon: String, label: String, isUploaded: Boolean, onUpload: () -> Unit, onReset: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = if (isUploaded) onReset else onUpload),
        shape = RoundedCornerShape(12.dp),
        color = if (isUploaded) WiomPositive100 else Color.White,
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("$icon $label", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomText)
                Text(
                    if (isUploaded) "${t("अपलोड हो गया", "Uploaded")} ✓" else t("टैप करें", "Tap to Upload"),
                    fontSize = 12.sp, color = if (isUploaded) WiomPositive else WiomHint,
                )
            }
            OutlinedButton(
                onClick = if (isUploaded) onReset else onUpload,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(if (isUploaded) t("बदलें", "Update") else t("अपलोड", "Upload"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}

// Screen 4: Registration Fee ₹2,000
@Composable
fun RegFeeScreen(viewModel: PaymentViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val scenario = OnboardingState.activeScenario

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("पंजीकरण शुल्क", "Registration Fee"),
            onBack = onBack,
        )

        when (scenario) {
            Scenario.REGFEE_FAILED -> {
                // ─── REGFEE_FAILED error screen ───
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
                            Text("\u20B92,000", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomText)
                        }
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Error", fontSize = 13.sp, color = WiomTextSec)
                            Text("BANK_GATEWAY_TIMEOUT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomNegative)
                        }
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Time", fontSize = 13.sp, color = WiomTextSec)
                            Text("just now", fontSize = 13.sp, color = WiomText)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    InfoBox("\uD83D\uDCA1", t("2-3 मिनट बाद दोबारा कोशिश करें", "Try again after 2-3 minutes"), type = InfoBoxType.WARNING)
                    Spacer(Modifier.height(16.dp))
                    WiomButton(t("दोबारा भुगतान करें", "Retry Payment"), onClick = {})
                    Spacer(Modifier.height(8.dp))
                    WiomButton(t("बाद में करें", "Pay Later"), onClick = {}, isSecondary = true)
                }
            }
            Scenario.REGFEE_TIMEOUT -> {
                // ─── REGFEE_TIMEOUT error screen ───
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(24.dp))
                    Text("\u23F3", fontSize = 40.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("भुगतान pending है", "Payment is pending"),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomWarning700,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                    ErrorCard(
                        icon = "\u23F3",
                        titleHi = "Bank response में देरी",
                        titleEn = "Bank response delayed",
                        messageHi = "Bank से response आने में 2-5 मिनट लग सकते हैं। कृपया थोड़ा इंतज़ार करें।",
                        messageEn = "Bank response may take 2-5 minutes. Please wait.",
                        type = "warning",
                    )
                    Spacer(Modifier.height(8.dp))
                    WiomCard {
                        Text(t("ट्रांज़ैक्शन विवरण", "TRANSACTION DETAILS"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WiomTextSec, letterSpacing = 0.5.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Amount", fontSize = 13.sp, color = WiomTextSec)
                            Text("\u20B92,000", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomText)
                        }
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("UPI Ref", fontSize = 13.sp, color = WiomTextSec)
                            Text("UPI123456789", fontSize = 13.sp, color = WiomText)
                        }
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Status", fontSize = 13.sp, color = WiomTextSec)
                            Text("\u23F3 Pending", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomWarning700)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    InfoBox("\uD83D\uDD12", t("48 घंटे में auto-refund अगर fail हो", "Auto-refund within 48hrs if failed"))
                    Spacer(Modifier.height(16.dp))
                    WiomButton(t("Status Refresh करें", "Refresh Status"), onClick = {})
                    Spacer(Modifier.height(8.dp))
                    WiomButton(t("हमसे बात करें", "Talk to us"), onClick = {}, isSecondary = true)
                }
            }
            else -> {
                // ─── Normal happy path ───
                var showPaymentSuccess by remember { mutableStateOf(false) }

                LaunchedEffect(showPaymentSuccess) {
                    if (showPaymentSuccess) {
                        delay(3000)
                        onNext()
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(
                                t("पंजीकरण शुल्क", "Registration Fee"),
                                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                            )
                            Spacer(Modifier.height(12.dp))
                            AmountBox("\u20B92,000", t("पंजीकरण शुल्क", "Registration Fee"))
                            Spacer(Modifier.height(12.dp))
                            WiomCard {
                                Text(
                                    "\u2139\uFE0F ${t("जरूरी जानकारी", "Important Information")}",
                                    fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomText,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    t(
                                        "भुगतान के बाद आपकी profile Business/QA team द्वारा review की जाएगी।",
                                        "After payment, your profile will be reviewed by the Business/QA team."
                                    ),
                                    fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                                )
                                Spacer(Modifier.height(8.dp))
                                TrustBadge("\uD83D\uDD12", t("Reject होने पर full refund मिलेगा", "Full refund if rejected"))
                            }
                            Spacer(Modifier.height(8.dp))
                            InfoBox("1.", t("पंजीकरण शुल्क भुगतान के बाद दस्तावेज़ सत्यापन प्रक्रिया शुरू होगी।", "Document Verification process will start after Registration fee payment."))
                            Spacer(Modifier.height(4.dp))
                            InfoBox("2.", t("कृपया भुगतान के 3 दिनों के भीतर सभी आवश्यक दस्तावेज़ जमा करें ताकि आपका आवेदन सक्रिय रहे।", "Please submit your documents within 3 days to keep your application active."))
                        }
                        BottomBar {
                            WiomButton(t("₹2,000 अभी भुगतान करें", "Pay ₹2,000 Now"), onClick = { showPaymentSuccess = true })
                        }
                    }

                    // Payment success overlay
                    if (showPaymentSuccess) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("✅", fontSize = 48.sp)
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    t("भुगतान सफल!", "Payment Successful!"),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "₹2,000",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
