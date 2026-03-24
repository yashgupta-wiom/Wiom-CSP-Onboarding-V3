package com.wiom.csp.data.repository

import com.wiom.csp.domain.model.*

interface OnboardingRepository {
    suspend fun sendOtp(phone: String): Result<Boolean>
    suspend fun verifyOtp(phone: String, otp: String): Result<Boolean>
    suspend fun checkPhoneDuplicate(phone: String): Result<Boolean>
    suspend fun checkServiceability(pincode: String): Result<Boolean>
    suspend fun processPayment(amount: Int): Result<String> // returns transaction ID
    suspend fun verifyBankAccount(holder: String, bank: String, account: String, ifsc: String): Result<BankVerificationResult>
    suspend fun getTrainingModules(): Result<List<TrainingModule>>
    suspend fun getPolicyQuizQuestions(): Result<List<QuizQuestion>>
}

data class BankVerificationResult(
    val pennyDropVerified: Boolean,
    val dedupPassed: Boolean,
    val nameMatch: Boolean,
)
