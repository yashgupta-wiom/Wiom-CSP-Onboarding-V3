# Wiom CSP Onboarding — Developer Guide

**Version:** 3.2 | **Last Updated:** 30 March 2026
**Audience:** Production development team

---

## 1. What This Repo Is (and Isn't)

### This is a REFERENCE PROTOTYPE — not production code.

**Use it for:**
- Understanding the full 15-screen onboarding flow (Pitch + Screens 0-14)
- Seeing the UI — every screen, every state, every error scenario
- Testing scenarios via the built-in Scenario Simulator
- Stakeholder demos and walkthroughs
- Design reference — all colors, typography, and components are production-accurate

**Do NOT use it as:**
- Starter code for production. The architecture is intentionally simplified for prototype speed.
- A codebase to fork or refactor. You will spend more time untangling than building fresh.

**For production, build from scratch using:**
- `PRD_AI_AGENT.md` as the technical spec (machine-readable, structured for implementation)
- `PRD_HUMAN.md` as the product spec (human-readable, full context and rationale)
- This prototype as the visual/behavioral reference
- Architecture: **MVVM + Hilt DI + Repository pattern + Navigation library + Room/DataStore**

### Why not use this code directly?

The prototype uses shortcuts that are fine for demos but wrong for production:

| Prototype Shortcut | Production Requirement |
|--------------------|----------------------|
| Global `OnboardingState` singleton | MVVM + Hilt + Repository + Room database + DataStore for persistence |
| `AnimatedContent` with integer screen index | Jetpack Navigation library with typed routes + deep links |
| No dependency injection | Hilt modules for all dependencies |
| No repository layer | Repository pattern with remote (Retrofit) + local (Room) data sources |
| Hardcoded simulated delays | Real API calls with proper error handling |

---

## 2. How to Test the Full Flow

### Option A: HTML Prototype (Recommended for fastest testing)

```
Open: prototype/index.html in any browser
```

This is the most complete version. It includes:
- All **15 screens** (Pitch + Screens 0-14) with full visual fidelity
- All **22 error scenarios** across 7 categories — triggerable via the Scenario Simulator panel
- Interactive upload flows (camera/gallery simulation, preview, progress bar)
- Bilingual toggle (Hindi/English) on every screen
- Full happy path + all branch point views (approved/rejected)

No build tools, no SDK, no dependencies. Just open the file.

### Option B: Android APK

```bash
# Pre-built APK (if available):
ls apk/

# Or build from source (requires Android SDK):
export ANDROID_HOME=~/Library/Android/sdk   # macOS
export ANDROID_HOME=~/Android/Sdk            # Linux
./gradlew assembleDebug
# APK output: app/build/outputs/apk/debug/app-debug.apk
```

The APK includes:
- Happy path flow through all 15 screens
- Scenario Simulator panel (expandable)
- Dashboard bridge receiver for remote control
- Bilingual toggle

> **Tip:** The HTML prototype is more complete for error scenario testing. Use the APK when you need to verify Android-specific behavior (navigation, back button, keyboard handling).

---

## 3. Dev Skip / Bypass Options

The prototype has built-in shortcuts to skip waiting states. These exist because Screens 9 and 10 are branch points where the app normally waits for an external team decision.

### Screen 9 (Verification) — "Dev: Skip to Stage 3"
- This screen shows a "waiting for verification team" state
- The dev skip button bypasses the wait and moves directly to Screen 10 (Technical Assessment)
- In production, this transition happens when the verification team approves via the admin panel

### Screen 10 (Technical Assessment) — "Dev: Skip to Policy & SLA"
- This screen shows a "waiting for technical team" state
- The dev skip button bypasses the wait and moves directly to Screen 11 (Policy & SLA)
- In production, this transition happens when the technical team approves via the admin panel

### Scenario Simulator Panel
- **Expanded by default** in the prototype
- Use it to trigger any of the 22 error scenarios on any screen
- Each scenario maps to a specific screen — triggering it instantly shows that error state
- Clear the scenario to return to the happy path

### Dashboard (Remote Control)
For remote-controlling the prototype app from a separate browser window:

```bash
# Terminal 1: Start the bridge server
cd dashboard/
python bridge.py

# Terminal 2 (or browser): Open the dashboard
open dashboard/index.html
```

The dashboard can:
- Navigate the app to any screen
- Trigger any error scenario
- Approve/reject at branch points (Screens 9 and 10)
- Pre-fill all form fields ("Filled Mode")
- Control language toggle

---

## 4. All 22 Error Scenarios (across 8 categories)

Reference: `PRD_HUMAN.md` Section 6 for full details including exact Hindi/English copy.

### Global Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 1 | `NO_INTERNET` | Any | Device loses connectivity | Full-screen error: "इंटरनेट कनेक्शन नहीं है" with troubleshooting tips and helpline 7836811111. **Retryable.** |
| 2 | `SERVER_ERROR` | Any | API server returns 5xx | Full-screen error: "सर्वर में समस्या है" with retry button and helpline. **Retryable.** |

### Registration & OTP Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 3 | `PHONE_DUPLICATE` | 0 (Phone Entry) | Phone number already registered | Error card: "This number is already registered" + options to use new number or login. **Blocked.** |
| 4 | `OTP_WRONG` | 1 (OTP + T&C) | Incorrect OTP digits entered | OTP boxes turn red. "Wrong OTP — 2 attempts remaining." **Retryable** (3 attempts max). |
| 5 | `OTP_EXPIRED` | 1 (OTP + T&C) | Timer runs out before OTP entry | OTP boxes fade/disable. "OTP has expired — don't worry, send a new OTP." **Retryable.** |

### Registration Fee Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 6 | `REGFEE_FAILED` | 4 (Registration Fee) | ₹2,000 payment declined by bank | "भुगतान नहीं हो पाया." Green reassurance: "पैसा कटा नहीं है." Error: BANK_GATEWAY_TIMEOUT. **Retryable.** |
| 7 | `REGFEE_TIMEOUT` | 4 (Registration Fee) | Payment gateway timed out | "भुगतान pending है." Orange card with UPI ref number. Refresh Status or Talk to Us. **Retryable.** |

### KYC Errors (Post-Registration Fee)

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 8 | `KYC_DAY1-4_NUDGES` | 5 (KYC) | 1-4 days after reg fee, docs not submitted | Day 1-3: Push notifications ("दस्तावेज़ जमा करें"). Day 4: Auto-rejection "आवेदन रद्द हो गया" with auto-refund of ₹2,000. **Informational (Day 1-3), Terminal (Day 4).** |

### Bank Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 9 | `BANK_DEDUP` | 6 (Bank Details) | Bank account already linked to another Wiom account, triggers after "Add Bank Document" tap | Bottom sheet: "बैंक खाता पहले से जुड़ा है" — shows linked account ending ****4567. Only option: "बैंक विवरण बदलें" (Change Bank Details). **Blocked.** |

### Refund Status Screens

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 10 | `REFUND_SUCCESS` | Post-rejection | Refund completed | "रिफंड सफल" — ₹2,000 credited confirmation |
| 11 | `REFUND_IN_PROGRESS` | Post-rejection | Refund being processed | "रिफंड प्रोसेस हो रहा है" — ₹2,000 with 5-6 working days estimate |
| 12 | `REFUND_FAILED` | Post-rejection | Refund processing failed | "रिफंड विफल" — contact support |

### Branch Point Rejections

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 13 | `VERIFICATION_PENDING` | 9 (Verification) | Documents submitted, under review | "सत्यापन लंबित है" with completed checklist. "समीक्षा में 3 कार्य दिवस." |
| 14 | `VERIFICATION_REJECTED` | 9 (Verification) | Verification team rejects application | "प्रोफ़ाइल अभी स्वीकृत नहीं हुई." "चिंता न करें — आपका पैसा सुरक्षित है." ₹2,000 refund in **5-6 working days**. **Terminal.** |
| 15 | `TECH_ASSESSMENT_REJECTED` | 10 (Technical Assessment) | Technical team rejects setup | "प्रोफ़ाइल अभी स्वीकृत नहीं हुई." Reason: "इंफ्रास्ट्रक्चर तैयार नहीं." **No refund at this stage.** CTA: "हमसे बात करें" (Talk to Us). **Terminal.** |

### Onboarding Fee Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 16 | `ONBOARDFEE_SUCCESS` | 12 (Onboarding Fee) | ₹20,000 payment successful | "भुगतान सफल!" celebration with auto-progress to next screen |
| 17 | `ONBOARDFEE_FAILED` | 12 (Onboarding Fee) | ₹20,000 payment declined | "भुगतान नहीं हो पाया." Reassurance + Retry + Talk to Us. **Retryable.** |
| 18 | `ONBOARDFEE_TIMEOUT` | 12 (Onboarding Fee) | ₹20,000 payment pending | "भुगतान लंबित है." UPI ref + Refresh + Talk to Us. **Retryable.** |

### Account Setup Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 19 | `ACCOUNT_SETUP_FAILED` | 13 (Account Setup) | Technical failure during setup | "खाता सेटअप विफल." Retry + Talk to Us. **Retryable.** |
| 20 | `ACCOUNT_SETUP_PENDING` | 13 (Account Setup) | Setup taking longer than expected | "खाता सेटअप लंबित है." Refresh Status + Talk to Us. |

### Error Classification Summary

| Type | Count | Behavior |
|------|-------|----------|
| **Blocking** | 3 | Cannot proceed — needs external resolution or "Talk to Us" (PHONE_DUPLICATE, BANK_DEDUP, TECH_ASSESSMENT_REJECTED) |
| **Retryable** | 8 | Can retry immediately or after fixing input (OTP_WRONG, OTP_EXPIRED, REGFEE_FAILED, REGFEE_TIMEOUT, ONBOARDFEE_FAILED, ONBOARDFEE_TIMEOUT, ACCOUNT_SETUP_FAILED, ACCOUNT_SETUP_PENDING) |
| **Terminal** | 1 | Application ends with refund (VERIFICATION_REJECTED — auto refund ₹2,000, no re-upload) |
| **Informational** | 8 | Status updates, reminders, nudges, and confirmation screens (KYC_DAY1-4_NUDGES, REFUND_SUCCESS/IN_PROGRESS/FAILED, VERIFICATION_PENDING, ONBOARDFEE_SUCCESS, ACCOUNT_SETUP_PENDING) |

---

## 5. Screen Flow with All States

### Pitch Screen (Welcome)
- **Happy path:** Wiom logo, tagline ("भारत का सबसे भरोसेमंद इंटरनेट पार्टनर नेटवर्क"), 4 benefit cards, "शुरू करें" / "Get Started" CTA
- **Empty state:** N/A (always shows content)
- **Error states:** None
- **Loading state:** None
- **Validation:** None
- **Notes:** Controlled by `pitchDismissed` flag in OnboardingState. Shown only once. No business values (₹ amounts).

### Screen 0: Phone Entry

| State | Description |
|-------|-------------|
| **Empty** | Phone field empty. T&C checkbox present and pre-checked. CTA disabled. Character counter shows "0/10 digits." |
| **Happy Path** | User enters 10 digits. T&C remains checked. CTA enables. Tap sends OTP, navigates to Screen 1. |
| **Error: PHONE_DUPLICATE** | Error card appears: "This number is already registered." Two CTAs: new number or login. |
| **Validation** | Not blank, exactly 10 digits, digits only. Non-digit characters are stripped. |

### Screen 1: OTP Verification

| State | Description |
|-------|-------------|
| **Empty** | 4 OTP boxes empty. CTA disabled. Timer counting down from 28s. |
| **Happy Path** | All 4 OTP digits filled. CTA enables. Tap verifies, navigates to Screen 2. |
| **Error: OTP_WRONG** | OTP boxes turn red. "Wrong OTP — X attempts remaining." Retryable up to 3 attempts. |
| **Error: OTP_EXPIRED** | OTP boxes fade to 50% opacity. "OTP has expired." Options: Resend OTP or Change Number. |
| **Loading** | Timer countdown (28s). After resend, timer restarts. |
| **Validation** | All 4 OTP digits must be filled for CTA to enable. |

### Screen 2: Personal & Business Info

| State | Description |
|-------|-------------|
| **Empty** | All 4 fields empty (Name, Email, Entity Type, Trade Name). CTA disabled. |
| **Happy Path** | All fields filled. CTA enables. Navigates to Screen 3. |
| **Error** | None — this screen has no error scenarios. |
| **Validation** | Name: not blank. Email: not blank + contains @ and `.` Entity Type: selected. Trade Name: not blank. |

### Screen 3: Business Location

| State | Description |
|-------|-------------|
| **Empty** | State pre-filled (Madhya Pradesh). City, Pincode, Address empty. CTA disabled until all fields filled (state, city, pincode, address required). |
| **Happy Path** | Fields filled. GPS badge shows coordinates. CTA navigates to Screen 4. |
| **Error** | None — this screen has no error scenarios in current version. |
| **Validation** | Pincode: if entered, must be exactly 6 digits. |

### Screen 4: Registration Fee (Rs 2,000)

| State | Description |
|-------|-------------|
| **Happy Path** | Shows amount, refund trust badge. Tap CTA, 2s processing delay, then navigates to Screen 5. |
| **Error: REGFEE_FAILED** | "Payment could not be processed." Reassurance: "No money deducted." Retry CTA. |
| **Error: REGFEE_TIMEOUT** | "Payment is pending." Orange card with UPI ref. Refresh status or contact support. |
| **Loading** | 2-second simulated payment processing with spinner. |

### Screen 5: KYC Documents

| State | Description |
|-------|-------------|
| **Empty** | 3 sub-stages: PAN → Aadhaar → GST. Progress bar at top. Each sub-stage has number input + document upload. CTA disabled until both filled. |
| **Happy Path** | Enter PAN number + upload PAN card → Enter Aadhaar number + upload front & back → Enter GST number (cross-validated with PAN) + upload certificate. Each sub-stage advances to next. After GST, navigates to Screen 6. |
| **Validation** | PAN: `[A-Z]{5}[0-9]{4}[A-Z]`. Aadhaar: 12 digits. GST: 15 chars, positions 2-12 must match PAN. All 3 documents must be uploaded. "सैंपल दस्तावेज़ देखें" link on each sub-stage. |

### Screen 6: Bank Details

| State | Description |
|-------|-------------|
| **Empty** | 3 fields: Account Number, Re-enter Account Number, IFSC Code. Info box: "बैंक विवरण [name] या [trade] के नाम पर होने चाहिए." CTA disabled. |
| **Happy Path** | All fields filled, tap "Add Bank Document" → dedup check runs → if clear, proceeds to bank document upload (mandatory) → after upload, proceeds to Screen 7. |
| **Error: BANK_DEDUP** | Bottom sheet (triggers after "Add Bank Document" tap): "बैंक खाता पहले से जुड़ा है" — linked to ****4567. Only option: "बैंक विवरण बदलें" (Change Bank Details). Blocked. |
| **Validation** | Account Number: min 9, max 18 digits. Confirm must match. IFSC: `[A-Z]{4}0[A-Z0-9]{6}`. Account masking on blur. CTA is "Add Bank Document" (not "Verify Bank Details"). |

### Screen 7: ISP Agreement Upload

| State | Description |
|-------|-------------|
| **Empty** | Upload placeholder shown. CTA disabled. |
| **Happy Path** | Document uploaded (green border, "DOT Ready" badge). CTA enables. Navigates to Screen 8. |
| **Error** | None — this screen has no error scenarios in current version. |
| **Loading** | Upload progress bar (same pattern as KYC). |
| **Validation** | ISP Agreement document must be uploaded. |

### Screen 8: Shop & Equipment Photos

| State | Description |
|-------|-------------|
| **Empty** | Both photo slots show placeholder. CTA disabled. |
| **Happy Path** | Both photos uploaded (green borders, checkmarks). CTA: "Submit for Verification." Navigates to Screen 9. |
| **Error** | None — no error scenarios on this screen. |
| **Loading** | Per-photo upload progress bar. |
| **Validation** | Both photos (Shop Front + Router/Equipment) must be uploaded. |

### Screen 9: Verification (Branch Point 1)

| State | Description |
|-------|-------------|
| **Pending (default)** | "सत्यापन लंबित है." Checklist of completed submissions (KYC, Bank, ISP, Shop & Equipment). "समीक्षा में 3 कार्य दिवस." No CTA. |
| **Approved** | Transitions to Screen 10 (Technical Assessment) (triggered by verification team via dashboard). |
| **Rejected (VERIFICATION_REJECTED)** | "प्रोफ़ाइल अभी स्वीकृत नहीं हुई." "चिंता न करें — आपका पैसा सुरक्षित है." ₹2,000 refund in **5-6 working days**. CTA: "रिफंड स्टेटस देखें." Terminal. |
| **Dev bypass** | "Dev: Skip to Stage 3" button skips to Screen 10. |

### Screen 10: Technical Assessment (Branch Point 2)

| State | Description |
|-------|-------------|
| **Pending (default)** | "तकनीकी मूल्यांकन जारी है." Checklist: Infrastructure Review, Network Readiness, Location Feasibility. "4-5 कार्य दिवस." No CTA. |
| **Approved** | Transitions to Screen 11 (Policy & SLA) (triggered by technical team via dashboard). |
| **Rejected (TECH_ASSESSMENT_REJECTED)** | "प्रोफ़ाइल अभी स्वीकृत नहीं हुई." Reason: "इंफ्रास्ट्रक्चर तैयार नहीं." **No refund at this stage.** CTA: "हमसे बात करें." Terminal. |
| **Dev bypass** | "Dev: Skip to Policy & SLA" button skips to Screen 11. |

### Screen 11: Policy, Payout & SLA

| State | Description |
|-------|-------------|
| **Happy Path** | Shows commission (₹300/₹300), payout terms (weekly via RazorpayX), SLA (4hr, 95%+). CTA: "Understood, proceed." Proceeds to Screen 12. |
| **Error** | None. |

### Screen 12: Onboarding Fee (Rs 20,000)

| State | Description |
|-------|-------------|
| **Happy Path** | Shows ₹20,000 amount + ₹22,000 total investment breakdown (Reg Fee ₹2,000 Paid + Onboarding ₹20,000 Due). 2s processing, navigates to Screen 13. |
| **Error: ONBOARDFEE_FAILED** | "भुगतान नहीं हो पाया." Reassurance: "पैसा कटा नहीं है." Retry + Talk to Us. **Retryable.** |
| **Error: ONBOARDFEE_TIMEOUT** | "भुगतान लंबित है." UPI ref + Refresh Status + Talk to Us. **Retryable.** |
| **Loading** | 2-second simulated payment processing. |

### Screen 13: CSP Account Setup (Automated)

| State | Description |
|-------|-------------|
| **Happy Path** | "खाता सेटअप जारी है" with spinner. Shows business name. Auto-progresses to Screen 14 after ~3s. |
| **Loading** | Gear emoji + spinner animation. |
| **Error: ACCOUNT_SETUP_FAILED** | "खाता सेटअप विफल." Retry + Talk to Us. **Retryable.** |
| **Error: ACCOUNT_SETUP_PENDING** | "खाता सेटअप लंबित है." Refresh Status + Talk to Us. |

### Screen 14: Successfully Onboarded (Terminal Success)

| State | Description |
|-------|-------------|
| **Happy Path** | "बधाई हो, राजेश!" celebration. "आप अब Wiom कनेक्शन सर्विस प्रोवाइडर हैं." Download Wiom Partner Plus app card. 3 important instructions (login, permissions, training). Next Steps section. |
| **Error** | None — this is the terminal success state. |

---

## 6. Architecture Recommendation for Production

```
com.wiom.csp/
├── di/                           # Hilt dependency injection modules
│   ├── NetworkModule.kt          # Retrofit, OkHttp, interceptors
│   ├── DatabaseModule.kt         # Room database, DAOs
│   └── RepositoryModule.kt       # Repository bindings
│
├── data/
│   ├── remote/
│   │   ├── WiomApiService.kt     # Retrofit interface (OTP, KYC, payments, etc.)
│   │   ├── dto/                  # Data transfer objects (API request/response models)
│   │   └── interceptor/          # Auth token interceptor, logging
│   ├── local/
│   │   ├── WiomDatabase.kt       # Room database
│   │   ├── dao/                  # DAOs for each entity
│   │   ├── entity/               # Room entities
│   │   └── DataStoreManager.kt   # Preferences DataStore (language, onboarding progress)
│   └── repository/
│       ├── AuthRepository.kt     # OTP send/verify, phone check
│       ├── KycRepository.kt      # Document upload, OCR, verification
│       ├── PaymentRepository.kt  # Razorpay integration, fee payments
│       ├── BankRepository.kt     # Bank dedup check, document upload
│       └── OnboardingRepository.kt  # Progress tracking, form data persistence
│
├── domain/
│   ├── model/
│   │   ├── Partner.kt            # Core domain model
│   │   ├── KycDocument.kt        # Document with status
│   │   ├── BankDetails.kt        # Bank verification state
│   │   └── OnboardingProgress.kt # Which screen, which stage, what's done
│   └── usecase/
│       ├── SendOtpUseCase.kt
│       ├── VerifyOtpUseCase.kt
│       ├── UploadDocumentUseCase.kt
│       ├── VerifyBankUseCase.kt
│       ├── ProcessPaymentUseCase.kt
│       └── CheckDedupUseCase.kt
│
├── ui/
│   ├── theme/
│   │   ├── Color.kt              # KEEP FROM PROTOTYPE — all Wiom brand colors
│   │   ├── Type.kt               # KEEP FROM PROTOTYPE — all typography styles
│   │   └── Shape.kt              # KEEP FROM PROTOTYPE — all corner radii
│   ├── components/
│   │   └── *.kt                  # Extract from prototype Common.kt — 20+ composables
│   ├── navigation/
│   │   ├── WiomNavGraph.kt       # Navigation graph with all routes
│   │   ├── Screen.kt             # Sealed class/enum for routes
│   │   └── NavigationHelper.kt   # Deep link handling
│   └── screens/
│       ├── phone/
│       │   ├── PhoneScreen.kt
│       │   └── PhoneViewModel.kt
│       ├── otp/
│       │   ├── OtpScreen.kt
│       │   └── OtpViewModel.kt
│       ├── personalinfo/
│       │   ├── PersonalInfoScreen.kt
│       │   └── PersonalInfoViewModel.kt
│       ├── location/
│       │   ├── LocationScreen.kt
│       │   └── LocationViewModel.kt
│       ├── regfee/
│       │   ├── RegFeeScreen.kt
│       │   └── RegFeeViewModel.kt
│       ├── kyc/
│       │   ├── KycScreen.kt
│       │   └── KycViewModel.kt
│       ├── bank/
│       │   ├── BankScreen.kt
│       │   └── BankViewModel.kt
│       ├── ispagreement/
│       │   ├── IspAgreementScreen.kt
│       │   └── IspAgreementViewModel.kt
│       ├── photos/
│       │   ├── PhotosScreen.kt
│       │   └── PhotosViewModel.kt
│       ├── verification/
│       │   ├── VerificationScreen.kt
│       │   └── VerificationViewModel.kt
│       ├── policy/
│       │   ├── PolicyScreen.kt
│       │   └── PolicyViewModel.kt
│       ├── onboardfee/
│       │   ├── OnboardFeeScreen.kt
│       │   └── OnboardFeeViewModel.kt
│       ├── techassessment/
│       │   ├── TechAssessmentScreen.kt
│       │   └── TechAssessmentViewModel.kt
│       ├── accountsetup/
│       │   ├── AccountSetupScreen.kt
│       │   └── AccountSetupViewModel.kt
│       └── success/
│           └── SuccessfullyOnboardedScreen.kt
│
└── util/
    ├── Strings.kt                # KEEP FROM PROTOTYPE — bilingual t() function
    ├── Validation.kt             # KEEP FROM PROTOTYPE — all validation rules
    ├── BilingualText.kt          # t(hindi, english) helper
    └── Constants.kt              # Business constants (fees, commissions, thresholds)
```

### Key Architecture Decisions

1. **One ViewModel per screen** — each screen's state is isolated and testable
2. **Repository pattern** — ViewModels never touch API or DB directly
3. **UseCases** — business logic lives here, not in ViewModels or Repositories
4. **Navigation library** — enables deep links (e.g., push notification -> Screen 9 result)
5. **Room + DataStore** — onboarding progress survives app kill; preferences (language) persist instantly
6. **Hilt** — constructor injection everywhere, no manual wiring

---

## 7. Design System Reference

### Colors — `ui/theme/Color.kt`

| Token Name | Hex | Usage |
|-----------|-----|-------|
| `WiomPrimary` | `#D9008D` | CTAs, brand accent, active elements |
| `WiomPrimaryLight` | `#FFE5F6` | Backgrounds, secondary buttons |
| `WiomText` | `#161021` | Body text |
| `WiomTextSecondary` | `#665E75` | Labels, descriptions |
| `WiomHint` | `#A7A1B2` | Placeholders |
| `WiomSurface` | `#FAF9FC` | Screen backgrounds |
| `WiomPositive` | `#008043` | Success, verified states |
| `WiomNegative` | `#D92130` | Errors, rejected states |
| `WiomWarning` | `#FF8000` | Pending, caution states |
| `WiomInfo` | `#6D17CE` | Informational boxes |
| `WiomHeader` | `#443152` | Status bar, app header |

**Rule: DO NOT hardcode hex values anywhere in screen code. Always reference the token.**

```kotlin
// WRONG
Text(color = Color(0xFFD9008D))

// RIGHT
Text(color = WiomPrimary)
```

### Typography — `ui/theme/Type.kt`

Font family: Noto Sans / Noto Sans Devanagari (for Hindi)

| Style | Size | Weight | Usage |
|-------|------|--------|-------|
| `headlineLarge` | 24sp | Bold | Screen titles |
| `headlineMedium` | 20sp | Bold | Section headers |
| `titleLarge` | 16sp | Bold | Card titles |
| `titleMedium` | 14sp | SemiBold | Subtitles |
| `bodyLarge` | 14sp | Normal | Body text |
| `bodyMedium` | 12sp | Normal | Secondary text |
| `bodySmall` | 10sp | Normal | Captions, hints |

**Rule: DO NOT hardcode font sizes. Always use `MaterialTheme.typography`.**

```kotlin
// WRONG
Text(fontSize = 24.sp, fontWeight = FontWeight.Bold)

// RIGHT
Text(style = MaterialTheme.typography.headlineLarge)
```

### Shapes — `ui/theme/Shape.kt`

| Size | Value | Usage |
|------|-------|-------|
| Small | 8dp | Tags, small cards |
| Medium | 12dp | Text fields, info boxes |
| Large | 16dp | Cards, buttons |
| Pill | 888dp | Chips, badges |

**Rule: DO NOT hardcode corner radii. Use `MaterialTheme.shapes`.**

### Component Library — `ui/components/Common.kt`

The prototype's `Common.kt` contains 20+ reusable composables. **Use them in production** (extract into individual files under `ui/components/`):

| Component | Purpose |
|-----------|---------|
| `WiomButton` | Primary CTA button (pink, full width, 48dp height) |
| `WiomOutlinedButton` | Secondary button |
| `WiomCard` | Standard card container with shadow |
| `WiomTextField` | Styled text input with validation state |
| `AppHeader` | Top bar with "Wiom Partner+" title + language toggle |
| `ProgressBar` | Stage progress indicator (3 stages) |
| `InfoBox` | Blue/purple informational callout |
| `ErrorCard` | Red error card with icon |
| `WarningCard` | Orange warning card |
| `SuccessCard` | Green success card with checkmark |
| `UploadRow` | Document upload row with states (empty/uploading/uploaded) |
| `TrustBadge` | Lock icon + text for security reassurance |
| `StatusChip` | Green chip with checkmark for Go Live screen |
| `QuickActionCard` | Tappable card for Go Live actions |
| `OtpBox` | Single OTP digit input with visual states |
| `BottomSheet` | Camera/gallery source picker |

**Rule: USE THESE COMPONENTS. Do not create custom versions that look similar.**

---

## 8. Bilingual System

### How it works

All user-facing text uses the `t()` function defined in `util/Strings.kt`:

```kotlin
// Usage
Text(t("OTP भेजें", "Send OTP"))
```

- **First parameter:** Hindi (default language)
- **Second parameter:** English
- The function reads a global `isEnglish` flag and returns the appropriate string
- Language toggle is in `AppHeader` on every screen — toggling is instant (no activity recreation)

### Rules

1. **Every user-facing string MUST use `t()`** — no hardcoded single-language text
2. **Hindi is the default** — when the app launches, it shows Hindi
3. **Do NOT use Android string resources** (`strings.xml`) — the `t()` function enables instant runtime toggle without activity recreation, which `strings.xml` cannot do
4. **Hindi text has been reviewed and approved** — do not change Hindi copy without product sign-off
5. All error messages, button labels, headers, info text, placeholders, and validation messages must be bilingual

### Production Enhancement

For production, consider moving the bilingual strings to a structured format:

```kotlin
// Option: Structured string keys (easier to maintain at scale)
object S {
    val sendOtp = BilingualText("OTP भेजें", "Send OTP")
    val verify = BilingualText("सत्यापित करें", "Verify")
    // ...
}

// Usage
Text(S.sendOtp.resolve())
```

But the core principle stays: **runtime toggle, no activity recreation, Hindi-first**.

---

## 9. Key Business Rules

**These values are contractual/business decisions. DO NOT CHANGE without explicit product approval.**

### Commission Structure
- New Connection: **Rs 300 per connection**
- Recharge Commission: **Rs 300 per recharge**
- Payout: Every Monday, via RazorpayX, directly to partner's bank account

### SLA Terms
- Customer complaint resolution: **4 hours**
- Connection uptime: **95%+**
- Equipment care: Partner's responsibility
- Wiom brand guidelines: Mandatory compliance

### Fees
- Registration Fee: **Rs 2,000** (refundable if Verification rejects at Screen 9, refund in 5-6 working days)
- Onboarding Fee: **Rs 20,000** (**no refund** if Tech Assessment rejects at Screen 10)
- Total Investment: **Rs 22,000**
- Help Number: **7836811111** (shown on error screens)

### ISP Agreement
- **Mandatory** — required for DOT (Department of Telecom) compliance
- Cannot be skipped or deferred
- Non-negotiable regulatory requirement

### Trade Name
- Gets permanently locked after registration fee is paid (Screen 4)
- Cannot be changed after that point

---

## 10. What Needs to Be Built for Production (Not in Prototype)

Reference: `PRD_HUMAN.md` Section 15 for the full comparison table.

| Feature | Prototype (Current) | Production (What to Build) |
|---------|---------------------|---------------------------|
| **OTP** | Any 4 digits work | Real SMS/WhatsApp OTP via API (MSG91, Twilio, or similar) |
| **T&C Content** | Static placeholder text | Legal-reviewed T&C document (versioned, auditable consent) |
| **KYC Upload** | Simulated progress bar | Real camera/gallery integration + OCR (Digilocker/NSDL API) + server upload |
| **KYC Verification** | Simulated pass/fail | Real PAN verification (NSDL), Aadhaar validation (UIDAI), PAN-Aadhaar linkage check |
| **ISP Agreement** | Simulated upload | Real document upload + DOT validation API |
| **Payments (Rs 2K)** | 2-second delay simulation | Real Razorpay payment gateway integration |
| **Payments (Rs 20K)** | 2-second delay simulation | Real Razorpay with multiple payment methods (UPI, NEFT, Card) |
| **Penny Drop** | Simulated success | Deferred to next version | Real Rs 1 bank credit via banking API (RazorpayX or similar) |
| **Dedup Check** | Simulated pass | Real database cross-reference against Bank A/C (KYC dedup deferred to next version) |
| **Verification (Screen 9)** | Dashboard approve/reject button | Backend queue + verification panel + push notification to partner |
| **Tech Assessment (Screen 10)** | Dashboard approve/reject button | Backend queue + technical review panel + push notification |
| **CSP Account Setup** | Animated checklist (cosmetic) | Real API calls: RazorpayX payout link, Zoho invoice setup, ledger creation, TDS/TCS config |
| **Account Setup (Screen 13)** | Animated spinner (cosmetic) | Real API calls: RazorpayX payout link, Zoho invoice setup, ledger creation, TDS/TCS config |
| **GPS** | Hardcoded coordinates (22.71 N, 75.85 E) | FusedLocationProvider API with runtime permission handling |
| **State Management** | `OnboardingState` singleton | MVVM + ViewModels + StateFlow + Room database + DataStore for persistence across app kills |
| **Navigation** | `AnimatedContent` + integer index | Jetpack Navigation library + typed routes + deep links |
| **Architecture** | Prototype shortcuts | MVVM + Hilt + Repository — replace MockOnboardingRepository with real API |
| **Backend** | None (all local simulation) | REST API server + authentication + session management |
| **Push Notifications** | None | FCM for branch point results (Verification, Tech Assessment) |
| **Analytics** | None | Event tracking + funnel analysis (Mixpanel, Amplitude, or similar) |
| **Error Reporting** | Console logs | Crashlytics / Sentry for production error monitoring |
| **Release** | Debug APK | ProGuard/R8 obfuscation + signed release APK/AAB |
| **Security** | No encryption | Certificate pinning, encrypted DataStore, secure API tokens |

### Priority Order for Production Build

1. **Backend API** — everything depends on this (auth, OTP, KYC, payments)
2. **Auth + OTP** — the entry point; blocks all testing
3. **Navigation + State persistence** — architectural foundation
4. **Payment gateway** — two critical screens depend on it (4 and 12)
5. **KYC + Document upload** — complex flow with OCR integration
6. **Bank verification** — bank dedup check + mandatory document upload
7. **Push notifications** — needed for branch point results
8. **Analytics + Error reporting** — needed before launch
9. **Security hardening** — certificate pinning, obfuscation, encryption

---

## Quick Reference: File Map

| File | What's In It |
|------|-------------|
| `PRD_HUMAN.md` | Full product spec — all 15 screens, 22 errors, business rules, validation, design system, QA/UAT cases |
| `PRD_AI_AGENT.md` | Machine-readable spec — structured for AI-assisted development |
| `CLAUDE.md` | AI assistant context — architecture summary, UX rules, screen flow |
| `prototype/index.html` | Complete HTML prototype — all screens + all errors + scenario simulator |
| `dashboard/index.html` | Remote control dashboard UI |
| `dashboard/bridge.py` | WebSocket bridge between dashboard and app |
| `app/src/.../ui/theme/Color.kt` | All Wiom brand colors |
| `app/src/.../ui/theme/Shape.kt` | All corner radii |
| `app/src/.../ui/components/Common.kt` | 20+ reusable composables |
| `app/src/.../ui/screens/Phase1Screens.kt` | Screens 0-4 (Registration) |
| `app/src/.../ui/screens/Phase2Screens.kt` | Screens 5-9 (Verification) |
| `app/src/.../ui/screens/Phase3Screens.kt` | Screens 10-14 (Activation) |
| `app/src/.../ui/navigation/NavGraph.kt` | Navigation Compose graph (16 routes: Pitch + Screens 0-14) |
| `app/src/.../ui/viewmodel/*.kt` | 12 ViewModels — one per screen (Policy and Success are display-only) |
| `app/src/.../util/Strings.kt` | Bilingual `t()` function |
| `app/src/.../util/Validation.kt` | All field validation rules |

---

*This guide covers everything you need to pick up the Wiom CSP Onboarding project. Start by running the HTML prototype, read through the PRD, then build production from scratch using the architecture recommendation above. When in doubt, the prototype is your visual/behavioral source of truth, and `PRD_HUMAN.md` is your product source of truth.*
