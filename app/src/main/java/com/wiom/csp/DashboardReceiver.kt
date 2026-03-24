package com.wiom.csp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.data.QuizQuestion
import com.wiom.csp.data.Scenario
import com.wiom.csp.data.TrainingModule
import com.wiom.csp.util.Lang
import org.json.JSONArray

// NOTE: DashboardReceiver uses OnboardingState for prototype testing.
// In production, replace with proper ViewModel/Repository communication.
class DashboardReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "com.wiom.csp.SCENARIO" -> {
                val name = intent.getStringExtra("name") ?: "NONE"
                val scenario = try { Scenario.valueOf(name) } catch (_: Exception) { Scenario.NONE }
                if (scenario == Scenario.NONE) OnboardingState.clearScenario()
                else OnboardingState.triggerScenario(scenario)
            }
            "com.wiom.csp.NAVIGATE" -> {
                val screen = intent.getIntExtra("screen", -1)
                if (screen in 0 until OnboardingState.TOTAL_SCREENS) OnboardingState.goTo(screen)
            }
            "com.wiom.csp.LANG" -> {
                val lang = intent.getStringExtra("lang") ?: "toggle"
                when (lang) {
                    "hi" -> Lang.isHindi = true
                    "en" -> Lang.isHindi = false
                    else -> Lang.toggle()
                }
            }
            "com.wiom.csp.RESET" -> {
                OnboardingState.clearScenario()
                OnboardingState.currentScreen = 0
                OnboardingState.verificationRejected = false
                OnboardingState.techAssessmentRejected = false
            }
            "com.wiom.csp.FILL" -> {
                val mode = intent.getStringExtra("mode") ?: "empty"
                if (mode == "filled") OnboardingState.fillAllScreens()
                else OnboardingState.emptyAllScreens()
            }
            "com.wiom.csp.TRAINING" -> {
                val configJson = intent.getStringExtra("config") ?: "[]"
                try {
                    val arr = JSONArray(configJson)
                    val modules = mutableListOf<TrainingModule>()
                    for (i in 0 until arr.length()) {
                        val m = arr.getJSONObject(i)
                        val questions = mutableListOf<QuizQuestion>()
                        val qArr = m.getJSONArray("questions")
                        for (j in 0 until qArr.length()) {
                            val q = qArr.getJSONObject(j)
                            val opts = mutableListOf<Pair<String, String>>()
                            val oArr = q.getJSONArray("options")
                            for (k in 0 until oArr.length()) {
                                val o = oArr.getJSONArray(k)
                                opts.add(o.getString(0) to o.getString(1))
                            }
                            questions.add(QuizQuestion(
                                questionHi = q.getString("questionHi"),
                                questionEn = q.getString("questionEn"),
                                options = opts,
                                correctIndex = q.getInt("correctIndex"),
                                hintHi = q.getString("hintHi"),
                                hintEn = q.getString("hintEn"),
                            ))
                        }
                        modules.add(TrainingModule(
                            id = m.getString("id"),
                            titleHi = m.getString("titleHi"),
                            titleEn = m.getString("titleEn"),
                            subtitleHi = m.optString("subtitleHi", ""),
                            subtitleEn = m.optString("subtitleEn", ""),
                            icon = m.optString("icon", "\uD83D\uDCDA"),
                            videoUrl = m.optString("videoUrl", ""),
                            questions = questions,
                        ))
                    }
                    OnboardingState.trainingModules.clear()
                    OnboardingState.trainingModules.addAll(modules)
                    OnboardingState.completedModuleIds.clear()
                } catch (_: Exception) { }
            }
            "com.wiom.csp.VERIFICATION" -> {
                val action = intent.getStringExtra("action") ?: "approved"
                when (action) {
                    "approved" -> {
                        OnboardingState.verificationRejected = false
                        OnboardingState.goTo(10) // Move to Policy, Payout & SLA
                    }
                    "rejected" -> {
                        OnboardingState.verificationRejected = true
                        OnboardingState.goTo(9) // Stay on Verification screen, show rejected
                    }
                }
            }
            "com.wiom.csp.TECHASSESSMENT" -> {
                val action = intent.getStringExtra("action") ?: "approved"
                when (action) {
                    "approved" -> {
                        OnboardingState.techAssessmentRejected = false
                        OnboardingState.goTo(13) // Move to CSP Account Setup
                    }
                    "rejected" -> {
                        OnboardingState.techAssessmentRejected = true
                        OnboardingState.goTo(12) // Stay on Tech Assessment screen, show rejected
                    }
                }
            }
            "com.wiom.csp.POLICYQUIZ" -> {
                val configJson = intent.getStringExtra("config") ?: "[]"
                try {
                    val arr = JSONArray(configJson)
                    val questions = mutableListOf<QuizQuestion>()
                    for (i in 0 until arr.length()) {
                        val q = arr.getJSONObject(i)
                        val opts = mutableListOf<Pair<String, String>>()
                        val oArr = q.getJSONArray("options")
                        for (k in 0 until oArr.length()) {
                            val o = oArr.getJSONArray(k)
                            opts.add(o.getString(0) to o.getString(1))
                        }
                        questions.add(QuizQuestion(
                            questionHi = q.getString("questionHi"),
                            questionEn = q.getString("questionEn"),
                            options = opts,
                            correctIndex = q.getInt("correctIndex"),
                            hintHi = q.getString("hintHi"),
                            hintEn = q.getString("hintEn"),
                        ))
                    }
                    // Policy quiz questions are stored separately — they could be
                    // consumed by the Screen 15 composable via a dedicated holder.
                    // For now we expose them through a training module with reserved id.
                    val policyModule = TrainingModule(
                        id = "policy_quiz",
                        titleHi = "\u092A\u0949\u0932\u093F\u0938\u0940 Quiz",
                        titleEn = "Policy Quiz",
                        subtitleHi = "\u0928\u0940\u0924\u093F \u0914\u0930 SLA \u092A\u094D\u0930\u0936\u094D\u0928",
                        subtitleEn = "Policy & SLA Questions",
                        icon = "\uD83D\uDCCB",
                        videoUrl = "",
                        questions = questions,
                    )
                    // Replace existing policy_quiz module if present, else add
                    val idx = OnboardingState.trainingModules.indexOfFirst { it.id == "policy_quiz" }
                    if (idx >= 0) OnboardingState.trainingModules[idx] = policyModule
                    else OnboardingState.trainingModules.add(policyModule)
                    OnboardingState.policyQuizPassed = false
                } catch (_: Exception) { }
            }
        }
    }
}
