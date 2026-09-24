package com.wealthvault.data.auth.wire

import com.wealthvault.data.auth.transport.model.ForgetPasswordResponse
import com.wealthvault.data.auth.transport.model.LoginResponse
import com.wealthvault.data.auth.transport.model.OTPResponse
import com.wealthvault.data.auth.transport.model.RefreshResponse
import com.wealthvault.data.auth.transport.model.RegisterResponse
import com.wealthvault.data.auth.transport.model.ResetPasswordResponse
import com.wealthvault.data.auth.transport.model.TokenResponse
import com.wealthvault.domain.auth.AuthenticatedSession
import com.wealthvault.domain.auth.OtpVerificationResult
import com.wealthvault.domain.auth.PasswordActionResult
import com.wealthvault.domain.auth.ProviderLinkResult
import com.wealthvault.domain.auth.RegistrationResult

internal fun LoginResponse.toDomain(): AuthenticatedSession = data.toSession(error)

internal fun RefreshResponse.toDomain(): AuthenticatedSession =
    data?.let {
        AuthenticatedSession(
            userId = it.userId,
            accessToken = it.accessToken,
            refreshToken = it.refreshToken,
        )
    } ?: throwAuthError(error, "Refresh response did not contain a session")

internal fun RegisterResponse.toDomain(): RegistrationResult {
    throwAuthErrorIfPresent(error)
    return RegistrationResult(userId = data?.userId)
}

internal fun ForgetPasswordResponse.toDomain(): PasswordActionResult =
    PasswordActionResult(success = requireActionSuccess(data?.success, error))

internal fun ResetPasswordResponse.toDomain(): PasswordActionResult =
    PasswordActionResult(success = requireActionSuccess(data?.success, error))

internal fun OTPResponse.toDomain(): OtpVerificationResult {
    throwAuthErrorIfPresent(error)
    val result = data ?: throwAuthError(error, "OTP response did not contain a result")
    return OtpVerificationResult(success = result.success, resetToken = result.resetToken)
}

internal fun TokenResponse.toDomain(): ProviderLinkResult {
    throwAuthErrorIfPresent(error)
    return ProviderLinkResult(
        lineId = lineId,
        message = message,
        success = success,
    )
}

private fun com.wealthvault.data.auth.transport.model.LoginData?.toSession(error: String?): AuthenticatedSession {
    throwAuthErrorIfPresent(error)
    val result = this ?: throwAuthError(error, "Auth response did not contain a session")
    return AuthenticatedSession(
        userId = result.userId ?: throwAuthError(error, "Auth response did not contain a user id"),
        accessToken = result.accessToken ?: throwAuthError(error, "Auth response did not contain an access token"),
        refreshToken = result.refreshToken ?: throwAuthError(error, "Auth response did not contain a refresh token"),
    )
}

private fun requireActionSuccess(success: Boolean?, error: String?): Boolean {
    throwAuthErrorIfPresent(error)
    return success == true
}

private fun throwAuthErrorIfPresent(error: String?) {
    if (!error.isNullOrBlank()) throw IllegalStateException(error)
}

private fun <T> throwAuthError(error: String?, fallback: String): T {
    throw IllegalStateException(error?.takeIf { it.isNotBlank() } ?: fallback)
}
