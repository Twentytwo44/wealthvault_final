package com.wealthvault.data.social.group.transport

import com.wealthvault.data.social.group.transport.getgroupdetail.GetGroupApiImpl
import com.wealthvault.data.social.group.transport.getmember.GetGroupMemberApiImpl
import com.wealthvault.data.social.group.transport.groupmsg.GetGroupMsgApiImpl
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

class GroupApiImplTest {
    @Test
    fun mapsGroupDetailToDomain() = runTest {
        val client = mockClient(
            """
            {
              "status": "success",
              "data": {"id": "group-1", "group_name": "Family", "member_count": 4}
            }
            """.trimIndent(),
        )

        val result = GetGroupApiImpl(client).getGroupDetail("group-1")

        assertEquals("group-1", result.data?.id)
        assertEquals("Family", result.data?.groupName)
        assertEquals(4, result.data?.memberCount)
        client.close()
    }

    @Test
    fun mapsMembersAndMessagesToDomain() = runTest {
        val membersClient = mockClient(
            """
            {"data": {"members": [{"id": "user-1", "username": "alice", "is_friend": true}]}}
            """.trimIndent(),
        )
        val members = GetGroupMemberApiImpl(membersClient).getGroupMembers("group-1")
        assertEquals("user-1", members.single().id)
        assertEquals(true, members.single().isFriend)
        membersClient.close()

        val messagesClient = mockClient(
            """
            {"messages": [{"sender_id": "user-1", "msg_type": "share", "content": "hello", "metadata": {"asset_id": "asset-1"}}]}
            """.trimIndent(),
        )
        val messages = GetGroupMsgApiImpl(messagesClient).getGroupMsg("group-1")
        assertEquals("user-1", messages.single().senderId)
        assertEquals("asset-1", messages.single().metadata?.assetId)
        messagesClient.close()
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
