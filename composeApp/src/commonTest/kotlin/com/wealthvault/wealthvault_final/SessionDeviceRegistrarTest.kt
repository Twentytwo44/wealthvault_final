package com.wealthvault.app

import com.wealthvault.app.navigation.SessionDeviceRegistrar
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.DeviceRegistration
import com.wealthvault.domain.auth.PushDeviceRepository
import com.wealthvault.domain.auth.PushDeviceToken
import com.wealthvault.domain.auth.PushNotificationProvider
import com.wealthvault.domain.auth.SessionDeviceInfo
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.auth.SessionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionDeviceRegistrarTest {
    @Test
    fun registrationIsOwnedByTheSessionAndPersistsDeviceInfo() = runTest {
        val session = FakeSessionManager()
        val repository = FakePushDeviceRepository()
        val registrar = SessionDeviceRegistrar(
            provider = ImmediatePushProvider(PushDeviceToken("token", "Android", "Pixel")),
            repository = repository,
            sessionManager = session,
            logger = NoOpAppLogger,
        )

        registrar.registerCurrentDevice()

        assertEquals(
            SessionDeviceInfo("token", "Android", "Pixel"),
            session.savedDevice,
        )
        assertEquals(
            DeviceRegistration("token", "Android", "Pixel"),
            repository.registeredDevice,
        )
    }

    @Test
    fun unavailableProviderTokenDoesNotCallBackend() = runTest {
        val repository = FakePushDeviceRepository()
        val registrar = SessionDeviceRegistrar(
            provider = ImmediatePushProvider(null),
            repository = repository,
            sessionManager = FakeSessionManager(),
            logger = NoOpAppLogger,
        )

        registrar.registerCurrentDevice()

        assertNull(repository.registeredDevice)
    }

    @Test
    fun backendFailureDoesNotUndoLocalDeviceState() = runTest {
        val session = FakeSessionManager()
        val registrar = SessionDeviceRegistrar(
            provider = ImmediatePushProvider(PushDeviceToken("token", "iOS", "iPhone")),
            repository = FakePushDeviceRepository(AppResult.Failure(AppError.Network(IllegalStateException("offline")))),
            sessionManager = session,
            logger = NoOpAppLogger,
        )

        registrar.registerCurrentDevice()

        assertEquals("token", session.savedDevice?.fcmToken)
    }

    @Test
    fun signedOutTokenRefreshIsPersistedWithoutCallingBackend() = runTest {
        val session = FakeSessionManager(SessionState.SignedOut)
        val repository = FakePushDeviceRepository()
        val registrar = SessionDeviceRegistrar(
            provider = ImmediatePushProvider(null),
            repository = repository,
            sessionManager = session,
            logger = NoOpAppLogger,
        )

        registrar.onDeviceTokenChanged(PushDeviceToken("new-token", "iOS", "iPhone"))

        assertEquals("new-token", session.savedDevice?.fcmToken)
        assertNull(repository.registeredDevice)
    }

    @Test
    fun authenticatedTokenRefreshIsSubmittedImmediately() = runTest {
        val repository = FakePushDeviceRepository()
        val registrar = SessionDeviceRegistrar(
            provider = ImmediatePushProvider(null),
            repository = repository,
            sessionManager = FakeSessionManager(),
            logger = NoOpAppLogger,
        )

        registrar.onDeviceTokenChanged(PushDeviceToken("rotated", "iOS", "iPhone"))

        assertEquals(
            DeviceRegistration("rotated", "iOS", "iPhone"),
            repository.registeredDevice,
        )
    }

    private class ImmediatePushProvider(
        private val value: PushDeviceToken?,
    ) : PushNotificationProvider {
        override fun getDeviceTokenInfo(
            onSuccess: (PushDeviceToken) -> Unit,
            onError: (String) -> Unit,
        ) {
            if (value == null) onError("unavailable") else onSuccess(value)
        }
    }

    private class FakePushDeviceRepository(
        private val result: AppResult<String> = AppResult.Success("device-id"),
    ) : PushDeviceRepository {
        var registeredDevice: DeviceRegistration? = null

        override suspend fun register(device: DeviceRegistration): AppResult<String> {
            registeredDevice = device
            return result
        }
    }

    private class FakeSessionManager(initialState: SessionState = SessionState.Authenticated) : SessionManager {
        private val state = MutableStateFlow(initialState)
        private val userIdFlow = MutableStateFlow<String?>(null)
        private val accessTokenFlow = MutableStateFlow<String?>("access")
        private val tokenFlow = MutableStateFlow<String?>(null)
        var savedDevice: SessionDeviceInfo? = null

        override val status: StateFlow<SessionState> = state
        override val accessToken: Flow<String?> = accessTokenFlow
        override val getUserId: Flow<String?> = userIdFlow
        override val fcmToken: Flow<String?> = tokenFlow

        override suspend fun saveUserId(userId: String?) {
            userIdFlow.value = userId
        }

        override suspend fun saveDeviceInfo(device: SessionDeviceInfo) {
            savedDevice = device
            tokenFlow.value = device.fcmToken
        }

        override suspend fun clear() {
            state.value = SessionState.SignedOut
            accessTokenFlow.value = null
            tokenFlow.value = null
        }
    }
}
