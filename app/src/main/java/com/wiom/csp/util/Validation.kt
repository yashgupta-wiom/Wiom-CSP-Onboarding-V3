package com.wiom.csp.util

object Validation {
    fun validatePhone(phone: String): String? {
        if (phone.isBlank()) return t("नंबर डालें", "Enter phone number")
        if (phone.length != 10) return t("10 अंकों का नंबर डालें", "Enter 10-digit number")
        if (!phone.all { it.isDigit() }) return t("केवल अंक डालें", "Enter digits only")
        return null
    }

    fun validateTnc(accepted: Boolean): String? {
        if (!accepted) return t("नियम व शर्तें स्वीकार करें", "Accept Terms & Conditions")
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
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!emailRegex.matches(email)) return t("सही ईमेल डालें", "Enter valid email")
        return null
    }

    fun validatePincode(pincode: String): String? {
        if (pincode.isBlank()) return t("पिनकोड डालें", "Enter pincode")
        if (pincode.length != 6) return t("6 अंकों का पिनकोड डालें", "Enter 6-digit pincode")
        if (!pincode.all { it.isDigit() }) return t("केवल अंक डालें", "Enter digits only")
        return null
    }

    fun validateTradeName(name: String): String? {
        if (name.isBlank()) return t("व्यापार का नाम डालें", "Enter trade name")
        if (name.length < 3) return t("कम से कम 3 अक्षर डालें", "Enter at least 3 characters")
        return null
    }

    fun validateEntityType(entityType: String): String? {
        if (entityType.isBlank()) return t("व्यापार प्रकार चुनें", "Select entity type")
        return null
    }

    fun validateBankIfsc(ifsc: String): String? {
        if (ifsc.isBlank()) return t("IFSC कोड डालें", "Enter IFSC code")
        if (ifsc.length != 11) return t("11 अक्षरों का IFSC डालें", "Enter 11-character IFSC")
        val ifscRegex = Regex("^[A-Z]{4}0[A-Z0-9]{6}$")
        if (!ifscRegex.matches(ifsc)) return t("सही IFSC कोड डालें", "Enter valid IFSC code")
        return null
    }
}
