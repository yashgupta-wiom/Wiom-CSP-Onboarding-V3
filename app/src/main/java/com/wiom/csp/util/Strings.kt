package com.wiom.csp.util

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object Lang {
    var isHindi by mutableStateOf(true)

    fun toggle() { isHindi = !isHindi }
}

fun t(hi: String, en: String): String = if (Lang.isHindi) hi else en

/**
 * Centralized string constants for translation management.
 * Usage: Text(S.sendOtp) — resolves to Hindi or English based on Lang.isHindi.
 *
 * Screens still use inline t() calls for screen-specific copy.
 * This object holds commonly reused strings to avoid duplication and drift.
 */
object S {
    // Common actions
    val sendOtp get() = t("OTP भेजें", "Send OTP")
    val verify get() = t("सत्यापित करें", "Verify")
    val proceed get() = t("आगे बढ़ें", "Proceed")
    val retry get() = t("दोबारा कोशिश करें", "Retry")
    val cancel get() = t("रद्द करें", "Cancel")
    val talkToUs get() = t("हमसे बात करें", "Talk to Us")
    val gotIt get() = t("समझ गया", "Got it")
    val upload get() = t("अपलोड", "Upload")
    val update get() = t("बदलें", "Update")
    val confirm get() = t("पुष्टि करें", "Confirm")
    val retake get() = t("दोबारा लें", "Retake")
    val refreshStatus get() = t("Status Refresh करें", "Refresh Status")

    // Headers
    val registration get() = t("पंजीकरण", "Registration")
    val verification get() = t("सत्यापन", "Verification")
    val activation get() = t("एक्टिवेशन", "Activation")
    val importantTerms get() = t("महत्वपूर्ण शर्तें", "Important Terms")
    val paymentStatus get() = t("भुगतान स्टेटस", "Payment Status")

    // Trust & reassurance (Wiom UX: warm tone, no-blame)
    val dontWorry get() = t("चिंता न करें", "Don't worry")
    val moneySafe get() = t("आपका पैसा सुरक्षित है", "Your money is safe")
    val noMoneyDeducted get() = t("पैसा कटा नहीं है", "No money was deducted")
    val fullRefundIfRejected get() = t("आवेदन अस्वीकार होने पर पूरा रिफंड", "Full Refund if application rejected")

    // Payment
    val paymentFailed get() = t("भुगतान नहीं हो पाया", "Payment failed")
    val paymentPending get() = t("भुगतान pending है", "Payment is pending")
    val paymentSuccessful get() = t("भुगतान सफल!", "Payment Successful!")
    val retryPayment get() = t("दोबारा भुगतान करें", "Retry Payment")

    // Upload flow
    val cameraCapture get() = t("कैमरा से फ़ोटो लें", "Take Photo from Camera")
    val galleryUpload get() = t("गैलरी से अपलोड करें", "Upload from Gallery")
    val viewSample get() = t("सैंपल दस्तावेज़ देखें", "View sample document")

    // Document labels
    val panCard get() = t("पैन कार्ड", "PAN Card")
    val aadhaarFront get() = t("आधार — सामने", "Aadhaar — Front")
    val aadhaarBack get() = t("आधार — पीछे", "Aadhaar — Back")
    val gstCertificate get() = t("जीएसटी प्रमाणपत्र", "GST Certificate")
    val ispAgreement get() = t("ISP अनुबंध", "ISP Agreement")
    val shopFrontPhoto get() = t("दुकान की फ़ोटो", "Shop Front Photo")
    val equipmentPhoto get() = t("राउटर/उपकरण फ़ोटो", "Router/Equipment Photo")
    val bankDocument get() = t("बैंक दस्तावेज़", "Bank Document")

    // Business values (DO NOT CHANGE without product approval)
    const val HELP_NUMBER = "7836811111"
    const val REG_FEE = "₹2,000"
    const val ONBOARD_FEE = "₹20,000"
    const val TOTAL_INVESTMENT = "₹22,000"
    const val COMMISSION_NEW = "₹300"
    const val COMMISSION_RECHARGE = "₹300"
    const val PAYOUT_SCHEDULE = "Every Monday by 10 AM"
    const val SLA_COMPLAINT = "4 hours"
    const val SLA_UPTIME = "95%+"
    const val REFUND_TIMELINE = "5-6 working days"
}
