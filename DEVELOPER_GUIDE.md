# Wiom CSP Onboarding — Developer Guide

**Version:** 3.0 | **Last Updated:** 24 March 2026
**Audience:** Production development team

---

## 1. What This Repo Is (and Isn't)

### This is a REFERENCE PROTOTYPE — not production code.

**Use it for:**
- Understanding the full 17-screen onboarding flow
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
| MVVM + Hilt + Repository (production-ready) | Room database + DataStore for persistence |
| `AnimatedContent` with integer screen index | Jetpack Navigation library with typed routes + deep links |
| MVVM with `ViewModel` per screen + `StateFlow` (done) | Already implemented |
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
- All **17 screens** with full visual fidelity
- All **18 error scenarios** — triggerable via the Scenario Simulator panel
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
- Happy path flow through all 17 screens
- Scenario Simulator panel (expandable)
- Dashboard bridge receiver for remote control
- Bilingual toggle

> **Tip:** The HTML prototype is more complete for error scenario testing. Use the APK when you need to verify Android-specific behavior (navigation, back button, keyboard handling).

---

## 3. Dev Skip / Bypass Options

The prototype has built-in shortcuts to skip waiting states. These exist because Screens 9 and 12 are branch points where the app normally waits for an external team decision.

### Screen 9 (Verification) — "Dev: Skip to Stage 3"
- This screen shows a "waiting for verification team" state
- The dev skip button bypasses the wait and moves directly to Screen 10 (Policy & SLA)
- In production, this transition happens when the verification team approves via the admin panel

### Screen 12 (Technical Assessment) — "Dev: Skip to Account Setup"
- This screen shows a "waiting for technical team" state
- The dev skip button bypasses the wait and moves directly to Screen 13 (CSP Account Setup)
- In production, this transition happens when the technical team approves via the admin panel

### Scenario Simulator Panel
- **Expanded by default** in the prototype
- Use it to trigger any of the 18 error scenarios on any screen
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
- Approve/reject at branch points (Screens 9 and 12)
- Pre-fill all form fields ("Filled Mode")
- Control language toggle

---

## 4. All 18 Error Scenarios

Reference: `PRD_HUMAN.md` Section 6 for full details including exact Hindi/English copy.

### Registration & OTP Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 1 | `PHONE_DUPLICATE` | 0 (Phone Entry) | Phone number already registered | Error card: "This number is already registered" + options to use new number or login. **Blocked.** |
| 2 | `OTP_WRONG` | 1 (OTP + T&C) | Incorrect OTP digits entered | OTP boxes turn red. "Wrong OTP — 2 attempts remaining." **Retryable** (3 attempts max). |
| 3 | `OTP_EXPIRED` | 1 (OTP + T&C) | Timer runs out before OTP entry | OTP boxes fade/disable. "OTP has expired — don't worry, send a new OTP." **Retryable.** |

### Location Error

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 4 | `AREA_NOT_SERVICEABLE` | 3 (Business Location) | Pincode not in Wiom service area | Orange warning: "This area is not serviceable yet. Join the waitlist!" Shows 47 people on waitlist. **Blocked.** |

### KYC Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 5 | `KYC_PAN_MISMATCH` | 5 (KYC Documents) | PAN name doesn't match Aadhaar name | PAN row turns red with "Name Mismatch" badge. Shows both names side-by-side. **Blocked.** |
| 6 | `KYC_AADHAAR_EXPIRED` | 5 (KYC Documents) | Aadhaar address is outdated | Aadhaar row turns orange with "Address Update Required." Directs to uidai.gov.in. **Blocked.** |
| 7 | `KYC_PAN_AADHAAR_UNLINKED` | 5 (KYC Documents) | PAN and Aadhaar not linked in NSDL | All docs show verified, but linking error card appears. Directs to incometax.gov.in. **Blocked.** |

### Payment Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 8 | `REGFEE_FAILED` | 4 (Registration Fee) | ₹2,000 payment declined by bank | "Payment could not be processed." Green reassurance: "No money deducted." Error: BANK_GATEWAY_TIMEOUT. **Retryable.** |
| 9 | `REGFEE_TIMEOUT` | 4 (Registration Fee) | Payment gateway timed out | "Payment is pending." Orange card with UPI ref number. "Auto-refund within 48hrs if failed." **Retryable.** |
| 10 | `ONBOARDFEE_FAILED` | 11 (Onboarding Fee) | ₹20,000 payment declined | Same structure as REGFEE_FAILED but error: UPI_LIMIT_EXCEEDED. Suggests NEFT/RTGS or card. **Retryable.** |

### Bank & Dedup Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 11 | `BANK_PENNYDROP_FAIL` | 6 (Bank Details) | ₹1 penny drop credit failed | Account number field turns red. "Penny drop failed — account number may be wrong or bank server is down." **Retryable.** |
| 12 | `BANK_NAME_MISMATCH` | 6 (Bank Details) | Bank account holder name differs from KYC name | Orange mismatch card showing both names side-by-side. **Retryable.** |
| 13 | `DEDUP_FOUND` | 6 (Bank Details) | Existing partner with same PAN/Bank account | Penny drop passes (green), but dedup alert (red). Shows matching partner details (ID, name, city, match type). **Blocked — must contact support.** |

### ISP Agreement Error

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 14 | `ISP_DOC_INVALID` | 7 (ISP Agreement) | Uploaded document is invalid/illegible | Document card turns red with "Document Invalid" badge. "Please upload a valid ISP Agreement." **Retryable.** |

### Branch Point Rejections

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 15 | `VERIFICATION_REJECTED` | 9 (Verification) | Verification team rejects application | "Profile not accepted yet." "Don't worry — your money is safe." Reason card + ₹2,000 refund in 5-7 days. **Terminal.** |
| 16 | `TECH_ASSESSMENT_REJECTED` | 12 (Tech Assessment) | Technical team rejects setup | "Technical Assessment not passed." "Don't worry — your money is safe." Reason card + ₹20,000 refund in 5-7 days. **Terminal.** |

### Training & Quiz Errors

| # | Scenario | Screen | Trigger | What User Sees |
|---|----------|--------|---------|---------------|
| 17 | `TRAINING_QUIZ_FAIL` | 14 (Training Modules) | Partner fails a module quiz | Score card with encouraging message. "Don't worry — review the module and try again." **Retryable (unlimited).** |
| 18 | `POLICY_QUIZ_FAIL` | 15 (Policy Quiz) | Score below 80% (< 4/5) | Score card (e.g., 2/5 with red progress bar). Two CTAs: "Review Modules" / "Retake Quiz." **Retryable (unlimited).** |

### Error Classification Summary

| Type | Count | Behavior |
|------|-------|----------|
| **Blocking** | 6 | Cannot proceed — needs external resolution (PHONE_DUPLICATE, AREA_NOT_SERVICEABLE, KYC_PAN_MISMATCH, KYC_AADHAAR_EXPIRED, KYC_PAN_AADHAAR_UNLINKED, DEDUP_FOUND) |
| **Terminal (Branch Rejection)** | 2 | Cannot proceed — refund initiated (VERIFICATION_REJECTED, TECH_ASSESSMENT_REJECTED) |
| **Retryable** | 10 | Can retry immediately or after fixing input |

---

## 5. Screen Flow with All States

### Pitch Screen (Welcome)
- **Happy path:** Wiom logo, tagline, 4 benefit cards, DOT/TRAI badges, "Get Started" CTA
- **Empty state:** N/A (always shows content)
- **Error states:** None
- **Loading state:** None
- **Validation:** None
- **Notes:** Controlled by `pitchDismissed` flag in OnboardingState. Shown only once. No business values (₹ amounts).

### Screen 0: Phone Entry

| State | Description |
|-------|-------------|
| **Empty** | Phone field empty. CTA disabled. Character counter shows "0/10 digits." |
| **Happy Path** | User enters 10 digits. CTA enables. Tap sends OTP, navigates to Screen 1. |
| **Error: PHONE_DUPLICATE** | Error card appears: "This number is already registered." Two CTAs: new number or login. |
| **Validation** | Not blank, exactly 10 digits, digits only. Non-digit characters are stripped. |

### Screen 1: OTP + T&C Acceptance

| State | Description |
|-------|-------------|
| **Empty** | T&C unchecked. 4 OTP boxes empty. CTA disabled. Timer counting down from 30s. |
| **Happy Path** | T&C checked + all 4 OTP digits filled. CTA enables. Tap verifies, navigates to Screen 2. |
| **Error: OTP_WRONG** | OTP boxes turn red. "Wrong OTP — X attempts remaining." Retryable up to 3 attempts. |
| **Error: OTP_EXPIRED** | OTP boxes fade to 50% opacity. "OTP has expired." Options: Resend OTP or Change Number. |
| **Loading** | Timer countdown (30s). After resend, timer restarts. |
| **Validation** | T&C must be checked. All 4 OTP digits must be filled. Both required for CTA. |

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
| **Empty** | State pre-filled (Madhya Pradesh). City, Pincode, Address empty. CTA still enabled (fields optional). |
| **Happy Path** | Fields filled. GPS badge shows coordinates. CTA navigates to Screen 4. |
| **Error: AREA_NOT_SERVICEABLE** | Orange warning card: "This area is not serviceable yet." Waitlist CTA. Blocked. |
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
| **Empty** | All 4 document rows show "Upload" placeholder. CTA disabled. |
| **Happy Path** | All 4 uploaded (green borders, checkmarks). CTA enables. Navigates to Screen 6. |
| **Error: KYC_PAN_MISMATCH** | PAN row turns red with "Name Mismatch" badge. Names shown side-by-side. Blocked. |
| **Error: KYC_AADHAAR_EXPIRED** | Aadhaar row turns orange with "Address Update Required." Directs to UIDAI. Blocked. |
| **Error: KYC_PAN_AADHAAR_UNLINKED** | All docs verified, but linking error card appears. Directs to income tax portal. Blocked. |
| **Loading** | Per-document upload: progress bar (50 steps x 80ms = ~4s per document). |
| **Validation** | All 4 documents (PAN, Aadhaar Front, Aadhaar Back, GST) must be uploaded. |

### Screen 6: Bank Details

| State | Description |
|-------|-------------|
| **Empty** | All 4 fields empty. Verify button disabled. Info: "Fill all bank details." |
| **Happy Path** | All fields filled, verify tapped, 2s delay, two green success cards (Penny Drop + Dedup). CTA enables. |
| **Error: BANK_PENNYDROP_FAIL** | Account number field turns red. "Penny drop failed." Retryable. |
| **Error: BANK_NAME_MISMATCH** | Orange mismatch card with both names shown. Retryable. |
| **Error: DEDUP_FOUND** | Penny drop passes (green) but dedup alert (red). Shows matching partner details. Blocked. |
| **Loading** | 2-second penny drop + dedup verification. |
| **Validation** | All 4 fields required (Account Holder Name, Bank Name, Account Number, IFSC Code). |

### Screen 7: ISP Agreement Upload

| State | Description |
|-------|-------------|
| **Empty** | Upload placeholder shown. CTA disabled. |
| **Happy Path** | Document uploaded (green border, "DOT Ready" badge). CTA enables. Navigates to Screen 8. |
| **Error: ISP_DOC_INVALID** | Document card turns red with "Document Invalid" badge. Retryable. |
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
| **Pending (default)** | "Verification in progress." Checklist of completed submissions. "Review may take 2-3 business days." No CTA. |
| **Approved** | Transitions to Screen 10 (triggered by verification team via dashboard). |
| **Rejected (VERIFICATION_REJECTED)** | "Profile not accepted yet." Reason card. ₹2,000 refund in 5-7 days. Terminal. |
| **Dev bypass** | "Dev: Skip to Stage 3" button skips to Screen 10. |

### Screen 10: Policy, Payout & SLA

| State | Description |
|-------|-------------|
| **Happy Path** | Shows commission (₹300/₹300), payout terms (weekly via RazorpayX), SLA (4hr, 95%+). CTA: "Understood, proceed." |
| **Error** | None. |

### Screen 11: Onboarding Fee (Rs 20,000)

| State | Description |
|-------|-------------|
| **Happy Path** | Shows ₹20,000 amount + ₹22,000 total investment breakdown. 2s processing, navigates to Screen 12. |
| **Error: ONBOARDFEE_FAILED** | Same pattern as REGFEE_FAILED with UPI_LIMIT_EXCEEDED error. Suggests alternative payment methods. |
| **Loading** | 2-second simulated payment processing. |

### Screen 12: Technical Assessment (Branch Point 2)

| State | Description |
|-------|-------------|
| **Pending (default)** | "Technical Assessment in progress." Checklist: Infrastructure Review, Network Readiness, Location Feasibility. No CTA. |
| **Approved** | Transitions to Screen 13 (triggered by technical team via dashboard). |
| **Rejected (TECH_ASSESSMENT_REJECTED)** | "Technical Assessment not passed." Reason card. ₹20,000 refund in 5-7 days. Terminal. |
| **Dev bypass** | "Dev: Skip to Account Setup" button skips to Screen 13. |

### Screen 13: CSP Account Setup (Automated)

| State | Description |
|-------|-------------|
| **Happy Path** | Auto-plays 5 setup items (800ms each): Ledger, RazorpayX, Zoho, Trade Name Lock, TDS/TCS. Then "All set!" card. |
| **Loading** | Each item cycles: gray (pending) -> spinning (processing) -> green checkmark (done). |
| **Error** | None in prototype. Production should handle individual setup failures. |

### Screen 14: CSP Training Modules

| State | Description |
|-------|-------------|
| **Empty** | All 3 modules show "Start" badge. Progress bar at 0/3. CTA disabled. |
| **Happy Path** | Complete all 3 modules (video + quiz each). CTA enables: "Start Wiom Policy Quiz." |
| **Error: TRAINING_QUIZ_FAIL** | Score card shown. "Don't worry — review the module and try again." Unlimited retries. |
| **Loading** | Video playback simulation (10 steps x 100ms). |

### Screen 15: Wiom Policy Quiz

| State | Description |
|-------|-------------|
| **Not started** | Quiz intro with info: "80% score required to pass." 5 questions. |
| **Happy Path** | Score >= 80% (4/5+). Celebration: "Congratulations! You passed!" CTA: "Go Live!" |
| **Error: POLICY_QUIZ_FAIL** | Score < 80%. Red progress bar. Two CTAs: "Review Modules" / "Retake Quiz." Unlimited retries. |
| **Validation** | Must score >= 80% (4 out of 5 correct). |

### Screen 16: Go Live!

| State | Description |
|-------|-------------|
| **Happy Path** | Celebration with confetti. "Congratulations, Rajesh!" 9 green status chips. 4 quick action cards. Download CTA. |
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
│       ├── BankRepository.kt     # Penny drop, dedup check
│       ├── OnboardingRepository.kt  # Progress tracking, form data persistence
│       └── TrainingRepository.kt # Training modules, quiz questions
│
├── domain/
│   ├── model/
│   │   ├── Partner.kt            # Core domain model
│   │   ├── KycDocument.kt        # Document with status
│   │   ├── BankDetails.kt        # Bank verification state
│   │   ├── TrainingModule.kt     # Module with completion state
│   │   └── OnboardingProgress.kt # Which screen, which stage, what's done
│   └── usecase/
│       ├── SendOtpUseCase.kt
│       ├── VerifyOtpUseCase.kt
│       ├── UploadDocumentUseCase.kt
│       ├── VerifyBankUseCase.kt
│       ├── ProcessPaymentUseCase.kt
│       ├── CheckDedupUseCase.kt
│       └── SubmitQuizUseCase.kt
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
│       ├── training/
│       │   ├── TrainingScreen.kt
│       │   └── TrainingViewModel.kt
│       ├── policyquiz/
│       │   ├── PolicyQuizScreen.kt
│       │   └── PolicyQuizViewModel.kt
│       └── golive/
│           ├── GoLiveScreen.kt
│           └── GoLiveViewModel.kt
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
- Registration Fee: **Rs 2,000** (refundable if Verification rejects at Screen 9)
- Onboarding Fee: **Rs 20,000** (refundable if Tech Assessment rejects at Screen 12)
- Total Investment: **Rs 22,000**
- Refund timeline: **5-7 working days**

### Policy Quiz
- Total questions: **5**
- Pass threshold: **80%** (4 out of 5 correct)
- Retries: Unlimited
- Questions cover all 3 training module topics

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
| **Penny Drop** | Simulated success | Real Rs 1 bank credit via banking API (RazorpayX or similar) |
| **Dedup Check** | Simulated pass | Real database cross-reference against PAN, Aadhaar, GST, Bank A/C |
| **Verification (Screen 9)** | Dashboard approve/reject button | Backend queue + verification panel + push notification to partner |
| **Tech Assessment (Screen 12)** | Dashboard approve/reject button | Backend queue + technical review panel + push notification |
| **CSP Account Setup** | Animated checklist (cosmetic) | Real API calls: RazorpayX payout link, Zoho invoice setup, ledger creation, TDS/TCS config |
| **Training Videos** | Dark placeholder box | Real video player (ExoPlayer) + content CDN + progress tracking |
| **Policy Quiz** | Hardcoded 5 questions | Dynamic question bank from backend (randomized, versioned) |
| **GPS** | Hardcoded coordinates (22.71 N, 75.85 E) | FusedLocationProvider API with runtime permission handling |
| **State Management** | MVVM + ViewModels + StateFlow (done) | Add Room database + DataStore for persistence across app kills |
| **Navigation** | `AnimatedContent` + integer index | Jetpack Navigation library + typed routes + deep links |
| **Architecture** | MVVM + Hilt + Repository (done) | Already implemented — replace MockOnboardingRepository with real API |
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
4. **Payment gateway** — two critical screens depend on it (4 and 11)
5. **KYC + Document upload** — complex flow with OCR integration
6. **Bank verification** — penny drop + dedup check APIs
7. **Push notifications** — needed for branch point results
8. **Training + Quiz** — content CDN + dynamic questions
9. **Analytics + Error reporting** — needed before launch
10. **Security hardening** — certificate pinning, obfuscation, encryption

---

## Quick Reference: File Map

| File | What's In It |
|------|-------------|
| `PRD_HUMAN.md` | Full product spec — all 17 screens, 18 errors, business rules, validation, design system, QA/UAT cases |
| `PRD_AI_AGENT.md` | Machine-readable spec — structured for AI-assisted development |
| `CLAUDE.md` | AI assistant context — architecture summary, UX rules, screen flow |
| `prototype/index.html` | Complete HTML prototype — all screens + all errors + scenario simulator |
| `dashboard/index.html` | Remote control dashboard UI |
| `dashboard/bridge.py` | WebSocket bridge between dashboard and app |
| `app/src/.../ui/theme/Color.kt` | All Wiom brand colors |
| `app/src/.../ui/theme/Shape.kt` | All corner radii |
| `app/src/.../ui/components/Common.kt` | 20+ reusable composables |
| `app/src/.../ui/screens/Stage1Screens.kt` | Screens 0-4 (Registration) |
| `app/src/.../ui/screens/Stage2Screens.kt` | Screens 5-9 (Verification) |
| `app/src/.../ui/screens/Stage3Screens.kt` | Screens 10-16 (Activation) |
| `app/src/.../ui/navigation/NavGraph.kt` | Navigation Compose graph (18 routes) |
| `app/src/.../ui/viewmodel/*.kt` | 14 ViewModels — one per screen, injected via Hilt |
| `app/src/.../util/Strings.kt` | Bilingual `t()` function |
| `app/src/.../util/Validation.kt` | All field validation rules |

---

*This guide covers everything you need to pick up the Wiom CSP Onboarding project. Start by running the HTML prototype, read through the PRD, then build production from scratch using the architecture recommendation above. When in doubt, the prototype is your visual/behavioral source of truth, and `PRD_HUMAN.md` is your product source of truth.*
