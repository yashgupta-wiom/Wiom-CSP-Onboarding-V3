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
                OnboardingState.qaRejected = false
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
                            icon = m.optString("icon", "📚"),
                            videoUrl = m.optString("videoUrl", ""),
                            questions = questions,
                        ))
                    }
                    OnboardingState.trainingModules.clear()
                    OnboardingState.trainingModules.addAll(modules)
                    OnboardingState.completedModuleIds.clear()
                } catch (_: Exception) { }
            }
            "com.wiom.csp.QA" -> {
                val action = intent.getStringExtra("action") ?: "approved"
                when (action) {
                    "approved" -> {
                        OnboardingState.qaRejected = false
                        OnboardingState.goTo(7) // Move to Policy & Rate Card
                    }
                    "rejected" -> {
                        OnboardingState.qaRejected = true
                        OnboardingState.goTo(6) // Stay on QA screen, show rejected
                    }
                }
            }
        }
    }
}
