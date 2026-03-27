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
                        placeholder = { Text(t("मोबाइल नंबर डालें", "Enter mobile number"), color = WiomHint) }
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
                            t("मैं सभी नियम व शर्तें स्वीकार करता/करती हूँ", "I accept all Terms & Conditions"),
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
    var timerSeconds by remember { mutableIntStateOf(30) }
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
        AppHeader(title = t("OTP वेरीफाई", "Verify OTP"), onBack = onBack)

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
                WiomButton(t("वेरीफाई करें", "Verify"), onClick = {})
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
                                timerSeconds = 30
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
                    t("वेरीफाई करें", "Verify"),
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
    val entityOptions = listOf("Individual")

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("रजिस्ट्रेशन", "Registration"),
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
                value = OnboardingState.personalName,
                onValueChange = { OnboardingState.personalName = it },
                placeholder = t("उदाहरण: राजेश कुमार", "Example: Rajesh Kumar"),
            )

            FieldLabel(t("ईमेल", "Email"))
            WiomTextField(
                value = OnboardingState.personalEmail,
                onValueChange = { OnboardingState.personalEmail = it },
                placeholder = t("उदाहरण: rajesh@email.com", "Example: rajesh@email.com"),
            )

            FieldLabel(t("बिज़नेस प्रकार", "Entity Type"))
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
                                OnboardingState.entityType = option
                                dropdownExpanded = false
                            },
                        )
                    }
                }
            }

            FieldLabel(t("व्यापार नाम", "Trade Name"))
            WiomTextField(
                value = OnboardingState.tradeName,
                onValueChange = { OnboardingState.tradeName = it },
                placeholder = t("उदाहरण: राजेश टेलीकॉम", "Example: Rajesh Telecom"),
            )
        }
        BottomBar {
            val allFilled = OnboardingState.personalName.isNotBlank() &&
                    OnboardingState.personalEmail.isNotBlank() &&
                    OnboardingState.entityType.isNotBlank() &&
                    OnboardingState.tradeName.isNotBlank()
            WiomButton(
                t("अब लोकेशन बताइए", "Next: Location"),
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
            title = t("रजिस्ट्रेशन", "Registration"),
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
            var stateExpanded by remember { mutableStateOf(false) }
            val indianStates = listOf(
                "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
                "Chhattisgarh", "Goa", "Gujarat", "Haryana",
                "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
                "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya",
                "Mizoram", "Nagaland", "Odisha", "Punjab",
                "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana",
                "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal",
                "Andaman & Nicobar Islands", "Chandigarh", "Dadra & Nagar Haveli and Daman & Diu",
                "Delhi", "Jammu & Kashmir", "Ladakh", "Lakshadweep", "Puducherry",
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
            WiomTextField(value = OnboardingState.address, onValueChange = { OnboardingState.address = it })

            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TrustBadge("\uD83D\uDCCD", t("GPS कैप्चर हुआ", "GPS Captured"))
                Text("22.71° N, 75.85° E", fontSize = 12.sp, color = WiomHint)
            }

            if (scenario == Scenario.AREA_NOT_SERVICEABLE) {
                // ─── AREA_NOT_SERVICEABLE error ───
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
        if (scenario != Scenario.AREA_NOT_SERVICEABLE) {
            BottomBar {
                WiomButton(t("अब registration शुल्क भरें", "Next: Registration Fee"), onClick = onNext)
            }
        }
    }
}

// Screen 4: KYC Documents
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(viewModel: KycViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val scenario = OnboardingState.activeScenario

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
            "pan" -> OnboardingState.panUploaded = true
            "aadhaar_front" -> OnboardingState.aadhaarFrontUploaded = true
            "aadhaar_back" -> OnboardingState.aadhaarBackUploaded = true
            "gst" -> OnboardingState.gstUploaded = true
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
                        // ─── Step 1: Choose source ───
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
                        // ─── Step 2: Preview photo ───
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
                        // ─── Step 3: Uploading ───
                        var uploadProgress by remember { mutableStateOf(0f) }
                        LaunchedEffect(Unit) {
                            // Simulate upload progress
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

            when (scenario) {
                Scenario.KYC_PAN_MISMATCH -> {
                    // ─── KYC_PAN_MISMATCH ───
                    UploadRowError("\uD83E\uDEAA", t("PAN Card", "PAN Card"), t("Name Mismatch ✗", "Name Mismatch ✗"), isError = true)
                    Spacer(Modifier.height(8.dp))
                    UploadRow("\uD83D\uDCC4", t("आधार कार्ड", "Aadhaar Card"), t("वेरीफाइड ✓", "Verified ✓"), isVerified = true)
                    Spacer(Modifier.height(8.dp))
                    UploadRow("\uD83D\uDCCB", t("GST प्रमाणपत्र", "GST Certificate"), t("वेरीफाइड ✓", "Verified ✓"), isVerified = true)
                    Spacer(Modifier.height(12.dp))
                    ErrorCard(
                        icon = "\uD83E\uDEAA",
                        titleHi = "PAN नाम मेल नहीं खाता",
                        titleEn = "PAN Name Mismatch",
                        messageHi = "PAN पर नाम: Rajesh K Sharma\nआपने डाला: राजेश कुमार\n\nकृपया सही नाम डालें या PAN अपडेट कराएं",
                        messageEn = "Name on PAN: Rajesh K Sharma\nYou entered: Rajesh Kumar\n\nPlease enter correct name or update PAN",
                        type = "error",
                    )
                }
                Scenario.KYC_AADHAAR_EXPIRED -> {
                    // ─── KYC_AADHAAR_EXPIRED ───
                    UploadRow("\uD83D\uDCC4", t("PAN Card", "PAN Card"), t("वेरीफाइड ✓", "Verified ✓"), isVerified = true)
                    Spacer(Modifier.height(8.dp))
                    UploadRowError("\uD83D\uDCC4", t("आधार कार्ड", "Aadhaar Card"), t("Address Update Required", "Address Update Required"), isWarning = true)
                    Spacer(Modifier.height(8.dp))
                    UploadRow("\uD83D\uDCCB", t("GST प्रमाणपत्र", "GST Certificate"), t("वेरीफाइड ✓", "Verified ✓"), isVerified = true)
                    Spacer(Modifier.height(12.dp))
                    ErrorCard(
                        icon = "\u26A0\uFE0F",
                        titleHi = "Aadhaar पता पुराना है",
                        titleEn = "Aadhaar Address Outdated",
                        messageHi = "आपके Aadhaar पर पता पुराना है। कृपया UIDAI पोर्टल पर अपडेट करें।",
                        messageEn = "Address on Aadhaar is outdated. Please update on UIDAI portal.",
                        type = "warning",
                    )
                    Spacer(Modifier.height(8.dp))
                    InfoBox("\uD83C\uDF10", t("uidai.gov.in पर अपडेट करें", "Update at uidai.gov.in"))
                }
                Scenario.KYC_PAN_AADHAAR_UNLINKED -> {
                    // ─── KYC_PAN_AADHAAR_UNLINKED ───
                    UploadRow("\uD83D\uDCC4", t("PAN Card", "PAN Card"), t("वेरीफाइड ✓", "Verified ✓"), isVerified = true)
                    Spacer(Modifier.height(8.dp))
                    UploadRow("\uD83D\uDCC4", t("आधार कार्ड", "Aadhaar Card"), t("वेरीफाइड ✓", "Verified ✓"), isVerified = true)
                    Spacer(Modifier.height(8.dp))
                    UploadRow("\uD83D\uDCCB", t("GST प्रमाणपत्र", "GST Certificate"), t("वेरीफाइड ✓", "Verified ✓"), isVerified = true)
                    Spacer(Modifier.height(12.dp))
                    ErrorCard(
                        icon = "\uD83D\uDD17",
                        titleHi = "PAN-Aadhaar लिंक नहीं है",
                        titleEn = "PAN-Aadhaar Not Linked",
                        messageHi = "आगे बढ़ने के लिए PAN और Aadhaar लिंक होना ज़रूरी है।",
                        messageEn = "PAN and Aadhaar must be linked to proceed.",
                        type = "error",
                    )
                    Spacer(Modifier.height(8.dp))
                    InfoBox("\uD83C\uDF10", t("incometax.gov.in पर लिंक करें", "Link at incometax.gov.in"))
                }
                else -> {
                    // ─── Normal happy path: interactive upload cards ───

                    // PAN Card
                    KycUploadCard(
                        emoji = "\uD83E\uDEAA",
                        labelHi = "PAN Card अपलोड करें",
                        labelEn = "Upload PAN Card",
                        nameHi = "PAN Card",
                        nameEn = "PAN Card",
                        isUploaded = OnboardingState.panUploaded,
                        onUploadClick = { sheetTarget = "pan" },
                        onRemoveClick = { OnboardingState.panUploaded = false },
                    )
                    Spacer(Modifier.height(8.dp))

                    // Aadhaar Front
                    KycUploadCard(
                        emoji = "\uD83D\uDCC4",
                        labelHi = "आधार कार्ड — सामने अपलोड करें",
                        labelEn = "Upload Aadhaar Card — Front",
                        nameHi = "आधार कार्ड — सामने",
                        nameEn = "Aadhaar Card — Front",
                        isUploaded = OnboardingState.aadhaarFrontUploaded,
                        onUploadClick = { sheetTarget = "aadhaar_front" },
                        onRemoveClick = { OnboardingState.aadhaarFrontUploaded = false },
                    )
                    Spacer(Modifier.height(8.dp))

                    // Aadhaar Back
                    KycUploadCard(
                        emoji = "\uD83D\uDCC4",
                        labelHi = "आधार कार्ड — पीछे अपलोड करें",
                        labelEn = "Upload Aadhaar Card — Back",
                        nameHi = "आधार कार्ड — पीछे",
                        nameEn = "Aadhaar Card — Back",
                        isUploaded = OnboardingState.aadhaarBackUploaded,
                        onUploadClick = { sheetTarget = "aadhaar_back" },
                        onRemoveClick = { OnboardingState.aadhaarBackUploaded = false },
                    )
                    Spacer(Modifier.height(8.dp))

                    // GST Certificate
                    KycUploadCard(
                        emoji = "\uD83D\uDCCB",
                        labelHi = "GST प्रमाणपत्र अपलोड करें",
                        labelEn = "Upload GST Certificate",
                        nameHi = "GST प्रमाणपत्र",
                        nameEn = "GST Certificate",
                        isUploaded = OnboardingState.gstUploaded,
                        onUploadClick = { sheetTarget = "gst" },
                        onRemoveClick = { OnboardingState.gstUploaded = false },
                    )
                }
            }
        }
        BottomBar {
            val isScenarioDisabled = scenario == Scenario.KYC_PAN_MISMATCH ||
                    scenario == Scenario.KYC_AADHAAR_EXPIRED ||
                    scenario == Scenario.KYC_PAN_AADHAAR_UNLINKED
            val allUploaded = OnboardingState.panUploaded &&
                    OnboardingState.aadhaarFrontUploaded &&
                    OnboardingState.aadhaarBackUploaded &&
                    OnboardingState.gstUploaded
            WiomButton(
                t("अब बैंक का विवरण दें", "Next: Bank Details"),
                onClick = onNext,
                enabled = !isScenarioDisabled && (scenario != Scenario.NONE || allUploaded),
            )
        }
    }
}

// ─── KYC Upload Card (reusable composable) ──────────────────────
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

// Screen 5: Registration Fee ₹2,000
@Composable
fun RegFeeScreen(viewModel: PaymentViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val scenario = OnboardingState.activeScenario

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        AppHeader(
            title = t("रजिस्ट्रेशन फ़ीस", "Registration Fee"),
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
                                "भुगतान के बाद आपकी profile Business/QA team द्वारा review की जाएगी।",
                                "After payment, your profile will be reviewed by the Business/QA team."
                            ),
                            fontSize = 14.sp, color = WiomTextSec, lineHeight = 20.sp,
                        )
                        Spacer(Modifier.height(8.dp))
                        TrustBadge("\uD83D\uDD12", t("Reject होने पर full refund मिलेगा", "Full refund if rejected"))
                    }
                    Spacer(Modifier.height(8.dp))
                    InfoBox("✓", t("भुगतान के बाद सत्यापन प्रक्रिया शुरू होगी", "Verification process will begin after payment"))
                }
                BottomBar {
                    WiomButton("₹2,000 ${t("भुगतान करें", "Pay Now")}", onClick = onNext)
                }
            }
        }
    }
}
