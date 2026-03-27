# Wiom CSP Onboarding App — Product Requirements Document

**Version:** 3.1 | **Date:** 26 March 2026 | **Status:** Prototype
**Repos:** [vikashPD/Wiom-CSP-Dashboards](https://github.com/vikashPD/Wiom-CSP-Dashboards) | [ashishagrawal-iam/Wiom-csp-onboarding-v2](https://github.com/ashishagrawal-iam/Wiom-csp-onboarding-v2)
**Package:** `com.wiom.csp`

---

## Table of Contents

1. [What This App Does](#1-what-this-app-does)
2. [Who Uses It](#2-who-uses-it)
3. [The Onboarding Journey (15 Screens + Pitch)](#3-the-onboarding-journey)
4. [Flow Diagram](#4-flow-diagram)
5. [Screen-by-Screen Specification](#5-screen-by-screen-specification)
6. [Error Scenarios & Scenario Simulator](#6-error-scenarios--scenario-simulator)
7. [Empty States & Edge Cases](#7-empty-states--edge-cases)
8. [Dashboard System (2 Dashboards)](#8-dashboard-system)
9. [Dashboard-App Interaction](#9-dashboard-app-interaction)
10. [Business Rules & Constants](#10-business-rules--constants)
11. [Validation Rules](#11-validation-rules)
12. [Design System Reference](#12-design-system-reference)
13. [QA Test Cases](#13-qa-test-cases)
14. [UAT Test Cases](#14-uat-test-cases)
15. [What's Prototype vs Production](#15-whats-prototype-vs-production)

---

## 1. What This App Does

This Android app takes a new Channel Sales Partner (CSP) through the complete journey of becoming a Wiom partner — from a pitch screen to being successfully onboarded and ready to serve customers.

The flow has **15 screens + Pitch** (Screens 0-14) across **3 phases**, with **28 documented error scenarios** across **8 simulator categories**, and **2 companion dashboards** (Control + QA Review).

**V3.1 Changes from V3.0:**
- Total screens reduced from 18 to 15 + Pitch (removed Training Modules, Policy Quiz, Go Live)
- Screen order changed: Tech Assessment (11) now BEFORE Onboarding Fee (12)
- KYC Documents (Screen 5) redesigned as 3 sub-stages with progress bar (PAN → Aadhaar → GST)
- Each KYC sub-stage: number entry with regex validation + document upload + view sample doc
- All KYC validations show errors on blur only (not while typing)
- Dedup checks added for PAN, Aadhaar, and GST
- Bank Details (Screen 6) simplified to 3 fields (removed account holder name and bank name)
- Bank verification uses bottom sheet overlays for failure scenarios
- ISP Agreement (Screen 7) now supports multi-page upload (PDF + up to 7 photos)
- Equipment Photos (Screen 8) now supports multi-photo (up to 5)
- All document screens have "View sample document" with actual sample images
- Verification rejected: auto refund flow, no re-upload option in Phase 1
- Tech Assessment rejected: no refund of Rs.2K, "Talk to Us" CTA (7836811111)
- Account Setup (Screen 13) auto-progresses after 3 seconds, no CTA
- Final screen (14): "Successfully Onboarded" with app download + instructions (replaces Go Live with chips)
- Removed PAN Name Mismatch, Aadhaar Expired, ISP Doc Invalid, Dedup Match Found scenarios
- Added PAN/Aadhaar/GST dedup scenarios, Bank Account Dedup scenario
- Activation phase steps: Screens 10-12 use 1/7, 2/7, 3/7; Screens 13-14 use 4/5, 5/5

**Key design philosophy:**
- **Hindi-first** — All text defaults to Hindi with a runtime toggle to English
- **No-blame errors** — Never blame the user; always reassure ("Don't worry")
- **Benefit-first** — Lead with what the partner gains
- **Trust badges** — Lock icons and verification badges build confidence
- **Warm tone** — Conversational, friendly, never bureaucratic

---

## 2. Who Uses It

| Actor | Role | How They Interact |
|-------|------|-------------------|
| **Partner** | New CSP applicant | Goes through the 15-screen onboarding flow on their Android phone |
| **QA Team** | Wiom Business/QA reviewers | Uses the **QA Review Dashboard** to review applications, view submitted data, and approve/reject with reasons |
| **Admin** | Dashboard operator | Uses the **Control Dashboard** to navigate screens, trigger scenarios, manage app state |
| **System** | Automated backend | Handles OTP, KYC verification, penny drop, dedup check, financial setup |

---

## 3. The Onboarding Journey

### Pitch Screen (Pre-flow)
Welcome screen — Wiom Partner+ branding. CTA: "Get Started"

### Phase 1 — Registration (Screens 0-4)

| Screen | Name | Step | What Happens |
|--------|------|------|-------------|
| 0 | Phone Entry | — | Mobile number (+91, 10 digits), T&C checkbox. CTA: "Send OTP" (enabled only when 10 digits + T&C checked) |
| 1 | OTP Verification | — | 4-digit OTP with 28-second countdown timer. CTA: "सत्यापित करें" / "Verify" |
| 2 | Personal & Business Info | Step 1/3 | Name (as per Aadhaar), email ID, entity type (Proprietorship only), business name. CTA: "व्यापार स्थान जोड़ें" / "Add Business Location" |
| 3 | Business Location | Step 2/3 | State (33 Indian states/UTs dropdown), city, pincode, address, GPS capture. CTA: "पंजीकरण शुल्क भरें" / "Pay Registration Fee" |
| 4 | Registration Fee | Step 3/3 | Pay Rs.2,000 (refundable). CTA: "₹2,000 अभी भुगतान करें" / "Pay ₹2,000 Now" |

### Phase 2 — Verification (Screens 5-9)

| Screen | Name | Step | Header | What Happens |
|--------|------|------|--------|-------------|
| 5 | KYC Documents | Step 1/5 | Verification | 3 sub-stages with progress bar: PAN → Aadhaar → GST. Each sub-stage has number entry + document upload + view sample doc. Dedup checks on all three. |
| 6 | Bank Details | Step 2/5 | Verification | 3 fields: Account Number, Re-enter Account Number, IFSC Code. Verify via penny drop → bottom sheet results. |
| 7 | ISP Agreement Upload | Step 3/5 | Verification | Multi-page upload: PDF / Camera (up to 7 pages) / Gallery (up to 7 pages). Mandatory details checklist. |
| 8 | Shop & Equipment Photos | Step 4/5 | Verification | Shop Front Photo (single) + Equipment Photos (multi, up to 5). Sub-header: "Shop Verification". |
| 9 | Verification Status | Step 5/5 | Verification | "All Documents Submitted" checklist. Branch: Approved → next / Rejected → refund flow (no re-upload in Phase 1). |

### Phase 3 — Activation (Screens 10-14)

| Screen | Name | Step | Header | What Happens |
|--------|------|------|--------|-------------|
| 10 | Policy & SLA | Step 1/7 | Important Terms | Commission rates, payout schedule, service levels to maintain. CTA: "समझ गया, आगे बढ़ें" / "Understood, proceed" |
| 11 | Technical Assessment | Step 2/7 | Activation | Infrastructure review, network readiness, location feasibility. TAT: 4-5 business days. Branch: Passed → next / Rejected → no refund + Talk to Us. |
| 12 | Onboarding Fee Rs.20K | Step 3/7 | Activation | Investment summary with Rs.2K (Paid) + Rs.20K (Due) = Rs.22K total. CTA: "₹20,000 अभी भुगतान करें" / "Pay ₹20,000 Now" |
| 13 | Account Setup | Step 4/5 | Activation | Loading screen auto-progresses after 3 seconds. No CTA. |
| 14 | Successfully Onboarded | Step 5/5 | Activation | Congratulations + Download Wiom Partner Plus App + Important Instructions. |

---

## 4. Flow Diagram

```
START
  |
  v
[Pitch Screen] ──────────────────────────────────────── Wiom Partner+ branding
  |
  v
[Screen 0: Phone Entry] ─────error──→ PHONE_DUPLICATE (blocked)
  |  (10 digits + T&C checkbox required)
  v
[Screen 1: OTP Verify] ──────error──→ OTP_WRONG (retry, 3 max)
  |                       ───error──→ OTP_EXPIRED (resend)
  v
[Screen 2: Personal & Business Info] ── Step 1/3
  |
  v
[Screen 3: Business Location] ───────── Step 2/3
  |
  v
[Screen 4: Rs.2,000 Fee] ───error──→ REGFEE_FAILED (retry)
  |                        ──error──→ REGFEE_TIMEOUT (retry)
  v
[Screen 5: KYC Documents] ──── 3 sub-stages (PAN → Aadhaar → GST)
  |  PAN ──────error──→ PAN_DEDUP (blocked)
  |  Aadhaar ──error──→ AADHAAR_DEDUP (blocked)
  |  GST ──────error──→ GST_DEDUP (blocked)
  |  (errors shown on blur, not while typing)
  v
[Screen 6: Bank Details] ──── Verify → 2s spinner → result:
  |  ──success──→ Verified delight → next
  |  ──error────→ PENNY_DROP_FAIL (bottom sheet: Change / Upload Doc)
  |  ──error────→ NAME_MISMATCH (bottom sheet: Change / Upload Doc)
  |  ──error────→ BANK_DEDUP (bottom sheet: Change only)
  v
[Screen 7: ISP Agreement] ── Multi-page upload (PDF + up to 7 photos)
  |
  v
[Screen 8: Shop & Equipment Photos] ── Shop front (1) + Equipment (up to 5)
  |
  v
[Screen 9: Verification Status] ═══════ BRANCH POINT ═══════
  |                                                          |
  |── APPROVED                                    REJECTED ──|
  v                                                          v
[Screen 10: Policy & SLA]                    AUTO REFUND FLOW
  |                                          (no re-upload in Phase 1)
  v
[Screen 11: Tech Assessment] ══════════ BRANCH POINT ═══════
  |                                                          |
  |── PASSED                                      REJECTED ──|
  v                                          "No refund at this moment"
[Screen 12: Rs.20,000 Fee] ──error──→ ONBOARDFEE_FAILED (retry)
  |                         ──error──→ ONBOARDFEE_TIMEOUT (refresh)
  v
[Screen 13: Account Setup] (auto-progress, 3 seconds, no CTA)
  |  ──error──→ SETUP_FAILED (retry + talk to us)
  |  ──error──→ SETUP_PENDING (refresh + talk to us)
  v
[Screen 14: Successfully Onboarded]
  |  Download Wiom Partner Plus App
  |  Important Instructions
  |
  END
```

### Error Classification

| Type | Errors | Behavior |
|------|--------|----------|
| **Blocking** | PHONE_DUPLICATE, PAN_DEDUP, AADHAAR_DEDUP, GST_DEDUP, BANK_DEDUP, TECH_ASSESSMENT_REJECTED | Cannot proceed — needs external resolution or "Talk to Us" |
| **Retryable** | OTP_WRONG, OTP_EXPIRED, REGFEE_FAILED, REGFEE_TIMEOUT, PENNY_DROP_FAIL, NAME_MISMATCH, ONBOARDFEE_FAILED, ONBOARDFEE_TIMEOUT, SETUP_FAILED, SETUP_PENDING | Can retry immediately or after fixing input |
| **Terminal** | VERIFICATION_REJECTED | Auto refund flow, no re-upload in Phase 1 |

---

## 5. Screen-by-Screen Specification

### Pitch Screen

**Purpose:** Welcome and branding — first impression before onboarding starts.

**What the partner sees:**
- Wiom Partner+ branding
- Brief value proposition
- CTA button: "Get Started"

**Rules:**
- No form fields, just a CTA to begin
- Navigates to Screen 0 (Phone Entry)

---

### Screen 0: Phone Entry

**Purpose:** Capture mobile number with Terms & Conditions acceptance.

**What the partner sees:**
- Header: "Wiom Partner+"
- Phone input field with +91 country code prefix
- 10-digit phone number input (numeric only)
- T&C checkbox with link to terms
- CTA button: "Send OTP"

**Rules:**
- CTA disabled until BOTH conditions met: exactly 10 digits + T&C checkbox checked
- If user unchecks T&C, CTA becomes disabled again
- Only numeric input allowed

---

### Screen 1: OTP Verification

**Purpose:** Confirm phone ownership via one-time password.

**What the partner sees:**
- Message showing which number OTP was sent to
- 4-digit OTP input (auto-focus)
- Countdown timer starting at 28 seconds
- After timer expires: "Resend OTP" + "Change Number" links

**Rules:**
- CTA enabled only when all 4 digits filled
- Timer counts down 1 second at a time
- Resend restarts the timer

---

### Screen 2: Personal & Business Info

**Purpose:** Collect identity and business details.

**Step Label:** Step 1/3

**Fields:**
1. **पूरा नाम (आधार अनुसार) / Full Name (as per Aadhaar)** — Required
2. **ईमेल आईडी / Email ID** — Required, must contain @ and .
3. **व्यापार इकाई प्रकार / Business Entity Type** — Dropdown with only "प्रोप्राइटरशिप (Proprietorship)" option
4. **व्यापार का नाम / Business Name** — Required; gets locked after registration fee is paid

**CTA:** "व्यापार स्थान जोड़ें" / "Add Business Location"

**Rules:**
- CTA enabled only when all 4 fields are filled
- Entity type dropdown shows only "प्रोप्राइटरशिप (Proprietorship)"

---

### Screen 3: Business Location

**Purpose:** Capture shop/office location for service area mapping.

**Step Label:** Step 2/3

**Fields:**
1. **State** — Dropdown with 33 Indian states and union territories
2. **City** — Editable text field
3. **Pincode** — 6 digits only
4. **Full Address** — Editable text field

**Special Elements:**
- GPS badge showing captured coordinates

**CTA:** "पंजीकरण शुल्क भरें" / "Pay Registration Fee"

**Indian States/UTs in dropdown (33):**
Andhra Pradesh, Arunachal Pradesh, Assam, Bihar, Chhattisgarh, Goa, Gujarat, Haryana, Himachal Pradesh, Jharkhand, Karnataka, Kerala, Madhya Pradesh, Maharashtra, Manipur, Meghalaya, Mizoram, Nagaland, Odisha, Punjab, Rajasthan, Sikkim, Tamil Nadu, Telangana, Tripura, Uttar Pradesh, Uttarakhand, West Bengal, Delhi, Jammu & Kashmir, Ladakh, Chandigarh, Puducherry

---

### Screen 4: Registration Fee (Rs.2,000)

**Purpose:** First payment — initiates the verification process.

**Step Label:** Step 3/3

**What the partner sees:**
- Large amount display: Rs.2,000
- Info about the registration process
- Trust badge: "Refundable"

**CTA:** "₹2,000 अभी भुगतान करें" / "Pay ₹2,000 Now"

**On payment:**
- 2-second simulated processing delay
- Trade name gets locked
- Navigates to KYC Documents (Screen 5)

---

### Screen 5: KYC Documents

**Purpose:** Collect and verify identity documents via 3 sub-stages.

**Step Label:** Step 1/5 | **Header:** Verification

**Sub-stage progress bar** shows PAN → Aadhaar → GST progression.

#### Sub-stage 1: PAN

**Fields:**
- PAN Number entry — Regex: `/^[A-Z]{5}[0-9]{4}[A-Z]$/` (10 characters, e.g., ABCDE1234F)
- PAN Card upload (camera or gallery)
- "View sample document" link with actual sample image

**Validation:** Error shown on blur only, not while typing.
**Dedup check:** After valid PAN entry, system checks for duplicate PAN. If duplicate found → dedup error overlay.

#### Sub-stage 2: Aadhaar

**Fields:**
- Aadhaar Number entry — 12 digits, displayed formatted as 4-4-4 (e.g., 1234 5678 9012)
- Aadhaar Front upload (camera or gallery)
- Aadhaar Back upload (camera or gallery)
- "View sample document" link with actual sample image

**Validation:** Error shown on blur only, not while typing.
**Dedup check:** After valid Aadhaar entry, system checks for duplicate. If duplicate found → dedup error overlay.

#### Sub-stage 3: GST

**Fields:**
- GST Number entry — 15 characters, regex: `/^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][0-9A-Z]{3}$/`
  - Characters 3-12 must match the PAN number entered earlier
- GST Certificate upload (camera or gallery)
- "View sample document" link with actual sample image

**Validation:** Error shown on blur only, not while typing.
**Dedup check:** After valid GST entry, system checks for duplicate. If duplicate found → dedup error overlay.

**Rules:**
- Header says "Verification" for all sub-stages
- All number entries validated on blur only
- Each sub-stage must be completed before proceeding to the next
- All 3 sub-stages must be completed before moving to Screen 6

---

### Screen 6: Bank Details

**Purpose:** Verify partner's bank account via penny drop.

**Step Label:** Step 2/5 | **Header:** Verification

**Fields (3 only):**
1. **Account Number** — Numeric only, 9-18 digits. Field changes to type=password (masked) on blur.
2. **Re-enter Account Number** — Must match Account Number exactly.
3. **IFSC Code** — Regex: `/^[A-Z]{4}0[A-Z0-9]{6}$/` (11 characters)

**Hint text:** "Bank details should belong to [Personal Name] or [Business Name]"

**CTA:** "Verify Bank Details"

**Verification Flow:**
1. CTA triggers 2-second spinner
2. Result is one of four outcomes:

| Outcome | What Shows | Options |
|---------|------------|---------|
| **Success** | Verified delight screen | Auto-proceeds → "Add ISP Agreement" |
| **Penny Drop Fail** | Bottom sheet overlay | "Change Bank Details" / "Upload Bank Document" |
| **Name Mismatch** | Bottom sheet overlay with name comparison | "Change Bank Details" / "Upload Bank Document" |
| **Bank Dedup** | Bottom sheet overlay | "Change Bank Details" only (no upload option) |

**Supporting Document Screen** (when "Upload Bank Document" chosen):
- Upload options: Bank Statement / Cancelled Cheque / Bank Passbook
- "View sample document" link with actual sample image

---

### Screen 7: ISP Agreement Upload

**Purpose:** Upload ISP agreement document for compliance verification.

**Step Label:** Step 3/5 | **Header:** Verification

**What the partner sees:**
- Multi-page upload options:
  - **PDF** upload
  - **Camera** capture (up to 7 pages)
  - **Gallery** selection (up to 7 pages)
- Mandatory details checklist:
  - ISP Company Name
  - LCO/Partner Name
  - Agreement Date
  - Valid (validity period)
  - License Number
  - Signatory Names
  - Stamp & Signature
- "View sample document" link with actual sample image
- Pro tips shown in upload sheet

**CTA:** Enabled after ISP agreement uploaded

---

### Screen 8: Shop & Equipment Photos

**Purpose:** Capture shop front and equipment photos for verification.

**Step Label:** Step 4/5 | **Header:** Verification | **Sub-header:** Shop Verification

**Two upload sections:**

1. **Shop Front Photo**
   - Single photo upload
   - "View sample document" link
   - Shop-specific pro tips

2. **Equipment Photos**
   - Multi-photo upload (up to 5 photos)
   - "View sample document" link
   - Mandatory requirements:
     - Power Backup photo
     - OLT Photo
     - ISP Switch photo

**Rules:**
- Both sections must have at least one photo before proceeding

---

### Screen 9: Verification Status

**Purpose:** Confirmation that all documents are submitted, awaiting review.

**Step Label:** Step 5/5 | **Header:** Verification

**What the partner sees:**
- "All Documents Submitted" message
- Completion checklist:
  - KYC Documents ✓
  - Bank Details ✓
  - ISP Agreement ✓
  - Shop & Equipment Photos ✓
  - Verification Review ⋯ (under review)
- Info: "समीक्षा में 3 कार्य दिवस" / "Review may take 3 business days"

**Branch Point:**

| Outcome | What Happens |
|---------|-------------|
| **Approved** | Proceeds to Screen 10 (Policy & SLA) |
| **Rejected** | Shows "Verification Rejected" with auto refund flow. **No re-upload option in Phase 1.** |

**Rejected Flow:**
- Refund status shown (Refund Success / Refund In Progress / Refund Failed)
- No CTA to fix and resubmit — partner must start fresh or contact support

---

### Screen 10: Policy & SLA

**Purpose:** Present Wiom's policies and service level agreement for partner acceptance.

**Step Label:** Step 1/7 | **Header:** Important Terms

**Subheading:** "Wiom's Policy and Service Level Agreement"

**Content:**
- **Commission:** Rs.300 per new connection, Rs.300 per recharge
- **Payout:** Every Monday by 10 AM
- **Service Levels to be Maintained:**
  - Complaints: 4-hour resolution
  - Uptime: 95%+
  - Equipment care
  - Brand compliance

**CTA:** "समझ गया, आगे बढ़ें" / "Understood, proceed"

---

### Screen 11: Technical Assessment

**Purpose:** Wiom team assesses partner's infrastructure, network, and location feasibility.

**Step Label:** Step 2/7 | **Header:** Activation

**What the partner sees:**
- Assessment areas: Infrastructure Review, Network Readiness, Location Feasibility
- TAT: 4-5 business days
- "You will also receive a call from our Network Quality team"

**Branch Point:**

| Outcome | What Happens |
|---------|-------------|
| **Passed** | Proceeds to Screen 12 (Onboarding Fee) |
| **Rejected** | Shows "Profile not accepted yet" + "No refund will be done at this moment" + "Talk to Us" CTA (7836811111) |

---

### Screen 12: Onboarding Fee (Rs.20,000)

**Purpose:** Second and final payment to activate the partnership.

**Step Label:** Step 3/7 | **Header:** Activation

**What the partner sees:**
- WiFi devices message
- **Investment Summary:**
  - Registration Fee: Rs.2,000 (Paid ✓)
  - Onboarding Fee: Rs.20,000 (Due)
  - Total: Rs.22,000

**CTA:** "₹20,000 अभी भुगतान करें" / "Pay ₹20,000 Now"

**Payment outcomes:**

| Outcome | What Happens |
|---------|-------------|
| **Success** | Auto-moves to Screen 13 |
| **Failed** | Retry CTA + "Talk to Us" (7836811111) |
| **Timeout** | Refresh CTA + "Talk to Us" (7836811111) |

---

### Screen 13: Account Setup

**Purpose:** Automated backend setup of the partner's account.

**Step Label:** Step 4/5 | **Header:** Activation

**What the partner sees:**
- Loading screen: "Account Setup in Progress for [Business Name]"
- Auto-progresses to success screen after 3 seconds
- **No CTA** — fully automated

**Error outcomes:**

| Outcome | What Happens |
|---------|-------------|
| **Failed** | Retry CTA + "Talk to Us" (7836811111) |
| **Pending** | Refresh CTA + "Talk to Us" (7836811111) |

---

### Screen 14: Successfully Onboarded

**Purpose:** Celebration and next steps for the newly onboarded partner.

**Step Label:** Step 5/5 | **Header:** Activation

**What the partner sees:**
- "Congratulations, [Name]! You are now a Wiom Connection Service Provider"
- **Next Steps:**
  - Download Wiom Partner Plus App
  - "Install Now" button
- **Important Instructions:**
  - Login to the app
  - Allow all permissions
  - Complete Mandatory Training

**Design notes:**
- NO green status chips
- NO 4 action cards
- Clean, focused congratulations screen with clear next steps

---

## 6. Error Scenarios & Scenario Simulator

### Scenario Simulator Categories (8 categories, 28 scenarios)

#### Category 1: Network/App Errors

| Scenario | When | What Shows | Outcome |
|----------|------|------------|---------|
| **No Internet** | Any screen, no connectivity | No internet error overlay | Blocked until connection restored |
| **Server Error** | Any screen, server down | Server error overlay | Retry |

#### Category 2: Registration & OTP

| Scenario | Screen | When | What Shows | Outcome |
|----------|--------|------|------------|---------|
| **Phone Duplicate** | 0 | Phone already registered | Error card + alternative CTAs | Blocked |
| **Wrong OTP** | 1 | Incorrect OTP digits | Red boxes, attempt counter | Retryable (3 max) |
| **OTP Expired** | 1 | Timer ran out | Faded boxes, "Resend OTP" / "Change Number" | Retryable |

#### Category 3: Registration Fee (Rs.2K)

| Scenario | Screen | When | What Shows | Outcome |
|----------|--------|------|------------|---------|
| **Rs.2K Failed** | 4 | Payment declined | "No money deducted" + retry | Retryable |
| **Rs.2K Timeout** | 4 | Gateway timeout | Pending status + refresh | Retryable |
| **Day 1-4 Nudges** | 4 | Payment pending for 1-4 days | Reminder nudges | Informational |
| **Refund Success** | 9 | Refund completed | Refund confirmation | Terminal |
| **Refund In Progress** | 9 | Refund processing | Progress status | Informational |
| **Refund Failed** | 9 | Refund could not process | Error + contact support | Needs support |

#### Category 4: KYC Dedup

| Scenario | Screen | When | What Shows | Outcome |
|----------|--------|------|------------|---------|
| **PAN Dedup** | 5 (PAN sub-stage) | PAN already registered | Dedup error overlay (deferred — triggers on blur after valid entry) | Blocked |
| **Aadhaar Dedup** | 5 (Aadhaar sub-stage) | Aadhaar already registered | Dedup error overlay (deferred — triggers on blur after valid entry) | Blocked |
| **GST Dedup** | 5 (GST sub-stage) | GST already registered | Dedup error overlay (deferred — triggers on blur after valid entry) | Blocked |

#### Category 5: Bank & Dedup

| Scenario | Screen | When | What Shows | Outcome |
|----------|--------|------|------------|---------|
| **Penny Drop Failed** | 6 | Bank credit failed | Bottom sheet: "Change Bank Details" / "Upload Bank Document" | Retryable |
| **Name Mismatch** | 6 | Bank holder name differs from KYC | Bottom sheet with name comparison: "Change Bank Details" / "Upload Bank Document" | Retryable |
| **Bank Account Dedup** | 6 | Account already registered | Bottom sheet: "Change Bank Details" only (deferred — triggers after Verify) | Blocked |

#### Category 6: Onboarding Fee (Rs.20K)

| Scenario | Screen | When | What Shows | Outcome |
|----------|--------|------|------------|---------|
| **Rs.20K Success** | 12 | Payment succeeds | Auto-move to Screen 13 | Success |
| **Rs.20K Failed** | 12 | Payment declined | Retry + "Talk to Us" (7836811111) | Retryable |
| **Rs.20K Timeout** | 12 | Gateway timeout | Refresh + "Talk to Us" (7836811111) | Retryable |

#### Category 7: Account Setup

| Scenario | Screen | When | What Shows | Outcome |
|----------|--------|------|------------|---------|
| **Setup Failed** | 13 | Backend setup error | Retry + "Talk to Us" (7836811111) | Retryable |
| **Setup Pending** | 13 | Setup taking longer | Refresh + "Talk to Us" (7836811111) | Retryable |

#### Category 8: Agreement & Tech Assessment

| Scenario | Screen | When | What Shows | Outcome |
|----------|--------|------|------------|---------|
| **Verification Pending** | 9 | Review in progress | Waiting status with checklist | Informational |
| **Verification Rejected** | 9 | QA rejects application | "Verification Rejected" + auto refund flow (no re-upload) | Terminal |
| **Refund In Progress (VR)** | 9 | Refund processing after rejection | Refund progress status | Informational |
| **Policy & SLA** | 10 | Displaying terms | Policy and SLA content | Normal flow |
| **Tech Assessment Rejected** | 11 | Infrastructure/network fails | "Profile not accepted yet" + "No refund at this moment" + "Talk to Us" (7836811111) | Blocked |

#### Category 9: Payment Failures

Rs.20K payment failure scenarios are documented under Category 6 (Onboarding Fee).

### Removed Scenarios (from V3.0)

| Old Scenario | Old ID | Why Removed |
|-------------|--------|-------------|
| PAN Name Mismatch | kyc-pan-mismatch | Replaced by dedup check model |
| Aadhaar Expired | kyc-aadhaar-expired | No longer in flow |
| ISP Doc Invalid | isp-doc-invalid | No longer a separate error |
| Dedup Match Found | dedup-found | Replaced by bank-dedup |
| Training Quiz Fail | training-quiz-fail | Training screen removed |
| Policy Quiz Fail | policy-quiz-fail | Quiz screen removed |

---

## 7. Empty States & Edge Cases

### Empty States

| Screen | Empty State | Behavior |
|--------|-------------|----------|
| 0 | No phone + T&C unchecked | CTA disabled |
| 0 | 10 digits + T&C unchecked | CTA disabled |
| 0 | <10 digits + T&C checked | CTA disabled |
| 1 | No OTP digits | CTA disabled |
| 2 | Missing any field | CTA disabled |
| 3 | No state selected | CTA disabled |
| 5 (PAN) | PAN number empty or invalid | Cannot proceed to upload |
| 5 (Aadhaar) | Aadhaar number empty or invalid | Cannot proceed to upload |
| 5 (GST) | GST number empty or invalid | Cannot proceed to upload |
| 6 | Any of 3 fields empty | Verify button disabled |
| 6 | Account numbers don't match | Verify button disabled |
| 7 | No ISP document uploaded | CTA disabled |
| 8 | Missing shop front photo | CTA disabled |
| 8 | Missing equipment photos | CTA disabled |

### Edge Cases

| Case | Expected Behavior |
|------|-------------------|
| Phone >10 digits | Error shown, extra digits blocked |
| T&C checkbox toggled | CTA enables/disables accordingly |
| PAN entered while typing (not blurred) | No error shown yet |
| PAN blurred with invalid format | Error shown on blur |
| GST chars 3-12 don't match PAN | Error shown on blur |
| Bank account number on blur | Field masks to type=password |
| Re-enter account number mismatch | Error shown |
| Penny drop fails | Bottom sheet with Change / Upload options |
| Bank dedup found | Bottom sheet with Change only (no upload option) |
| Verification rejected | Auto refund flow, no re-upload |
| Tech assessment rejected | No refund, Talk to Us CTA |
| Account setup takes >3s | Shows pending state |
| Navigate back then forward | All data preserved |
| Scenario trigger then clear | Returns to happy path |

---

## 8. Dashboard System (2 Dashboards)

### Dashboard 1: Control Dashboard (`dashboard/control.html`)

**Purpose:** Admin tool for navigating, testing, and controlling the app.

**Sections:**
1. **Control Buttons** — Restart App, Reset, Hindi/English toggle, Fill/Empty data, Screenshot
2. **Screen Navigation** — Grid of 16 screen tiles (Pitch + 0-14), active screen highlighted
3. **Scenario Simulator** — 28 error scenario buttons grouped by 8 categories, with "Clear Scenario"

### Dashboard 2: QA Review Dashboard (`dashboard/qa-review.html`)

**Purpose:** QA team tool for reviewing and deciding on CSP applications.

**Layout:** Split panel — Application List (left) + Detail View (right)

**Left Panel — Application List:**
- Filter chips: All | Pending | Approved | Rejected (with counts)
- Search by name, phone, city
- Each card: Name, Phone, City, Status badge, KYC count, time ago
- "LIVE" badge on emulator-connected application

**Right Panel — Application Detail (on click):**
- Applicant name, ID, trade name, submission date
- **Approve/Reject CTAs** (for Pending applications)
- **Mandatory reason selection** on Reject
- Current decision badge + "Change Decision" button (for decided applications)
- Summary strip: Phone, Entity Type, KYC count, Rs.2K status, City
- Collapsible sections: Personal Info, Location, KYC Documents (with view option), Registration Fee

**Key Features:**
- Decisions persist in localStorage
- Reversible — any decision can be changed back to Pending
- Live device sends Approve/Reject to emulator via bridge
- Auto-refreshes live app data every 5 seconds

---

## 9. Dashboard-App Interaction

| Dashboard Action | Intent/Endpoint | App Receiver |
|-----------------|----------------|--------------|
| Navigate to screen | `com.wiom.csp.NAVIGATE` (screen number) | `DashboardReceiver` |
| Navigate to Pitch | `com.wiom.csp.NAVIGATE` (screen: -1) | `DashboardReceiver` |
| Trigger scenario | `com.wiom.csp.SCENARIO` (scenario name) | `DashboardReceiver` |
| Change language | `com.wiom.csp.LANG` (hi/en) | `DashboardReceiver` |
| Reset state | `com.wiom.csp.RESET` | `DashboardReceiver` |
| Fill/Empty forms | `com.wiom.csp.FILL` (filled/empty) | `DashboardReceiver` |
| QA decision | `com.wiom.csp.QA` (approved/rejected + reason) | `DashboardReceiver` |
| Dump state | `com.wiom.csp.DUMP_STATE` | `DashboardReceiver` → writes state.json |
| Restart app | `adb shell am force-stop` + `am start` | Direct ADB |
| Screenshot | `adb exec-out screencap -p` | Direct ADB |
| Read state | `adb shell run-as com.wiom.csp cat state.json` | Direct ADB |

**Bridge Server:** Python HTTP server on port 8092. Uses `run-as` for reading app-private state files.

---

## 10. Business Rules & Constants

### Fee Structure

| Fee | Amount | Refundable? | When |
|-----|--------|-------------|------|
| Registration | Rs.2,000 | Yes (if verification rejects) | Screen 4 |
| Onboarding | Rs.20,000 | No | Screen 12 |
| **Total** | **Rs.22,000** | | |

### Commission Structure

| Type | Amount | Frequency |
|------|--------|-----------|
| New Connection | Rs.300 | Per event |
| Recharge | Rs.300 | Per event |
| Payout | Bank transfer | Every Monday by 10 AM |

### Service Levels to be Maintained

| Metric | Requirement |
|--------|-------------|
| Complaint Resolution | 4 hours |
| Connection Uptime | 95%+ |
| Equipment Care | Partner responsibility |
| Brand Compliance | Mandatory |

### Refund Policy

| Scenario | Refund | Timeline |
|----------|--------|----------|
| Verification Rejected | Rs.2,000 (auto refund) | Shown in app |
| Payment Timeout | Full amount | Auto-refund |
| Tech Assessment Rejected | No refund of Rs.2,000 | "Talk to Us" for support |

### Help Contact

| Channel | Number |
|---------|--------|
| Talk to Us (throughout app) | 7836811111 |

---

## 11. Validation Rules

| Field | Regex / Rule | Length | Notes |
|-------|-------------|-------|-------|
| Phone | `[0-9]{10}` | 10 digits | Numeric only |
| OTP | `[0-9]{4}` | 4 digits | Auto-focus |
| PAN | `/^[A-Z]{5}[0-9]{4}[A-Z]$/` | 10 chars | e.g., ABCDE1234F. Validated on blur. |
| Aadhaar | `/^[0-9]{12}$/` | 12 digits | Displayed as 4-4-4 format. Validated on blur. |
| GST | `/^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][0-9A-Z]{3}$/` | 15 chars | Chars 3-12 must match PAN. Validated on blur. |
| Bank Account | `[0-9]{9,18}` | 9-18 digits | Numeric only. Masked (type=password) on blur. Double entry must match. |
| IFSC | `/^[A-Z]{4}0[A-Z0-9]{6}$/` | 11 chars | 5th char is always 0 |
| Pincode | `[0-9]{6}` | 6 digits | Numeric only |
| Email | Must contain `@` and `.` | — | Basic validation |
| Name | Not blank | — | Required |

**Validation timing:** All KYC number fields (PAN, Aadhaar, GST) show errors **only on blur**, not while the user is typing.

---

## 12. Design System Reference

### Colors

| Token | Hex | Usage |
|-------|-----|-------|
| Primary | `#D9008D` | CTAs, brand accent |
| Primary Light | `#FFE5F6` | Backgrounds |
| Text | `#161021` | Body text |
| Text Secondary | `#665E75` | Labels |
| Hint | `#A7A1B2` | Placeholders |
| Surface | `#FAF9FC` | Screen backgrounds |
| Positive | `#008043` | Success, verified |
| Negative | `#D92130` | Errors, rejected |
| Warning | `#FF8000` | Pending, caution |
| Info | `#6D17CE` | Info boxes |
| Header | `#443152` | Status bar, app header |

### Corner Radii
- Small: 8dp (tags)
- Medium: 12dp (inputs)
- Large: 16dp (cards, buttons)
- Pill: 888dp (chips, badges)

---

## 13. QA Test Cases

### Happy Path

| ID | Test | Expected |
|----|------|----------|
| HP-01 | Complete full onboarding (15 screens) | Partner reaches "Successfully Onboarded" screen |
| HP-02 | Language toggle on every screen | All text switches Hindi/English |
| HP-03 | T&C checkbox toggle | CTA enables/disables correctly |
| HP-04 | PAN number entry + blur | Validation fires on blur, not while typing |
| HP-05 | Aadhaar number formatting | Displays as 4-4-4 (1234 5678 9012) |
| HP-06 | GST chars 3-12 match PAN | Accepted without error |
| HP-07 | GST chars 3-12 don't match PAN | Error shown on blur |
| HP-08 | Bank account masked on blur | Field changes to type=password |
| HP-09 | Bank re-entry matches | Verify button enabled |
| HP-10 | Penny drop success | Verified delight screen → next |
| HP-11 | ISP multi-page upload (7 photos) | All pages captured and shown |
| HP-12 | Equipment multi-photo (5 photos) | All photos captured and shown |
| HP-13 | View sample document on all KYC screens | Sample image displayed |
| HP-14 | Verification approved → Policy & SLA | Correct navigation |
| HP-15 | Tech assessment passed → Onboarding fee | Correct navigation |
| HP-16 | Account setup auto-progress | Screen auto-advances after 3 seconds |
| HP-17 | Successfully onboarded screen | Shows congratulations + download app + instructions |

### Error Scenarios

| ID | Scenario | Expected |
|----|----------|----------|
| ERR-01 | No Internet | No internet error overlay |
| ERR-02 | Server Error | Server error overlay |
| ERR-03 | Phone Duplicate | Error card, blocked |
| ERR-04 | Wrong OTP | Red boxes, attempt counter |
| ERR-05 | OTP Expired | Faded boxes, resend |
| ERR-06 | Rs.2K Failed | "No money deducted" + retry |
| ERR-07 | Rs.2K Timeout | Pending + refresh |
| ERR-08 | PAN Dedup | Dedup overlay on blur |
| ERR-09 | Aadhaar Dedup | Dedup overlay on blur |
| ERR-10 | GST Dedup | Dedup overlay on blur |
| ERR-11 | Penny Drop Failed | Bottom sheet: Change / Upload Doc |
| ERR-12 | Name Mismatch | Bottom sheet: name comparison + Change / Upload Doc |
| ERR-13 | Bank Account Dedup | Bottom sheet: Change only |
| ERR-14 | Verification Rejected | Auto refund flow, no re-upload |
| ERR-15 | Tech Assessment Rejected | "No refund" + Talk to Us |
| ERR-16 | Rs.20K Failed | Retry + Talk to Us |
| ERR-17 | Rs.20K Timeout | Refresh + Talk to Us |
| ERR-18 | Account Setup Failed | Retry + Talk to Us |
| ERR-19 | Account Setup Pending | Refresh + Talk to Us |
| ERR-20 | Refund Success | Refund confirmation shown |
| ERR-21 | Refund In Progress | Progress status shown |
| ERR-22 | Refund Failed | Error + contact support |
| ERR-23 | Day 1-4 Reg Fee Nudges | Reminder nudges shown |

---

## 14. UAT Test Cases

| ID | Persona | Scenario | Acceptance Criteria |
|----|---------|----------|---------------------|
| UAT-01 | Rajesh, Indore, Individual | Happy path (15 screens) | Reaches "Successfully Onboarded" with download + instructions |
| UAT-02 | Anil, PAN dedup | KYC PAN already exists | Dedup overlay shown on blur after valid PAN entry |
| UAT-03 | Sunita, Aadhaar dedup | KYC Aadhaar already exists | Dedup overlay shown on blur |
| UAT-04 | Deepak, bank dedup | Bank account already exists | Bottom sheet with "Change Bank Details" only |
| UAT-05 | Mohit, penny drop fail | Bank verification fails | Bottom sheet with Change / Upload Doc options |
| UAT-06 | Priya, name mismatch | Bank holder name differs | Bottom sheet with name comparison |
| UAT-07 | Kavita, verification rejected | QA rejects application | Auto refund flow, no re-upload |
| UAT-08 | Ravi, tech assessment rejected | Infrastructure fails | "No refund" + Talk to Us (7836811111) |
| UAT-09 | Neha, Rs.20K failed | Payment declined | Retry + Talk to Us |
| UAT-10 | Hindi speaker | Hindi-first UX | All text culturally appropriate |
| UAT-11 | QA reviewer | Dashboard QA workflow | Review → Approve/Reject with reason |
| UAT-12 | Admin | Control dashboard | Navigate all 16 screens, trigger 28 scenarios |

---

## 15. What's Prototype vs Production

| Feature | Prototype (Current) | Production (Needed) |
|---------|---------------------|---------------------|
| OTP | Simulated (any 4 digits) | Real SMS/WhatsApp OTP |
| KYC Validation | Regex only, dedup simulated | Real PAN/Aadhaar/GST API verification + dedup |
| KYC Upload | Simulated progress | Real camera/gallery + OCR |
| Payments | 2-second delay | Real Razorpay gateway |
| Penny Drop | Simulated outcomes | Real Rs.1 bank credit |
| Bank Dedup | Simulated | Real database cross-reference |
| QA Review | Dashboard + localStorage | Backend queue + admin panel + database |
| ISP Agreement | Simulated multi-page upload | Real document upload + compliance verification |
| Tech Assessment | Simulated check | Real infrastructure + network assessment |
| Account Setup | 3-second auto-progress | Real RazorpayX + Zoho + ledger APIs |
| GPS | Hardcoded coordinates | FusedLocationProvider |
| T&C | Checkbox only | Real T&C document + versioning |
| State Dropdown | 36 states | API-driven with city/pincode lookup |
| Sample Documents | Static images | API-served latest samples |
| Help Number | Hardcoded 7836811111 | Dynamic from config |
| State Management | In-memory singleton | Room + DataStore |
| Architecture | No ViewModel | MVVM + Hilt + Repository |
| Backend | bridge.py (ADB) | REST API + auth + push notifications |
| Analytics | None | Event tracking + funnel analysis |

---

*This document covers the complete specification of the Wiom CSP Onboarding App V3.1 — 15 screens + Pitch, 28 error scenarios across 8 categories, 2 dashboards, business rules, validation, design tokens, QA cases, and UAT cases.*
