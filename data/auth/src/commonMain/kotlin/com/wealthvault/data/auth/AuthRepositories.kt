package com.wealthvault.data.auth

import com.wealthvault.data.auth.transport.fgpassword.ForgetApi
import com.wealthvault.data.auth.transport.googlelink.GoogleLoginApi
import com.wealthvault.data.auth.transport.linelink.LineLinkApi
import com.wealthvault.data.auth.transport.login.LoginApi
import com.wealthvault.data.auth.transport.otp.OTPApi
import com.wealthvault.data.auth.transport.register.RegisterApi
import com.wealthvault.data.auth.transport.rspassword.ResetApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.AuthRepository
import com.wealthvault.domain.auth.AuthenticatedSession
import com.wealthvault.domain.auth.DeviceRegistration
import com.wealthvault.domain.auth.LoginCredentials
import com.wealthvault.domain.auth.OtpRepository
import com.wealthvault.domain.auth.OtpVerificationRequest
import com.wealthvault.domain.auth.PasswordRecoveryRepository
import com.wealthvault.domain.auth.PasswordRecoveryRequest
import com.wealthvault.domain.auth.PasswordResetRepository
import com.wealthvault.domain.auth.PasswordResetRequest
import com.wealthvault.domain.auth.ProviderAuthRepository
import com.wealthvault.domain.auth.ProviderLoginResult
import com.wealthvault.domain.auth.ProviderToken
import com.wealthvault.domain.auth.PushDeviceRepository
import com.wealthvault.domain.auth.RegistrationCredentials
import com.wealthvault.domain.auth.RegistrationRepository
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.auth.SessionTokenStore
import com.wealthvault.domain.auth.SessionTokens
import com.wealthvault.domain.auth.GoogleIdentity
import com.wealthvault.domain.auth.GoogleSignInProvider
import com.wealthvault.domain.profile.DeviceRegistrationRepository
import com.wealthvault.domain.profile.LineLinkRepository
import com.wealthvault.data.auth.google.GoogleAuthRepository
import com.wealthvault.data.auth.transport.registerdevice.RegisterDeviceApi
import com.wealthvault.data.auth.transport.unregisterdevice.UnregisterDeviceApi
import org.koin.dsl.module

/**
 * Transport adapters for the auth bounded context.
 *
 * These classes are intentionally owned by data:auth. Feature modules only
 * consume the domain repositories registered by [authRepositoriesModule], so
 * endpoint interfaces and response shapes cannot leak into presentation.
 */

private data class LoginTransport(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
)

private class AuthNetworkDataSource(
    private val loginApi: LoginApi,
) {
    suspend fun login(credentials: LoginCredentials): AppResult<LoginTransport> =
        runSuspendAppCatching {
            val result = loginApi.login(credentials.username, credentials.password)
            LoginTransport(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                userId = result.userId,
            )
        }
}

private class AuthRepositoryImpl(
    private val networkDataSource: AuthNetworkDataSource,
    private val sessionManager: SessionManager,
    private val sessionTokenStore: SessionTokenStore,
    private val logger: AppLogger = NoOpAppLogger,
) : AuthRepository {
    override suspend fun login(credentials: LoginCredentials): AppResult<AuthenticatedSession> {
        val networkResult = networkDataSource.login(credentials)
        if (networkResult is AppResult.Failure) return networkResult

        return try {
            val data = networkResult.getOrThrow()
            logger.debug("Login response received")
            sessionTokenStore.saveTokens(SessionTokens(data.accessToken, data.refreshToken))
            sessionManager.saveUserId(data.userId)
            AppResult.Success(
                AuthenticatedSession(
                    userId = data.userId,
                    accessToken = data.accessToken,
                    refreshToken = data.refreshToken,
                ),
            )
        } catch (error: Throwable) {
            AppResult.Failure(error.toAppError())
        }
    }
}

private class GoogleNetworkDataSource(
    private val googleLoginApi: GoogleLoginApi,
) {
    suspend fun login(request: ProviderToken): AppResult<ProviderLoginResult> =
        runSuspendAppCatching {
            val result = googleLoginApi.glogin(request.token)
            ProviderLoginResult(
                success = true,
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                userId = result.userId,
            )
        }
}

private class GoogleSignInProviderImpl(
    private val googleAuthRepository: GoogleAuthRepository,
) : GoogleSignInProvider {
    override suspend fun signIn(): AppResult<GoogleIdentity?> = runSuspendAppCatching {
        googleAuthRepository.login()?.let { GoogleIdentity(idToken = it.idToken) }
    }
}

private class GoogleRepositoryImpl(
    private val networkDataSource: GoogleNetworkDataSource,
    private val sessionManager: SessionManager,
    private val sessionTokenStore: SessionTokenStore,
    private val logger: AppLogger = NoOpAppLogger,
) : ProviderAuthRepository {
    override suspend fun login(token: ProviderToken): AppResult<ProviderLoginResult> =
        when (val result = networkDataSource.login(token)) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                try {
                    val data = result.value
                    logger.debug("Google login response received")
                    sessionTokenStore.saveTokens(SessionTokens(data.accessToken, data.refreshToken))
                    sessionManager.saveUserId(data.userId)
                    AppResult.Success(data)
                } catch (error: Throwable) {
                    AppResult.Failure(error.toAppError())
                }
            }
        }
}

private class RegisterDataSource(
    private val registerApi: RegisterApi,
) {
    suspend fun register(credentials: RegistrationCredentials): AppResult<String> =
        runSuspendAppCatching {
            val result = registerApi.register(credentials.username, credentials.password)
            result.userId ?: error("User is null, cannot create user")
        }
}

private class RegisterRepositoryImpl(
    private val networkDataSource: RegisterDataSource,
    private val logger: AppLogger = NoOpAppLogger,
) : RegistrationRepository {
    override suspend fun register(credentials: RegistrationCredentials): AppResult<Unit> =
        when (val result = networkDataSource.register(credentials)) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                logger.debug("Registration response received")
                AppResult.Success(Unit)
            }
        }
}

private class ForgetNetworkDataSource(
    private val forgetApi: ForgetApi,
) {
    suspend fun requestOtp(request: PasswordRecoveryRequest): AppResult<Boolean> =
        runSuspendAppCatching {
            val result = forgetApi.forgetpassword(request.email)
            if (!result.success) error("Password recovery request failed")
            true
        }
}

private class ForgetRepositoryImpl(
    private val networkDataSource: ForgetNetworkDataSource,
) : PasswordRecoveryRepository {
    override suspend fun requestOtp(request: PasswordRecoveryRequest): AppResult<Unit> =
        when (val result = networkDataSource.requestOtp(request)) {
            is AppResult.Failure -> result
            is AppResult.Success -> AppResult.Success(Unit)
        }
}

private class OtpNetworkDataSource(
    private val otpApi: OTPApi,
) {
    suspend fun verify(request: OtpVerificationRequest): AppResult<String> =
        runSuspendAppCatching {
            val result = otpApi.otp(request.email, request.otp)
            if (result.success) result.resetToken else error("OTP verification failed")
        }
}

private class OtpRepositoryImpl(
    private val networkDataSource: OtpNetworkDataSource,
) : OtpRepository {
    override suspend fun verify(request: OtpVerificationRequest): AppResult<String> =
        networkDataSource.verify(request)
}

private class ResetNetworkDataSource(
    private val resetApi: ResetApi,
) {
    suspend fun reset(request: PasswordResetRequest): AppResult<Boolean> =
        runSuspendAppCatching {
            val result = resetApi.reset(request.resetToken, request.password)
            if (!result.success) error("Password reset failed")
            true
        }
}

private class ResetRepositoryImpl(
    private val networkDataSource: ResetNetworkDataSource,
) : PasswordResetRepository {
    override suspend fun reset(request: PasswordResetRequest): AppResult<Unit> =
        when (val result = networkDataSource.reset(request)) {
            is AppResult.Failure -> result
            is AppResult.Success -> AppResult.Success(Unit)
        }
}

private class RegisterDeviceDataSource(
    private val registerDeviceApi: RegisterDeviceApi,
) {
    suspend fun register(request: DeviceRegistration): AppResult<String> =
        runSuspendAppCatching {
            val result = registerDeviceApi.registerDevice(
                token = request.token,
                platform = request.platform,
                deviceName = request.deviceName,
            )
            result.message ?: error("Device registration failed")
        }
}

private class RegisterDeviceRepositoryImpl(
    private val networkDataSource: RegisterDeviceDataSource,
) : PushDeviceRepository {
    override suspend fun register(device: DeviceRegistration): AppResult<String> =
        networkDataSource.register(device)
}

private class UnregisterDeviceDataSource(
    private val unregisterDeviceApi: UnregisterDeviceApi,
) {
    suspend fun unregister(token: String): AppResult<Unit> = runSuspendAppCatching {
        val result = unregisterDeviceApi.unregisterDevice(token)
        check(!result.message.isNullOrBlank()) { "Unregister device response is empty" }
    }
}

private class UnregisterDeviceRepositoryImpl(
    private val dataSource: UnregisterDeviceDataSource,
) : DeviceRegistrationRepository {
    override suspend fun unregister(token: String): AppResult<Unit> = dataSource.unregister(token)
}

private class LineLinkDataSource(
    private val lineLinkApi: LineLinkApi,
) {
    suspend fun link(idToken: String): AppResult<Unit> = runSuspendAppCatching {
        val result = lineLinkApi.link(idToken)
        if (result.success == false) {
            error(result.message ?: "LINE account link failed")
        }
    }
}

private class LineLinkRepositoryImpl(
    private val dataSource: LineLinkDataSource,
    private val logger: AppLogger = NoOpAppLogger,
) : LineLinkRepository {
    override suspend fun link(idToken: String): AppResult<Unit> =
        when (val result = dataSource.link(idToken)) {
            is AppResult.Success -> {
                logger.info("LINE account link response received")
                result
            }
            is AppResult.Failure -> result
        }
}

/** Koin registrations for auth repositories and their private transport adapters. */
internal val authRepositoriesModule = module {
    single<GoogleSignInProvider> { GoogleSignInProviderImpl(get()) }
    factory { AuthNetworkDataSource(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get()) }

    factory { GoogleNetworkDataSource(get()) }
    single<ProviderAuthRepository> { GoogleRepositoryImpl(get(), get(), get(), get()) }

    factory { RegisterDataSource(get()) }
    single<RegistrationRepository> { RegisterRepositoryImpl(get(), get()) }

    factory { ForgetNetworkDataSource(get()) }
    single<PasswordRecoveryRepository> { ForgetRepositoryImpl(get()) }

    factory { OtpNetworkDataSource(get()) }
    single<OtpRepository> { OtpRepositoryImpl(get()) }

    factory { ResetNetworkDataSource(get()) }
    single<PasswordResetRepository> { ResetRepositoryImpl(get()) }

    factory { RegisterDeviceDataSource(get()) }
    single<PushDeviceRepository> { RegisterDeviceRepositoryImpl(get()) }

    factory { UnregisterDeviceDataSource(get()) }
    single<DeviceRegistrationRepository> { UnregisterDeviceRepositoryImpl(get()) }

    factory { LineLinkDataSource(get()) }
    single<LineLinkRepository> { LineLinkRepositoryImpl(get(), get()) }
}
