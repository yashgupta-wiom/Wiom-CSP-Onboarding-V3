# Onboarding Service (ONB) — Product Input Answers

**Version:** V0 (Phase 1)
**Date:** 2026-03-30
**Author:** Yash Gupta
**Source PRD:** Wiom CSP Onboarding App — Complete PRD V3.2

---

## Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                     PHASE 1: REGISTRATION                           │
│                                                                     │
│  Screen 0          Screen 1         Screen 2         Screen 3      │
│  Phone Entry  ───► OTP Verify  ───► Personal &  ───► Business      │
│  (+91, 10 digits)  (4-digit,        Business Info     Location     │
│                     max 3 tries)     (Name, Email,    (State, City, │
│  ▲ PHONE DEDUP     ▲ AUTO           Entity, Biz      Pincode, GPS) │
│    [automated]       [automated]     Name)                          │
│                                                                     │
│                                      Screen 4                       │
│                                      Reg Fee ──────────────────┐    │
│                                      Rs.2,000 (refundable)     │    │
│                                      ▲ PAYMENT [automated]     │    │
│                                      Locks business name       │    │
└────────────────────────────────────────────────────────────┼────┘
                                                             │
                                                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     PHASE 2: VERIFICATION                           │
│                                                                     │
│  Screen 5: KYC Documents (3 sequential sub-stages)                  │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐            │
│  │ PAN          │──►│ Aadhaar      │──►│ GST          │            │
│  │ Number+Photo │   │ Number+Front │   │ Number+Photo │            │
│  └──────────────┘   │ +Back        │   │ ▲ PAN MATCH  │            │
│                     └──────────────┘   └──────┬───────┘            │
│                                               │                     │
│                               │                                     │
│                               ▼                                     │
│  Screen 6                Screen 7               Screen 8            │
│  Bank Details ─────────► ISP Agreement ────────► Shop & Equipment   │
│  (Acct, IFSC)            (PDF/7 photos)          Photos (1 shop +  │
│  + mandatory bank doc    No checks               up to 5 equipment)│
│  ▲ BANK DEDUP                                    No checks          │
│    [automated]                                                      │
│                                                                     │
│  Screen 9: Verification Status                                      │
│  ┌──────────────────────────────────────────────────────┐           │
│  │               ★ MANUAL GATE 1: QA REVIEW ★           │           │
│  │  QA team reviews all docs via QA Review Dashboard    │           │
│  │  TAT: up to 3 business days                          │           │
│  │                                                      │           │
│  │  APPROVED ──► proceed to Tech Assessment (Screen 10)  │           │
│  │  REJECTED ──► auto-refund Rs.2,000 → flow ends       │           │
│  │               (no re-upload in V0)                   │           │
│  └──────────────────────────────────────────────────────┘           │
└─────────────────────────────────────────────────────────────────────┘
                              │
                    (if APPROVED)
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     PHASE 3: ACTIVATION                             │
│                                                                     │
│  Screen 10                 Screen 11                                │
│  Tech Assessment ────────► Policy & SLA                              │
│  ┌──────────────────────────────────┐   (Commission: Rs.300,        │
│  │  ★ MANUAL GATE 2: TECH REVIEW ★ │    Payout: Mon 10AM,          │
│  │  Network Quality team            │    SLA: 4hr/95%+)             │
│  │  TAT: 4–5 business days          │   Self-serve acceptance       │
│  │                                  │                                │
│  │  PASSED ──► proceed to Policy    │                                │
│  │  REJECTED ──► no refund,         │                                │
│  │    "Talk to Us" (7836811111)     │                                │
│  │    Flow ends.                    │                                │
│  └───────────────┬──────────────────┘                                │
│                  │                                                   │
│                  ▼                                                   │
│  Screen 12                  Screen 13            Screen 14          │
│  Onboarding Fee ──────────► Account Setup ──────► Success!          │
│  Rs.20,000                  (RazorpayX, Zoho,    "Congratulations"  │
│  (non-refundable)           Ledger creation)     Download Partner   │
│  ▲ PAYMENT [automated]      Auto-advance 3s      Plus App          │
│                              ▲ AUTO [automated]                     │
└─────────────────────────────────────────────────────────────────────┘
                              │
                    (CSP downloads Partner Plus App)
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                  POST-ONBOARDING (Partner Plus App)                  │
│                                                                     │
│  Login to Partner Plus App                                          │
│       ↓                                                             │
│  Complete Mandatory Training Modules                                │
│       ↓                                                             │
│  Complete Quiz Modules                                              │
│       ↓                                                             │
│  ╔═══════════════════════════════════════════════════════╗           │
│  ║  🔔 CSP_ONBOARDED event fires                        ║           │
│  ║  CSP is now ELIGIBLE for routing in DAS and CCS      ║           │
│  ╚═══════════════════════════════════════════════════════╝           │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Question 1: What are the onboarding stages?

**Three phases, 15 screens + post-app training:**

| # | Stage | Screens | Description |
|---|-------|---------|-------------|
| 1 | **Registration** | 0–4 | Phone verification, personal/business info, location, registration fee (Rs.2,000) |
| 2 | **Verification** | 5–9 | KYC document submission (PAN → Aadhaar → GST), bank details, ISP agreement, shop/equipment photos, QA review |
| 3 | **Activation** | 10–14 | Technical assessment, Policy & SLA acceptance, onboarding fee (Rs.20,000), account setup, success screen |
| 4 | **Post-Onboarding** | Partner Plus App | Download app → login → complete training → complete quiz → CSP_ONBOARDED |

**State machine progression:**

```
PHONE_ENTERED → OTP_VERIFIED → PROFILE_SUBMITTED → LOCATION_SUBMITTED
→ REG_FEE_PAID → KYC_SUBMITTED → BANK_DOC_UPLOADED → ISP_AGREEMENT_UPLOADED
→ PHOTOS_UPLOADED → VERIFICATION_UNDER_REVIEW
→ VERIFICATION_APPROVED / VERIFICATION_REJECTED
→ TECH_ASSESSMENT_PENDING → TECH_ASSESSMENT_PASSED / TECH_ASSESSMENT_REJECTED
→ POLICY_ACCEPTED
→ ONBOARDING_FEE_PAID → ACCOUNT_SETUP_COMPLETE → APP_DOWNLOADED
→ TRAINING_COMPLETE → QUIZ_COMPLETE → CSP_ONBOARDED
```

---

## Question 2: What checks happen at each stage?

| Stage | Check | Type |
|-------|-------|------|
| Screen 0: Phone Entry | Phone Duplicate Check | Guard — blocks if number already exists |
| Screen 1: OTP | OTP Verification (4-digit, max 3 attempts) | Guard — blocks on 3 failures |
| Screen 5: KYC — PAN | PAN Format Validation (regex on blur) | Guard — blocks on invalid format |
| Screen 5: KYC — Aadhaar | Aadhaar Format Validation (12 digits, on blur) | Guard — blocks on invalid format |
| Screen 5: KYC — GST | GST Format Validation + PAN Match (chars 3–12, on blur) | Guard — blocks on invalid/mismatch |
| Screen 6: Bank Details | Bank Account Dedup (on 'Add Bank Document' tap) | Guard — blocks if account linked to another CSP |
| Screen 4: Reg Fee | Payment Processing (Rs.2,000) | Guard — blocks on failure/timeout |
| Screen 9: Verification | QA Manual Review (via QA Dashboard) | Gate — approve or reject |
| Screen 10: Tech Assessment | On-ground/phone assessment by Network Quality team | Gate — pass or reject |
| Screen 12: Onboarding Fee | Payment Processing (Rs.20,000) | Guard — blocks on failure/timeout |
| Screen 13: Account Setup | RazorpayX + Zoho + Ledger account creation | Guard — blocks on setup failure |
| Partner Plus App | Training Module Completion | Gate — must complete all modules |
| Partner Plus App | Quiz Module Completion | Gate — must pass quiz |

**Screens with NO checks (data collection / display only):** Screen 2 (Personal Info), Screen 3 (Location), Screen 7 (ISP Agreement upload), Screen 8 (Photos upload), Screen 11 (Policy & SLA), Screen 14 (Success).

---

## Question 3: Which checks are automated vs manual?

| Check | Automated or Manual | Details |
|-------|-------------------|---------|
| Phone Duplicate | Automated (system event) | Instant backend lookup |
| OTP Verification | Automated (system event) | SMS/WhatsApp delivery + match |
| PAN/Aadhaar/GST Format Validation | Automated (client-side) | Regex validation on blur |
| Bank Account Dedup | Automated (system event) | Backend lookup on 'Add Bank Document' tap |
| Registration Fee Payment | Automated (system event) | Razorpay gateway |
| Onboarding Fee Payment | Automated (system event) | Razorpay gateway |
| Account Setup | Automated (system event) | RazorpayX + Zoho + Ledger APIs |
| Refund on Verification Rejection | Automated (system event) | Auto-triggered, no human step |
| **QA / Verification Review** | **Manual** (ops task + confirmation) | QA team reviews via dashboard → Approve/Reject with mandatory reason |
| **Technical Assessment** | **Manual** (ops task + confirmation) | Network Quality team conducts assessment, TAT 4–5 business days |
| **Training Completion** | **Manual** (CSP self-serve) | CSP completes modules in Partner Plus App |
| **Quiz Completion** | **Manual** (CSP self-serve) | CSP completes quiz in Partner Plus App |

**Summary: 8 automated checks, 2 ops-manual gates (QA Review + Tech Assessment), 2 CSP-manual steps (Training + Quiz).**

---

## Question 4: What documents does the CSP submit?

| # | Document | Stage | Required | Format | Notes |
|---|----------|-------|----------|--------|-------|
| 1 | **PAN Card** | Screen 5, Sub-stage 1 | Mandatory | Number entry + 1 photo (camera/gallery) | Format validation on blur |
| 2 | **Aadhaar Card** | Screen 5, Sub-stage 2 | Mandatory | Number entry + front photo + back photo | 12 digits, formatted 4-4-4, format validation on blur |
| 3 | **GST Certificate** | Screen 5, Sub-stage 3 | Mandatory | Number entry + 1 photo | Chars 3–12 must match PAN, format validation on blur |
| 4 | **Bank Proof** | Screen 6 | Mandatory | Bank Statement OR Cancelled Cheque OR Bank Passbook | Required for all CSPs |
| 5 | **ISP Agreement** | Screen 7 | Mandatory | PDF or up to 7 photos (camera/gallery) | Must show: ISP name, partner name, date, validity, license #, signatories, stamp & signature |
| 6 | **Shop Front Photo** | Screen 8 | Mandatory | 1 image | — |
| 7 | **Equipment Photos** | Screen 8 | Mandatory | Up to 5 images | Must include: Power Backup, OLT, ISP Switch |

**Total: 7 mandatory document types.**

---

## Question 5: What is the approval flow?

**Hybrid: mostly automated with 2 manual gates.**

```
AUTOMATED ZONE (Registration)
  Phone dedup → OTP → Profile → Location → Reg fee payment
  All system-driven. No human approval needed.
         │
AUTOMATED ZONE (Document Collection)
  Bank dedup check
  All system-driven. CSP is blocked or proceeds automatically.
         │
  ★ MANUAL GATE 1: QA REVIEW (Screen 9) ★
  │  Who: QA team via QA Review Dashboard
  │  Action: Review all submitted documents
  │  Outcomes:
  │    APPROVED → CSP proceeds to Activation phase
  │    REJECTED → Auto-refund Rs.2,000, flow ends (no re-upload in V0)
  │  Rejection requires mandatory reason selection
  │  TAT: Up to 3 business days
         │
  ★ MANUAL GATE 2: TECH ASSESSMENT (Screen 10) ★
  │  Who: Network Quality team
  │  Action: Assess infrastructure, network readiness, location feasibility
  │  Outcomes:
  │    PASSED → CSP proceeds to Policy & SLA
  │    REJECTED → No refund, "Talk to Us" only, flow ends
  │  TAT: 4–5 business days
         │
SELF-SERVE (Policy Acceptance — Screen 11)
  CSP reads and accepts Policy & SLA. No approval needed.
         │
AUTOMATED ZONE (Activation)
  Onboarding fee payment → Account setup → Success screen
  All system-driven.
         │
CSP SELF-SERVE (Post-Onboarding)
  Download app → Login → Training → Quiz → CSP_ONBOARDED
```

**There is no auto-approve path.** Every CSP must pass through both manual gates.

**Note — Registration Fee nudge reminders:** If the CSP does not pay the Rs.2,000 registration fee (Screen 4), the system sends Day 1–4 nudge reminder notifications. After Day 4, the application goes dormant. This is a time-based automated trigger that the state machine must handle.

---

## Question 6: What happens on rejection?

### V0 Rejection Policy

| Rejection Point | Refund | Can CSP Retry? | What CSP Sees |
|----------------|--------|---------------|---------------|
| **QA Review Rejection** (Screen 9) | Yes — Rs.2,000 auto-refund | **No.** No re-upload in V0. Flow ends. | "Verification Rejected" → Refund status shown (In Progress → Success/Failed) |
| **Tech Assessment Rejection** (Screen 10) | **No refund** | **No.** Flow ends. | "Profile not accepted yet" + "No refund will be done at this moment" + "Talk to Us" (7836811111) |

### Refund states after QA Rejection:
```
REJECTED → REFUND_IN_PROGRESS → REFUND_SUCCESS
                               → REFUND_FAILED (support contact shown)
```

### V0 Scope Limitations:
- **No retry mechanism** — rejected CSPs cannot re-apply in V0
- **No re-upload** — rejected documents cannot be corrected and resubmitted
- **No cooloff period** — not applicable since retry doesn't exist
- **No attempt limits** — not applicable since retry doesn't exist
- **Mobile number dedup** prevents a rejected CSP from starting a fresh application with the same number

These will be addressed in future versions.

---

## Question 7: Is fresh onboarding different from re-entry onboarding?

### V0: Only fresh onboarding exists.

| Aspect | Fresh Onboarding (V0) | Re-entry Onboarding |
|--------|----------------------|---------------------|
| **Status** | Live — full 15-screen flow + post-app training | **Not in V0** |
| **Trigger** | New CSP applying for first time | N/A in V0 |
| **Blocking mechanism** | Mobile number dedup prevents any previously registered CSP from re-onboarding | — |
| **Future plan** | — | Will use Exclusion Policy v1.0 (permanent ban for VIO-04/08, identity linkage, liability checks, cooloff via P133) |
| **Future events** | — | CSP_REENTRY_EVALUATED → CSP_REENTRY_APPROVED / CSP_REENTRY_DENIED |
| **Future trigger** | — | EXIT_COMPLETED from Exit OS |

**For V0 build spec:** The ONB state machine only needs to handle the fresh onboarding path. Re-entry will be a separate path added in a future version. The existing re-entry events and parameters (documented in Exclusion Policy v1.0) are already defined and locked — they just won't be consumed by ONB yet.

---

## Question 8: What event marks "onboarding complete"?

### Terminal Event: `CSP_ONBOARDED`

This event does **not** fire when the CSP completes the 15-screen app flow. It fires later, after the CSP completes post-onboarding steps in the Partner Plus App.

### Full sequence to reach CSP_ONBOARDED:

```
Screen 14: Success screen shown in Onboarding App
    ↓
CSP downloads Wiom Partner Plus App
    ↓
CSP logs into Partner Plus App
    ↓
CSP allows all required permissions
    ↓
CSP completes all Mandatory Training modules
    ↓
CSP completes all Quiz modules
    ↓
═══════════════════════════════════════════
  CSP_ONBOARDED event fires
  CSP is now ELIGIBLE for customer routing
═══════════════════════════════════════════
```

### What downstream services consume this event:
- **DAS (Demand Allocation Service):** Adds CSP to the eligible pool for demand routing
- **CCS (Customer Connection Service):** Adds CSP to the eligible pool for customer connections

### Pre-conditions (all must be true before CSP_ONBOARDED):
1. Registration fee (Rs.2,000) — paid
2. KYC documents (PAN, Aadhaar, GST) — submitted
3. Bank details — submitted + bank document uploaded
4. ISP Agreement — uploaded
5. Shop & Equipment photos — uploaded
6. QA Review — approved
7. Tech Assessment — passed
8. Policy & SLA — accepted
9. Onboarding Fee (Rs.20,000) — paid
10. Account Setup — successful (RazorpayX + Zoho + Ledger)
11. Partner Plus App — downloaded and logged in
12. Training modules — completed
13. Quiz modules — completed

### System handoff:
- **Onboarding App** (Screens 0–14) gets the CSP to "approved, paid, and set up"
- **Partner Plus App** handles training, quiz, and fires `CSP_ONBOARDED` upon full completion

---

## V0 Scope Summary

| Feature | In V0? | Notes |
|---------|--------|-------|
| Fresh onboarding (15 screens) | Yes | Full flow as described |
| Post-onboarding training + quiz | Yes | In Partner Plus App |
| CSP_ONBOARDED terminal event | Yes | Fires after training + quiz |
| QA Review (manual gate) | Yes | Via QA Review Dashboard |
| Tech Assessment (manual gate) | Yes | By Network Quality team |
| Auto-refund on QA rejection | Yes | Rs.2,000 |
| Re-upload after rejection | No | Future version |
| Retry after rejection | No | Future version |
| Re-entry onboarding | No | Future version (Exclusion Policy v1.0 rules ready) |
| Cooloff periods | No | Future version |
| Attempt limits | No | Future version |
