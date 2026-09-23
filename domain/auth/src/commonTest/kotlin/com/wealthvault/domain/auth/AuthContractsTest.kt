package com.wealthvault.domain.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthContractsTest {
    @Test
    fun authValueObjectsPreserveTheStableBoundary() {
        assertEquals(
            SessionTokens("access", "refresh"),
            SessionTokens("access", "refresh"),
        )
        assertEquals(
            SessionDeviceInfo("fcm", "android", "Pixel"),
            SessionDeviceInfo("fcm", "android", "Pixel"),
        )
        assertEquals(
            PushDeviceToken("fcm", "ios", "iPhone"),
            PushDeviceToken("fcm", "ios", "iPhone"),
        )
        assertEquals(LoginCredentials("alice", "secret"), LoginCredentials("alice", "secret"))
        assertEquals(
            AuthenticatedSession("user-1", "access", "refresh"),
            AuthenticatedSession("user-1", "access", "refresh"),
        )
        assertEquals(RegistrationResult("user-1"), RegistrationResult("user-1"))
        assertEquals(PasswordActionResult(true), PasswordActionResult(true))
        assertEquals(OtpVerificationResult(true, "reset"), OtpVerificationResult(true, "reset"))
        assertEquals(ProviderLinkResult("line-1", "linked", true), ProviderLinkResult("line-1", "linked", true))
        assertEquals(RegistrationCredentials("alice", "secret"), RegistrationCredentials("alice", "secret"))
        assertEquals(DeviceRegistration("token", "android", "Pixel"), DeviceRegistration("token", "android", "Pixel"))
        assertEquals(PasswordRecoveryRequest("alice@example.com"), PasswordRecoveryRequest("alice@example.com"))
        assertEquals(OtpVerificationRequest("alice@example.com", "123456"), OtpVerificationRequest("alice@example.com", "123456"))
        assertEquals(PasswordResetRequest("reset", "new-secret"), PasswordResetRequest("reset", "new-secret"))
        assertEquals(ProviderToken("provider-token"), ProviderToken("provider-token"))
        assertEquals(GoogleIdentity("google-id"), GoogleIdentity("google-id"))
        assertEquals(
            ProviderLoginResult(success = true, accessToken = "access", refreshToken = "refresh", userId = "user-1"),
            ProviderLoginResult(success = true, accessToken = "access", refreshToken = "refresh", userId = "user-1"),
        )
    }

    @Test
    fun optionalContractsHaveSafeDefaultsAndStableStates() {
        assertEquals(SessionTokens(null, null), SessionTokens(null, null))
        assertEquals(RegistrationResult(null), RegistrationResult(null))
        assertEquals(ProviderLinkResult(), ProviderLinkResult())
        assertEquals(ProviderLoginResult(), ProviderLoginResult())
        assertFalse(PasswordActionResult(false).success)
        assertTrue(OtpVerificationResult(true, "token").success)
        assertEquals(SessionState.Loading, SessionState.valueOf("Loading"))
        assertEquals(SessionState.Authenticated, SessionState.valueOf("Authenticated"))
        assertEquals(SessionState.SignedOut, SessionState.valueOf("SignedOut"))
    }
}
