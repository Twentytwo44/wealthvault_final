package com.wealthvault.data.social.group.transport

import com.wealthvault.data.social.group.transport.getgrouplist.GetAllGroupApiImpl
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

class GetAllGroupApiImplTest {
    @Test
    fun mapsWireResponseToDomainSummary() = runTest {
        val client = HttpClient(MockEngine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler {
                    respond(
                        content = """
                            {
                              "data": [{
                                "id": "group-1",
                                "group_name": "Family",
                                "group_profile": "family.png",
                                "created_by": "user-1",
                                "member_count": 3,
                                "created_at": "2026-01-01",
                                "updated_at": "2026-01-02"
                              }]
                            }
                        """.trimIndent(),
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            }
        }

        val groups = GetAllGroupApiImpl(client).getAllGroup()

        assertEquals("group-1", groups.single().id)
        assertEquals("Family", groups.single().groupName)
        assertEquals(3, groups.single().memberCount)
        client.close()
    }
}
