package com.wiom.csp.domain.model

data class CspApplication(
    val id: String = "",
    val phone: String = "",
    val tncAccepted: Boolean = false,
    val otpVerified: Boolean = false,
    val personalName: String = "",
    val email: String = "",
    val entityType: String = "",
    val tradeName: String = "",
    val state: String = "Madhya Pradesh",
    val city: String = "",
    val pincode: String = "",
    val address: String = "",
    val gpsCoordinates: String = "",
    val regFeePaid: Boolean = false,
    val panUploaded: Boolean = false,
    val aadhaarFrontUploaded: Boolean = false,
    val aadhaarBackUploaded: Boolean = false,
    val gstUploaded: Boolean = false,
    val ispAgreementUploaded: Boolean = false,
    val shopPhotoUploaded: Boolean = false,
    val equipmentPhotoUploaded: Boolean = false,
    val bankAccountHolder: String = "",
    val bankName: String = "",
    val bankAccountNumber: String = "",
    val bankIfsc: String = "",
    val bankVerified: Boolean = false,
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val verificationReason: String = "",
    val onboardFeePaid: Boolean = false,
    val techAssessmentStatus: VerificationStatus = VerificationStatus.PENDING,
    val techAssessmentReason: String = "",
    val trainingModulesCompleted: Int = 0,
    val policyQuizPassed: Boolean = false,
    val isLive: Boolean = false,
)

enum class VerificationStatus { PENDING, APPROVED, REJECTED }

data class TrainingModule(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val subtitleHi: String,
    val subtitleEn: String,
    val icon: String,
    val questions: List<QuizQuestion>,
)

data class QuizQuestion(
    val questionHi: String,
    val questionEn: String,
    val options: List<Pair<String, String>>,
    val correctIndex: Int,
    val hintHi: String,
    val hintEn: String,
)
