package com.wiom.csp.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * ============================================================================
 * WIOM CSP ONBOARDING — RETROFIT API SERVICE
 * ============================================================================
 *
 * Retrofit interface for all Wiom CSP Onboarding backend endpoints.
 * Each method maps to one API from ApiContracts.kt.
 *
 * AUTH PATTERN:
 * - Token is obtained from verifyOtp() and must be passed as a Bearer token.
 * - The @Header("Authorization") parameter expects "Bearer <token>".
 * - Config endpoint requires no auth.
 *
 * FILE UPLOAD PATTERN:
 * - File uploads use @Multipart annotation.
 * - Files are sent as @Part MultipartBody.Part.
 * - Other fields are sent as @Part RequestBody.
 *
 * BASE URL: Set in Retrofit builder (e.g. "https://api.wiom.in/api/v1/")
 *
 * Last updated: 2026-03-26
 * ============================================================================
 */
interface WiomApiService {

    // ════════════════════════════════════════════════════════════════════════
    // OTP APIs — Phone & OTP Screens (Screens 0-1)
    // ════════════════════════════════════════════════════════════════════════

    /** Send OTP to the given phone number. */
    @POST("otp/send")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): SendOtpResponse

    /** Verify the OTP entered by the user. Returns auth token on success. */
    @POST("otp/verify")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): VerifyOtpResponse

    /** Resend OTP if the previous one expired or wasn't received. */
    @POST("otp/resend")
    suspend fun resendOtp(
        @Body request: ResendOtpRequest
    ): ResendOtpResponse


    // ════════════════════════════════════════════════════════════════════════
    // REGISTRATION APIs — Personal, Location, Fee Screens (Screens 2-4)
    // ════════════════════════════════════════════════════════════════════════

    /** Check if a phone number is already registered (deduplication). */
    @POST("registration/check-phone")
    suspend fun checkPhone(
        @Body request: CheckPhoneRequest
    ): CheckPhoneResponse

    /** Save personal info and create an application. Returns applicationId. */
    @POST("registration/personal-info")
    suspend fun savePersonalInfo(
        @Header("Authorization") token: String,
        @Body request: PersonalInfoRequest
    ): PersonalInfoResponse

    /** Save location details for the application. */
    @POST("registration/location")
    suspend fun saveLocation(
        @Header("Authorization") token: String,
        @Body request: LocationRequest
    ): LocationResponse

    /** Process registration fee payment (₹2,000). */
    @POST("registration/payment")
    suspend fun payRegistrationFee(
        @Header("Authorization") token: String,
        @Body request: RegistrationPaymentRequest
    ): RegistrationPaymentResponse


    // ════════════════════════════════════════════════════════════════════════
    // KYC APIs — KYC Screen (Screen 5: PAN → Aadhaar → GST)
    // ════════════════════════════════════════════════════════════════════════

    /** Validate PAN number and check for duplicates. */
    @POST("kyc/validate-pan")
    suspend fun validatePan(
        @Header("Authorization") token: String,
        @Body request: ValidatePanRequest
    ): ValidatePanResponse

    /** Validate Aadhaar number and check for duplicates. */
    @POST("kyc/validate-aadhaar")
    suspend fun validateAadhaar(
        @Header("Authorization") token: String,
        @Body request: ValidateAadhaarRequest
    ): ValidateAadhaarResponse

    /** Validate GST number, check duplicates, and verify PAN match. */
    @POST("kyc/validate-gst")
    suspend fun validateGst(
        @Header("Authorization") token: String,
        @Body request: ValidateGstRequest
    ): ValidateGstResponse

    /**
     * Upload a KYC document photo (PAN card, Aadhaar front/back, GST certificate).
     *
     * Usage example:
     *   val filePart = MultipartBody.Part.createFormData("file", fileName, fileRequestBody)
     *   val typePart = "PAN_CARD".toRequestBody("text/plain".toMediaType())
     *   val appIdPart = applicationId.toRequestBody("text/plain".toMediaType())
     */
    @Multipart
    @POST("kyc/upload-document")
    suspend fun uploadKycDocument(
        @Header("Authorization") token: String,
        @Part("applicationId") applicationId: RequestBody,
        @Part("documentType") documentType: RequestBody,
        @Part file: MultipartBody.Part
    ): UploadDocumentResponse


    // ════════════════════════════════════════════════════════════════════════
    // BANK APIs — Bank Details Screen (Screen 6)
    // ════════════════════════════════════════════════════════════════════════

    /** Verify bank account via penny drop + name match + dedup. */
    @POST("bank/verify")
    suspend fun verifyBankAccount(
        @Header("Authorization") token: String,
        @Body request: BankVerifyRequest
    ): BankVerifyResponse

    /** Upload supporting document for bank name mismatch (cancelled cheque/passbook). */
    @Multipart
    @POST("bank/upload-supporting-doc")
    suspend fun uploadBankSupportingDoc(
        @Header("Authorization") token: String,
        @Part("applicationId") applicationId: RequestBody,
        @Part file: MultipartBody.Part
    ): BankSupportingDocResponse


    // ════════════════════════════════════════════════════════════════════════
    // ISP AGREEMENT API — ISP Agreement Screen (Screen 7)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Upload ISP agreement as PDF or photos.
     *
     * For PDF: send a single file part.
     * For PHOTOS: send multiple file parts (one per page photo).
     */
    @Multipart
    @POST("isp/upload")
    suspend fun uploadIspAgreement(
        @Header("Authorization") token: String,
        @Part("applicationId") applicationId: RequestBody,
        @Part("uploadType") uploadType: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): IspUploadResponse


    // ════════════════════════════════════════════════════════════════════════
    // SHOP & EQUIPMENT PHOTO APIs — Shop Photos Screen (Screen 8)
    // ════════════════════════════════════════════════════════════════════════

    /** Upload shop front photo (single image). */
    @Multipart
    @POST("shop/upload-front-photo")
    suspend fun uploadShopFrontPhoto(
        @Header("Authorization") token: String,
        @Part("applicationId") applicationId: RequestBody,
        @Part file: MultipartBody.Part
    ): ShopFrontPhotoResponse

    /** Upload equipment photos (multiple images: router, ONT, cables, etc.). */
    @Multipart
    @POST("shop/upload-equipment-photos")
    suspend fun uploadEquipmentPhotos(
        @Header("Authorization") token: String,
        @Part("applicationId") applicationId: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): EquipmentPhotosResponse


    // ════════════════════════════════════════════════════════════════════════
    // VERIFICATION API — Verification Status Screen (Screen 9)
    // ════════════════════════════════════════════════════════════════════════

    /** Poll verification status. Expected TAT: 3 business days. */
    @GET("verification/status")
    suspend fun getVerificationStatus(
        @Header("Authorization") token: String,
        @Query("applicationId") applicationId: String
    ): VerificationStatusResponse


    // ════════════════════════════════════════════════════════════════════════
    // TECHNICAL ASSESSMENT API — Tech Assessment Screen (Screen 11)
    // ════════════════════════════════════════════════════════════════════════

    /** Poll tech assessment status. Expected TAT: 5 business days. */
    @GET("tech-assessment/status")
    suspend fun getTechAssessmentStatus(
        @Header("Authorization") token: String,
        @Query("applicationId") applicationId: String
    ): TechAssessmentStatusResponse


    // ════════════════════════════════════════════════════════════════════════
    // ONBOARDING FEE API — Onboarding Fee Screen (Screen 12)
    // ════════════════════════════════════════════════════════════════════════

    /** Process onboarding fee payment (₹20,000). */
    @POST("onboarding/payment")
    suspend fun payOnboardingFee(
        @Header("Authorization") token: String,
        @Body request: OnboardingPaymentRequest
    ): OnboardingPaymentResponse


    // ════════════════════════════════════════════════════════════════════════
    // ACCOUNT SETUP API — Account Setup Screen (Screen 13)
    // ════════════════════════════════════════════════════════════════════════

    /** Poll account setup status. Typically completes in under 1 minute. */
    @GET("account/setup-status")
    suspend fun getAccountSetupStatus(
        @Header("Authorization") token: String,
        @Query("applicationId") applicationId: String
    ): AccountSetupStatusResponse


    // ════════════════════════════════════════════════════════════════════════
    // REFUND API — Shown when application is rejected
    // ════════════════════════════════════════════════════════════════════════

    /** Check refund status for registration fee (₹2,000). Expected TAT: 6 business days. */
    @GET("refund/status")
    suspend fun getRefundStatus(
        @Header("Authorization") token: String,
        @Query("applicationId") applicationId: String
    ): RefundStatusResponse


    // ════════════════════════════════════════════════════════════════════════
    // CONFIG API — App-wide configuration (no auth required)
    // ════════════════════════════════════════════════════════════════════════

    /** Fetch app config on launch. Cache for 24 hours. No auth needed. */
    @GET("config")
    suspend fun getAppConfig(): AppConfigResponse
}
