package com.wealthvault.user_api

import com.wealthvault.core.model.Money
import com.wealthvault.domain.profile.UpdateUserDataRequest
import com.wealthvault.user_api.closefriend.CloseFriendApiImpl
import com.wealthvault.`user-api`.dashboard.DashboardApiImpl
import com.wealthvault.`user-api`.friend.FriendApiImpl
import com.wealthvault.`user-api`.friendmsg.GetFriendMsgApiImpl
import com.wealthvault.`user-api`.friendprofile.GetFriendProfileApiImpl
import com.wealthvault.`user-api`.pendingfriend.PendingFriendApiImpl
import com.wealthvault.`user-api`.updateclosefriend.UpdateCloseFriendApiImpl
import com.wealthvault.`user-api`.updateuser.UpdateUserApiImpl
import com.wealthvault.`user-api`.user.UserApiImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class UserApiImplTest {
    @Test
    fun mapsFriendListAndPendingFriendsToDomain() = runTest {
        val friendClient = mockClient(
            """
            {"data":{"friends":[{"id":"friend-1","username":"alice","share_enabled":true}]}}
            """.trimIndent(),
        )
        val pendingClient = mockClient(
            """
            {"data":{"friends":[{"id":"pending-1","username":"bob","shared_enabled":true,"is_close":false}]}}
            """.trimIndent(),
        )

        val friend = FriendApiImpl(friendClient).getFriend().single()
        val pending = PendingFriendApiImpl(pendingClient).pendingFriend().single()

        assertEquals("friend-1", friend.id)
        assertEquals(true, friend.shareEnabled)
        assertEquals("pending-1", pending.id)
        assertEquals(true, pending.sharedEnabled)
        friendClient.close()
        pendingClient.close()
    }

    @Test
    fun mapsDashboardToFixedPointDomainValues() = runTest {
        val client = mockClient(
            """
            {
              "assets":[{"id":"asset-1","type":"CASH","name":"Wallet","amount":123.45}],
              "friend_count":2,
              "liabilities":[],
              "net_worth":{"count":1,"total_assets":123.45,"total_liabilities":0.0,"value":123.45},
              "unique_shared_item_count":1
            }
            """.trimIndent(),
        )

        val result = DashboardApiImpl(client).getDashboard()

        assertEquals(2, result.friendCount)
        assertEquals(Money.fromDecimal("123.45"), result.assets.single().value)
        assertEquals(Money.fromDecimal("123.45"), result.netWorth?.value)
        client.close()
    }

    @Test
    fun mapsFriendProfileAndMessagesToDomain() = runTest {
        val profileClient = mockClient(
            """
            {"data":{"user_info":{"id":"friend-2","username":"carol"},"item_preview":[{"item_id":"item-1","type":"cash","asset_detail":{"id":"cash-1","amount":99.50}}]}}
            """.trimIndent(),
        )
        val messageClient = mockClient(
            """
            {"messages":[{"id":"message-1","sender_id":"friend-2","content":"hello","metadata":{"asset_id":"cash-1"}}]}
            """.trimIndent(),
        )

        val profile = GetFriendProfileApiImpl(profileClient).getFriendProfile("friend-2")
        val message = GetFriendMsgApiImpl(messageClient).getFriendMsg("friend-2").single()

        assertEquals("friend-2", profile.userInfo?.id)
        assertEquals(Money.fromDecimal("99.50"), profile.itemPreview.single().assetDetail?.amount)
        assertEquals("message-1", message.id)
        assertEquals("cash-1", message.metadata?.assetId)
        profileClient.close()
        messageClient.close()
    }

    @Test
    fun mapsUserProfileAndCloseFriendBoundariesToDomain() = runTest {
        val userClient = mockClient(
            """
            {"data":{"id":"user-1","username":"alice","email":"alice@example.com","shared_age":31,"shared_enabled":true}}
            """.trimIndent(),
        )
        val closeFriendClient = mockClient(
            """
            {"data":[{"id":"friend-1","username":"bob","email":"bob@example.com","first_name":"Bob","last_name":"Smith","phone_number":"123","profile":"avatar","birthday":"1990-01-01","shared_age":30,"shared_enabled":true,"created_at":"created","updated_at":"updated","is_close":true}]}
            """.trimIndent(),
        )

        val user = UserApiImpl(userClient).getUser()
        val closeFriend = CloseFriendApiImpl(closeFriendClient).getCloseFriend().single()

        assertEquals("user-1", user.id)
        assertEquals(true, user.shareEnabled)
        assertEquals("friend-1", closeFriend.id)
        assertEquals(true, closeFriend.isClose)
        userClient.close()
        closeFriendClient.close()
    }

    @Test
    fun mapsUserMutationsWithoutExposingWireDtos() = runTest {
        val updateUserClient = mockClient(
            """
            {"data":{"id":"user-1","username":"alice-2","email":"alice@example.com","first_name":"Alice","share_enabled":false}}
            """.trimIndent(),
        )
        val updateCloseFriendClient = mockClient(
            """
            {"status":"success","data":{"success":true}}
            """.trimIndent(),
        )

        val updated = UpdateUserApiImpl(updateUserClient).updateUser(
            UpdateUserDataRequest(
                username = "alice-2",
                firstName = "Alice",
                lastName = "Example",
                birthday = "1990-01-01",
                phoneNumber = "123",
            ),
        )
        val closeFriendUpdated = UpdateCloseFriendApiImpl(updateCloseFriendClient)
            .updateCloseFriend(friendId = "friend-1", isClose = true)

        assertEquals("user-1", updated.id)
        assertEquals("alice-2", updated.username)
        assertEquals(false, updated.shareEnabled)
        assertEquals(true, closeFriendUpdated)
        updateUserClient.close()
        updateCloseFriendClient.close()
    }

    private fun mockClient(payload: String) = HttpClient(MockEngine) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        engine {
            addHandler {
                respond(
                    content = payload,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }
        }
    }
}
