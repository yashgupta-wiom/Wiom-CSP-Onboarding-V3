# CLAUDE.md — Context for AI-assisted development

## What is this project?

Wiom CSP (Channel Sales Partner) onboarding Android app — a 15-screen flow (plus Pitch) that takes a new internet service partner from registration to successfully onboarded. Built as an interactive prototype for stakeholder review and developer handoff.

## Build & Run

```bash
export ANDROID_HOME=~/Library/Android/sdk
./gradlew assembleDebug
# APK at: app/build/outputs/apk/debug/app-debug.apk
```

## Architecture

- **Single Activity** (`MainActivity`) → `OnboardingHost` composable → 15 screen composables
- **No ViewModel/Repository pattern yet** — state is in `OnboardingState` singleton (intentional for prototype speed)
- **No navigation library** — `OnboardingHost` uses `AnimatedContent` keyed on `currentScreen` integer
- **Bilingual via `t(hi, en)` function** — NOT Android string resources. This is intentional for instant toggle without activity recreation.

## Wiom UX Rules (MUST follow)

1. **Hindi-first**: Default language is Hindi. English is secondary.
2. **No-blame errors**: Never say "you entered wrong X". Say "X मेल नहीं खाता" (X doesn't match).
3. **Benefit-first**: Lead with what user gains. "QA investigation शुरू होगी" not "You must wait".
4. **Trust badges**: Always show 🔒 and ✓ for verified/secure states.
5. **Warm tone**: "चिंता न करें" (don't worry), "बधाई हो" (congratulations).
6. **No fear language**: Never use words like "penalty", "punishment", "disconnection".

## Key Business Values

- New Connection commission: ₹300/connection
- Recharge commission: ₹300
- Registration fee: ₹2,000 (auto-refund if rejected)
- Onboarding fee: ₹20,000
- Payout: Every Monday by 10 AM
- SLA: 4hr complaint resolution, 95%+ uptime
- Help number: 7836811111

## Design Tokens

All Wiom colors are in `ui/theme/Color.kt`. The primary brand color is `#D9008D` (magenta pink).
Corner radii: 8dp (small), 12dp (input), 16dp (card/button), 888dp (pill).

## Screen Flow (V3 — 15 screens + Pitch: Screens 0-14)

[Pitch] → 0:Phone → 1:OTP → 2:Personal(1/3) → 3:Location(2/3) → 4:RegFee(3/3) → 5:KYC(1/5, 3 sub-stages: PAN→Aadhaar→GST) → 6:Bank(2/5) → 7:ISP(3/5) → 8:ShopPhotos(4/5) → 9:Verification(5/5, branch: approved/rejected) → 10:Policy(1/5, "Important Terms") → 11:TechAssessment(2/5, branch: pass/fail) → 12:OnboardFee(3/5) → 13:AccountSetup(4/5, auto-progress) → 14:SuccessfullyOnboarded(5/5)

## File Layout

- `PitchScreen.kt` — Pitch screen (pre-flow)
- `Phase1Screens.kt` — Screens 0-4 (Phone, OTP, Personal, Location, RegFee)
- `Phase2Screens.kt` — Screens 5-9 (KYC, Bank, ISP, ShopPhotos, Verification)
- `Phase3Screens.kt` — Screens 10-14 (Policy, TechAssessment, OnboardFee, AccountSetup, SuccessfullyOnboarded)
- `Common.kt` — All reusable composables
- `OnboardingHost.kt` — Screen router, progress bar, language toggle

## What NOT to change without checking

- Commission values (₹300/₹300) — business decision
- SLA terms (4hr, 95%+) — contractual
- Fee amounts (₹2K, ₹20K) — pricing decision
- Payout schedule (Monday by 10 AM) — operational commitment
- Help number (7836811111) — published contact
- Hindi copy — reviewed and approved
- Color tokens — from Figma design system, not arbitrary
