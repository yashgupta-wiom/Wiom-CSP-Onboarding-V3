package com.wiom.csp.util

object Validation {
    fun validatePhone(phone: String): String? {
        if (phone.isBlank()) return t("नंबर डालें", "Enter phone number")
        if (phone.length != 10) return t("10 अंकों का नंबर डालें", "Enter 10-digit number")
        if (!phone.all { it.isDigit() }) return t("केवल अंक डालें", "Enter digits only")
        return null
    }

    fun validateOtp(digits: List<String>): String? {
        if (digits.any { it.isBlank() }) return t("पूरा OTP डालें", "Enter complete OTP")
        return null
    }

    fun validateName(name: String): String? {
        if (name.isBlank()) return t("नाम डालें", "Enter name")
        return null
    }

    fun validateEmail(email: String): String? {
        if (email.isBlank()) return t("ईमेल डालें", "Enter email")
        if (!email.contains("@") || !email.contains(".")) return t("सही ईमेल डालें", "Enter valid email")
        return null
    }

    fun validatePincode(pincode: String): String? {
        if (pincode.isBlank()) return t("पिनकोड डालें", "Enter pincode")
        if (pincode.length != 6) return t("6 अंकों का पिनकोड डालें", "Enter 6-digit pincode")
        return null
    }
}
