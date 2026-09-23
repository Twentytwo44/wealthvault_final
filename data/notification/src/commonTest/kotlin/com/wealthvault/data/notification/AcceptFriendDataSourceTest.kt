package com.wealthvault.data.notification

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.domain.social.AcceptFriendRequest
import com.wealthvault.domain.social.AcceptFriendResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class AcceptFriendDataSourceTest {
    @Test
    fun mapsTransportOutcomeWithoutExposingWireDto() = runTest {
        val transport = FakeAcceptFriendTransport(success = "true")
        val dataSource = AcceptFriendDataSource(transport)

        val result = assertIs<AppResult.Success<*>>(
            dataSource.acceptFriend(AcceptFriendRequest("requester-1", "accept")),
        )

        assertEquals("requester-1", transport.requesterId)
        assertEquals("accept", transport.action)
        assertEquals("true", (result.value as AcceptFriendResult).success)
    }

    private class FakeAcceptFriendTransport(private val success: String?) : AcceptFriendTransport {
        var requesterId: String? = null
        var action: String? = null

        override suspend fun acceptFriend(requesterId: String, action: String): String? {
            this.requesterId = requesterId
            this.action = action
            return success
        }
    }
}
