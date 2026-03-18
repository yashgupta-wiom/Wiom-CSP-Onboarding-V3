package com.wiom.csp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.ui.theme.*
import com.wiom.csp.util.Lang
import com.wiom.csp.util.t

@Composable
fun OnboardingHost() {
    val screen = OnboardingState.currentScreen
    val meta = OnboardingState.screenMetas[screen]

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        // Screen content
        AnimatedContent(
            targetState = screen,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it / 3 } + fadeIn() togetherWith
                        slideOutHorizontally { -it / 3 } + fadeOut()
                } else {
                    slideInHorizontally { -it / 3 } + fadeIn() togetherWith
                        slideOutHorizontally { it / 3 } + fadeOut()
                }
            },
            label = "screen_transition",
        ) { targetScreen ->
            when (targetScreen) {
                0 -> PhoneEntryScreen(onNext = { OnboardingState.next() })
                1 -> OtpScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                2 -> PersonalInfoScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                3 -> LocationScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                4 -> KycScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                5 -> RegFeeScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                6 -> QaInvestigationScreen(onNext = { OnboardingState.next() })
                7 -> PolicyRateCardScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                8 -> BankDedupScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                9 -> AgreementScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                10 -> TechReviewScreen(onNext = { OnboardingState.next() })
                11 -> OnboardingFeeScreen(onNext = { OnboardingState.next() })
                12 -> FinancialSetupScreen(onNext = { OnboardingState.next() })
                13 -> TrainingScreen(onNext = { OnboardingState.next() })
                14 -> GoLiveScreen()
            }
        }
    }
}
