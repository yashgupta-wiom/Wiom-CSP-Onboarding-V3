package com.wiom.csp.data.repository

/**
 * Repository interface for CSP Onboarding — 15-screen flow.
 *
 * In production: replace MockOnboardingRepository with real API implementation
 * using WiomApiService (Retrofit) and ApiContracts data classes.
 */
interface OnboardingRepository {
    suspend fun sendOtp(phone: String): Result<Boolean>
    suspend fun verifyOtp(phone: String, otp: String): Result<Boolean>
    suspend fun checkPhoneDuplicate(phone: String): Result<Boolean>
    suspend fun processPayment(amount: Int): Result<String> // returns transaction ID
    suspend fun verifyBankAccount(account: String, ifsc: String): Result<BankVerificationResult>
}

data class BankVerificationResult(
    val pennyDropVerified: Boolean,
    val dedupPassed: Boolean,
    val nameMatch: Boolean,
    val accountHolderName: String = "",
    val bankName: String = "",
    val duplicateAccountPhone: String? = null,
)
