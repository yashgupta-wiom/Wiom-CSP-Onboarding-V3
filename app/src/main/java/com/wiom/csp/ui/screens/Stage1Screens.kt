package com.wiom.csp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiom.csp.ui.components.*
import com.wiom.csp.ui.theme.*
import com.wiom.csp.ui.viewmodel.OtpViewModel
import com.wiom.csp.ui.viewmodel.PaymentResult
import com.wiom.csp.ui.viewmodel.PaymentViewModel
import com.wiom.csp.ui.viewmodel.PersonalInfoViewModel
import com.wiom.csp.ui.viewmodel.LocationViewModel
import com.wiom.csp.ui.viewmodel.PhoneViewModel
import com.wiom.csp.util.t

// Screen 0: Phone Entry
@Composable
fun PhoneEntryScreen(viewModel: PhoneViewModel, onNext: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(title = "Wiom Partner+")

            if (uiState.isDuplicate) {
                // ─── Duplicate phone error screen ───
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
                    WiomButton(t("नए नंबर से OTP भेजें", "Send OTP with new number"), onClick = { viewModel.dismissDuplicate() })
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
                            t("पार्टनर बनें", "Become a Partner"),
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
                        OutlinedTextField(
                            value = uiState.phone,
                            onValueChange = { viewModel.onPhoneChanged(it) },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = WiomBorderInput,
                                focusedBorderColor = WiomBorderFocus,
                                unfocusedContainerColor = Color.White,
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text(t("मोबाइल नंबर डालें", "Enter mobile number"), color = WiomHint) }
                        )
                    }
                    // Inline phone error
                    FieldValidationError(uiState.phoneError)
                    // Character count hint
                    if (uiState.phone.isNotEmpty() && uiState.phone.length < 10 && uiState.phoneError == null) {
                        Text(
                            t("${uiState.phone.length}/10 अंक", "${uiState.phone.length}/10 digits"),
                            fontSize = 12.sp,
                            color = WiomHint,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                        )
                    } else {
                        Spacer(Modifier.height(8.dp))
                    }
                    InfoBox("\uD83D\uDD12", t("OTP आपके नंबर पर भेजा जाएगा", "OTP will be sent to your number"))
                }
                BottomBar {
                    WiomButton(
                        t("OTP भेजें", "Send OTP"),
                        onClick = { viewModel.sendOtp { onNext() } },
                        enabled = uiState.phone.length == 10 && uiState.phoneError == null,
                    )
                }
            }
        }

        // Loading overlay
        LoadingOverlay(
            messageHi = "OTP भेज रहे हैं...",
            messageEn = "Sending OTP...",
            isVisible = uiState.isLoading
        )
    }
}

// Screen 1: OTP Verification + T&C
@Composable
fun OtpTncScreen(viewModel: OtpViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Start timer on first composition
    LaunchedEffect(Unit) {
        viewModel.startTimer()
    }

    // Format phone display
    val phoneDisplay = if (uiState.phone.length == 10) {
        val p = uiState.phone
        "+91 ${p.substring(0, 5)} ${p.substring(5)}"
    } else {
        "+91 98765 43210"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(title = t("OTP वेरीफाई", "Verify OTP"), onBack = onBack)

            if (uiState.otpError != null && uiState.timerExpired) {
                // ─── OTP expired error screen ───
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
                    WiomButton(t("नया OTP भेजें", "Send New OTP"), onClick = { viewModel.resendOtp() })
                    Spacer(Modifier.height(8.dp))
                    WiomButton(t("नंबर बदलें", "Change Number"), onClick = { viewModel.changeNumber(); onBack() }, isSecondary = true)
                }
            } else if (uiState.otpError != null && !uiState.timerExpired) {
                // ─── OTP wrong error screen ───
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
                        messageHi = "कृपया दोबारा कोशिश करें",
                        messageEn = "Please try again",
                        type = "error",
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        t("OTP दोबारा भेजें", "Resend OTP"),
                        fontSize = 14.sp, color = WiomPrimary, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { viewModel.resendOtp() },
                    )
                }
                BottomBar {
                    WiomButton(t("वेरीफाई करें", "Verify"), onClick = { viewModel.verifyOtp { onNext() } })
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
                            checked = uiState.tncAccepted,
                            onCheckedChange = { viewModel.onTncChanged(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = WiomPrimary,
                                uncheckedColor = WiomBorderInput,
                            ),
                        )
                        Spacer(Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    t("मैं सभी नियम व शर्तें स्वीकार करता/करती हूँ", "I accept all Terms & Conditions"),
                                    fontSize = 14.sp,
                                    color = WiomText,
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                t("नियम व शर्तें पढ़ें", "Read T&C"),
                                fontSize = 12.sp,
                                color = WiomPrimary,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable { /* Open T&C */ },
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    TrustBadge("\uD83D\uDD12", t("डिजिटल सहमति", "Digital Consent"))
                    Spacer(Modifier.height(16.dp))

                    // ─── OTP Section ───
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
                        digits = uiState.digits,
                        onDigitsChange = { newDigits ->
                            newDigits.forEachIndexed { index, digit ->
                                if (digit != uiState.digits[index]) {
                                    viewModel.onDigitChanged(index, digit)
                                }
                            }
                        },
                    )

                    // Timer / Resend / Change Number
                    if (!uiState.timerExpired) {
                        Text(
                            t("OTP दोबारा भेजें", "Resend OTP") + " ${uiState.timerSeconds}s",
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
                                modifier = Modifier.clickable { viewModel.resendOtp() },
                            )
                            Text("|", fontSize = 14.sp, color = WiomHint)
                            Text(
                                t("नंबर बदलें", "Change Number"),
                                fontSize = 14.sp,
                                color = WiomTextSec,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { viewModel.changeNumber(); onBack() },
                            )
                        }
                    }
                }
                BottomBar {
                    val allFilled = uiState.digits.all { it.isNotBlank() }
                    WiomButton(
                        t("वेरीफाई करें", "Verify"),
                        onClick = { viewModel.verifyOtp { onNext() } },
                        enabled = uiState.tncAccepted && allFilled,
                    )
                }
            }
        }

        // Loading overlay
        LoadingOverlay(
            messageHi = "सत्यापित कर रहे हैं...",
            messageEn = "Verifying...",
            isVisible = uiState.isLoading
        )
    }
}

// Screen 2: Personal & Business Info
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(viewModel: PersonalInfoViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Entity type dropdown state
    var dropdownExpanded by remember { mutableStateOf(false) }
    val entityOptions = listOf("Individual", "Proprietorship", "Partnership", "Private Limited", "LLP")

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("रजिस्ट्रेशन", "Registration"),
            onBack = onBack,
            rightText = t("स्टेप 1/4", "Step 1/4")
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                t("व्यक्तिगत जानकारी", "Personal Information"),
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t("आपकी पहचान और बिज़नेस की जानकारी", "Your identity and business information"),
                fontSize = 14.sp, color = WiomTextSec,
            )
            Spacer(Modifier.height(16.dp))

            FieldLabel(t("पूरा नाम (आधार अनुसार)", "Full Name (as per Aadhaar)"))
            WiomTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChanged(it) },
                placeholder = t("उदाहरण: राजेश कुमार", "Example: Rajesh Kumar"),
            )
            FieldValidationError(uiState.nameError)

            FieldLabel(t("ईमेल", "Email"))
            WiomTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                placeholder = t("उदाहरण: rajesh@email.com", "Example: rajesh@email.com"),
            )
            FieldValidationError(uiState.emailError)

            FieldLabel(t("बिज़नेस प्रकार", "Entity Type"))
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it },
            ) {
                OutlinedTextField(
                    value = uiState.entityType,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text(t("बिज़नेस टाइप चुनें", "Select business type"), color = WiomHint) },
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
                                viewModel.onEntityTypeChanged(option)
                                dropdownExpanded = false
                            },
                        )
                    }
                }
            }
            FieldValidationError(uiState.entityTypeError)
            Spacer(Modifier.height(8.dp))

            FieldLabel(t("व्यापार नाम", "Trade Name"))
            WiomTextField(
                value = uiState.tradeName,
                onValueChange = { viewModel.onTradeNameChanged(it) },
                placeholder = t("उदाहरण: राजेश टेलीकॉम", "Example: Rajesh Telecom"),
            )
            FieldValidationError(uiState.tradeNameError)
        }
        BottomBar {
            WiomButton(
                t("अब लोकेशन बताइए", "Next: Location"),
                onClick = {
                    viewModel.validateAll()
                    if (viewModel.uiState.value.isFormValid) onNext()
                },
                enabled = uiState.isFormValid,
            )
        }
    }
}

// Screen 3: Location
@Composable
fun LocationScreen(viewModel: LocationViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val isNotServiceable = uiState.isServiceable == false

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(
                title = t("रजिस्ट्रेशन", "Registration"),
                onBack = onBack,
                rightText = t("स्टेप 2/4", "Step 2/4")
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    t("लोकेशन जानकारी", "Location Information"),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    t("आपकी दुकान/ऑफिस की लोकेशन", "Your shop/office location"),
                    fontSize = 14.sp, color = WiomTextSec,
                )
                Spacer(Modifier.height(16.dp))

                FieldLabel(t("राज्य", "State"))
                WiomTextField(value = uiState.state, onValueChange = {}, readOnly = true)

                FieldLabel(t("शहर", "City"))
                WiomTextField(value = uiState.city, onValueChange = { viewModel.onCityChanged(it) })

                FieldLabel(t("पिनकोड", "Pincode"))
                WiomTextField(value = uiState.pincode, onValueChange = { viewModel.onPincodeChanged(it) })
                FieldValidationError(uiState.pincodeError)

                FieldLabel(t("पूरा पता", "Full Address"))
                WiomTextField(value = uiState.address, onValueChange = { viewModel.onAddressChanged(it) })

                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TrustBadge("\uD83D\uDCCD", t("GPS कैप्चर हुआ", "GPS Captured"))
                    Text("22.71° N, 75.85° E", fontSize = 12.sp, color = WiomHint)
                }

                if (isNotServiceable) {
                    // ─── Area not serviceable error ───
                    Spacer(Modifier.height(16.dp))
                    ErrorCard(
                        icon = "📍",
                        titleHi = "यह एरिया अभी सर्विसेबल नहीं है",
                        titleEn = "This area is not serviceable yet",
                        messageHi = "हम जल्द ही इस एरिया में आ रहे हैं। Waitlist में जुड़ें और पहले मौका पाएं!",
                        messageEn = "We're coming to this area soon. Join the waitlist and get first opportunity!",
                        type = "warning",
                    )
                    Spacer(Modifier.height(8.dp))
                    InfoBox("\uD83D\uDCCB", t("Waitlist में 47 लोग पहले से हैं", "47 people already on waitlist"))
                    Spacer(Modifier.height(12.dp))
                    WiomButton(t("Waitlist में जुड़ें", "Join Waitlist"), onClick = {})
                    Spacer(Modifier.height(8.dp))
                    WiomButton(t("दूसरा पिनकोड डालें", "Enter different pincode"), onClick = {}, isSecondary = true)
                }
            }
            if (!isNotServiceable) {
                BottomBar {
                    WiomButton(
                        t("अब पंजीकरण शुल्क भरें", "Next: Registration Fee"),
                        onClick = { viewModel.checkServiceability() },
                    )
                }
            }
        }

        // Loading overlay
        LoadingOverlay(
            messageHi = "सर्विसेबिलिटी जांच रहे हैं...",
            messageEn = "Checking serviceability...",
            isVisible = uiState.isLoading
        )
    }
}

// Screen 4: Registration Fee ₹2,000
@Composable
fun RegFeeScreen(viewModel: PaymentViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Set amount on first composition
    LaunchedEffect(Unit) {
        viewModel.setAmount(2000)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
            AppHeader(
                title = t("रजिस्ट्रेशन फ़ीस", "Registration Fee"),
                onBack = onBack,
                rightText = t("स्टेप 4/4", "Step 4/4")
            )

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
                        WiomButton(t("दोबारा भुगतान करें", "Retry Payment"), onClick = { viewModel.retry() })
                        Spacer(Modifier.height(8.dp))
                        WiomButton(t("बाद में करें", "Pay Later"), onClick = {}, isSecondary = true)
                    }
                }
                PaymentResult.TIMEOUT -> {
                    // ─── Payment timeout/pending screen ───
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
                                Text(uiState.transactionId ?: "UPI123456789", fontSize = 13.sp, color = WiomText)
                            }
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Status", fontSize = 13.sp, color = WiomTextSec)
                                Text("\u23F3 Pending", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WiomWarning700)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoBox("\uD83D\uDD12", t("48 घंटे में auto-refund अगर fail हो", "Auto-refund within 48hrs if failed"))
                        Spacer(Modifier.height(16.dp))
                        WiomButton(t("Status Refresh करें", "Refresh Status"), onClick = { viewModel.retry() })
                        Spacer(Modifier.height(8.dp))
                        WiomButton(t("हमसे बात करें", "Talk to us"), onClick = {}, isSecondary = true)
                    }
                }
                else -> {
                    // ─── Normal happy path ───
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            t("रजिस्ट्रेशन फ़ीस", "Registration Fee"),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText,
                        )
                        Spacer(Modifier.height(12.dp))
                        AmountBox("\u20B92,000", t("रजिस्ट्रेशन फ़ीस", "Registration Fee"))
                        Spacer(Modifier.height(12.dp))
                        WiomCard {
                            Text(
                                "\u2139\uFE0F ${t("जरूरी जानकारी", "Important Information")}",
                                fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomText,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                t(
                                    "शुल्क भुगतान के बाद आपके दस्तावेज़ जमा किए जाएंगे",
                                    "Your documents will be collected after fee payment"
                                ),
                                fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                            )
                            Spacer(Modifier.height(8.dp))
                            TrustBadge("\uD83D\uDD12", t("Reject होने पर full refund मिलेगा", "Full refund if rejected"))
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoBox("\uD83D\uDCB0", t("शुल्क भुगतान के बाद सत्यापन प्रक्रिया शुरू होगी", "Verification process will start after fee payment"))
                    }
                    BottomBar {
                        WiomButton(
                            "\u20B92,000 ${t("भुगतान करें", "Pay Now")}",
                            onClick = { viewModel.initiatePayment { onNext() } },
                        )
                    }
                }
            }
        }

        // Loading overlay
        LoadingOverlay(
            messageHi = "भुगतान प्रोसेस हो रहा है...",
            messageEn = "Processing payment...",
            isVisible = uiState.isProcessing
        )
    }
}
