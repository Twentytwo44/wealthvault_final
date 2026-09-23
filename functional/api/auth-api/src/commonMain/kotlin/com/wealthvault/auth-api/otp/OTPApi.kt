package com.wealthvault.`auth-api`.otp

import com.wealthvault.domain.auth.OtpVerificationResult

interface OTPApi {
    suspend fun otp(email: String, otp: String): OtpVerificationResult
}
