package com.wealthvault.profile.ui

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.SessionDeviceInfo
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.auth.SessionState
import com.wealthvault.domain.profile.DeviceRegistrationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SessionLogoutCoordinatorTest {
    @Test
    fun unexpectedBackendFailureStillClearsTheLocalSession() = runTest {
        val session = FakeSessionManager()
        val coordinator = SessionLogoutCoordinator(
            deviceRepository = ThrowingDeviceRepository(),
            sessionManager = session,
            logger = NoOpAppLogger,
        )

        val result = coordinator.logout()

        assertIs<AppResult.Success<Unit>>(result)
        assertEquals(1, session.clearCount)
        assertEquals(SessionState.SignedOut, session.status.value)
    }

    @Test
    fun backendRejectionDoesNotPreventLocalLogout() = runTest {
        val session = FakeSessionManager()
        val repository = RecordingDeviceRepository(
            AppResult.Failure(AppError.Network(IllegalStateException("offline"))),
        )
        val coordinator = SessionLogoutCoordinator(repository, session, NoOpAppLogger)

        val result = coordinator.logout()

        assertIs<AppResult.Success<Unit>>(result)
        assertEquals("push-token", repository.token)
        assertEquals(1, session.clearCount)
    }

    @Test
    fun screenCancellationStillClearsSecureSession() = runTest {
        val session = FakeSessionManager()
        val coordinator = SessionLogoutCoordinator(
            deviceRepository = CancellingDeviceRepository(),
            sessionManager = session,
            logger = NoOpAppLogger,
        )

        val logoutJob = launch { coordinator.logout() }
        runCurrent()
        logoutJob.cancelAndJoin()

        assertEquals(1, session.clearCount)
        assertEquals(SessionState.SignedOut, session.status.value)
    }

    @Test
    fun localSecureStorageFailureIsReported() = runTest {
        val coordinator = SessionLogoutCoordinator(
            deviceRepository = RecordingDeviceRepository(AppResult.Success(Unit)),
            sessionManager = FakeSessionManager(failClear = true),
            logger = NoOpAppLogger,
        )

        val result = coordinator.logout()

        assertIs<AppResult.Failure>(result)
    }

    private class RecordingDeviceRepository(
        private val result: AppResult<Unit>,
    ) : DeviceRegistrationRepository {
        var token: String? = null

        override suspend fun unregister(token: String): AppResult<Unit> {
            this.token = token
            return result
        }
    }

    private class ThrowingDeviceRepository : DeviceRegistrationRepository {
        override suspend fun unregister(token: String): AppResult<Unit> {
            error("backend unavailable")
        }
    }

    private class CancellingDeviceRepository : DeviceRegistrationRepository {
        override suspend fun unregister(token: String): AppResult<Unit> = awaitCancellation()
    }

    private class FakeSessionManager(
        private val failClear: Boolean = false,
    ) : SessionManager {
        private val state = MutableStateFlow(SessionState.Authenticated)
        private val access = MutableStateFlow<String?>("access-token")
        private val userId = MutableStateFlow<String?>("user-1")
        private val pushToken = MutableStateFlow<String?>("push-token")
        var clearCount = 0

        override val status: StateFlow<SessionState> = state
        override val accessToken: Flow<String?> = access
        override val getUserId: Flow<String?> = userId
        override val fcmToken: Flow<String?> = pushToken

        override suspend fun saveUserId(userId: String?) {
            this.userId.value = userId
        }

        override suspend fun saveDeviceInfo(device: SessionDeviceInfo) {
            pushToken.value = device.fcmToken
        }

        override suspend fun clear() {
            clearCount += 1
            check(!failClear) { "secure storage unavailable" }
            access.value = null
            userId.value = null
            pushToken.value = null
            state.value = SessionState.SignedOut
        }
    }
}
