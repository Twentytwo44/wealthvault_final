package com.wealthvault.data.auth.wire

import com.wealthvault.data.auth.transport.model.ForgetPasswordData
import com.wealthvault.data.auth.transport.model.ForgetPasswordResponse
import com.wealthvault.data.auth.transport.model.LoginData
import com.wealthvault.data.auth.transport.model.LoginResponse
import com.wealthvault.data.auth.transport.model.OTPData
import com.wealthvault.data.auth.transport.model.OTPResponse
import com.wealthvault.data.auth.transport.model.RefreshData
import com.wealthvault.data.auth.transport.model.RefreshResponse
import com.wealthvault.data.auth.transport.model.RegisterResponse
import com.wealthvault.data.auth.transport.model.ResetPasswordData
import com.wealthvault.data.auth.transport.model.ResetPasswordResponse
import com.wealthvault.data.auth.transport.model.TokenResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class AuthWireMappersTest {
    @Test
    fun loginResponseMapsToDomainSession() {
        val result = LoginResponse(
            data = LoginData(
                success = true,
                accessToken = "access",
                refreshToken = "refresh",
                userId = "user-1",
            ),
        ).toDomain()

        assertEquals("user-1", result.userId)
        assertEquals("access", result.accessToken)
        assertEquals("refresh", result.refreshToken)
    }

    @Test
    fun loginErrorDoesNotCrossTheApiBoundary() {
        assertFailsWith<IllegalStateException> {
            LoginResponse(error = "invalid credentials").toDomain()
        }
    }

    @Test
    fun otpAndPasswordActionsMapWithoutWireTypes() {
        val otp = OTPResponse(data = OTPData(success = true, resetToken = "reset")).toDomain()
        val action = ForgetPasswordResponse(data = ForgetPasswordData(success = true)).toDomain()

        assertEquals("reset", otp.resetToken)
        assertEquals(true, action.success)
        assertEquals(true, ResetPasswordResponse(data = ResetPasswordData(true)).toDomain().success)
    }

    @Test
    fun providerLinkPreservesOptionalBackendMetadata() {
        val result = TokenResponse(lineId = "line-1", message = "linked", success = true).toDomain()

        assertEquals("line-1", result.lineId)
        assertEquals("linked", result.message)
        assertEquals(true, result.success)
    }

    @Test
    fun refreshAndRegistrationResponsesMapSuccessAndMissingData() {
        val refresh = RefreshResponse(
            data = RefreshData(
                success = true,
                accessToken = "access",
                refreshToken = "refresh",
                userId = "user-1",
            ),
        ).toDomain()
        assertEquals("user-1", refresh.userId)
        assertEquals("refresh", refresh.refreshToken)
        assertNull(RegisterResponse().toDomain().userId)
        assertFailsWith<IllegalStateException> { RefreshResponse().toDomain() }
        assertFailsWith<IllegalStateException> { RefreshResponse(error = "").toDomain() }
        assertFailsWith<IllegalStateException> { RefreshResponse(error = "server rejected").toDomain() }
    }

    @Test
    fun actionAndOtpMappersPreserveFalseAndErrorBranches() {
        assertEquals(false, ForgetPasswordResponse(data = ForgetPasswordData(false)).toDomain().success)
        assertEquals(false, ResetPasswordResponse(data = ResetPasswordData(false)).toDomain().success)
        assertFailsWith<IllegalStateException> {
            ForgetPasswordResponse(error = "backend error").toDomain()
        }
        assertFailsWith<IllegalStateException> {
            RegisterResponse(error = "registration failed").toDomain()
        }
        assertFailsWith<IllegalStateException> { OTPResponse().toDomain() }
        assertFailsWith<IllegalStateException> { OTPResponse(error = "").toDomain() }
        assertFailsWith<IllegalStateException> { OTPResponse(error = "invalid otp").toDomain() }
        assertFailsWith<IllegalStateException> { TokenResponse(error = "link failed").toDomain() }
    }

    @Test
    fun loginMapperRejectsEveryRequiredSessionFieldWhenMissing() {
        assertFailsWith<IllegalStateException> { LoginResponse().toDomain() }
        assertFailsWith<IllegalStateException> { LoginResponse(error = "").toDomain() }
        assertFailsWith<IllegalStateException> {
            LoginResponse(data = LoginData(accessToken = "access", refreshToken = "refresh")).toDomain()
        }
        assertFailsWith<IllegalStateException> {
            LoginResponse(data = LoginData(userId = "user-1", refreshToken = "refresh")).toDomain()
        }
        assertFailsWith<IllegalStateException> {
            LoginResponse(data = LoginData(userId = "user-1", accessToken = "access")).toDomain()
        }
        assertFailsWith<IllegalStateException> {
            LoginResponse(
                data = LoginData(userId = "user-1", refreshToken = "refresh"),
                error = "session payload incomplete",
            ).toDomain()
        }
        assertFailsWith<IllegalStateException> { LoginResponse(error = "invalid credentials").toDomain() }
    }
}
