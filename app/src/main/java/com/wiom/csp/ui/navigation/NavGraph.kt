package com.wiom.csp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.wiom.csp.ui.screens.*
import com.wiom.csp.ui.viewmodel.*

@Composable
fun OnboardingNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.PITCH) {
        composable(Routes.PITCH) {
            PitchScreen(onGetStarted = { navController.navigate(Routes.PHONE) { popUpTo(Routes.PITCH) { inclusive = true } } })
        }
        composable(Routes.PHONE) {
            val vm: PhoneViewModel = hiltViewModel()
            PhoneEntryScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.OTP) },
            )
        }
        composable(Routes.OTP) {
            val vm: OtpViewModel = hiltViewModel()
            OtpTncScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.PERSONAL) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.PERSONAL) {
            val vm: PersonalInfoViewModel = hiltViewModel()
            PersonalInfoScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.LOCATION) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.LOCATION) {
            val vm: LocationViewModel = hiltViewModel()
            LocationScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.REG_FEE) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.REG_FEE) {
            val vm: PaymentViewModel = hiltViewModel()
            RegFeeScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.KYC) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.KYC) {
            val vm: KycViewModel = hiltViewModel()
            KycScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.BANK) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.BANK) {
            val vm: BankViewModel = hiltViewModel()
            BankDedupScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.ISP_AGREEMENT) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.ISP_AGREEMENT) {
            val vm: IspAgreementViewModel = hiltViewModel()
            IspAgreementScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.PHOTOS) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.PHOTOS) {
            val vm: PhotosViewModel = hiltViewModel()
            ShopPhotosScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.VERIFICATION) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.VERIFICATION) {
            val vm: VerificationViewModel = hiltViewModel()
            VerificationScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.POLICY_SLA) },
            )
        }
        composable(Routes.POLICY_SLA) {
            PolicySlaScreen(
                onNext = { navController.navigate(Routes.ONBOARD_FEE) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.ONBOARD_FEE) {
            val vm: PaymentViewModel = hiltViewModel()
            OnboardingFeeScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.TECH_ASSESSMENT) },
            )
        }
        composable(Routes.TECH_ASSESSMENT) {
            val vm: TechAssessmentViewModel = hiltViewModel()
            TechAssessmentScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.ACCOUNT_SETUP) },
            )
        }
        composable(Routes.ACCOUNT_SETUP) {
            val vm: AccountSetupViewModel = hiltViewModel()
            CspAccountSetupScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.TRAINING) },
            )
        }
        composable(Routes.TRAINING) {
            val vm: TrainingViewModel = hiltViewModel()
            TrainingScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.POLICY_QUIZ) },
            )
        }
        composable(Routes.POLICY_QUIZ) {
            val vm: PolicyQuizViewModel = hiltViewModel()
            PolicyQuizScreen(
                viewModel = vm,
                onNext = { navController.navigate(Routes.GO_LIVE) { popUpTo(Routes.PITCH) { inclusive = true } } },
                onBackToTraining = { navController.popBackStack() },
            )
        }
        composable(Routes.GO_LIVE) {
            GoLiveScreen()
        }
    }
}
