package com.wiom.csp.domain.model

/**
 * Domain models for CSP Onboarding — 15-screen flow.
 */

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
    // KYC
    val panNumber: String = "",
    val panUploaded: Boolean = false,
    val aadhaarNumber: String = "",
    val aadhaarFrontUploaded: Boolean = false,
    val aadhaarBackUploaded: Boolean = false,
    val gstNumber: String = "",
    val gstUploaded: Boolean = false,
    // Bank (3 fields only — no account holder name or bank name)
    val bankAccountNumber: String = "",
    val bankIfsc: String = "",
    val bankVerified: Boolean = false,
    val bankSupportDocUploaded: Boolean = false,
    // ISP Agreement
    val ispAgreementUploaded: Boolean = false,
    val ispPageCount: Int = 0,
    val ispUploadType: String = "", // "PDF" or "PHOTOS"
    // Shop & Equipment
    val shopPhotoUploaded: Boolean = false,
    val equipmentPhotoUploaded: Boolean = false,
    val equipmentPhotoCount: Int = 0,
    // Verification
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    // Onboarding fee
    val onboardFeePaid: Boolean = false,
    // Tech assessment
    val techAssessmentStatus: VerificationStatus = VerificationStatus.PENDING,
    // Account setup
    val isOnboarded: Boolean = false,
)

enum class VerificationStatus { PENDING, APPROVED, REJECTED }
