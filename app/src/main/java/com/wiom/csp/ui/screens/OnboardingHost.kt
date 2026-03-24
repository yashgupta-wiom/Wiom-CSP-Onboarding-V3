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
    val pitchDismissed = OnboardingState.pitchDismissed
    val screen = OnboardingState.currentScreen

    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        if (!pitchDismissed) {
            // Show Pitch screen before the main flow
            PitchScreen(onGetStarted = {
                OnboardingState.pitchDismissed = true
            })
        } else {
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
                    4 -> RegFeeScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                    5 -> KycScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                    6 -> BankDedupScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                    7 -> IspAgreementScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                    8 -> ShopPhotosScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                    9 -> VerificationScreen(onNext = { OnboardingState.next() })
                    10 -> PolicyRateCardScreen(onNext = { OnboardingState.next() }, onBack = { OnboardingState.prev() })
                    11 -> OnboardingFeeScreen(onNext = { OnboardingState.next() })
                    12 -> TechAssessmentScreen(onNext = { OnboardingState.next() })
                    13 -> AccountSetupScreen(onNext = { OnboardingState.next() })
                    14 -> TrainingScreen(onNext = { OnboardingState.next() })
                    15 -> PolicyQuizScreen(onNext = { OnboardingState.next() })
                    16 -> GoLiveScreen()
                }
            }
        }
    }
}
