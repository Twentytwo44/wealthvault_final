package com.wealthvault.share_api

import com.wealthvault.core.model.Money
import com.wealthvault.share_api.getitemtosharegroup.GetItemToShareApiImpl
import com.wealthvault.share_api.getsharefriend.GetShareFriendApiImpl
import com.wealthvault.share_api.itemsharetargets.GetItemShareTargetsApiImpl
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

class ShareApiImplTest {
    @Test
    fun mapsItemsToShareIntoDomainMoney() = runTest {
        val client = mockClient(
            """
            {
              "items": [{
                "id": "cash-1",
                "type": "cash",
                "name": "Emergency",
                "value": 1234.50,
                "is_shared": true
              }]
            }
            """.trimIndent(),
        )

        val result = GetItemToShareApiImpl(client).getItemsToShare("friend", "friend-1").single()

        assertEquals("cash-1", result.id)
        assertEquals(Money.fromDecimal("1234.50"), result.value)
        assertEquals(true, result.isShared)
        client.close()
    }

    @Test
    fun mapsSharedFriendAssetAndTargetsToDomain() = runTest {
        val client = mockClient(
            """
            {
              "data": [{
                "shared_item_id": "shared-1",
                "shared_by": "user-1",
                "type": "liability",
                "asset_detail": {
                  "id": "liability-1",
                  "principal": 5000.25,
                  "creditor": "Bank"
                }
              }]
            }
            """.trimIndent(),
        )

        val result = GetShareFriendApiImpl(client).getShareFriend("friend-1").single()

        assertEquals("shared-1", result.groupItemId)
        assertEquals(Money.fromDecimal("5000.25"), result.assetDetail?.principal)
        assertEquals("Bank", result.assetDetail?.creditor)
        client.close()
    }

    @Test
    fun mapsShareTargetsToDomain() = runTest {
        val client = mockClient(
            """
            {
              "groups": [{"group_id": "group-1", "group_name": "Family", "member_count": 3}],
              "friends": [{"friend_id": "friend-1", "username": "alice"}],
              "emails": [{"email": "alice@example.com", "is_sent": true}]
            }
            """.trimIndent(),
        )

        val result = GetItemShareTargetsApiImpl(client).getItemShareTargets("cash", "cash-1")

        assertEquals("group-1", result.groups?.single()?.groupId)
        assertEquals("friend-1", result.friends?.single()?.friendId)
        assertEquals(true, result.emails?.single()?.isSent)
        client.close()
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
