# Wiom CSP Onboarding App (V3)

Android app (Kotlin + Jetpack Compose) for the **Wiom Channel Sales Partner (CSP) onboarding flow** — a 16-screen (Pitch + Screens 0-14) interactive prototype with **two browser-based dashboards** for controlling the app and reviewing QA applications.

## Quick Start

### Option 1: Install pre-built APK
```bash
adb install apk/wiom-csp-onboarding-v3.apk
adb shell am start -n com.wiom.csp/.MainActivity
```

### Option 2: Build from source
```bash
export ANDROID_HOME=~/Library/Android/sdk
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Option 3: Full setup with Dashboards
```bash
# 1. Install APK (Option 1 or 2 above)
# 2. Start the bridge server
cd dashboard && python3 bridge.py
# 3. Open dashboards in Chrome
open dashboard/control.html      # Control Dashboard
open dashboard/qa-review.html    # QA Review Dashboard
```

## Dashboards

This repo includes **two browser-based dashboards** that connect to the Android app running in an emulator via a Python bridge server.

### Control Dashboard (`dashboard/control.html`)
- Navigate all 15 screens (Pitch through Successfully Onboarded)
- Fill/empty all form data with one click
- Switch Hindi/English language
- Simulate 22 error scenarios across 8 categories
- Live app screenshot preview

### QA Review Dashboard (`dashboard/qa-review.html`)
- List of all CSP applications with filter (Pending/Approved/Rejected) and search
- Click any application to view full details: Personal Info, Location, KYC Documents, Registration Fee
- Approve or Reject with mandatory rejection reason
- Reversible decisions — can change Approve/Reject anytime
- LIVE device connection — real-time data from emulator

### Bridge Server (`dashboard/bridge.py`)
- Python HTTP server (port 8092) that connects dashboards to the Android emulator via ADB
- Endpoints: `/status`, `/data`, `/screenshot`, POST actions (navigate, fill, scenario, qa, lang)

## The 16-Screen Onboarding Flow (V3)

### Pitch Screen (Pre-flow)
Welcome screen with Wiom branding — "Wiom पार्टनर बनें". CTA: "शुरू करें"

### Phase 1 — Registration (Screens 0-4)

| # | Screen | Step | What happens |
|---|--------|------|-------------|
| 0 | **Phone Entry** | — | Mobile number (+91), T&C checkbox, "नियम व शर्तें पढ़ें" link. CTA: "OTP भेजें" |
| 1 | **OTP Verification** | — | 4-digit OTP input, 28s resend timer. CTA: "वेरीफाई करें" |
| 2 | **Personal Info** | Step 1/3 | Name, email, entity type (Individual), trade name. CTA: "अब लोकेशन बताइए" |
| 3 | **Location** | Step 2/3 | State (33 Indian states/UTs dropdown), city, pincode, address, GPS. CTA: "अब registration शुल्क भरें" |
| 4 | **Registration Fee** | Step 3/3 | ₹2,000 payment with auto-refund guarantee. CTA: "₹2,000 भुगतान करें" |

### Phase 2 — Documentation & Verification (Screens 5-9)

| # | Screen | Step | What happens |
|---|--------|------|-------------|
| 5 | **KYC Documents** | Step 1/5 | 3 sub-stages: PAN → Aadhaar (front+back) → GST upload with camera/gallery. CTA: "अब बैंक का विवरण दें" |
| 6 | **Bank Details** | Step 2/5 | 3 fields: Account number, Re-enter Account number, IFSC. CTA: "Add Bank Document" → mandatory bank document upload |
| 7 | **ISP Agreement** | Step 3/5 | Multi-page ISP agreement upload. CTA: "आगे बढ़ें" |
| 8 | **Shop & Equipment Photos** | Step 4/5 | Multi-photo: Shop front + router/equipment photos with helper hints. CTA: "सत्यापन के लिए जमा करें" |
| 9 | **Verification** | Step 5/5 | Checklist of all submitted items. **Two paths:** Approved → Tech Assessment, Rejected → auto refund (no re-upload) |

### Phase 3 — Activation (Screens 10-14)

| # | Screen | Step | What happens |
|---|--------|------|-------------|
| 10 | **Technical Assessment** | Step 1/7 | Device + infra check. **Two paths:** Pass → Policy & SLA, Fail → no refund + Talk to Us |
| 11 | **Policy & SLA** | Step 2/7 | "Important Terms" — Commission rates (₹300), SLA terms, compliance rules. CTA: "समझ गया, आगे बढ़ें" |
| 12 | **Onboarding Fee ₹20K** | Step 3/7 | Fee breakdown, payment. CTA: "₹20,000 भुगतान करें" |
| 13 | **Account Setup** | Step 4/5 | Auto-progress setup: ledger, payout, invoice, TDS config. Completes automatically. |
| 14 | **Successfully Onboarded** | Step 5/5 | Celebration with completion chips, quick actions |

## Error Scenarios (22 across 8 categories)

| Category | Scenarios |
|----------|-----------|
| Global | No Internet, Server Error |
| Registration & OTP | Phone Already Registered, Wrong OTP, OTP Expired |
| Reg Fee | ₹2K Payment Failed, ₹2K Payment Timeout |
| KYC Reminders | Day 1/2/3 Reminder, Day 4 Auto-Reject |
| Bank | Bank Account Dedup |
| Verification | Verification Pending, Verification Rejected, Refund (Success/In-Progress/Failed) |
| Onboarding & Setup | ₹20K Success/Failed/Timeout, Account Setup Failed/Pending |

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.1.0 |
| UI | Jetpack Compose + Material3 |
| Build | Gradle 8.11.1 (Kotlin DSL) |
| Min SDK | 24 (Android 7.0) |
| Target/Compile SDK | 35 |
| Architecture | Single-activity, composable screens |
| i18n | Runtime bilingual (Hindi/English) via `t()` helper |
| Dashboards | Vanilla HTML/CSS/JS + Python bridge |

## Project Structure

```
Wiom-CSP-Onboarding-V3/
├── apk/
│   └── wiom-csp-onboarding-v3.apk    # Pre-built APK (16 MB)
├── app/                               # Android source code
│   └── src/main/java/com/wiom/csp/
│       ├── DashboardReceiver.kt       # ADB broadcast receiver for dashboard control
│       ├── data/
│       │   └── OnboardingState.kt     # Global state + scenarios
│       └── ui/screens/
│           ├── PitchScreen.kt         # Pitch screen (pre-flow)
│           ├── OnboardingHost.kt      # Screen router + progress bar
│           ├── Phase1Screens.kt       # Screens 0-4 (Phone → RegFee)
│           ├── Phase2Screens.kt       # Screens 5-9 (KYC → Verification)
│           └── Phase3Screens.kt       # Screens 10-14 (Policy → Successfully Onboarded)
├── dashboard/
│   ├── bridge.py                      # Python bridge server (port 8092)
│   ├── control.html                   # Control Dashboard
│   └── qa-review.html                 # QA Review Dashboard
├── prototype/
│   └── index.html                     # HTML prototype (reference)
├── CLAUDE.md                          # AI dev context
├── PRD_AI_AGENT.md                    # PRD for AI agents
├── PRD_HUMAN.md                       # PRD for human developers
└── README.md                          # This file
```

## Key Business Values

- **Registration Fee:** ₹2,000 (auto-refund if rejected in Phase 1)
- **Onboarding Fee:** ₹20,000 (incl. GST)
- **Total Investment:** ₹22,000
- **New Connection Commission:** ₹300/connection
- **Recharge Commission:** ₹300
- **Payout:** Every Monday by 10 AM
- **SLA:** 4hr complaint resolution, 95%+ uptime
- **Help Number:** 7836811111

## Wiom UX Principles

1. **Hindi-first** — Default language is Hindi, English via toggle
2. **No-blame errors** — "चिंता न करें" (Don't worry), never blame the user
3. **Benefit-first** — Lead with what user gains
4. **Trust badges** — Lock icons and green verification badges
5. **Warm tone** — Conversational, friendly, never bureaucratic

## What to Build Next (Production Roadmap)

- [ ] Replace bridge.py with real API server (Node/Python/Go)
- [ ] Database for application state and QA decisions
- [ ] Authentication for QA reviewers
- [ ] Real OTP verification via SMS gateway
- [ ] Payment gateway integration (Razorpay) for ₹2K and ₹20K
- [ ] Camera/gallery picker for KYC and shop photo uploads
- [ ] Aadhaar e-Sign integration
- [ ] GPS location capture via FusedLocationProvider
- [ ] Push notifications for status updates
- [ ] State persistence (Room/DataStore)
- [ ] Analytics/event tracking
- [ ] ProGuard/R8 for release builds

## Package Info

- **Package name:** `com.wiom.csp`
- **Main Activity:** `com.wiom.csp.MainActivity`
- **Dashboard Receiver:** `com.wiom.csp.DashboardReceiver`
