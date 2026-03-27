package com.wiom.csp.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ============================================================================
 * WIOM CSP ONBOARDING — API CONTRACT DEFINITIONS
 * ============================================================================
 *
 * This file defines ALL request/response data classes for the Wiom CSP
 * Onboarding backend APIs. It covers the full 15-screen + Pitch flow.
 *
 * Backend team: implement endpoints exactly as specified here.
 * Mobile team: consume these contracts via WiomApiService (Retrofit interface).
 *
 * Base URL: /api/v1/
 * Auth: Bearer token in Authorization header (obtained from OTP verify)
 * Content-Type: application/json (except file uploads which use multipart/form-data)
 *
 * Last updated: 2026-03-26
 * ============================================================================
 */


// ════════════════════════════════════════════════════════════════════════════
// SECTION 1: ENUMS — Status values used across multiple endpoints
// ════════════════════════════════════════════════════════════════════════════

/** Payment outcome — returned by both registration fee and onboarding fee endpoints. */
@Serializable
enum class PaymentStatus {
    @SerialName("SUCCESS") SUCCESS,
    @SerialName("FAILED") FAILED,
    @SerialName("TIMEOUT") TIMEOUT
}

/** Verification and tech-assessment outcome. */
@Serializable
enum class ReviewStatus {
    @SerialName("PENDING") PENDING,
    @SerialName("APPROVED") APPROVED,
    @SerialName("REJECTED") REJECTED
}

/** Bank verification outcome — more granular than a simple pass/fail. */
@Serializable
enum class BankVerificationStatus {
    @SerialName("SUCCESS") SUCCESS,
    @SerialName("PENNY_DROP_FAIL") PENNY_DROP_FAIL,
    @SerialName("NAME_MISMATCH") NAME_MISMATCH,
    @SerialName("DEDUP") DEDUP
}

/** KYC document types accepted for upload. */
@Serializable
enum class DocumentType {
    @SerialName("PAN_CARD") PAN_CARD,
    @SerialName("AADHAAR_FRONT") AADHAAR_FRONT,
    @SerialName("AADHAAR_BACK") AADHAAR_BACK,
    @SerialName("GST_CERTIFICATE") GST_CERTIFICATE
}

/** ISP agreement upload format. */
@Serializable
enum class IspUploadType {
    @SerialName("PDF") PDF,
    @SerialName("PHOTOS") PHOTOS
}

/** Account setup progress. */
@Serializable
enum class AccountSetupStatus {
    @SerialName("IN_PROGRESS") IN_PROGRESS,
    @SerialName("COMPLETED") COMPLETED,
    @SerialName("FAILED") FAILED
}

/** Refund progress. */
@Serializable
enum class RefundStatus {
    @SerialName("SUCCESS") SUCCESS,
    @SerialName("IN_PROGRESS") IN_PROGRESS,
    @SerialName("FAILED") FAILED
}


// ════════════════════════════════════════════════════════════════════════════
// SECTION 2: OTP APIs — Screens 0 (Phone) & 1 (OTP)
// ════════════════════════════════════════════════════════════════════════════
// Flow: User enters phone → receives OTP via SMS → verifies OTP → gets token.

/** POST /api/v1/otp/send — Send OTP to a phone number. */
@Serializable
data class SendOtpRequest(
    val phone: String   // 10-digit Indian mobile number (without +91)
)

@Serializable
data class SendOtpResponse(
    val success: Boolean,
    val message: String  // e.g. "OTP sent successfully" or error reason
)

/** POST /api/v1/otp/verify — Verify the OTP entered by the user. */
@Serializable
data class VerifyOtpRequest(
    val phone: String,
    val otp: String      // 6-digit OTP
)

@Serializable
data class VerifyOtpResponse(
    val success: Boolean,
    val token: String,       // JWT auth token — must be sent in all subsequent requests
    val isNewUser: Boolean   // true = first time, false = returning applicant
)

/** POST /api/v1/otp/resend — Resend OTP (e.g. if previous one expired). */
@Serializable
data class ResendOtpRequest(
    val phone: String
)

@Serializable
data class ResendOtpResponse(
    val success: Boolean,
    val message: String
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 3: REGISTRATION APIs — Screens 2 (Personal), 3 (Location), 4 (Fee)
// ════════════════════════════════════════════════════════════════════════════
// Flow: Check phone dedup → personal info → location → pay ₹2,000 registration fee.

/** POST /api/v1/registration/check-phone — Check if phone is already registered. */
@Serializable
data class CheckPhoneRequest(
    val phone: String
)

@Serializable
data class CheckPhoneResponse(
    val isDuplicate: Boolean,         // true = this phone is already in the system
    val maskedPhone: String? = null   // e.g. "98****1234" — shown if duplicate found
)

/** POST /api/v1/registration/personal-info — Save personal details (Screen 2). */
@Serializable
data class PersonalInfoRequest(
    val token: String,
    val personalName: String,   // Full name as on PAN/Aadhaar
    val email: String,          // Valid email address
    val entityType: String,     // "Individual" or "Company"
    val tradeName: String       // Shop/business name
)

@Serializable
data class PersonalInfoResponse(
    val success: Boolean,
    val applicationId: String   // Unique ID for this onboarding application — used in ALL subsequent calls
)

/** POST /api/v1/registration/location — Save location details (Screen 3). */
@Serializable
data class LocationRequest(
    val token: String,
    val applicationId: String,
    val state: String,
    val city: String,
    val pincode: String,      // 6-digit Indian pincode
    val address: String,      // Full street address
    val gpsLat: Double,       // GPS latitude from device
    val gpsLng: Double        // GPS longitude from device
)

@Serializable
data class LocationResponse(
    val success: Boolean
)

/**
 * POST /api/v1/registration/payment — Pay ₹2,000 registration fee (Screen 4).
 *
 * BUSINESS RULE: This fee is auto-refunded if the application is rejected
 * at the verification stage (Screen 9). See RefundStatusResponse.
 */
@Serializable
data class RegistrationPaymentRequest(
    val token: String,
    val applicationId: String,
    val amount: Int,              // Always 2000 (₹2,000) — validated server-side
    val paymentMethod: String     // e.g. "UPI", "CARD", "NET_BANKING"
)

@Serializable
data class RegistrationPaymentResponse(
    val success: Boolean,
    val transactionId: String,    // Unique payment transaction reference
    val status: PaymentStatus     // SUCCESS / FAILED / TIMEOUT
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 4: KYC APIs — Screen 5 (KYC has 3 sub-stages: PAN → Aadhaar → GST)
// ════════════════════════════════════════════════════════════════════════════
// Flow: Validate document number → check for duplicates → upload document photo.

/** POST /api/v1/kyc/validate-pan — Validate PAN number and check duplicates. */
@Serializable
data class ValidatePanRequest(
    val token: String,
    val applicationId: String,
    val panNumber: String       // 10-character PAN (e.g. ABCDE1234F)
)

@Serializable
data class ValidatePanResponse(
    val isValid: Boolean,                   // true = valid PAN format/exists
    val isDuplicate: Boolean,               // true = PAN already used by another CSP
    val duplicateAccountPhone: String? = null  // Masked phone of existing account (if duplicate)
)

/** POST /api/v1/kyc/validate-aadhaar — Validate Aadhaar number and check duplicates. */
@Serializable
data class ValidateAadhaarRequest(
    val token: String,
    val applicationId: String,
    val aadhaarNumber: String   // 12-digit Aadhaar number
)

@Serializable
data class ValidateAadhaarResponse(
    val isValid: Boolean,
    val isDuplicate: Boolean,
    val duplicateAccountPhone: String? = null
)

/** POST /api/v1/kyc/validate-gst — Validate GST number, check duplicates, match with PAN. */
@Serializable
data class ValidateGstRequest(
    val token: String,
    val applicationId: String,
    val gstNumber: String,      // 15-character GSTIN
    val panNumber: String       // Must match the PAN submitted earlier
)

@Serializable
data class ValidateGstResponse(
    val isValid: Boolean,
    val isDuplicate: Boolean,
    val panMatch: Boolean,                  // true = GST's embedded PAN matches submitted PAN
    val duplicateAccountPhone: String? = null
)

/**
 * POST /api/v1/kyc/upload-document — Upload a KYC document photo.
 *
 * NOTE: This is a multipart/form-data request, NOT JSON.
 * The file is sent as a MultipartBody.Part.
 * Other fields (token, applicationId, documentType) are sent as @Part strings.
 */
@Serializable
data class UploadDocumentResponse(
    val success: Boolean,
    val documentId: String   // Server-assigned ID for the uploaded document
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 5: BANK APIs — Screen 6 (Bank Details)
// ════════════════════════════════════════════════════════════════════════════
// Flow: Enter account + IFSC → penny-drop verification → optional supporting doc upload.

/**
 * POST /api/v1/bank/verify — Verify bank account via penny drop.
 *
 * Backend performs:
 * 1. Penny drop verification (₹1 transfer to confirm account is active)
 * 2. Name matching (account holder name vs registered name)
 * 3. Deduplication (check if same bank account used by another CSP)
 */
@Serializable
data class BankVerifyRequest(
    val token: String,
    val applicationId: String,
    val accountNumber: String,   // Bank account number
    val ifscCode: String         // 11-character IFSC code (e.g. SBIN0001234)
)

@Serializable
data class BankVerifyResponse(
    val success: Boolean,
    val verificationStatus: BankVerificationStatus,  // SUCCESS / PENNY_DROP_FAIL / NAME_MISMATCH / DEDUP
    val accountHolderName: String? = null,            // Name returned by bank (for display)
    val bankName: String? = null,                     // Bank name derived from IFSC
    val duplicateAccountPhone: String? = null          // Shown if DEDUP — masked phone of existing account
)

/**
 * POST /api/v1/bank/upload-supporting-doc — Upload supporting document if name mismatch.
 *
 * NOTE: Multipart/form-data request.
 * Used when bank account holder name doesn't match the registered name.
 * CSP uploads a cancelled cheque or passbook page as proof.
 */
@Serializable
data class BankSupportingDocResponse(
    val success: Boolean,
    val documentId: String
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 6: ISP AGREEMENT API — Screen 7 (ISP Agreement Upload)
// ════════════════════════════════════════════════════════════════════════════
// CSP uploads their ISP agreement — either as a single PDF or multiple photos.

/**
 * POST /api/v1/isp/upload — Upload ISP agreement document(s).
 *
 * NOTE: Multipart/form-data request.
 * - If uploadType is PDF: single file expected.
 * - If uploadType is PHOTOS: multiple image files expected.
 */
@Serializable
data class IspUploadResponse(
    val success: Boolean,
    val pageCount: Int    // Number of pages/photos uploaded
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 7: SHOP & EQUIPMENT PHOTO APIs — Screen 8 (Shop Photos)
// ════════════════════════════════════════════════════════════════════════════
// CSP uploads shop front photo and equipment photos for verification.

/**
 * POST /api/v1/shop/upload-front-photo — Upload shop front photo.
 *
 * NOTE: Multipart/form-data request. Single image file.
 */
@Serializable
data class ShopFrontPhotoResponse(
    val success: Boolean
)

/**
 * POST /api/v1/shop/upload-equipment-photos — Upload equipment photos.
 *
 * NOTE: Multipart/form-data request. Multiple image files.
 * Equipment includes: router, ONT, cables, etc.
 */
@Serializable
data class EquipmentPhotosResponse(
    val success: Boolean,
    val photoCount: Int   // Number of photos successfully uploaded
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 8: VERIFICATION API — Screen 9 (Verification Status)
// ════════════════════════════════════════════════════════════════════════════
// Wiom team reviews the submitted documents. CSP polls this endpoint.
// If REJECTED: registration fee (₹2,000) is auto-refunded.
// If APPROVED: CSP proceeds to Phase 3 (Policy → Tech Assessment → etc.)

/**
 * GET /api/v1/verification/status?applicationId={id}
 *
 * Token is sent in Authorization header.
 * Poll every 30 seconds or use push notification to know when status changes.
 */
@Serializable
data class VerificationStatusResponse(
    val status: ReviewStatus,               // PENDING / APPROVED / REJECTED
    val rejectionReason: String? = null,     // Human-readable reason (if rejected)
    val estimatedDays: Int                   // Expected TAT in business days (default: 3)
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 9: TECHNICAL ASSESSMENT API — Screen 11 (Tech Assessment)
// ════════════════════════════════════════════════════════════════════════════
// Wiom technical team assesses CSP's equipment and readiness.
// If REJECTED: application ends (no fee to refund at this stage).
// If APPROVED: CSP proceeds to pay onboarding fee.

/**
 * GET /api/v1/tech-assessment/status?applicationId={id}
 *
 * Token is sent in Authorization header.
 */
@Serializable
data class TechAssessmentStatusResponse(
    val status: ReviewStatus,               // PENDING / APPROVED / REJECTED
    val rejectionReason: String? = null,
    val estimatedDays: Int                   // Expected TAT in business days (default: 5)
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 10: ONBOARDING FEE API — Screen 12 (Onboarding Fee Payment)
// ════════════════════════════════════════════════════════════════════════════
// After passing verification + tech assessment, CSP pays ₹20,000 onboarding fee.

/** POST /api/v1/onboarding/payment — Pay ₹20,000 onboarding fee. */
@Serializable
data class OnboardingPaymentRequest(
    val token: String,
    val applicationId: String,
    val amount: Int,              // Always 20000 (₹20,000) — validated server-side
    val paymentMethod: String     // e.g. "UPI", "CARD", "NET_BANKING"
)

@Serializable
data class OnboardingPaymentResponse(
    val success: Boolean,
    val transactionId: String,
    val status: PaymentStatus     // SUCCESS / FAILED / TIMEOUT
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 11: ACCOUNT SETUP API — Screen 13 (Account Setup)
// ════════════════════════════════════════════════════════════════════════════
// Backend creates the CSP's partner account after onboarding fee is paid.
// This is an async process — mobile polls for completion.

/**
 * GET /api/v1/account/setup-status?applicationId={id}
 *
 * Token is sent in Authorization header.
 * Poll every 5 seconds. Typically completes in under 1 minute.
 */
@Serializable
data class AccountSetupStatusResponse(
    val status: AccountSetupStatus,       // IN_PROGRESS / COMPLETED / FAILED
    val partnerCode: String? = null       // Unique CSP partner code (assigned on COMPLETED)
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 12: REFUND API — Shown if application is rejected at Screen 9
// ════════════════════════════════════════════════════════════════════════════
// Registration fee (₹2,000) is auto-refunded when verification is REJECTED.
// CSP can check refund progress.

/**
 * GET /api/v1/refund/status?applicationId={id}
 *
 * Token is sent in Authorization header.
 */
@Serializable
data class RefundStatusResponse(
    val status: RefundStatus,            // SUCCESS / IN_PROGRESS / FAILED
    val amount: Int,                     // Refund amount in rupees (2000)
    val utrNumber: String? = null,       // Bank UTR number (available when SUCCESS)
    val refundDate: String? = null,      // ISO 8601 date string (e.g. "2026-03-28")
    val estimatedDays: Int? = null       // Estimated days remaining (if IN_PROGRESS)
)


// ════════════════════════════════════════════════════════════════════════════
// SECTION 13: CONFIG API — App-wide configuration values
// ════════════════════════════════════════════════════════════════════════════
// Mobile app fetches this on launch to get current business values.
// This avoids hardcoding amounts, SLAs, and contact info in the app.

/**
 * GET /api/v1/config — No auth required.
 *
 * Returns current business configuration. Cache for 24 hours on mobile.
 */
@Serializable
data class AppConfigResponse(
    val registrationFee: Int,             // Currently 2000 (₹2,000)
    val onboardingFee: Int,               // Currently 20000 (₹20,000)
    val commissionNewConnection: Int,     // Currently 300 (₹300 per new connection)
    val commissionRecharge: Int,          // Currently 300 (₹300 per recharge)
    val payoutSchedule: String,           // Currently "Every Monday by 10 AM"
    val slaResolutionHours: Int,          // Currently 4 (hours to resolve complaints)
    val slaUptimePercent: Int,            // Currently 95 (minimum uptime guarantee %)
    val helpNumber: String,               // Currently "7836811111"
    val verificationTatDays: Int,         // Currently 3 (business days for document review)
    val techAssessmentTatDays: Int,       // Currently 5 (business days for tech assessment)
    val refundTatDays: Int                // Currently 6 (business days for refund processing)
)
