package com.wiom.csp.data.repository

import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Mock implementation of OnboardingRepository for prototype testing.
 *
 * This is the ONLY file that needs to be replaced with real API calls for production.
 * Use WiomApiService (Retrofit) + ApiContracts data classes for the real implementation.
 */
class MockOnboardingRepository @Inject constructor() : OnboardingRepository {

    override suspend fun sendOtp(phone: String): Result<Boolean> {
        delay(1000)
        return Result.success(true)
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<Boolean> {
        delay(1000)
        return if (otp == "4729") Result.success(true)
        else Result.failure(Exception("Invalid OTP"))
    }

    override suspend fun checkPhoneDuplicate(phone: String): Result<Boolean> {
        delay(500)
        return Result.success(false) // false = not duplicate
    }

    override suspend fun processPayment(amount: Int): Result<String> {
        delay(2000)
        return Result.success("TXN-${System.currentTimeMillis()}")
    }

    override suspend fun verifyBankAccount(account: String, ifsc: String): Result<BankVerificationResult> {
        delay(2000)
        return Result.success(BankVerificationResult(
            pennyDropVerified = true,
            dedupPassed = true,
            nameMatch = true,
            accountHolderName = "Rajesh Kumar",
            bankName = "State Bank of India",
            duplicateAccountPhone = null,
        ))
    }
}
