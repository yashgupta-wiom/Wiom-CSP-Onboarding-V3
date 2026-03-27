package com.wiom.csp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.Scenario
import com.wiom.csp.util.Lang

/**
 * DashboardReceiver — Broadcast receiver for testing via adb or external dashboards.
 *
 * Supports 15-screen flow (Screens 0-14, plus Pitch).
 * Screen order: Phone(0) → OTP(1) → Personal(2) → Location(3) → RegFee(4)
 * → KYC(5) → Bank(6) → ISP(7) → ShopPhotos(8) → Verification(9)
 * → PolicySLA(10) → TechAssessment(11) → OnboardFee(12)
 * → AccountSetup(13) → SuccessfullyOnboarded(14)
 *
 * Example adb commands:
 *   adb shell am broadcast -a com.wiom.csp.SCENARIO -e name BANK_PENNYDROP_FAIL
 *   adb shell am broadcast -a com.wiom.csp.NAVIGATE --ei screen 5
 *   adb shell am broadcast -a com.wiom.csp.LANG -e lang hi
 *   adb shell am broadcast -a com.wiom.csp.FILL -e mode filled
 *   adb shell am broadcast -a com.wiom.csp.RESET
 */
class DashboardReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {

            // Trigger an error scenario
            "com.wiom.csp.SCENARIO" -> {
                val name = intent.getStringExtra("name") ?: "NONE"
                val scenario = try { Scenario.valueOf(name) } catch (_: Exception) { Scenario.NONE }
                if (scenario == Scenario.NONE) OnboardingState.clearScenario()
                else OnboardingState.triggerScenario(scenario)
            }

            // Navigate to a specific screen (0-14)
            "com.wiom.csp.NAVIGATE" -> {
                val screen = intent.getIntExtra("screen", -1)
                if (screen in 0 until OnboardingState.TOTAL_SCREENS) OnboardingState.goTo(screen)
            }

            // Toggle or set language
            "com.wiom.csp.LANG" -> {
                val lang = intent.getStringExtra("lang") ?: "toggle"
                when (lang) {
                    "hi" -> Lang.isHindi = true
                    "en" -> Lang.isHindi = false
                    else -> Lang.toggle()
                }
            }

            // Reset to initial state
            "com.wiom.csp.RESET" -> {
                OnboardingState.clearScenario()
                OnboardingState.currentScreen = 0
                OnboardingState.verificationRejected = false
                OnboardingState.techAssessmentRejected = false
            }

            // Fill or empty all form data
            "com.wiom.csp.FILL" -> {
                val mode = intent.getStringExtra("mode") ?: "empty"
                if (mode == "filled") OnboardingState.fillAllScreens()
                else OnboardingState.emptyAllScreens()
            }

            // Set verification decision (approved/rejected)
            "com.wiom.csp.VERIFICATION" -> {
                val action = intent.getStringExtra("action") ?: "approved"
                when (action) {
                    "approved" -> {
                        OnboardingState.verificationRejected = false
                        OnboardingState.goTo(10) // Move to Policy & SLA
                    }
                    "rejected" -> {
                        OnboardingState.verificationRejected = true
                        OnboardingState.goTo(9) // Stay on Verification, show rejected
                    }
                }
            }

            // QA decision (legacy dashboard compatibility)
            "com.wiom.csp.QA" -> {
                val decision = intent.getStringExtra("decision") ?: "approved"
                when (decision) {
                    "approved" -> {
                        OnboardingState.verificationRejected = false
                        OnboardingState.goTo(10)
                    }
                    "rejected" -> {
                        OnboardingState.verificationRejected = true
                        OnboardingState.goTo(9)
                    }
                }
            }

            // Set tech assessment decision (approved/rejected)
            "com.wiom.csp.TECHASSESSMENT" -> {
                val action = intent.getStringExtra("action") ?: "approved"
                when (action) {
                    "approved" -> {
                        OnboardingState.techAssessmentRejected = false
                        OnboardingState.goTo(12) // Move to Onboarding Fee
                    }
                    "rejected" -> {
                        OnboardingState.techAssessmentRejected = true
                        OnboardingState.goTo(11) // Stay on Tech Assessment, show rejected
                    }
                }
            }
        }
    }
}
