package com.wealthvault.app.navigation

import cafe.adriel.voyager.navigator.Navigator
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.auth.SessionState
import com.wealthvault.domain.profile.CurrentUserRepository
import com.wealthvault.domain.profile.UserData
import com.wealthvault.introduction.ui.IntroScreen
import com.wealthvault.login.ui.LoginScreen
import com.wealthvault.navigation.MainScreen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withTimeoutOrNull

/** Owns process-level authentication routing for the composition root. */
internal class AppCoordinator(
    private val sessionManager: SessionManager,
    private val currentUserRepository: CurrentUserRepository,
    private val deviceRegistrar: SessionDeviceRegistrar,
    private val logger: AppLogger,
) {
    /**
     * Device registration must outlive the splash/login route that triggered
     * it. The coordinator is a composition-root singleton, so this scope is
     * tied to the application process rather than a disposable screen.
     */
    private val registrationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var registrationJob: Job? = null

    suspend fun observe(navigator: Navigator) {
        try {
            // Accessing the flow initializes secure-session migration and
            // resolves SessionState from Loading before routing begins.
            sessionManager.accessToken.first()

            // Keep a short branded hand-off without adding a fixed multi-second
            // startup tax. Session loading remains the source of truth for the
            // routing decision.
            delay(SPLASH_HANDOFF_DELAY_MS)

            sessionManager.status
                .filter { it != SessionState.Loading }
                .collectLatest { state ->
                    when (state) {
                        SessionState.Authenticated -> {
                            // Registration is session-scoped and best effort;
                            // it must not delay profile routing or be cancelled
                            // merely because the login screen is replaced.
                            if (registrationJob?.isActive != true) {
                                registrationJob = registrationScope.launch {
                                    deviceRegistrar.registerCurrentDevice()
                                }
                            }
                            routeAuthenticated(navigator)
                        }
                        SessionState.SignedOut -> navigator.replaceAll(LoginScreen())
                        SessionState.Loading -> Unit
                    }
                }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            logger.warn("App session routing failed", error)
            navigator.replaceAll(LoginScreen())
        }
    }

    private suspend fun routeAuthenticated(navigator: Navigator) {
        // Do not hold the whole application on a profile request when the
        // device is offline or the backend is slow. The authenticated shell
        // is still useful because each screen owns its cache/retry state.
        val result = withTimeoutOrNull(PROFILE_ROUTE_TIMEOUT_MS) {
            currentUserRepository.getUser()
        }
        if (result == null) {
            logger.warn("Profile lookup timed out during session routing")
            navigator.replaceAll(MainScreen())
            return
        }

        when (authenticatedRoute(result)) {
            AuthenticatedRoute.Intro -> navigator.replaceAll(IntroScreen())
            AuthenticatedRoute.Main -> navigator.replaceAll(MainScreen())
            AuthenticatedRoute.Login -> {
                logger.warn("Profile lookup during session routing was unauthorized", result.errorOrNull()?.toThrowable())
                navigator.replaceAll(LoginScreen())
            }
        }
    }

    private companion object {
        const val SPLASH_HANDOFF_DELAY_MS = 350L
        const val PROFILE_ROUTE_TIMEOUT_MS = 2_000L
    }
}

/** Pure routing decision kept separate so every authentication branch is testable. */
internal enum class AuthenticatedRoute {
    Intro,
    Main,
    Login,
}

internal fun authenticatedRoute(result: AppResult<UserData>): AuthenticatedRoute = when (result) {
    is AppResult.Success -> {
        val birthday = result.value.birthday
        if (birthday.isNullOrBlank() || birthday.startsWith("1970-01-01")) {
            AuthenticatedRoute.Intro
        } else {
            AuthenticatedRoute.Main
        }
    }

    is AppResult.Failure -> if (result.error == AppError.Unauthorized) {
        AuthenticatedRoute.Login
    } else {
        // Keep an authenticated session usable during a transient
        // profile/network outage. Screens render cache and expose retry state.
        AuthenticatedRoute.Main
    }
}

private fun <T> AppResult<T>.errorOrNull(): AppError? =
    (this as? AppResult.Failure)?.error
