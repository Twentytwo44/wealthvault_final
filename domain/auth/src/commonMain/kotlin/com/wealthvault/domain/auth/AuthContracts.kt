package com.wealthvault.domain.auth

import com.wealthvault.core.architecture.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Session state exposed to presentation and composition roots.
 *
 * The contract intentionally contains no DataStore, Keychain, Keystore, or
 * transport types. Those details stay in the data/platform adapter.
 */
enum class SessionState {
    Loading,
    Authenticated,
    SignedOut,
}

/**
 * Network-facing token boundary. Storage adapters implement this contract;
 * HTTP code never needs to depend on DataStore, Keychain, or Keystore types.
 */
data class SessionTokens(
    val accessToken: String?,
    val refreshToken: String?,
)

interface SessionTokenStore {
    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>

    suspend fun saveTokens(tokens: SessionTokens)
    suspend fun clearTokens()
}

data class SessionDeviceInfo(
    val fcmToken: String?,
    val platform: String?,
    val deviceName: String?,
)

/** Device token supplied by the platform push adapter to auth/session flows. */
data class PushDeviceToken(
    val fcmToken: String,
    val platform: String,
    val deviceName: String,
)

/** Platform push SDK boundary; Firebase/APNs details stay in the platform module. */
interface PushNotificationProvider {
    fun getDeviceTokenInfo(
        onSuccess: (PushDeviceToken) -> Unit,
        onError: (String) -> Unit,
    )
}

interface SessionManager {
    val status: StateFlow<SessionState>
    val accessToken: Flow<String?>
    val getUserId: Flow<String?>
    val fcmToken: Flow<String?>

    /** Persist the authenticated identity without exposing storage details. */
    suspend fun saveUserId(userId: String?)

    suspend fun saveDeviceInfo(device: SessionDeviceInfo)
    suspend fun clear()
}

/** User input required to authenticate with the backend. */
data class LoginCredentials(
    val username: String,
    val password: String,
)

/** Domain representation of a successful sign-in; API DTOs never leave data code. */
data class AuthenticatedSession(
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
)

/** Result returned when the backend creates a new account. */
data class RegistrationResult(
    val userId: String?,
)

/** Result returned by password recovery mutations. */
data class PasswordActionResult(
    val success: Boolean,
)

/** Result returned by OTP verification. */
data class OtpVerificationResult(
    val success: Boolean,
    val resetToken: String,
)

/** Result returned when a social provider account is linked. */
data class ProviderLinkResult(
    val lineId: String? = null,
    val message: String? = null,
    val success: Boolean? = null,
)

/** Stable auth boundary consumed by presentation and use cases. */
interface AuthRepository {
    suspend fun login(credentials: LoginCredentials): AppResult<AuthenticatedSession>
}

data class RegistrationCredentials(
    val username: String,
    val password: String,
)

/** Device metadata submitted by auth/session flows; the wire DTO stays in data code. */
data class DeviceRegistration(
    val token: String?,
    val platform: String?,
    val deviceName: String?,
)

data class PasswordRecoveryRequest(val email: String)

data class OtpVerificationRequest(val email: String, val otp: String)

data class PasswordResetRequest(val resetToken: String, val password: String)

data class ProviderToken(val token: String)

/** Identity returned by a platform provider after interactive sign-in. */
data class GoogleIdentity(val idToken: String)

/** Platform-agnostic provider boundary; Google SDK details stay in data. */
interface GoogleSignInProvider {
    suspend fun signIn(): AppResult<GoogleIdentity?>
}

data class ProviderLoginResult(
    val success: Boolean? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userId: String? = null,
)

interface RegistrationRepository {
    suspend fun register(credentials: RegistrationCredentials): AppResult<Unit>
}

/** Provider-login boundary used by Google/Apple sign-in adapters. */
interface ProviderAuthRepository {
    suspend fun login(token: ProviderToken): AppResult<ProviderLoginResult>
}

/** Push-device registration boundary; transport DTOs stay inside data. */
interface PushDeviceRepository {
    suspend fun register(device: DeviceRegistration): AppResult<String>
}

/** Password recovery contracts keep OTP/reset transport out of presentation. */
interface PasswordRecoveryRepository {
    suspend fun requestOtp(request: PasswordRecoveryRequest): AppResult<Unit>
}

interface OtpRepository {
    suspend fun verify(request: OtpVerificationRequest): AppResult<String>
}

interface PasswordResetRepository {
    suspend fun reset(request: PasswordResetRequest): AppResult<Unit>
}
