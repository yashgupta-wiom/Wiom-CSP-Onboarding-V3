package com.wiom.csp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.Scenario
import com.wiom.csp.data.scenarioMeta
import com.wiom.csp.ui.theme.*
import com.wiom.csp.util.t

// Status bar mockup
@Composable
fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(WiomHeader)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("9:41", color = WiomSurface, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        Text("100%", color = WiomSurface, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

// App header bar
@Composable
fun AppHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    rightText: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth().background(WiomHeader)) {
        Spacer(Modifier.windowInsetsPadding(WindowInsets.statusBars))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(WiomHeader)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = WiomSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
        }
        Text(
            title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = WiomSurface,
            modifier = Modifier.weight(1f)
        )
        if (rightText != null) {
            Text(rightText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WiomSurface.copy(alpha = 0.5f))
        }
        // Language toggle button
        Spacer(Modifier.width(8.dp))
        Text(
            if (com.wiom.csp.util.Lang.isHindi) "En" else "हि",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WiomPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(888.dp))
                .background(WiomPrimaryLight)
                .clickable { com.wiom.csp.util.Lang.toggle() }
                .padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

// Primary CTA button
@Composable
fun WiomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSecondary: Boolean = false,
    backgroundColor: Color = if (isSecondary) WiomPrimaryLight else WiomPrimary,
    textColor: Color = if (isSecondary) WiomPrimary else Color.White,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.4f),
            disabledContentColor = textColor.copy(alpha = 0.4f),
        ),
        elevation = if (!isSecondary && enabled) ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        ) else ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

// Bottom bar with CTA
@Composable
fun BottomBar(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = WiomSurface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            content = content
        )
    }
}

// Card component
@Composable
fun WiomCard(
    modifier: Modifier = Modifier,
    borderColor: Color = WiomBorder,
    backgroundColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shadowElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

// Input field label
@Composable
fun FieldLabel(text: String) {
    Text(
        text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = WiomTextSec,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

// Styled text field
@Composable
fun WiomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    isVerified: Boolean = false,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onFocusChanged: ((Boolean) -> Unit)? = null,
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = if (errorMessage != null) 4.dp else 12.dp)
                .then(if (onFocusChanged != null) Modifier.onFocusChanged { onFocusChanged(it.isFocused) } else Modifier),
            readOnly = readOnly,
            visualTransformation = visualTransformation,
            placeholder = { Text(placeholder, color = WiomHint) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = when {
                    isError -> WiomNegative
                    isVerified -> WiomPositive
                    else -> WiomBorderFocus
                },
                unfocusedBorderColor = when {
                    isError -> WiomNegative
                    isVerified -> WiomPositive
                    else -> WiomBorderInput
                },
                focusedContainerColor = when {
                    isError -> WiomNegative100
                    isVerified -> WiomPositive100
                    else -> Color.White
                },
                unfocusedContainerColor = when {
                    isError -> WiomNegative100
                    isVerified -> WiomPositive100
                    else -> Color.White
                },
            ),
            trailingIcon = if (isVerified) {
                { Icon(Icons.Default.Check, "Verified", tint = WiomPositive) }
            } else null,
            singleLine = true,
        )
        if (errorMessage != null) {
            Text(
                errorMessage,
                fontSize = 12.sp,
                color = WiomNegative,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
            )
        }
    }
}

// Info box (ibox-p, ibox-g, ibox-o)
@Composable
fun InfoBox(
    icon: String,
    text: String,
    type: InfoBoxType = InfoBoxType.INFO,
    modifier: Modifier = Modifier,
) {
    val (bg, textColor) = when (type) {
        InfoBoxType.INFO -> WiomInfo100 to WiomTextSec
        InfoBoxType.SUCCESS -> WiomPositive100 to Color(0xFF005C30)
        InfoBoxType.WARNING -> WiomWarning200 to WiomWarning700
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(icon, fontSize = 16.sp)
        Text(text, fontSize = 14.sp, color = textColor, lineHeight = 20.sp)
    }
}

enum class InfoBoxType { INFO, SUCCESS, WARNING }

// Upload row — overload matching screen call sites: (icon, label, isUploaded, onUpload)
@Composable
fun UploadRow(
    icon: String,
    label: String,
    isUploaded: Boolean,
    onUpload: () -> Unit,
) {
    UploadRow(
        icon = icon,
        name = label,
        statusText = if (isUploaded) "${t("अपलोड हो गया", "Uploaded")} ✓" else t("टैप करें", "Tap to Upload"),
        isVerified = isUploaded,
        onClick = onUpload,
    )
}

// Upload row — base implementation
@Composable
fun UploadRow(
    icon: String,
    name: String,
    statusText: String,
    isVerified: Boolean = false,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isVerified) WiomPositive100 else Color.White)
            .border(
                1.dp,
                if (isVerified) WiomPositive300 else WiomBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("$icon $name", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = WiomText)
            Text(
                statusText,
                fontSize = 12.sp,
                color = if (isVerified) WiomPositive else WiomHint,
                fontWeight = if (isVerified) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
        if (isVerified) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(WiomPositive100)
                    .border(1.dp, WiomPositive, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("✓", color = WiomPositive, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// Amount display box
@Composable
fun AmountBox(amount: String, label: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WiomBgSec)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(amount, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = WiomText)
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 14.sp, color = WiomTextSec)
    }
}

// Checklist item
@Composable
fun ChecklistItem(
    text: String,
    subtitle: String? = null,
    isDone: Boolean = true,
    isWaiting: Boolean = false,
    isLast: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> WiomPositive
                        isWaiting -> WiomWarning200
                        else -> WiomBgSec
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (isDone) "✓" else "⋯",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDone) Color.White else WiomWarning700,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = WiomText)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = if (isWaiting) WiomWarning700 else WiomTextSec)
            }
        }
    }
}

// Trust badge
@Composable
fun TrustBadge(icon: String, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(888.dp))
            .background(WiomPositive100)
            .border(1.dp, WiomPositive300, RoundedCornerShape(888.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(icon, fontSize = 14.sp)
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WiomPositive)
    }
}

// Chip
@Composable
fun WiomChip(
    text: String,
    backgroundColor: Color = WiomPositive100,
    textColor: Color = WiomPositive,
) {
    Text(
        text,
        modifier = Modifier
            .clip(RoundedCornerShape(888.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = textColor,
    )
}

// Section header
@Composable
fun SectionHeader(text: String) {
    Text(
        text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = WiomTextSec,
        letterSpacing = 0.5.sp,
    )
}

// Progress bar
@Composable
fun WiomProgressBar(progress: Float) {
    // Legacy overload — delegates to segmented version using OnboardingState
    WiomSegmentedProgressBar(
        currentScreen = OnboardingState.currentScreen,
        totalScreens = OnboardingState.TOTAL_SCREENS,
    )
}

@Composable
fun WiomSegmentedProgressBar(currentScreen: Int, totalScreens: Int = 15) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for (i in 0 until totalScreens) {
            val color = when {
                i < currentScreen -> WiomPositive      // Done
                i == currentScreen -> WiomPrimary      // Active
                else -> WiomBorderInput                // Future
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

// Verification row
@Composable
fun VerificationItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("✓", fontSize = 14.sp, color = WiomPositive, fontWeight = FontWeight.Bold)
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WiomPositive)
    }
}

// Module card for training
@Composable
fun ModuleCard(
    icon: String,
    title: String,
    subtitle: String,
    isDone: Boolean = false,
    isCurrent: Boolean = false,
    badgeText: String = "",
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    isDone -> WiomPositive100
                    isCurrent -> WiomPrimaryLight
                    else -> Color.White
                }
            )
            .border(
                1.dp,
                when {
                    isDone -> WiomPositive300
                    isCurrent -> WiomPrimary
                    else -> WiomBorder
                },
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isDone) WiomPositive100 else WiomWarning200),
            contentAlignment = Alignment.Center,
        ) {
            Text(icon, fontSize = 16.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomText)
            Text(subtitle, fontSize = 12.sp, color = WiomTextSec)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isDone) WiomPositive100 else WiomWarning200)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                if (isDone) "✓" else badgeText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDone) WiomPositive else WiomWarning700,
            )
        }
    }
}

// Quick action card for Go Live screen
@Composable
fun QuickActionCard(icon: String, title: String, subtitle: String, onClick: () -> Unit = {}) {
    WiomCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(icon, fontSize = 20.sp)
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WiomText)
                Text(subtitle, fontSize = 12.sp, color = WiomTextSec)
            }
        }
    }
}

// OTP input row
@Composable
fun OtpRow(
    values: List<String> = listOf("4", "7", "2", "9"),
    isError: Boolean = false,
    isExpired: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
    ) {
        values.forEach { digit ->
            Box(
                modifier = Modifier
                    .size(48.dp, 56.dp)
                    .then(
                        if (isExpired) Modifier.border(2.dp, WiomHint, RoundedCornerShape(12.dp))
                        else if (isError) Modifier.border(2.dp, WiomNegative, RoundedCornerShape(12.dp))
                        else Modifier.border(2.dp, WiomBorderFocus, RoundedCornerShape(12.dp))
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    digit,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpired) WiomText.copy(alpha = 0.4f) else WiomText,
                )
            }
        }
    }
}

// Interactive OTP input row with hidden TextField
@Composable
fun OtpInputRow(
    digits: List<String>,
    onDigitsChange: (List<String>) -> Unit,
) {
    // Hidden text field that captures keyboard input
    val combinedValue = digits.joinToString("")
    var focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
    ) {
        // Hidden input field
        androidx.compose.foundation.text.BasicTextField(
            value = combinedValue,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() }.take(4)
                val newDigits = List(4) { i -> filtered.getOrElse(i) { ' ' }.toString().trim() }
                onDigitsChange(newDigits)
            },
            modifier = Modifier
                .matchParentSize()
                .then(Modifier.alpha(0f))
                .focusRequester(focusRequester),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
            ),
            singleLine = true,
        )

        // Visible OTP boxes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        ) {
            digits.forEachIndexed { index, digit ->
                val isFocused = combinedValue.length == index
                Box(
                    modifier = Modifier
                        .size(48.dp, 56.dp)
                        .border(
                            2.dp,
                            if (digit.isNotEmpty()) WiomPositive
                            else if (isFocused) WiomPrimary
                            else WiomBorderFocus,
                            RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (digit.isNotEmpty()) WiomPositive100 else Color.White)
                        .clickable { focusRequester.requestFocus() },
                    contentAlignment = Alignment.Center,
                ) {
                    if (digit.isNotEmpty()) {
                        Text(digit, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomText)
                    } else if (isFocused) {
                        // Blinking cursor
                        Text("|", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WiomPrimary)
                    }
                }
            }
        }
    }

    // Auto-focus on composition
    LaunchedEffect(Unit) {
        try { focusRequester.requestFocus() } catch (_: Exception) {}
    }
}

// Stepper dots
@Composable
fun StepperDots(total: Int, current: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WiomBgSec)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    ) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .widthIn(max = 32.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        when {
                            i < current -> WiomPositive
                            i == current -> WiomPrimary
                            else -> WiomBorderInput
                        }
                    )
            )
        }
    }
}

// ─── Error Card ─────────────────────────────────────────────────
@Composable
fun ErrorCard(
    icon: String,
    titleHi: String,
    titleEn: String,
    messageHi: String,
    messageEn: String,
    type: String = "error",
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val (borderColor, bgColor, titleColor) = when (type) {
        "warning" -> Triple(WiomWarning, WiomWarning200, WiomWarning700)
        "info" -> Triple(WiomInfo, WiomInfo100, WiomInfo)
        else -> Triple(WiomNegative, WiomNegative100, WiomNegative)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text("$icon ${t(titleHi, titleEn)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = titleColor)
        Spacer(Modifier.height(4.dp))
        Text(t(messageHi, messageEn), fontSize = 14.sp, color = WiomText, lineHeight = 20.sp)
        if (content != null) {
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

// ─── Loading Overlay ────────────────────────────────────────────
@Composable
fun LoadingOverlay(messageHi: String, messageEn: String, isVisible: Boolean) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = WiomPrimary, strokeWidth = 4.dp)
                Spacer(Modifier.height(16.dp))
                Text(
                    t(messageHi, messageEn),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ─── Loading Overlay (state-driven convenience overload) ────────
@Composable
fun LoadingOverlay(message: String = "") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = WiomPrimary)
            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(message, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

// ─── Upload Row Error ───────────────────────────────────────────
@Composable
fun UploadRowError(
    icon: String,
    name: String,
    statusText: String,
    isError: Boolean = false,
    isWarning: Boolean = false,
) {
    val (borderColor, bgColor, statusColor) = when {
        isError -> Triple(WiomNegative, WiomNegative100, WiomNegative)
        isWarning -> Triple(WiomWarning, WiomWarning200, WiomWarning700)
        else -> Triple(WiomBorder, Color.White, WiomHint)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("$icon $name", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = WiomText)
            Text(statusText, fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
        }
        if (isError) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(WiomNegative100)
                    .border(1.dp, WiomNegative, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("✗", color = WiomNegative, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
        if (isWarning) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(WiomWarning200)
                    .border(1.dp, WiomWarning, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("⚠", color = WiomWarning700, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// ─── Scenario Simulator Panel ───────────────────────────────────
@Composable
fun ScenarioSimulatorPanel(currentScreen: Int) {
    val expanded = OnboardingState.simulatorExpanded
    val active = OnboardingState.activeScenario
    val activeMeta = scenarioMeta[active]
    val panelBg = Color(0xFF1A1025)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(panelBg)
            .animateContentSize(),
    ) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { OnboardingState.simulatorExpanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("\uD83E\uDDEA", fontSize = 14.sp)
                Text(
                    "Scenario Simulator",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                if (active != Scenario.NONE && activeMeta != null) {
                    Text(
                        "${activeMeta.icon} ${t(activeMeta.labelHi, activeMeta.labelEn)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(888.dp))
                            .background(WiomPrimary)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }

        // Expanded body
        if (expanded) {
            val screenScenarios = OnboardingState.scenariosForScreen(currentScreen)
            val allScenarios = Scenario.entries.filter { it != Scenario.NONE }
            var showAll by remember { mutableStateOf(false) }
            val displayList = if (showAll) allScenarios else screenScenarios

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                // Toggle: this screen / all
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp),
                ) {
                    Text(
                        t("इस स्क्रीन", "This Screen"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!showAll) WiomPrimary else WiomHint,
                        modifier = Modifier
                            .clip(RoundedCornerShape(888.dp))
                            .background(if (!showAll) WiomPrimaryLight else Color.Transparent)
                            .clickable { showAll = false }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                    Text(
                        t("सब देखें", "Show All"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showAll) WiomPrimary else WiomHint,
                        modifier = Modifier
                            .clip(RoundedCornerShape(888.dp))
                            .background(if (showAll) WiomPrimaryLight else Color.Transparent)
                            .clickable { showAll = true }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                    Spacer(Modifier.weight(1f))
                    if (active != Scenario.NONE) {
                        Text(
                            "Clear",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiomNegative,
                            modifier = Modifier
                                .clip(RoundedCornerShape(888.dp))
                                .background(WiomNegative100)
                                .clickable { OnboardingState.clearScenario() }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }

                if (displayList.isEmpty()) {
                    Text(
                        t("इस स्क्रीन के लिए कोई scenario नहीं", "No scenarios for this screen"),
                        fontSize = 12.sp,
                        color = WiomHint,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                } else {
                    // Group by category
                    val grouped = displayList.groupBy { scenarioMeta[it]?.category ?: "" }
                    grouped.forEach { (category, scenarios) ->
                        Text(
                            category.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiomHint,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                        )
                        // Buttons in a flow
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            scenarios.forEach { scenario ->
                                val meta = scenarioMeta[scenario]!!
                                val isActive = active == scenario
                                Text(
                                    "${meta.icon} ${t(meta.labelHi, meta.labelEn)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isActive) Color.White else Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isActive) WiomNegative else Color.White.copy(alpha = 0.1f)
                                        )
                                        .clickable { OnboardingState.triggerScenario(scenario) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ─── Field Validation Error ─────────────────────────────────────
@Composable
fun FieldValidationError(error: String?) {
    if (error != null) {
        Text(
            text = error,
            color = WiomNegative,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 8.dp)
        )
    }
}

// ─── Retry Card ─────────────────────────────────────────────────
@Composable
fun RetryCard(
    titleHi: String, titleEn: String,
    messageHi: String, messageEn: String,
    onRetry: () -> Unit
) {
    WiomCard {
        Column {
            Text(t(titleHi, titleEn), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WiomWarning)
            Spacer(modifier = Modifier.height(4.dp))
            Text(t(messageHi, messageEn), fontSize = 14.sp, color = WiomText, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            WiomButton(
                text = t("फिर से कोशिश करें", "Retry"),
                onClick = onRetry
            )
        }
    }
}

// ─── Empty State Card ───────────────────────────────────────────
@Composable
fun EmptyStateCard(
    emoji: String,
    titleHi: String, titleEn: String,
    subtitleHi: String, subtitleEn: String
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(t(titleHi, titleEn), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WiomText, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(4.dp))
        Text(t(subtitleHi, subtitleEn), fontSize = 14.sp, color = WiomTextSec, textAlign = TextAlign.Center)
    }
}

// ─── Toast Notification (auto-dismisses after 2500ms) ───────────
@Composable
fun WiomToast(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    type: String = "info", // "info", "success", "error"
) {
    if (isVisible) {
        LaunchedEffect(message) {
            delay(2500)
            onDismiss()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (type) {
                    "success" -> WiomPositive
                    "error" -> WiomNegative
                    else -> Color(0xFF443152)
                },
                shadowElevation = 8.dp,
            ) {
                Text(
                    message,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ─── Payment Success Overlay ────────────────────────────────────
@Composable
fun PaymentSuccessOverlay(amountText: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
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
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                amountText,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = WiomPositive,
            )
        }
    }
}
