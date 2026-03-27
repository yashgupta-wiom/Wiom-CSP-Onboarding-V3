# Wiom CSP Onboarding App — Product Requirements Document (AI-Agent Format)

> **Version:** 3.1
> **Date:** 2026-03-26
> **Status:** Prototype (hardcoded data, no backend)
> **Package:** `com.wiom.csp`
> **Repos:**
> - https://github.com/ashishagrawal-iam/Wiom-csp-onboarding-v2
> - https://github.com/vikashPD/Wiom-CSP-Dashboards

---

## SYSTEM_OVERVIEW

```yaml
product: Wiom CSP Onboarding App
purpose: End-to-end onboarding flow for new Channel Sales Partners (CSPs)
platform: Android (Kotlin + Jetpack Compose + Material3)
min_sdk: 24 (Android 7.0)
target_sdk: 35
architecture: Single-activity, composable screens, no ViewModel (prototype)
navigation: AnimatedContent keyed on currentScreen integer (Pitch + 0-14, 16 screens total)
language: Bilingual Hindi/English via runtime t(hi, en) toggle
state_management: Global singleton OnboardingState with mutableStateOf
build: Gradle 8.11.1 (Kotlin DSL), Kotlin 2.1.0
```

---

## ACTORS

```yaml
actors:
  - id: PARTNER
    description: New CSP applicant going through onboarding
    actions: Fill forms, upload documents, make payments

  - id: QA_TEAM
    description: Wiom Business/QA team reviewing partner applications
    actions: Approve or reject applications with reasons after documentation phase
    interface: QA Review Dashboard (dashboard/qa-review.html)

  - id: SYSTEM
    description: Automated backend processes
    actions: OTP generation, KYC auto-verify, penny drop, dedup check, account setup

  - id: ADMIN
    description: Dashboard operator
    actions: Navigate screens, trigger scenarios, control language
    interface: Control Dashboard (dashboard/control.html)
```

---

## FLOW_PHASES

```yaml
phases:
  - id: PHASE_1
    name: Registration
    screens: [0, 1, 2, 3, 4]
    description: Partner identity capture, location, and registration fee collection

  - id: PHASE_2
    name: Verification
    screens: [5, 6, 7, 8, 9]
    description: KYC documents, bank verification, ISP agreement, shop photos, verification status
    header: "Verification"

  - id: PHASE_3
    name: Activation
    screens: [10, 11, 12, 13, 14]
    description: Policy & SLA, technical assessment, onboarding fee, account setup, successfully onboarded
```

---

## SCREENS

### PITCH: Pitch Screen

```yaml
id: PITCH
phase: PRE_FLOW
title_hi: "Wiom पार्टनर बनें"
title_en: "Become a Wiom Partner"
branding: "Wiom Partner+"
purpose: Introduce Wiom partnership opportunity and benefits before registration begins

cta:
  text_hi: "शुरू करें"
  text_en: "Get Started"
  next_screen: 0
```

### SCREEN_0: Phone Entry

```yaml
id: SCREEN_0
phase: PHASE_1
title_hi: "मोबाइल नंबर"
title_en: "Mobile Number"
step_label: null
purpose: Capture partner phone number for OTP-based authentication

fields:
  - id: phone_number
    type: text_input
    prefix: "+91"
    max_length: 10
    keyboard: numeric
    placeholder_hi: "10 अंकों का नंबर"
    placeholder_en: "10 digit number"
    validation:
      - rule: not_blank
        error_hi: "नंबर डालें"
        error_en: "Enter phone number"
      - rule: length_exact(10)
        error_hi: "10 अंकों का नंबर डालें"
        error_en: "Enter 10-digit number"
      - rule: digits_only
        error_hi: "केवल अंक डालें"
        error_en: "Enter digits only"
      - rule: length_max(10)
        error_hi: "केवल 10 अंकों का नंबर डालें"
        error_en: "Only 10-digit numbers allowed"

  - id: termsAccepted
    type: checkbox
    default: true
    text_hi: "आगे बढ़कर, मैं नियम व शर्तें स्वीकार करता/करती हूँ"
    text_en: "By Continuing, I accept the Terms and Conditions"
    link:
      text_hi: "नियम व शर्तें पढ़ें"
      text_en: "Read Terms & Conditions"
      action: OPEN_TERMS_WEBPAGE

display_elements:
  - emoji: "🤝"
  - title_hi: "Wiom पार्टनर बनें"
  - title_en: "Become a Wiom Partner"
  - subtitle_hi: "Wiom के साथ अपना बिज़नेस शुरू करें"
  - subtitle_en: "Start your business with Wiom"
  - character_count: "X/10 अंक" (shown when < 10 digits)
  - info_box:
      icon: "🔒"
      text_hi: "OTP आपके नंबर पर भेजा जाएगा"
      text_en: "OTP will be sent to your number"
      type: INFO

cta:
  text_hi: "OTP भेजें"
  text_en: "Send OTP"
  enabled_when: phone_number.length == 10 AND termsAccepted == true
  action: SEND_OTP
  next_screen: 1

error_scenarios:
  - PHONE_DUPLICATE
```

### SCREEN_0_ERROR: PHONE_DUPLICATE

```yaml
id: PHONE_DUPLICATE
trigger: Phone number already registered in system
screen: 0

display:
  emoji: "📱"
  title_hi: "यह नंबर पहले से रजिस्टर्ड है"
  title_en: "This number is already registered"

  error_card:
    type: error
    icon: "📱"
    title_hi: "अकाउंट मौजूद है"
    title_en: "Account Exists"
    message_hi: "इस नंबर से पहले से एक अकाउंट बना हुआ है। आप लॉगिन कर सकते हैं या नए नंबर से रजिस्टर कर सकते हैं।"
    message_en: "An account already exists with this number. You can login or register with a new number."

  info_box:
    icon: "🔒"
    text_hi: "आपका पुराना डेटा सुरक्षित है"
    text_en: "Your existing data is safe"
    type: SUCCESS

ctas:
  - text_hi: "नए नंबर से OTP भेजें"
    text_en: "Send OTP with new number"
    type: primary
    action: CLEAR_AND_RETRY
  - text_hi: "लॉगिन करें"
    text_en: "Login"
    type: secondary
    action: NAVIGATE_TO_LOGIN
```

### SCREEN_1: OTP Verification

```yaml
id: SCREEN_1
phase: PHASE_1
title_hi: "OTP सत्यापन"
title_en: "Verify OTP"
purpose: Verify phone ownership via 4-digit OTP

fields:
  - id: otp_digits
    type: otp_input
    length: 4
    keyboard: numeric
    auto_focus: true
    validation:
      - rule: all_digits_filled
        error_hi: "पूरा OTP डालें"
        error_en: "Enter complete OTP"

display_elements:
  - subtitle_hi: "+91 XXXXX XXXXX पर भेजा गया"
  - subtitle_en: "Sent to +91 XXXXX XXXXX"

timer:
  initial_seconds: 28
  countdown_interval_ms: 1000
  on_expire:
    show_resend_link: true
    resend_text_hi: "OTP दोबारा भेजें"
    resend_text_en: "Resend OTP"
    resend_action: RESTART_TIMER(28)
    show_change_number: true
    change_number_text_hi: "नंबर बदलें"
    change_number_text_en: "Change Number"
    change_number_action: GO_BACK

otp_box_states:
  empty: { border: dark, background: white }
  focused: { border: pink, background: white, cursor: blinking }
  filled: { border: green, background: green_light }
  error: { border: red, background: white }
  expired: { border: gray, background: white, opacity: 0.5 }

cta:
  text_hi: "सत्यापित करें"
  text_en: "Verify"
  enabled_when: all_4_digits_filled
  action: VERIFY_OTP
  next_screen: 2

error_scenarios:
  - OTP_WRONG
  - OTP_EXPIRED
```

### SCREEN_1_ERROR: OTP_WRONG

```yaml
id: OTP_WRONG
trigger: Incorrect OTP entered
screen: 1

display:
  otp_boxes: { state: error, border: red, content: empty }
  error_card:
    type: error
    icon: "❌"
    title_hi: "गलत OTP"
    title_en: "Wrong OTP"
    message_hi: "कृपया दोबारा कोशिश करें — 2 प्रयास बाकी हैं"
    message_en: "Please try again — 2 attempts remaining"
  resend_link:
    text_hi: "OTP दोबारा भेजें"
    text_en: "Resend OTP"

cta:
  text_hi: "सत्यापित करें"
  text_en: "Verify"
  action: RETRY_OTP

business_rules:
  max_attempts: 3
  lockout_after_max: true
```

### SCREEN_1_ERROR: OTP_EXPIRED

```yaml
id: OTP_EXPIRED
trigger: OTP validity period exceeded
screen: 1

display:
  otp_boxes: { state: expired, border: gray, opacity: 0.5 }
  error_card:
    type: warning
    icon: "⏰"
    title_hi: "OTP expired हो गया"
    title_en: "OTP has expired"
    message_hi: "चिंता न करें — नया OTP भेजें"
    message_en: "Don't worry — send a new OTP"

ctas:
  - text_hi: "नया OTP भेजें"
    text_en: "Send new OTP"
    type: primary
    action: RESEND_OTP
  - text_hi: "नंबर बदलें"
    text_en: "Change Number"
    type: secondary
    action: GO_BACK
```

### SCREEN_2: Personal & Business Info

```yaml
id: SCREEN_2
phase: PHASE_1
title_hi: "व्यक्तिगत और व्यापार जानकारी"
title_en: "Personal & Business Info"
step_label: "स्टेप 1/3 | Step 1/3"
purpose: Capture partner identity and business details

fields:
  - id: personal_name
    type: text_input
    label_hi: "पूरा नाम (आधार अनुसार)"
    label_en: "Full Name (as per Aadhaar)"
    placeholder_hi: "उदाहरण: राजेश कुमार"
    placeholder_en: "Example: Rajesh Kumar"
    required: true
    validation:
      - rule: not_blank
        error_hi: "नाम डालें"
        error_en: "Enter name"

  - id: personal_email
    type: email_input
    label_hi: "ईमेल आईडी"
    label_en: "Email ID"
    placeholder_hi: "उदाहरण: rajesh@email.com"
    placeholder_en: "Example: rajesh@email.com"
    required: true
    validation:
      - rule: not_blank
        error_hi: "ईमेल डालें"
        error_en: "Enter email"
      - rule: contains_at_and_dot
        error_hi: "सही ईमेल डालें"
        error_en: "Enter valid email"

  - id: entity_type
    type: dropdown
    label_hi: "व्यापार इकाई प्रकार"
    label_en: "Business Entity Type"
    options: ["प्रोप्राइटरशिप (Proprietorship)"]
    required: true

  - id: trade_name
    type: text_input
    label_hi: "व्यापार का नाम"
    label_en: "Business Name"
    placeholder_hi: "उदाहरण: राजेश टेलीकॉम"
    placeholder_en: "Example: Rajesh Telecom"
    required: true
    lock_after: REGFEE_PAID

cta:
  text_hi: "व्यापार स्थान जोड़ें"
  text_en: "Add Business Location"
  enabled_when: all_fields_filled
  next_screen: 3

error_scenarios: []
```

### SCREEN_3: Business Location

```yaml
id: SCREEN_3
phase: PHASE_1
title_hi: "व्यापार स्थान"
title_en: "Business Location"
step_label: "स्टेप 2/3 | Step 2/3"
purpose: Capture shop/office location for service area validation

fields:
  - id: state
    type: dropdown
    label_hi: "राज्य"
    label_en: "State"
    options:
      - "Andhra Pradesh"
      - "Arunachal Pradesh"
      - "Assam"
      - "Bihar"
      - "Chhattisgarh"
      - "Goa"
      - "Gujarat"
      - "Haryana"
      - "Himachal Pradesh"
      - "Jharkhand"
      - "Karnataka"
      - "Kerala"
      - "Madhya Pradesh"
      - "Maharashtra"
      - "Manipur"
      - "Meghalaya"
      - "Mizoram"
      - "Nagaland"
      - "Odisha"
      - "Punjab"
      - "Rajasthan"
      - "Sikkim"
      - "Tamil Nadu"
      - "Telangana"
      - "Tripura"
      - "Uttar Pradesh"
      - "Uttarakhand"
      - "West Bengal"
      - "Delhi"
      - "Jammu & Kashmir"
      - "Ladakh"
      - "Chandigarh"
      - "Puducherry"
    required: true

  - id: city
    type: text_input
    placeholder_hi: "शहर"
    placeholder_en: "City"
    required: true

  - id: pincode
    type: text_input
    max_length: 6
    keyboard: numeric
    required: true
    validation:
      - rule: not_blank
        error_hi: "पिनकोड डालें"
        error_en: "Enter pincode"
      - rule: length_exact(6)
        error_hi: "6 अंकों का पिनकोड डालें"
        error_en: "Enter 6-digit pincode"

  - id: address
    type: text_input
    placeholder_hi: "पूरा पता"
    placeholder_en: "Full Address"
    required: true

display_elements:
  - gps_badge:
      icon: "🏙️"
      text_hi: "GPS कैप्चर हुआ"
      text_en: "GPS Captured"
      coordinates: "22.71° N, 75.85° E"

cta:
  text_hi: "पंजीकरण शुल्क भरें"
  text_en: "Pay Registration Fee"
  enabled_when: selectedState != "" && city != "" && pincode.length == 6 && address != ""
  next_screen: 4

error_scenarios: []
```

### SCREEN_4: Registration Fee

```yaml
id: SCREEN_4
phase: PHASE_1
title_hi: "पंजीकरण शुल्क"
title_en: "Registration Fee"
step_label: "स्टेप 3/3 | Step 3/3"
purpose: Collect ₹2,000 registration fee (refundable on rejection)

display_elements:
  - amount_box:
      amount: "₹2,000"
      label_hi: "पंजीकरण शुल्क"
      label_en: "Registration Fee"
  - info_card:
      icon: "ℹ️"
      title_hi: "जरूरी जानकारी"
      title_en: "Important Information"
      message_hi: "भुगतान के बाद आपकी profile Business/QA team द्वारा review की जाएगी।"
      message_en: "After payment, your profile will be reviewed by the Business/QA team."
      trust_badge:
        icon: "🔒"
        text_hi: "Reject होने पर full refund मिलेगा"
        text_en: "Full refund if rejected"
  - info_box:
      icon: "💰"
      text_hi: "फ़ीस के बाद QA investigation शुरू होगी"
      text_en: "QA investigation will start after fee payment"

cta:
  text_hi: "₹2,000 अभी भुगतान करें"
  text_en: "Pay ₹2,000 Now"
  action: PROCESS_PAYMENT
  simulation_delay_ms: 2000
  on_success:
    set: regFeePaid = true
    lock: trade_name
    next_screen: 5

error_scenarios:
  - REGFEE_FAILED
  - REGFEE_TIMEOUT
```

### SCREEN_4_ERROR: REGFEE_FAILED

```yaml
id: REGFEE_FAILED
trigger: Payment gateway declined the transaction
screen: 4

display:
  emoji: "😟"
  title_hi: "भुगतान नहीं हो पाया"
  title_en: "Payment could not be processed"
  reassurance_card:
    type: success
    icon: "✅"
    title_hi: "पैसा कटा नहीं है"
    title_en: "No money deducted"
    message_hi: "चिंता न करें — आपके अकाउंट से कोई पैसा नहीं कटा है।"
    message_en: "Don't worry — no money has been deducted from your account."
  transaction_details:
    amount: "₹2,000"
    error_code: "BANK_GATEWAY_TIMEOUT"
    time: "just now"
  info_box:
    type: warning
    icon: "💡"
    text_hi: "2-3 मिनट बाद दोबारा कोशिश करें"
    text_en: "Try again after 2-3 minutes"

ctas:
  - text_hi: "दोबारा भुगतान करें"
    text_en: "Retry Payment"
    type: primary
  - text_hi: "बाद में करें"
    text_en: "Pay Later"
    type: secondary
```

### SCREEN_4_ERROR: REGFEE_TIMEOUT

```yaml
id: REGFEE_TIMEOUT
trigger: Payment gateway timeout / connection lost during transaction
screen: 4

display:
  emoji: "⏳"
  title_hi: "भुगतान pending है"
  title_en: "Payment is pending"
  error_card:
    type: warning
    icon: "⏳"
    title_hi: "Bank response में देरी"
    title_en: "Bank response delayed"
    message_hi: "Bank से response आने में 2-5 मिनट लग सकते हैं। कृपया थोड़ा इंतज़ार करें।"
    message_en: "Bank response may take 2-5 minutes. Please wait."
  transaction_details:
    amount: "₹2,000"
    upi_ref: "UPI123456789"
    status: "⏳ Pending"
  info_box:
    icon: "🔒"
    text_hi: "48 घंटे में auto-refund अगर fail हो"
    text_en: "Auto-refund within 48hrs if failed"

ctas:
  - text_hi: "Status Refresh करें"
    text_en: "Refresh Status"
    type: primary
  - text_hi: "हमसे बात करें"
    text_en: "Talk to us"
    type: secondary
```

### SCREEN_5: KYC Documents

```yaml
id: SCREEN_5
phase: PHASE_2
header: "Verification"
title_hi: "KYC दस्तावेज़"
title_en: "KYC Documents"
step_label: "स्टेप 1/5 | Step 1/5"
purpose: Capture and verify identity documents (PAN, Aadhaar, GST) with inline validation and dedup checks

sub_stages:
  progress_bar: true  # visual progress bar across 3 sub-stages

  - id: pan
    label_hi: "PAN Card"
    label_en: "PAN Card"
    fields:
      - id: pan_number
        type: text_input
        label_hi: "PAN नंबर"
        label_en: "PAN Number"
        validation:
          regex: "^[A-Z]{5}[0-9]{4}[A-Z]$"
          validate_on: blur
          error_hi: "सही PAN नंबर डालें (उदा: ABCDE1234F)"
          error_en: "Enter valid PAN number (e.g., ABCDE1234F)"
    upload:
      - id: pan_card_photo
        label_hi: "PAN Card अपलोड करें"
        label_en: "Upload PAN Card"
        required: true
    view_sample_doc: true
    dedup_check: true  # deferred — triggers kyc-pan-dedup scenario

  - id: aadhaar
    label_hi: "आधार कार्ड"
    label_en: "Aadhaar Card"
    fields:
      - id: aadhaar_number
        type: text_input
        label_hi: "आधार नंबर"
        label_en: "Aadhaar Number"
        format: "4-4-4"  # displayed as XXXX XXXX XXXX
        validation:
          regex: "^[0-9]{12}$"
          validate_on: blur
          error_hi: "12 अंकों का आधार नंबर डालें"
          error_en: "Enter 12-digit Aadhaar number"
    upload:
      - id: aadhaar_front
        label_hi: "आधार कार्ड — सामने अपलोड करें"
        label_en: "Upload Aadhaar Card — Front"
        required: true
      - id: aadhaar_back
        label_hi: "आधार कार्ड — पीछे अपलोड करें"
        label_en: "Upload Aadhaar Card — Back"
        required: true
    view_sample_doc: true
    dedup_check: true  # deferred — triggers kyc-aadhaar-dedup scenario

  - id: gst
    label_hi: "GST प्रमाणपत्र"
    label_en: "GST Certificate"
    fields:
      - id: gst_number
        type: text_input
        label_hi: "GST नंबर"
        label_en: "GST Number"
        max_length: 15
        validation:
          regex: "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][0-9A-Z]{3}$"
          cross_validation: "characters 3-12 must match PAN number"
          validate_on: blur
          error_hi: "सही GST नंबर डालें"
          error_en: "Enter valid GST number"
    upload:
      - id: gst_certificate
        label_hi: "GST प्रमाणपत्र अपलोड करें"
        label_en: "Upload GST Certificate"
        required: true
    view_sample_doc: true
    dedup_check: true  # deferred — triggers kyc-gst-dedup scenario

errors_shown_on: blur  # all validation errors appear only when field loses focus

upload_flow:
  step_1_choose_source:
    title_hi: "{docName} अपलोड करें"
    title_en: "Upload {docName}"
    options:
      - icon: "📷"
        label_hi: "कैमरा से फ़ोटो लें"
        label_en: "Take Photo"
      - icon: "🖼️"
        label_hi: "गैलरी से चुनें"
        label_en: "Choose from Gallery"
    tip_hi: "💡 साफ़ फ़ोटो लें — सारे अक्षर दिखने चाहिए"
    tip_en: "Take a clear photo — all text must be visible"

  step_2_preview:
    title_hi: "फ़ोटो रिव्यू करें"
    title_en: "Review Photo"
    quality_badges:
      - "✓ साफ़ दिख रहा है | Clear"
      - "✓ पूरा दिख रहा है | Complete"
    cta_save_hi: "यह फ़ोटो सेव करें"
    cta_save_en: "Save this photo"
    cta_retake_hi: "दोबारा फ़ोटो लें"
    cta_retake_en: "Retake photo"

  step_3_uploading:
    simulation_steps: 50
    simulation_interval_ms: 80
    total_time_ms: 4000
    success_text_hi: "अपलोड हो गया!"
    success_text_en: "Upload complete!"

document_states:
  not_uploaded: { border: gray, background: white, badge: none }
  uploaded: { border: green, background: green_light, badge: "✓", show_remove_button: true }
  error: { border: red, background: red_light, badge: "✗" }
  warning: { border: orange, background: orange_light, badge: "⚠" }

cta:
  text_hi: "अब बैंक का विवरण दें"
  text_en: "Next: Bank Details"
  enabled_when: all_documents_uploaded_and_numbers_valid
  next_screen: 6

error_scenarios:
  - KYC_PAN_DEDUP
  - KYC_AADHAAR_DEDUP
  - KYC_GST_DEDUP
```

### SCREEN_5_ERROR: KYC_PAN_DEDUP

```yaml
id: KYC_PAN_DEDUP
trigger: PAN number already registered with another partner (deferred dedup check)
screen: 5

display:
  error_card:
    type: error
    icon: "🪪"
    title_hi: "PAN पहले से रजिस्टर्ड है"
    title_en: "PAN Already Registered"
    message_hi: "इस PAN नंबर से पहले से एक पार्टनर रजिस्टर्ड है।"
    message_en: "A partner is already registered with this PAN number."

deferred: true  # check happens asynchronously, result shown later
blocks_progression: true
```

### SCREEN_5_ERROR: KYC_AADHAAR_DEDUP

```yaml
id: KYC_AADHAAR_DEDUP
trigger: Aadhaar number already registered with another partner (deferred dedup check)
screen: 5

display:
  error_card:
    type: error
    icon: "📄"
    title_hi: "आधार पहले से रजिस्टर्ड है"
    title_en: "Aadhaar Already Registered"
    message_hi: "इस आधार नंबर से पहले से एक पार्टनर रजिस्टर्ड है।"
    message_en: "A partner is already registered with this Aadhaar number."

deferred: true
blocks_progression: true
```

### SCREEN_5_ERROR: KYC_GST_DEDUP

```yaml
id: KYC_GST_DEDUP
trigger: GST number already registered with another partner (deferred dedup check)
screen: 5

display:
  error_card:
    type: error
    icon: "📋"
    title_hi: "GST पहले से रजिस्टर्ड है"
    title_en: "GST Already Registered"
    message_hi: "इस GST नंबर से पहले से एक पार्टनर रजिस्टर्ड है।"
    message_en: "A partner is already registered with this GST number."

deferred: true
blocks_progression: true
```

### SCREEN_6: Bank Details

```yaml
id: SCREEN_6
phase: PHASE_2
header: "Verification"
title_hi: "बैंक विवरण"
title_en: "Bank Details"
step_label: "स्टेप 2/5 | Step 2/5"
purpose: Verify bank account via penny drop and run dedup check

fields:
  - id: bank_account_number
    type: text_input
    label_hi: "अकाउंट नंबर"
    label_en: "Account Number"
    placeholder_hi: "अकाउंट नंबर डालें"
    placeholder_en: "Enter account number"
    keyboard: numeric
    min_length: 9
    max_length: 18
    mask_on_blur: true  # masked after field loses focus
    required: true
    validation:
      - rule: length_range(9, 18)
        error_hi: "9-18 अंकों का अकाउंट नंबर डालें"
        error_en: "Enter 9-18 digit account number"

  - id: bank_account_number_confirm
    type: text_input
    label_hi: "अकाउंट नंबर दोबारा डालें"
    label_en: "Re-enter Account Number"
    placeholder_hi: "अकाउंट नंबर दोबारा डालें"
    placeholder_en: "Re-enter account number"
    keyboard: numeric
    required: true
    validation:
      - rule: must_match(bank_account_number)
        error_hi: "अकाउंट नंबर मेल नहीं खाता"
        error_en: "Account numbers do not match"

  - id: bank_ifsc
    type: text_input
    label_hi: "IFSC कोड"
    label_en: "IFSC Code"
    placeholder_hi: "IFSC कोड डालें"
    placeholder_en: "Enter IFSC code"
    required: true
    validation:
      - rule: regex("^[A-Z]{4}0[A-Z0-9]{6}$")
        error_hi: "सही IFSC कोड डालें"
        error_en: "Enter valid IFSC code"

# NOTE: No account holder name or bank name fields in this screen

hint:
  text_hi: "Bank details [Personal Name] या [Business Name] के नाम होनी चाहिए"
  text_en: "Bank details should belong to [Personal Name] or [Business Name]"

verification_flow:
  cta:
    text_hi: "Verify Bank Details"
    text_en: "Verify Bank Details"
    enabled_when: all_fields_filled_and_valid
    simulation_delay_ms: 2000  # 2s spinner

  outcomes:
    success:
      display:
        type: success_delight
        title_hi: "बैंक वेरीफाइड!"
        title_en: "Bank Verified!"
      cta:
        text_hi: "अब ISP अनुबंध अपलोड करें"
        text_en: "Add ISP Agreement"
        next_screen: 7

    penny_drop_fail:
      display:
        type: bottom_sheet
        title_hi: "₹1 credit नहीं हो पाया"
        title_en: "Penny drop failed"
        message_hi: "अकाउंट नंबर गलत हो सकता है या बैंक सर्वर डाउन है।"
        message_en: "Account number may be wrong or bank server is down."
      ctas:
        - text_hi: "Bank Details बदलें"
          text_en: "Change Bank Details"
          type: primary
          action: EDIT_BANK_FIELDS
        - text_hi: "Bank Document अपलोड करें"
          text_en: "Upload Bank Document"
          type: secondary
          action: UPLOAD_BANK_DOC
      supporting_docs:
        - "Bank Statement"
        - "Cancelled Cheque"
        - "Bank Passbook"

    name_mismatch:
      display:
        type: bottom_sheet
        title_hi: "Penny Drop — नाम मेल नहीं खाता"
        title_en: "Penny Drop — Name Mismatch"
        name_comparison:
          bank_name: "Rajesh Kumar Sharma"
          entered_name: "Rajesh Kumar"
      ctas:
        - text_hi: "Bank Details बदलें"
          text_en: "Change Bank Details"
          type: primary
          action: EDIT_BANK_FIELDS
        - text_hi: "Bank Document अपलोड करें"
          text_en: "Upload Bank Document"
          type: secondary
          action: UPLOAD_BANK_DOC
      supporting_docs:
        - "Bank Statement"
        - "Cancelled Cheque"
        - "Bank Passbook"

    bank_dedup:
      display:
        type: bottom_sheet
        title_hi: "Bank Account पहले से रजिस्टर्ड है"
        title_en: "Bank Account Already Registered"
        message_hi: "इस Bank Account से पहले से एक पार्टनर रजिस्टर्ड है।"
        message_en: "A partner is already registered with this bank account."
      ctas:
        - text_hi: "Bank Details बदलें"
          text_en: "Change Bank Details"
          type: primary
          action: EDIT_BANK_FIELDS
      # NOTE: No "Upload Bank Document" option for dedup — only change details

error_scenarios:
  - BANK_PENNYDROP_FAIL
  - BANK_NAME_MISMATCH
  - BANK_DEDUP
```

### SCREEN_7: ISP Agreement

```yaml
id: SCREEN_7
phase: PHASE_2
header: "Verification"
title_hi: "ISP अनुबंध"
title_en: "ISP Agreement"
step_label: "स्टेप 3/5 | Step 3/5"
purpose: Upload ISP agreement document for DOT compliance and TRAI guidelines

upload_methods:
  - id: pdf
    label_hi: "PDF अपलोड करें"
    label_en: "Upload PDF"
    type: file_picker
    accept: "application/pdf"

  - id: camera
    label_hi: "कैमरा से फ़ोटो लें"
    label_en: "Take Photo"
    type: camera
    max_pages: 7  # multi-page capture, up to 7 photos

  - id: gallery
    label_hi: "गैलरी से चुनें"
    label_en: "Choose from Gallery"
    type: gallery
    max_pages: 7  # multi-select, up to 7 images

mandatory_details_checklist:
  title_hi: "ISP अनुबंध में अनिवार्य विवरण:"
  title_en: "Mandatory details required in ISP Agreement:"
  items:
    - hi: "ISP कंपनी का नाम" | en: "ISP Company Name"
    - hi: "LCO / पार्टनर का नाम" | en: "LCO / Partner Name"
    - hi: "अनुबंध की तिथि" | en: "Agreement Date"
    - hi: "अनुबंध वैध होना चाहिए (समाप्त नहीं)" | en: "Agreement should be Valid (Not Expired)"
    - hi: "लाइसेंस नंबर" | en: "License Number"
    - hi: "संपर्क / हस्ताक्षरकर्ता का नाम" | en: "Contact / Signatory Names"
    - hi: "पार्टनर और ISP की मुहर और हस्ताक्षर" | en: "Partner and ISP stamp and signature"

view_sample_doc: true

cta:
  text_hi: "आगे बढ़ें"
  text_en: "Proceed"
  disabled_text_hi: "ISP अनुबंध अपलोड करें"
  disabled_text_en: "Upload ISP Agreement"
  enabled_when: isp_agreement_uploaded
  next_screen: 8

error_scenarios: []
```

### SCREEN_8: Shop & Equipment Photos

```yaml
id: SCREEN_8
phase: PHASE_2
header: "Verification"
sub_header_hi: "दुकान सत्यापन"
sub_header_en: "Shop Verification"
title_hi: "दुकान और उपकरण फ़ोटो"
title_en: "Shop & Equipment Photos"
step_label: "स्टेप 4/5 | Step 4/5"
purpose: Capture shop front and equipment photos for verification

documents:
  - id: shop_front_photo
    icon: "🏪"
    label_hi: "दुकान के सामने की फ़ोटो"
    label_en: "Shop Front Photo"
    required: true
    max_photos: 1  # single photo only
    view_sample_doc: true
    pro_tips: true  # shows tips for good photo capture

  - id: equipment_photos
    icon: "📡"
    label_hi: "उपकरण की फ़ोटो"
    label_en: "Equipment Photos"
    required: true
    max_photos: 5  # multi-photo, up to 5
    view_sample_doc: true
    mandatory_requirements: true  # shows mandatory checklist for equipment

cta:
  text_hi: "सत्यापन के लिए जमा करें"
  text_en: "Submit for Verification"
  enabled_when: shop_front_photo_uploaded AND at_least_one_equipment_photo_uploaded
  next_screen: 9

error_scenarios: []
```

### SCREEN_9: Verification Status

```yaml
id: SCREEN_9
phase: PHASE_2
header: "Verification"
title_hi: "सत्यापन"
title_en: "Verification Status"
step_label: "स्टेप 5/5 | Step 5/5"
purpose: Confirmation that all documents are submitted, awaiting verification

display_elements:
  - title_hi: "सभी दस्तावेज़ जमा हो गए"
  - title_en: "All Documents Submitted"

checklist:
  - { text_hi: "KYC दस्तावेज़", text_en: "KYC Documents", status: done }
  - { text_hi: "बैंक विवरण", text_en: "Bank Details", status: done }
  - { text_hi: "ISP अनुबंध", text_en: "ISP Agreement", status: done }
  - { text_hi: "दुकान और उपकरण फ़ोटो", text_en: "Shop & Equipment Photos", status: done }
  - { text_hi: "सत्यापन समीक्षा", text_en: "Verification Review", status: waiting, sub_text_hi: "समीक्षा जारी...", sub_text_en: "Under review..." }

tat:
  text_hi: "समीक्षा में 3 कार्य दिवस"
  text_en: "Review may take 3 business days"

cta: null  # No CTA — waits for verification decision

decision_point:
  decision_maker: QA_TEAM (via QA Review Dashboard)
  outcomes:
    - APPROVED → SCREEN_10
    - REJECTED → rejection with auto refund, no re-upload (Phase 1)

rejected_state:
  description: "Auto refund initiated, no re-upload option in Phase 1"
  refund_card:
    type: success
    icon: "🔒"
    title_hi: "Refund शुरू हो गया"
    title_en: "Refund initiated"
    amount: "₹2,000"
    timeline: "5-6 working days"

error_scenarios:
  - VERIFICATION_REJECTED
```

### SCREEN_9_ERROR: VERIFICATION_REJECTED

```yaml
id: VERIFICATION_REJECTED
trigger: QA team rejects the application
screen: 9

display:
  emoji: "😔"
  title_hi: "सत्यापन सफल नहीं"
  title_en: "Verification not successful"
  subtitle_hi: "चिंता न करें — आपका पैसा सुरक्षित है"
  subtitle_en: "Don't worry — your money is safe"

refund:
  amount: "₹2,000"
  timeline: "5-6 working days"
  auto_initiated: true

re_upload: false  # no re-upload option in Phase 1

blocks_progression: true
```

### SCREEN_10: Policy & SLA

```yaml
id: SCREEN_10
phase: PHASE_3
header_hi: "महत्वपूर्ण शर्तें"
header_en: "Important Terms"
title_hi: "नीतियां और रेट कार्ड"
title_en: "Policy & Rate Card"
step_label: "स्टेप 1/7 | Step 1/7"
purpose: Partner reviews and acknowledges commission structure and SLA terms

display_elements:
  - commission_card:
      title_hi: "कमीशन संरचना"
      title_en: "COMMISSION STRUCTURE"
      rows:
        - { label_hi: "नया कनेक्शन", label_en: "New Connection", value: "₹300/कनेक्शन", color: green }
        - { label_hi: "रिचार्ज कमीशन", label_en: "Recharge Commission", value: "₹300", color: green }

  - payout_info:
      text_hi: "Payout हर Monday, सुबह 10 बजे तक"
      text_en: "Payout every Monday by 10 AM"

  - sla_card:
      title_hi: "SLA शर्तें"
      title_en: "SERVICE LEVELS"
      items:
        - hi: "ग्राहक शिकायत: 4 घंटे में समाधान"
          en: "Customer complaints: 4hr resolution"
        - hi: "कनेक्शन 95%+ चालू रहना चाहिए"
          en: "Connection 95%+ to be up and running"
        - hi: "उपकरण देखभाल की ज़िम्मेदारी"
          en: "Equipment care responsibility"
        - hi: "Wiom ब्रांड गाइडलाइन का पालन"
          en: "Wiom brand guidelines compliance"

cta:
  text_hi: "समझ गया, आगे बढ़ें"
  text_en: "Understood, proceed"
  next_screen: 11

error_scenarios: []
```

### SCREEN_11: Technical Assessment

```yaml
id: SCREEN_11
phase: PHASE_3
header: "Activation"
title_hi: "तकनीकी मूल्यांकन"
title_en: "Technical Assessment"
step_label: "स्टेप 2/7 | Step 2/7"
purpose: Network quality team verifies infrastructure readiness — BEFORE onboarding fee

tat:
  text_hi: "4-5 business days में Network Quality team कॉल करेगी"
  text_en: "Network Quality team will call within 4-5 business days"

decision_point:
  decision_maker: NETWORK_QUALITY_TEAM
  method: phone_call
  outcomes:
    - PASSED → SCREEN_12 (Onboarding Fee)
    - REJECTED → tech_assessment_rejected

rejected_state:
  display:
    emoji: "❌"
    title_hi: "तकनीकी मूल्यांकन पास नहीं हुआ"
    title_en: "Technical Assessment Not Passed"
  refund: false  # no refund on tech assessment rejection
  contact:
    text_hi: "हमसे बात करें"
    text_en: "Talk to Us"
    phone: "7836811111"

cta: null  # waits for tech assessment decision

error_scenarios:
  - TECH_ASSESSMENT_REJECTED
```

### SCREEN_11_ERROR: TECH_ASSESSMENT_REJECTED

```yaml
id: TECH_ASSESSMENT_REJECTED
trigger: Technical assessment failed due to infrastructure issues
screen: 11

display:
  emoji: "❌"
  title_hi: "तकनीकी मूल्यांकन पास नहीं हुआ"
  title_en: "Technical Assessment Not Passed"
  error_card:
    type: error
    icon: "🔧"
    title_hi: "तकनीकी आवश्यकताएं पूरी नहीं"
    title_en: "Technical Requirements Not Met"
    message_hi: "कृपया तकनीकी आवश्यकताएं पूरी करें।"
    message_en: "Please meet the technical requirements."

refund: false  # no refund

cta:
  text_hi: "हमसे बात करें"
  text_en: "Talk to Us"
  action: CALL_SUPPORT
  phone: "7836811111"

blocks_progression: true
```

### SCREEN_12: Onboarding Fee

```yaml
id: SCREEN_12
phase: PHASE_3
header: "Activation"
title_hi: "ऑनबोर्डिंग फ़ीस"
title_en: "Onboarding Fee"
step_label: "स्टेप 3/7 | Step 3/7"
purpose: Collect ₹20,000 onboarding fee after tech assessment passes

display_elements:
  - amount_box:
      amount: "₹20,000"
      label_hi: "ऑनबोर्डिंग फ़ीस"
      label_en: "Onboarding Fee"

  - wifi_devices_message:
      text_hi: "WiFi devices आपको दिए जाएंगे"
      text_en: "WiFi devices will be provided to you"

  - investment_summary:
      title_hi: "निवेश सारांश"
      title_en: "Investment Summary"
      rows:
        - { label_hi: "पंजीकरण शुल्क (भुगतान हुआ)", label_en: "Reg Fee (Paid)", value: "₹2,000 ✓" }
        - { label_hi: "ऑनबोर्डिंग फ़ीस", label_en: "Onboarding Fee", value: "₹20,000" }
        - { label_hi: "कुल Investment", label_en: "Total Investment", value: "₹22,000", bold: true }

cta:
  text_hi: "₹20,000 अभी भुगतान करें"
  text_en: "Pay ₹20,000 Now"
  simulation_delay_ms: 2000
  on_success:
    set: onboardFeePaid = true
    next_screen: 13

error_scenarios:
  - ONBOARDFEE_FAILED
  - ONBOARDFEE_TIMEOUT
```

### SCREEN_12_ERROR: ONBOARDFEE_FAILED

```yaml
id: ONBOARDFEE_FAILED
trigger: ₹20,000 payment declined
screen: 12

display:
  emoji: "😟"
  title_hi: "भुगतान नहीं हो पाया"
  title_en: "Payment could not be processed"
  reassurance_card:
    type: success
    icon: "✅"
    title_hi: "पैसा कटा नहीं है"
    title_en: "No money deducted"
    message_hi: "चिंता न करें — आपके अकाउंट से कोई पैसा नहीं कटा है।"
    message_en: "Don't worry — no money has been deducted from your account."
  transaction_details:
    amount: "₹20,000"
    error_code: "UPI_LIMIT_EXCEEDED"
    time: "just now"
  info_box:
    type: warning
    icon: "💡"
    text_hi: "UPI limit ₹1L/day — NEFT/RTGS या कार्ड से भुगतान करें"
    text_en: "UPI limit ₹1L/day — try NEFT/RTGS or card"

ctas:
  - text_hi: "दोबारा भुगतान करें"
    text_en: "Retry Payment"
    type: primary
  - text_hi: "बाद में करें"
    text_en: "Pay Later"
    type: secondary
```

### SCREEN_12_ERROR: ONBOARDFEE_TIMEOUT

```yaml
id: ONBOARDFEE_TIMEOUT
trigger: ₹20,000 payment timeout / connection lost
screen: 12

display:
  emoji: "⏳"
  title_hi: "भुगतान pending है"
  title_en: "Payment is pending"
  error_card:
    type: warning
    icon: "⏳"
    title_hi: "Bank response में देरी"
    title_en: "Bank response delayed"
    message_hi: "Bank से response आने में 2-5 मिनट लग सकते हैं। कृपया थोड़ा इंतज़ार करें।"
    message_en: "Bank response may take 2-5 minutes. Please wait."
  transaction_details:
    amount: "₹20,000"
    status: "⏳ Pending"
  info_box:
    icon: "🔒"
    text_hi: "48 घंटे में auto-refund अगर fail हो"
    text_en: "Auto-refund within 48hrs if failed"

ctas:
  - text_hi: "Status Refresh करें"
    text_en: "Refresh Status"
    type: primary
  - text_hi: "हमसे बात करें"
    text_en: "Talk to us"
    type: secondary
```

### SCREEN_13: Account Setup

```yaml
id: SCREEN_13
phase: PHASE_3
header: "Activation"
title_hi: "अकाउंट सेटअप"
title_en: "Account Setup"
step_label: "स्टेप 4/5 | Step 4/5"
purpose: Automated backend setup of partner accounts — auto-progression, no user interaction

interaction: NONE
auto_progress_seconds: 3  # auto-advances after 3 seconds
cta: null  # no CTA button — auto-progression only

animation:
  type: sequential_checklist
  auto_advance: true

error_states:
  failed:
    display:
      title_hi: "सेटअप में समस्या"
      title_en: "Setup Failed"
    cta:
      text_hi: "दोबारा कोशिश करें"
      text_en: "Retry"
      action: RETRY_SETUP

  pending:
    display:
      title_hi: "सेटअप चल रहा है..."
      title_en: "Setup in progress..."
    cta:
      text_hi: "Refresh करें"
      text_en: "Refresh"
      action: REFRESH_STATUS

on_success:
  next_screen: 14

error_scenarios:
  - ACCOUNT_SETUP_FAILED
  - ACCOUNT_SETUP_PENDING
```

### SCREEN_13_ERROR: ACCOUNT_SETUP_FAILED

```yaml
id: ACCOUNT_SETUP_FAILED
trigger: Backend account setup process failed
screen: 13

display:
  emoji: "❌"
  title_hi: "अकाउंट सेटअप विफल"
  title_en: "Account Setup Failed"
  error_card:
    type: error
    title_hi: "सेटअप में समस्या आई"
    title_en: "Setup encountered an issue"
    message_hi: "चिंता न करें — दोबारा कोशिश करें"
    message_en: "Don't worry — please retry"

cta:
  text_hi: "दोबारा कोशिश करें"
  text_en: "Retry"
  action: RETRY_SETUP
```

### SCREEN_13_ERROR: ACCOUNT_SETUP_PENDING

```yaml
id: ACCOUNT_SETUP_PENDING
trigger: Backend account setup is taking longer than expected
screen: 13

display:
  emoji: "⏳"
  title_hi: "सेटअप चल रहा है..."
  title_en: "Setup in progress..."
  info_card:
    type: warning
    title_hi: "कृपया थोड़ा इंतज़ार करें"
    title_en: "Please wait a moment"
    message_hi: "सेटअप में थोड़ी देरी हो रही है।"
    message_en: "Setup is taking a bit longer."

cta:
  text_hi: "Refresh करें"
  text_en: "Refresh"
  action: REFRESH_STATUS
```

### SCREEN_14: Successfully Onboarded

```yaml
id: SCREEN_14
phase: PHASE_3
header: "Activation"
title_hi: "ऑनबोर्डिंग सफल"
title_en: "Successfully Onboarded"
step_label: "स्टेप 5/5 | Step 5/5"
purpose: Celebration and activation — partner is now a Wiom Connection Service Provider

display_elements:
  - title_hi: "Wiom Connection Service Provider"
  - title_en: "Wiom Connection Service Provider"
  - subtitle_hi: "बधाई हो! आप अब Wiom पार्टनर हैं"
  - subtitle_en: "Congratulations! You are now a Wiom Partner"

app_download:
  title_hi: "Wiom Partner Plus App डाउनलोड करें"
  title_en: "Download Wiom Partner Plus App"
  cta:
    text_hi: "Install Now"
    text_en: "Install Now"
    action: OPEN_PLAY_STORE

instructions:
  - step: 1
    text_hi: "App में Login करें"
    text_en: "Login to the App"
  - step: 2
    text_hi: "Permissions दें"
    text_en: "Grant Permissions"
  - step: 3
    text_hi: "Training पूरी करें"
    text_en: "Complete Training"

error_scenarios: []
```

---

## SCENARIO_SIMULATOR

```yaml
scenario_categories:
  - id: NETWORK_APP
    name: "Network / App Errors"
    scenarios:
      - id: no-internet
        description: "No internet connection"
        display: generic offline error screen
      - id: server-error
        description: "Server error / 5xx response"
        display: generic server error screen

  - id: REGISTRATION_OTP
    name: "Registration & OTP"
    scenarios:
      - id: phone-duplicate
        screen: 0
        description: "Phone number already registered"
      - id: otp-wrong
        screen: 1
        description: "Incorrect OTP entered"
      - id: otp-expired
        screen: 1
        description: "OTP validity period exceeded"

  - id: REG_FEE
    name: "Registration Fee"
    scenarios:
      - id: regfee-failed
        screen: 4
        description: "Registration fee payment declined"
      - id: regfee-timeout
        screen: 4
        description: "Registration fee payment timeout"
      - id: kyc-day1-reminder
        description: "Day 1 reminder to complete KYC"
      - id: kyc-day2-reminder
        description: "Day 2 reminder to complete KYC"
      - id: kyc-day3-reminder
        description: "Day 3 reminder to complete KYC"
      - id: kyc-day4-autoreject
        description: "Day 4 auto-rejection for incomplete KYC"
      - id: refund-success
        description: "Refund completed successfully"
      - id: refund-in-progress
        description: "Refund is being processed"
      - id: refund-failed
        description: "Refund failed"

  - id: KYC
    name: "KYC Dedup"
    scenarios:
      - id: kyc-pan-dedup
        screen: 5
        description: "PAN already registered (deferred check)"
        deferred: true
      - id: kyc-aadhaar-dedup
        screen: 5
        description: "Aadhaar already registered (deferred check)"
        deferred: true
      - id: kyc-gst-dedup
        screen: 5
        description: "GST already registered (deferred check)"
        deferred: true

  - id: BANK
    name: "Bank Verification"
    scenarios:
      - id: bank-pennydrop-fail
        screen: 6
        description: "Penny drop verification failed"
        deferred: true
      - id: bank-name-mismatch
        screen: 6
        description: "Bank account holder name differs from KYC name"
        deferred: true
      - id: bank-dedup
        screen: 6
        description: "Bank account already registered with another partner"
        deferred: true

  - id: ONBOARD_FEE
    name: "Onboarding Fee"
    scenarios:
      - id: onboardfee-success
        screen: 12
        description: "Onboarding fee payment successful"
      - id: onboardfee-failed
        screen: 12
        description: "Onboarding fee payment declined"
      - id: onboardfee-timeout
        screen: 12
        description: "Onboarding fee payment timeout"

  - id: ACCOUNT_SETUP
    name: "Account Setup"
    scenarios:
      - id: account-setup-failed
        screen: 13
        description: "Account setup process failed"
      - id: account-setup-pending
        screen: 13
        description: "Account setup taking longer than expected"

  - id: AGREEMENT_TECH
    name: "Agreement & Tech Assessment"
    scenarios:
      - id: verification-pending
        screen: 9
        description: "Verification still in progress"
      - id: verification-rejected
        screen: 9
        description: "Verification rejected by QA team"
      - id: refund-in-progress-vr
        screen: 9
        description: "Refund in progress after verification rejection"
      - id: policy-sla
        screen: 10
        description: "Policy & SLA acknowledgement"
      - id: tech-assessment-rejected
        screen: 11
        description: "Technical assessment failed"

  - id: PAYMENT_FAILURES
    name: "Payment Failures (backward compat)"
    description: "Legacy category maintained for backward compatibility"
    scenarios: []  # references scenarios from REG_FEE and ONBOARD_FEE categories
```

---

## BUSINESS_CONSTANTS

```yaml
fees:
  registration: 2000  # INR, refundable on rejection
  onboarding: 20000   # INR
  total_investment: 22000

commissions:
  new_connection: 300  # INR per connection
  recharge: 300        # INR flat
  payout_frequency: "Every Monday by 10 AM"
  payout_method: "Bank transfer"

sla:
  complaint_resolution: "4 hours"
  uptime_requirement: "95%+"
  equipment_care: "Partner responsibility"
  brand_compliance: "Mandatory"

help:
  phone: "7836811111"

timelines:
  verification_tat: "3 business days"
  tech_assessment_tat: "4-5 business days"
  refund_tat: "5-6 working days"
  payment_timeout_auto_refund: "48 hours"

agreement:
  term: "12 months, auto-renewable"
  termination_notice: "30 days"
  compliance: ["DOT", "TRAI", "Wiom brand guidelines"]
```

---

## STATE_MACHINE

```yaml
states:
  - NEW → PITCH (Pitch Screen)
  - PITCH_DONE → SCREEN_0 (Phone Entry)
  - OTP_SENT → SCREEN_1 (OTP Verification)
  - REGISTERED → SCREEN_2-4 (Personal → RegFee)
  - DOCUMENTED → SCREEN_5-8 (KYC → Shop Photos)
  - VERIFICATION → SCREEN_9 (Verification Status) [BRANCH POINT]
    - → APPROVED → SCREEN_10 (Policy & SLA)
    - → REJECTED → auto refund, no re-upload (Phase 1)
  - POLICY_DONE → SCREEN_10 (Policy & SLA)
  - TECH_ASSESS → SCREEN_11 (Technical Assessment) [BRANCH POINT]
    - → PASSED → SCREEN_12 (Onboarding Fee)
    - → FAILED → no refund, Talk to Us (7836811111)
  - PAYMENT_2 → SCREEN_12 (Onboarding Fee)
  - SETUP → SCREEN_13 (Account Setup, auto 3s)
  - ONBOARDED → SCREEN_14 (Successfully Onboarded)

error_states:
  PHONE_DUPLICATE: blocks at SCREEN_0
  OTP_WRONG: retryable at SCREEN_1 (max 3 attempts)
  OTP_EXPIRED: retryable at SCREEN_1
  REGFEE_FAILED: retryable at SCREEN_4
  REGFEE_TIMEOUT: retryable at SCREEN_4
  KYC_PAN_DEDUP: deferred, blocks at SCREEN_5
  KYC_AADHAAR_DEDUP: deferred, blocks at SCREEN_5
  KYC_GST_DEDUP: deferred, blocks at SCREEN_5
  BANK_PENNYDROP_FAIL: deferred, bottom sheet at SCREEN_6 (Change Details / Upload Doc)
  BANK_NAME_MISMATCH: deferred, bottom sheet at SCREEN_6 (Change Details / Upload Doc)
  BANK_DEDUP: deferred, bottom sheet at SCREEN_6 (Change Details only)
  VERIFICATION_REJECTED: blocks at SCREEN_9 (auto refund, no re-upload)
  TECH_ASSESSMENT_REJECTED: blocks at SCREEN_11 (no refund, Talk to Us)
  ONBOARDFEE_FAILED: retryable at SCREEN_12
  ONBOARDFEE_TIMEOUT: retryable at SCREEN_12
  ACCOUNT_SETUP_FAILED: retryable at SCREEN_13
  ACCOUNT_SETUP_PENDING: refreshable at SCREEN_13
```

---

## VALIDATION_RULES

```yaml
phone:
  regex: 10 digits
  rules:
    - not_blank → "नंबर डालें / Enter phone number"
    - length == 10 → "10 अंकों का नंबर डालें / Enter 10-digit number"
    - digits_only → "केवल अंक डालें / Enter digits only"
    - length > 10 → "केवल 10 अंकों का नंबर डालें / Only 10-digit numbers allowed"

otp:
  - all_4_filled → "पूरा OTP डालें / Enter complete OTP"

name:
  - not_blank → "नाम डालें / Enter name"

email:
  - not_blank → "ईमेल डालें / Enter email"
  - contains_at_and_dot → "सही ईमेल डालें / Enter valid email"

pincode:
  regex: /^[0-9]{6}$/
  rules:
    - not_blank → "पिनकोड डालें / Enter pincode"
    - length == 6 → "6 अंकों का पिनकोड डालें / Enter 6-digit pincode"

pan:
  regex: /^[A-Z]{5}[0-9]{4}[A-Z]$/
  validate_on: blur

aadhaar:
  regex: /^[0-9]{12}$/
  format: "4-4-4"
  validate_on: blur

gst:
  regex: /^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][0-9A-Z]{3}$/
  cross_validation: "characters 3-12 must match PAN number"
  validate_on: blur

bank_account:
  length: 9-18 digits
  double_entry: must match
  mask_on_blur: true

ifsc:
  regex: /^[A-Z]{4}0[A-Z0-9]{6}$/
```

---

## MOCK_DATA (Filled Mode)

```yaml
phone: "9876543210"
otp: ["4", "7", "2", "9"]
name: "राजेश कुमार"
email: "rajesh@email.com"
entity_type: "प्रोप्राइटरशिप (Proprietorship)"
trade_name: "Rajesh Telecom"
city: "Indore"
pincode: "452010"
address: "123, Vijay Nagar, Indore"
state: "Madhya Pradesh"
gps: "22.71° N, 75.85° E"
pan: "ABCDE1234F"
aadhaar: "123456789012"
gst: "23ABCDE1234F1ZT"
bank_account: "XXXX XXXX 4521"
bank_ifsc: "SBIN0001234"
```

---

## QA_TEST_CASES

### Happy Path Tests

```yaml
TC_HP_001:
  name: "Complete onboarding end-to-end"
  steps: Pitch → Screen 0 → 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 (approved) → 10 → 11 (pass) → 12 → 13 → 14
  expected: Partner reaches Successfully Onboarded screen (Screen 14)

TC_HP_002:
  name: "Language toggle works on all screens"
  steps: Navigate to each screen, toggle हि/En, verify all text switches
  expected: All labels, buttons, messages switch between Hindi and English

TC_HP_003:
  name: "Back navigation preserves form data"
  steps: Fill Screen 2, go to Screen 3, go back to Screen 2
  expected: Personal info fields retain entered values

TC_HP_004:
  name: "OTP timer countdown and resend"
  steps: Enter phone → go to OTP screen → wait 28s → tap resend
  expected: Timer counts from 28 to 0, resend link appears, new timer starts

TC_HP_005:
  name: "KYC document upload with number entry + photo"
  steps: Enter PAN number → upload PAN card → Enter Aadhaar → upload front + back → Enter GST → upload cert
  expected: All 3 sub-stages completed with progress bar, documents in green verified state

TC_HP_006:
  name: "Bank verification with penny drop"
  steps: Enter account number → re-enter to confirm → enter IFSC → tap Verify → wait 2s
  expected: Success delight shown, "Add ISP Agreement" CTA appears

TC_HP_007:
  name: "Account setup auto-progression"
  steps: Reach Screen 13 → observe 3s auto-progression
  expected: Auto-advances to Screen 14 after 3 seconds

TC_HP_008:
  name: "Successfully Onboarded screen"
  steps: Complete full flow to Screen 14
  expected: "Wiom Connection Service Provider" title, app download CTA, and instructions shown

TC_HP_009:
  name: "Filled mode populates all screens"
  steps: Trigger fill command from dashboard → navigate through all screens
  expected: All form fields pre-filled with mock data

TC_HP_010:
  name: "ISP Agreement multi-page upload"
  steps: Screen 7 → upload via camera (3 pages) → verify all pages shown
  expected: Multi-page upload works, up to 7 pages, mandatory checklist shown
```

### Error Scenario Tests

```yaml
TC_ERR_001:
  name: "Phone duplicate detection"
  scenario: phone-duplicate
  screen: 0
  expected: Error card shown, login + new number CTAs available

TC_ERR_002:
  name: "Wrong OTP with attempt counter"
  scenario: otp-wrong
  screen: 1
  expected: Red OTP boxes, "2 attempts remaining" message

TC_ERR_003:
  name: "OTP expiry with resend option"
  scenario: otp-expired
  screen: 1
  expected: Faded OTP boxes, "send new OTP" + "change number" buttons

TC_ERR_004:
  name: "Registration fee payment failed"
  scenario: regfee-failed
  screen: 4
  expected: Reassurance card (no money deducted), retry + later CTAs

TC_ERR_005:
  name: "Registration fee payment timeout"
  scenario: regfee-timeout
  screen: 4
  expected: Pending status with UPI ref, auto-refund info, refresh + support CTAs

TC_ERR_006:
  name: "PAN dedup detected"
  scenario: kyc-pan-dedup
  screen: 5
  expected: Deferred error, PAN already registered message, progression blocked

TC_ERR_007:
  name: "Aadhaar dedup detected"
  scenario: kyc-aadhaar-dedup
  screen: 5
  expected: Deferred error, Aadhaar already registered message, progression blocked

TC_ERR_008:
  name: "GST dedup detected"
  scenario: kyc-gst-dedup
  screen: 5
  expected: Deferred error, GST already registered message, progression blocked

TC_ERR_009:
  name: "Penny drop verification failed"
  scenario: bank-pennydrop-fail
  screen: 6
  expected: Bottom sheet with Change Bank Details + Upload Bank Document CTAs

TC_ERR_010:
  name: "Bank account name mismatch"
  scenario: bank-name-mismatch
  screen: 6
  expected: Bottom sheet with name comparison, Change Bank Details + Upload Bank Document CTAs

TC_ERR_011:
  name: "Bank account dedup"
  scenario: bank-dedup
  screen: 6
  expected: Bottom sheet with Change Bank Details only (no upload option)

TC_ERR_012:
  name: "Verification rejected"
  scenario: verification-rejected
  screen: 9
  expected: Rejection shown, auto refund initiated, no re-upload option

TC_ERR_013:
  name: "Technical assessment rejected"
  scenario: tech-assessment-rejected
  screen: 11
  expected: Assessment failed, no refund, Talk to Us CTA with phone 7836811111

TC_ERR_014:
  name: "Onboarding fee payment failed"
  scenario: onboardfee-failed
  screen: 12
  expected: Reassurance card, UPI limit info, retry + later CTAs

TC_ERR_015:
  name: "Onboarding fee payment timeout"
  scenario: onboardfee-timeout
  screen: 12
  expected: Pending status, auto-refund info, refresh + support CTAs

TC_ERR_016:
  name: "Account setup failed"
  scenario: account-setup-failed
  screen: 13
  expected: Failed message, Retry CTA

TC_ERR_017:
  name: "Account setup pending"
  scenario: account-setup-pending
  screen: 13
  expected: Pending message, Refresh CTA

TC_ERR_018:
  name: "No internet connection"
  scenario: no-internet
  expected: Generic offline error screen

TC_ERR_019:
  name: "Server error"
  scenario: server-error
  expected: Generic server error screen

TC_ERR_020:
  name: "KYC Day 1-4 reminders"
  scenarios: kyc-day1-reminder, kyc-day2-reminder, kyc-day3-reminder, kyc-day4-reminder
  expected: Reminder notifications shown for pending KYC completion

TC_ERR_021:
  name: "Refund scenarios"
  scenarios: refund-success, refund-in-progress, refund-failed
  expected: Appropriate refund status shown for each state
```

### Edge Case Tests

```yaml
TC_EDGE_001:
  name: "Empty form submission blocked"
  steps: On each screen with required fields, try tapping CTA without filling anything
  expected: CTA remains disabled, no navigation occurs

TC_EDGE_002:
  name: "Partial KYC upload"
  steps: Complete PAN sub-stage but not Aadhaar, try to proceed
  expected: CTA disabled until all 3 sub-stages (PAN, Aadhaar, GST) completed

TC_EDGE_003:
  name: "Bank account number mismatch"
  steps: Enter account number, enter different number in re-enter field
  expected: Validation error "Account numbers do not match"

TC_EDGE_004:
  name: "GST-PAN cross validation"
  steps: Enter PAN ABCDE1234F, then enter GST with different chars 3-12
  expected: GST validation error on blur

TC_EDGE_005:
  name: "Bank account masked on blur"
  steps: Enter account number → tap outside field
  expected: Account number gets masked (only last 4 digits visible)

TC_EDGE_006:
  name: "Remove uploaded KYC document"
  steps: Upload PAN card → tap remove button
  expected: Document returns to not-uploaded state

TC_EDGE_007:
  name: "OTP timer edge at exactly 0"
  steps: Watch timer count to 0
  expected: Timer disappears, resend link + change number link appear

TC_EDGE_008:
  name: "T&C checkbox unchecked on Phone Entry"
  steps: Uncheck the terms checkbox on Screen 0
  expected: OTP CTA becomes disabled

TC_EDGE_009:
  name: "Navigate backward then forward"
  steps: Go to Screen 4 → back to Screen 2 → forward to Screen 4
  expected: All data preserved, no duplicate submissions

TC_EDGE_010:
  name: "Scenario trigger then clear"
  steps: Trigger any error scenario → clear scenario
  expected: Screen returns to happy path state

TC_EDGE_011:
  name: "Dashboard disconnect during operation"
  steps: Disconnect USB while dashboard is sending commands
  expected: Dashboard shows "No Device" status, commands fail gracefully

TC_EDGE_012:
  name: "Multiple rapid screen navigations"
  steps: Click screen buttons rapidly in dashboard
  expected: App navigates to final target without crash

TC_EDGE_013:
  name: "Phone number >10 digits"
  steps: Enter 11+ digits in phone field
  expected: Error shown "केवल 10 अंकों का नंबर डालें", CTA disabled

TC_EDGE_014:
  name: "ISP Agreement max pages"
  steps: Try uploading 8+ photos via camera or gallery
  expected: Upload capped at 7, user informed of limit

TC_EDGE_015:
  name: "Equipment photos max limit"
  steps: Try uploading 6+ equipment photos
  expected: Upload capped at 5, user informed of limit

TC_EDGE_016:
  name: "Account setup auto-progression timing"
  steps: Reach Screen 13, measure time before auto-advance
  expected: Screen auto-advances to 14 after exactly 3 seconds
```

### UAT Test Cases

```yaml
TC_UAT_001:
  name: "New partner completes full onboarding"
  persona: "Rajesh Kumar, Indore, Individual, first-time partner"
  flow: Pitch → Phone → OTP → Personal(1/3) → Location(2/3) → ₹2K(3/3) → KYC(1/5) → Bank(2/5) → ISP(3/5) → Photos(4/5) → Verification(5/5, approved) → Policy(1/7) → Tech Assessment(2/7, pass) → ₹20K(3/7) → Account Setup(4/5) → Onboarded(5/5)
  acceptance: All 16 screens visited (Pitch + 0-14), partner successfully onboarded

TC_UAT_002:
  name: "Partner with KYC dedup issue"
  persona: "Anil Verma, PAN already registered with another partner"
  flow: Pitch → Phone → OTP → Personal → Location → ₹2K → KYC (PAN dedup detected)
  acceptance: Clear deferred error message, progression blocked

TC_UAT_003:
  name: "Partner verification rejected"
  persona: "Deepak Jain, QA rejects application"
  flow: ... → Verification (rejected) → Auto refund initiated
  acceptance: ₹2,000 refund initiated, no re-upload option, 5-6 working days timeline shown

TC_UAT_004:
  name: "Partner with bank penny drop failure"
  persona: "Sunita Devi, wrong account number"
  flow: ... → Bank (penny drop fail) → Change details or Upload bank document
  acceptance: Bottom sheet shown with two options, supporting doc types listed

TC_UAT_005:
  name: "Partner with bank name mismatch"
  persona: "Mohit Patel, bank name different from KYC"
  flow: ... → Bank (name mismatch) → See comparison → Change details or Upload doc
  acceptance: Name comparison shown in bottom sheet, two resolution options

TC_UAT_006:
  name: "Partner with bank dedup"
  persona: "Kavita Singh, bank account already registered"
  flow: ... → Bank (dedup) → Change bank details only
  acceptance: Bottom sheet with only Change Bank Details (no upload option)

TC_UAT_007:
  name: "Partner with payment issues"
  persona: "Mohit Patel, UPI limit exceeded for ₹20K"
  flow: ... → ₹20K (failed) → Switch to NEFT → ₹20K (success) → Continue
  acceptance: Helpful error message, alternative payment suggestion, no money lost

TC_UAT_008:
  name: "Partner tech assessment rejected"
  persona: "Ravi Kumar, infrastructure not ready"
  flow: ... → Tech Assessment (rejected) → No refund, Talk to Us
  acceptance: No refund info, Talk to Us CTA with phone 7836811111

TC_UAT_009:
  name: "Hindi-first UX verification"
  persona: Any partner, Hindi-speaking
  flow: Complete entire flow in Hindi
  acceptance: All text meaningful in Hindi, no English-only screens, culturally appropriate

TC_UAT_010:
  name: "QA Review Dashboard workflow"
  persona: QA team member using QA Review Dashboard
  flow: Open qa-review.html → Search partner → Review docs → Approve/Reject
  acceptance: Dashboard updates app in real-time, correct screen shown

TC_UAT_011:
  name: "Control Dashboard workflow"
  persona: Admin using Control Dashboard
  flow: Open control.html → Navigate screens (16 tiles) → Trigger scenarios (28)
  acceptance: All 16 screens navigable, all scenarios triggerable
```

---

## DASHBOARD_SYSTEM

```yaml
dashboard:
  control_dashboard:
    location: dashboard/control.html
    purpose: Screen navigation, scenario triggering, language control
    server_port: 8092
    connection: ADB over USB/WiFi
    protocol: HTTP POST with JSON body
    status_check_interval_ms: 5000

    controls:
      - id: restart_app
        action: { action: "restart" }
        adb: "am force-stop com.wiom.csp && am start -n com.wiom.csp/.MainActivity"

      - id: reset_to_screen_0
        action: { action: "reset" }
        intent: "com.wiom.csp.RESET"

      - id: set_hindi
        action: { action: "lang", lang: "hi" }
        intent: "com.wiom.csp.LANG --es lang hi"

      - id: set_english
        action: { action: "lang", lang: "en" }
        intent: "com.wiom.csp.LANG --es lang en"

      - id: fill_all
        action: { action: "fill", mode: "filled" }
        intent: "com.wiom.csp.FILL --es mode filled"

      - id: empty_all
        action: { action: "fill", mode: "empty" }
        intent: "com.wiom.csp.FILL --es mode empty"

      - id: navigate_to_screen
        action: { action: "navigate", screen: N }
        intent: "com.wiom.csp.NAVIGATE --ei screen N"
        tiles: 16  # Pitch + 0-14

      - id: trigger_scenario
        action: { action: "scenario", name: "SCENARIO_NAME" }
        intent: "com.wiom.csp.SCENARIO --es name SCENARIO_NAME"
        scenarios: 28  # 9 categories (1 empty), 28 total scenarios

      - id: clear_scenario
        action: { action: "scenario", name: "NONE" }

      - id: dump_state
        action: { action: "data", mode: "DUMP_STATE" }
        intent: "com.wiom.csp.DATA --es mode DUMP_STATE"
        run_as: true

    screenshot:
      endpoint: "GET /screenshot"
      adb: "adb exec-out screencap -p"
      temp_file: "/tmp/csp_dash_screen.png"
      format: PNG

  qa_review_dashboard:
    location: dashboard/qa-review.html
    purpose: QA team reviews partner applications, approves/rejects
    features:
      - application_list: "List of all partner applications"
      - detail_view: "Individual application detail with documents"
      - approve_reject: "Approve or reject"
      - search_filter: "Search and filter applications"

    controls:
      - id: qa_approve
        action: { action: "qa", decision: "approved" }
        intent: "com.wiom.csp.QA --es action approved"

      - id: qa_reject
        action: { action: "qa", decision: "rejected" }
        intent: "com.wiom.csp.QA --es action rejected"

  bridge_receiver: "com.wiom.csp/.DashboardReceiver"
  intent_actions:
    - "com.wiom.csp.SCENARIO"
    - "com.wiom.csp.NAVIGATE"
    - "com.wiom.csp.LANG"
    - "com.wiom.csp.RESET"
    - "com.wiom.csp.FILL"
    - "com.wiom.csp.QA"
    - "com.wiom.csp.DATA"
```

---

## DESIGN_SYSTEM_TOKENS

```yaml
colors:
  primary: "#D9008D"
  primary_light: "#FFE5F6"
  primary_200: "#FFCCED"
  primary_300: "#FFB2E4"
  text: "#161021"
  text_secondary: "#665E75"
  hint: "#A7A1B2"
  surface: "#FAF9FC"
  bg_secondary: "#F1EDF7"
  bg_tertiary: "#E8E4F0"
  border: "#E8E4F0"
  border_input: "#D7D3E0"
  border_focus: "#352D42"
  positive: "#008043"
  positive_100: "#E1FAED"
  positive_200: "#C9F0DD"
  positive_300: "#A5E5C6"
  negative: "#D92130"
  negative_100: "#FFE5E7"
  negative_200: "#FFCCD0"
  warning: "#FF8000"
  warning_dark: "#B85C00"
  warning_100: "#FFE6CC"
  info: "#6D17CE"
  info_100: "#F1E5FF"
  info_200: "#E4CCFF"
  header: "#443152"
  dark: "#161021"

typography:
  family: "Noto Sans, Noto Sans Devanagari"
  headline_large: { size: 24sp, weight: Bold, line_height: 32sp }
  headline_medium: { size: 20sp, weight: Bold, line_height: 28sp }
  title_large: { size: 16sp, weight: Bold, line_height: 24sp }
  title_medium: { size: 14sp, weight: SemiBold, line_height: 20sp }
  body_large: { size: 14sp, weight: Normal, line_height: 20sp }
  body_medium: { size: 12sp, weight: Normal, line_height: 16sp }
  body_small: { size: 10sp, weight: Normal, line_height: 14sp }
  label_large: { size: 16sp, weight: Bold, line_height: 24sp }
  label_medium: { size: 12sp, weight: SemiBold, line_height: 16sp }
  label_small: { size: 10sp, weight: Bold, line_height: 14sp }

corner_radius:
  small: 8dp
  medium: 12dp
  large: 16dp
  pill: 888dp

elevation:
  level_1: "0 1px 3px rgba(0,0,0,0.15)"
  level_2: "0 2px 6px rgba(0,0,0,0.15)"
  level_4: "0 4px 12px rgba(0,0,0,0.15)"
  pink_glow: "0 4px 12px rgba(217,43,144,0.3)"

component_sizes:
  button_height: 48dp
  header_height: 48dp
  status_bar_height: 32dp
  otp_box: { width: 48dp, height: 56dp }
```

---

## FILE_STRUCTURE

```yaml
repo_root:
  - README.md
  - CLAUDE.md
  - PRD_AI_AGENT.md
  - apk/
    - csp_app.apk
  - prototype/
    - index.html (16 screens + error scenarios + admin dashboard)
  - dashboard/
    - control.html (screen navigation, scenario control)
    - qa-review.html (application list, detail view, approve/reject)
    - bridge.py (ADB bridge server on port 8092)
  - app/src/main/java/com/wiom/csp/
    - CspApplication.kt
    - MainActivity.kt
    - data/
      - OnboardingState.kt (global state singleton)
    - util/
      - Strings.kt (bilingual t() helper)
      - Validation.kt (form validation functions)
    - ui/
      - theme/
        - Color.kt
        - Type.kt
        - Shape.kt
        - Theme.kt
      - components/
        - Common.kt (20+ reusable composables)
      - screens/
        - OnboardingHost.kt (screen router + progress strip)
        - PitchScreen.kt (Pitch screen)
        - Phase1Screens.kt (Screens 0-4)
        - Phase2Screens.kt (Screens 5-9)
        - Phase3Screens.kt (Screens 10-14)
    - DashboardReceiver.kt (BroadcastReceiver for dashboard commands)
```
