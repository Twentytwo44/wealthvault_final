package com.wealthvault.data.auth.transport.otp

import com.wealthvault.domain.auth.OtpVerificationResult

internal interface OTPApi {
    suspend fun otp(email: String, otp: String): OtpVerificationResult
}
