package com.wealthvault.data.auth

import com.wealthvault.data.auth.transport.fgpassword.ForgetApi
import com.wealthvault.data.auth.transport.googlelink.GoogleLoginApi
import com.wealthvault.data.auth.transport.linelink.LineLinkApi
import com.wealthvault.data.auth.transport.login.LoginApi
import com.wealthvault.data.auth.transport.otp.OTPApi
import com.wealthvault.data.auth.transport.register.RegisterApi
import com.wealthvault.data.auth.transport.rspassword.ResetApi
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.AuthRepository
import com.wealthvault.domain.auth.AuthenticatedSession
import com.wealthvault.domain.auth.DeviceRegistration
import com.wealthvault.domain.auth.GoogleIdentity
import com.wealthvault.domain.auth.GoogleSignInProvider
import com.wealthvault.domain.auth.LoginCredentials
import com.wealthvault.domain.auth.OtpRepository
import com.wealthvault.domain.auth.OtpVerificationRequest
import com.wealthvault.domain.auth.PasswordActionResult
import com.wealthvault.domain.auth.PasswordRecoveryRepository
import com.wealthvault.domain.auth.PasswordRecoveryRequest
import com.wealthvault.domain.auth.PasswordResetRepository
import com.wealthvault.domain.auth.PasswordResetRequest
import com.wealthvault.domain.auth.ProviderAuthRepository
import com.wealthvault.domain.auth.ProviderLinkResult
import com.wealthvault.domain.auth.ProviderLoginResult
import com.wealthvault.domain.auth.ProviderToken
import com.wealthvault.domain.auth.PushDeviceRepository
import com.wealthvault.domain.auth.RegistrationCredentials
import com.wealthvault.domain.auth.RegistrationRepository
import com.wealthvault.domain.auth.SessionDeviceInfo
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.auth.SessionState
import com.wealthvault.domain.auth.SessionTokenStore
import com.wealthvault.domain.auth.SessionTokens
import com.wealthvault.domain.profile.DeviceRegistrationRepository
import com.wealthvault.domain.profile.LineLinkRepository
import com.wealthvault.data.auth.google.GoogleAuth
import com.wealthvault.data.auth.google.GoogleAuthRepository
import com.wealthvault.data.auth.transport.registerdevice.RegisterDeviceApi
import com.wealthvault.data.auth.transport.unregisterdevice.UnregisterDeviceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AuthRepositoriesTest {
    @Test
    fun everyAuthRepositoryMapsSuccessfulTransportToDomain() = runTest {
        val fakes = AuthFakes()
        val application = koinApplication {
            modules(module {
                single<LoginApi> { fakes }
                single<GoogleLoginApi> { fakes }
                single<RegisterApi> { fakes }
                single<ForgetApi> { fakes }
                single<OTPApi> { fakes }
                single<ResetApi> { fakes }
                single<LineLinkApi> { fakes }
                single<RegisterDeviceApi> { fakes }
                single<UnregisterDeviceApi> { fakes }
                single<SessionManager> { fakes }
                single<SessionTokenStore> { fakes }
                single<AppLogger> { NoOpAppLogger }
                single<GoogleAuthRepository> { GoogleAuthRepository(fakes) }
            }, authRepositoriesModule)
        }
        val koin = application.koin

        val login = assertIs<com.wealthvault.core.architecture.AppResult.Success<AuthenticatedSession>>(
            koin.get<AuthRepository>().login(LoginCredentials("alice", "secret")),
        )
        assertEquals("user-1", login.value.userId)
        assertEquals("user-1", fakes.savedUserId)

        val provider = assertIs<com.wealthvault.core.architecture.AppResult.Success<ProviderLoginResult>>(
            koin.get<ProviderAuthRepository>().login(ProviderToken("google-token")),
        )
        assertEquals("google-access", provider.value.accessToken)
        assertEquals("user-1", fakes.savedUserId)

        assertIs<com.wealthvault.core.architecture.AppResult.Success<Unit>>(
            koin.get<RegistrationRepository>().register(RegistrationCredentials("alice", "secret")),
        )
        assertEquals("user-1", fakes.registeredUserId)
        assertIs<com.wealthvault.core.architecture.AppResult.Success<Unit>>(
            koin.get<PasswordRecoveryRepository>().requestOtp(PasswordRecoveryRequest("alice@example.com")),
        )
        assertIs<com.wealthvault.core.architecture.AppResult.Success<String>>(
            koin.get<OtpRepository>().verify(OtpVerificationRequest("alice@example.com", "123456")),
        )
        assertIs<com.wealthvault.core.architecture.AppResult.Success<Unit>>(
            koin.get<PasswordResetRepository>().reset(PasswordResetRequest("reset", "new-secret")),
        )
        assertEquals("reset", fakes.resetToken)
        assertIs<com.wealthvault.core.architecture.AppResult.Success<String>>(
            koin.get<PushDeviceRepository>().register(DeviceRegistration("fcm", "android", "Pixel")),
        )
        assertIs<com.wealthvault.core.architecture.AppResult.Success<Unit>>(
            koin.get<DeviceRegistrationRepository>().unregister("fcm"),
        )
        assertIs<com.wealthvault.core.architecture.AppResult.Success<Unit>>(
            koin.get<LineLinkRepository>().link("line-id-token"),
        )
        assertEquals("line-id-token", fakes.linkedToken)

        val googleIdentity = assertIs<com.wealthvault.core.architecture.AppResult.Success<GoogleIdentity?>>(
            koin.get<GoogleSignInProvider>().signIn(),
        )
        assertEquals("google-id-token", googleIdentity.value?.idToken)
        application.close()
    }

    @Test
    fun authRepositoriesExposeBackendFailuresAsAppResultFailures() = runTest {
        val fakes = AuthFakes()
        val application = koinApplication {
            modules(module {
                single<LoginApi> { fakes }
                single<GoogleLoginApi> { fakes }
                single<RegisterApi> { fakes }
                single<ForgetApi> { fakes }
                single<OTPApi> { fakes }
                single<ResetApi> { fakes }
                single<LineLinkApi> { fakes }
                single<RegisterDeviceApi> { fakes }
                single<UnregisterDeviceApi> { fakes }
                single<SessionManager> { fakes }
                single<SessionTokenStore> { fakes }
                single<AppLogger> { NoOpAppLogger }
                single<GoogleAuthRepository> { GoogleAuthRepository(fakes) }
            }, authRepositoriesModule)
        }
        val koin = application.koin

        fakes.registration = null
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<RegistrationRepository>().register(RegistrationCredentials("alice", "secret")),
        )
        fakes.forget = PasswordActionResult(false)
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<PasswordRecoveryRepository>().requestOtp(PasswordRecoveryRequest("alice@example.com")),
        )
        fakes.otp = com.wealthvault.domain.auth.OtpVerificationResult(false, "")
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<OtpRepository>().verify(OtpVerificationRequest("alice@example.com", "000000")),
        )
        fakes.reset = PasswordActionResult(false)
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<PasswordResetRepository>().reset(PasswordResetRequest("reset", "bad")),
        )
        fakes.deviceMutation = com.wealthvault.core.model.DeviceMutationResult(message = null)
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<PushDeviceRepository>().register(DeviceRegistration("fcm", "android", "Pixel")),
        )
        fakes.unregister = com.wealthvault.core.model.DeviceMutationResult(message = "")
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<DeviceRegistrationRepository>().unregister("fcm"),
        )
        fakes.link = ProviderLinkResult(success = false, message = "denied")
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<LineLinkRepository>().link("line-id-token"),
        )
        application.close()
    }

    @Test
    fun authRepositoriesHandleTransportExceptionsAndNullableProviderResponses() = runTest {
        val fakes = AuthFakes()
        val application = koinApplication {
            modules(module {
                single<LoginApi> { fakes }
                single<GoogleLoginApi> { fakes }
                single<RegisterApi> { fakes }
                single<ForgetApi> { fakes }
                single<OTPApi> { fakes }
                single<ResetApi> { fakes }
                single<LineLinkApi> { fakes }
                single<RegisterDeviceApi> { fakes }
                single<UnregisterDeviceApi> { fakes }
                single<SessionManager> { fakes }
                single<SessionTokenStore> { fakes }
                single<AppLogger> { NoOpAppLogger }
                single<GoogleAuthRepository> { GoogleAuthRepository(fakes) }
            }, authRepositoriesModule)
        }
        val koin = application.koin

        fakes.loginThrows = true
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<AuthRepository>().login(LoginCredentials("alice", "secret")),
        )
        fakes.loginThrows = false
        fakes.sessionSaveThrows = true
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<AuthRepository>().login(LoginCredentials("alice", "secret")),
        )
        fakes.sessionSaveThrows = false

        fakes.googleThrows = true
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<ProviderAuthRepository>().login(ProviderToken("google-token")),
        )
        fakes.googleThrows = false
        fakes.googleUser = null
        val noGoogleUser = assertIs<com.wealthvault.core.architecture.AppResult.Success<GoogleIdentity?>>(
            koin.get<GoogleSignInProvider>().signIn(),
        )
        assertNull(noGoogleUser.value)

        fakes.link = ProviderLinkResult(success = false)
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<LineLinkRepository>().link("line-id-token"),
        )
        fakes.unregister = com.wealthvault.core.model.DeviceMutationResult(message = null)
        assertIs<com.wealthvault.core.architecture.AppResult.Failure>(
            koin.get<DeviceRegistrationRepository>().unregister("fcm"),
        )
        application.close()
    }

    private class AuthFakes :
        LoginApi,
        GoogleLoginApi,
        RegisterApi,
        ForgetApi,
        OTPApi,
        ResetApi,
        LineLinkApi,
        RegisterDeviceApi,
        UnregisterDeviceApi,
        GoogleAuth,
        SessionManager,
        SessionTokenStore {
        private val access = MutableStateFlow<String?>(null)
        private val refresh = MutableStateFlow<String?>(null)
        private val user = MutableStateFlow<String?>(null)
        private val fcm = MutableStateFlow<String?>(null)
        private val state = MutableStateFlow(SessionState.SignedOut)

        var registration: com.wealthvault.domain.auth.RegistrationResult? = com.wealthvault.domain.auth.RegistrationResult("user-1")
        var forget: PasswordActionResult = PasswordActionResult(true)
        var otp: com.wealthvault.domain.auth.OtpVerificationResult = com.wealthvault.domain.auth.OtpVerificationResult(true, "reset")
        var reset: PasswordActionResult = PasswordActionResult(true)
        var deviceMutation: com.wealthvault.core.model.DeviceMutationResult = com.wealthvault.core.model.DeviceMutationResult("registered", true)
        var unregister: com.wealthvault.core.model.DeviceMutationResult = com.wealthvault.core.model.DeviceMutationResult("unregistered", true)
        var link: ProviderLinkResult = ProviderLinkResult(success = true)
        var savedUserId: String? = null
        var registeredUserId: String? = null
        var resetToken: String? = null
        var linkedToken: String? = null
        var loginThrows = false
        var googleThrows = false
        var sessionSaveThrows = false
        var googleUser: GoogleIdentity? = GoogleIdentity("google-id-token")

        override val accessToken: Flow<String?> = access
        override val refreshToken: Flow<String?> = refresh
        override val getUserId: Flow<String?> = user
        override val fcmToken: Flow<String?> = fcm
        override val status = state

        override suspend fun login(email: String, password: String): AuthenticatedSession {
            if (loginThrows) error("login unavailable")
            return AuthenticatedSession("user-1", "google-access", "google-refresh")
        }
        override suspend fun glogin(token: String): AuthenticatedSession {
            if (googleThrows) error("provider unavailable")
            return AuthenticatedSession("user-1", "google-access", "google-refresh")
        }
        override suspend fun register(email: String, password: String) = (registration ?: com.wealthvault.domain.auth.RegistrationResult(null))
            .also { registeredUserId = it.userId }
        override suspend fun forgetpassword(email: String) = forget
        override suspend fun otp(email: String, otp: String) = this.otp.also { resetToken = it.resetToken }
        override suspend fun reset(resetToken: String, password: String) = reset
        override suspend fun link(token: String) = link.also { linkedToken = token }
        override suspend fun registerDevice(token: String?, platform: String?, deviceName: String?) = deviceMutation
        override suspend fun unregisterDevice(token: String) = unregister
        override suspend fun signIn() = googleUser

        override suspend fun saveTokens(tokens: SessionTokens) {
            if (sessionSaveThrows) error("session unavailable")
            access.value = tokens.accessToken
            refresh.value = tokens.refreshToken
            state.value = if (tokens.accessToken == null) SessionState.SignedOut else SessionState.Authenticated
        }

        override suspend fun saveUserId(userId: String?) {
            savedUserId = userId
            user.value = userId
        }

        override suspend fun saveDeviceInfo(device: SessionDeviceInfo) {
            fcm.value = device.fcmToken
        }

        override suspend fun clear() {
            access.value = null
            refresh.value = null
            state.value = SessionState.SignedOut
        }

        override suspend fun clearTokens() = clear()
    }

}
