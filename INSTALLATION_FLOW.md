# Installation Flow — Booking-to-Installation Workflow

This document covers the **13-step on-site installation flow** that a CSP/technician follows when installing internet service at a customer's premises. This is a separate flow from the CSP onboarding — it's what the partner does **after** they're onboarded and receive installation tasks.

## Overview

The installation flow is triggered when a CSP taps "Start Installation" on an **IN_PROGRESS INSTALL** task that's assigned to them. It walks them through every step from arrival to customer sign-off.

**Total Steps:** 13
**Trigger:** IN_PROGRESS INSTALL task (self-assigned)
**Completion:** Task marked as INSTALLED, pending activation verification

---

## The 13 Steps

### Step 1: Reached Customer
- **What:** Technician confirms arrival at customer's premises
- **Data shown:** Connection ID, Area, Task ID
- **CTA:** "I Have Reached the Customer"
- **Purpose:** Timestamps arrival for SLA tracking

### Step 2: Selfie Verification
- **What:** Camera capture of technician's selfie at location
- **Requires:** Camera permission, photo capture
- **Validation:** Cannot proceed without photo
- **Purpose:** Proof of physical presence at location

### Step 3: Aadhaar Photos
- **What:** Capture customer's Aadhaar card — front AND back
- **Requires:** Two separate camera captures
- **Validation:** Both front + back required to proceed
- **Purpose:** Customer identity verification / KYC compliance

### Step 4: Customer Payment
- **What:** Confirm payment collected from customer
- **Payment methods:** Cash collection, UPI/Online, Wallet deduction (pre-paid)
- **CTA:** "Payment Confirmed"
- **Purpose:** Revenue confirmation before proceeding with installation

### Step 5: Start Router
- **What:** Power on router and verify hardware readiness
- **Checklist (4 items):**
  1. Connect power cable to the router
  2. Wait for all LED indicators to stabilize
  3. Ensure WAN port is connected via fiber/ethernet
  4. Confirm LAN port connectivity (if wired)
- **CTA:** "Router is Ready"

### Step 6: Protocol Selection
- **What:** Select and configure ISP connection protocol
- **Options:** PPPoE / Static IP / DHCP (selectable chips)
- **Dynamic fields per protocol:**
  - **PPPoE:** Username, Password, Service Name (optional)
  - **Static IP:** IP Address, Gateway, Network/Subnet, Primary DNS, Secondary DNS
  - **DHCP:** Auto-configured (shows Interface: wan, Protocol: dhcp, Device: eth0.2, Metric: 10)
- **Validation:**
  - PPPoE: username + password required
  - Static IP: IP + gateway + network + DNS1 required
  - DHCP: no input needed

### Step 7: NetBox & VLAN Configuration
- **What:** Enter NetBox device ID and configure VLAN
- **Fields:**
  - NetBox ID (text input)
  - VLAN Mode: TAG / TRANSPARENT / UNTAG (selectable chips)
  - VLAN ID: Only shown when mode = TAG, range 128–1492
- **Validation:**
  - NetBox ID required
  - If TAG mode: VLAN ID must be 128–1492 (shows error text if out of range)
  - TRANSPARENT/UNTAG: no VLAN ID needed

### Step 8: WiFi Connect
- **What:** Verify router is broadcasting WiFi correctly
- **Shows:** Network name "wiom net"
- **States:** Not connected → Connected ✓ (green highlight)
- **CTA:** "Confirm WiFi Connected" → then "Continue"

### Step 9: Installation Photos
- **What:** Document the physical installation with 3 photos
- **Required photos:**
  1. Device Setup
  2. Wiring
  3. Power Plug
- **Validation:** All 3 photos required to proceed
- **Storage:** Saved to `install_photos/` cache directory

### Step 10: Optical Power Check
- **What:** Enter optical power reading from ONT/ONU device
- **Input:** dBm value (numeric)
- **Signal quality indicator:**
  - **Good:** -8 dBm to -25 dBm (green "Signal Good" badge)
  - **Weak:** < -25 dBm (yellow "Signal Weak — may cause issues")
  - **Too Strong:** > -8 dBm (yellow "Signal Too Strong — check attenuator")
- **Note:** Can proceed with any reading (even weak/strong) — it's informational

### Step 11: Speed Test
- **What:** Verify connection quality
- **Two modes:**
  1. **Auto test:** Tap play button → 3-second simulation → fills in 85.4/42.1 Mbps
  2. **Manual entry:** Download (Mbps) + Upload (Mbps) text fields
- **Results display:** Green card showing ↓ Download and ↑ Upload side by side
- **Validation:** Both download + upload values required

### Step 12: Happy Code
- **What:** 4-digit OTP from customer confirming satisfaction
- **UI:** 4 individual digit boxes (like OTP input) + hidden text field for actual input
- **Note text:** "The customer acknowledges that the internet will be fully ready within 2 days."
- **Validation:** Exactly 4 digits required
- **CTA:** "Verify & Complete"

### Step 13: Installation Complete
- **What:** Success screen with summary
- **Shows:**
  - Large green checkmark
  - "Installation Complete!" heading
  - Task ID, Connection ID, Area, Status: INSTALLED
  - "Pending activation verification" note
- **CTA:** "Back to Tasks"
- **Backend action:** Calls `finishInstallation(taskId)` → fires INSTALL action → marks task as INSTALLED

---

## Architecture & Integration

### How it fits in the app

```
HomeScreen
├── Normal home content (stats, tasks, etc.)
└── Installation overlay (when installationTaskId is set)
    └── InstallationFlowScreen(task, onBack, onComplete)
        ├── InstallationTopBar (step label, progress bar, back nav)
        └── Step composables (13 steps via when() block)
```

### State Management

```kotlin
// In HomeViewModel (HomeUiState)
val installationTaskId: String? = null  // null = no installation active

// Actions
startInstallation(taskId)   // Sets installationTaskId, shows overlay
finishInstallation(taskId)  // Fires INSTALL action, clears installationTaskId
cancelInstallation()        // Clears installationTaskId, returns to home
```

### Step state is local

All step data (URIs, text fields, selections) lives in `remember` variables inside `InstallationFlowScreen`. This means:
- **State is lost if the user leaves the flow** — this is intentional for the prototype
- Production version should persist to Room/DataStore for crash recovery

### Camera / FileProvider setup

```xml
<!-- res/xml/file_paths.xml -->
<paths>
    <cache-path name="proof_images" path="proof_images/" />
    <cache-path name="install_photos" path="install_photos/" />  <!-- Required! -->
</paths>
```

**Known crash:** If `install_photos` path is missing from `file_paths.xml`, the app crashes when opening the installation flow because `PhotoCaptureButton` creates files in that directory.

**Fix applied:** Made FileProvider URI creation lazy (deferred to camera launch) instead of eager during composition.

### Navigation trigger

```kotlin
// In TaskDetailScreen.kt and TaskCard.kt
// For IN_PROGRESS, self-assigned INSTALL tasks:
CTA label: "Start Installation"  (was "Mark Installed")
Action: "START_INSTALLATION"      (was "INSTALL")
```

---

## Files Reference

### Created
| File | Lines | Description |
|------|-------|-------------|
| `ui/installation/InstallationFlowScreen.kt` | ~1426 | All 13 steps + shared UI helpers (StepCard, InputField, PhotoCaptureButton, SelectableChip, etc.) |

### Modified (in the original CSP app codebase)
| File | Change |
|------|--------|
| `HomeViewModel.kt` | Added `installationTaskId` state, `startInstallation()`, `finishInstallation()`, `cancelInstallation()` |
| `HomeScreen.kt` | Added installation flow overlay |
| `TaskDetailScreen.kt` | Changed CTA for IN_PROGRESS INSTALL tasks |
| `TaskCard.kt` | Same CTA change |
| `MockInterceptor.kt` | Added TSK-4006 mock task (INSTALL, IN_PROGRESS, self-assigned) |
| `res/xml/file_paths.xml` | Added `install_photos` cache path |

### Mock data for testing
```
Task ID: TSK-4006
Type: INSTALL
Status: IN_PROGRESS
Connection: CONN-88234
Area: Goregaon East
Assignment: Self-assigned to logged-in CSP
```

---

## Original Implementation Source

The full Kotlin implementation lives at:
```
/Users/wiom/Desktop/11 mar 2026/Claude-Generated/csp-design-audit/android/
  app/src/main/java/com/wiom/csp/ui/installation/InstallationFlowScreen.kt
```

APK with this flow:
```
/Users/wiom/Desktop/Wiom Claude Work/WiomCSP-InstallationFlow-dev-debug.apk
Package: com.wiom.csp.dev (dev flavor)
```

---

## What to Build Next (Production)

- [ ] Persist step state to Room DB for crash/background recovery
- [ ] Real camera integration with image compression before upload
- [ ] Upload photos to S3/backend as they're captured (not all at end)
- [ ] Real speed test integration (Ookla SDK or custom)
- [ ] Happy Code verification against backend OTP
- [ ] GPS coordinates capture at "Reached Customer" step
- [ ] Offline support — queue completion if no network
- [ ] Add Hindi translations (currently English-only)
- [ ] Timer/SLA tracking from arrival to completion
- [ ] Backend sync for protocol/VLAN config to router management system
