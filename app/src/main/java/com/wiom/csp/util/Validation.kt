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

    fun validatePan(pan: String): String? {
        if (pan.isBlank()) return t("पैन नंबर डालें", "Enter PAN number")
        val panRegex = Regex("^[A-Z]{5}[0-9]{4}[A-Z]$")
        if (!panRegex.matches(pan)) return t("कृपया सही पैन नंबर दर्ज करें", "Please enter valid PAN number")
        return null
    }

    fun validateAadhaar(aadhaar: String): String? {
        val stripped = aadhaar.replace(" ", "")
        if (stripped.isBlank()) return t("आधार नंबर डालें", "Enter Aadhaar number")
        val aadhaarRegex = Regex("^[0-9]{12}$")
        if (!aadhaarRegex.matches(stripped)) return t("कृपया सही आधार नंबर दर्ज करें", "Please enter valid Aadhaar number")
        return null
    }

    fun validateGst(gst: String, panNumber: String = ""): String? {
        if (gst.isBlank()) return t("जीएसटी नंबर डालें", "Enter GST number")
        val gstRegex = Regex("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][0-9A-Z]{3}$")
        if (!gstRegex.matches(gst)) return t("कृपया सही जीएसटी नंबर दर्ज करें", "Please enter valid GST number")
        if (panNumber.isNotBlank() && gst.length >= 12 && gst.substring(2, 12) != panNumber) {
            return t("जीएसटी नंबर पैन कार्ड से मेल नहीं खाता", "GST number mismatch with PAN Card")
        }
        return null
    }

    fun validateBankAccount(account: String): String? {
        if (account.isBlank()) return t("बैंक खाता संख्या डालें", "Enter bank account number")
        if (!account.all { it.isDigit() }) return t("केवल अंक डालें", "Enter digits only")
        if (account.length < 9) return t("कम से कम 9 अंक डालें", "Enter at least 9 digits")
        if (account.length > 18) return t("18 अंक से ज़्यादा नहीं", "Must not exceed 18 digits")
        return null
    }

    fun validateBankAccountMatch(account: String, confirmAccount: String): String? {
        if (confirmAccount.isBlank()) return t("बैंक खाता संख्या दोबारा डालें", "Re-enter bank account number")
        if (account != confirmAccount) return t("बैंक खाता संख्या मेल नहीं खाती", "Bank account number mismatch")
        return null
    }
}
