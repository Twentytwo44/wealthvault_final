package com.wealthvault.profile

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.profile.data.ProfileDataSource
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
import kotlin.test.assertIs

class ProfileDataSourceTest {
    @Test
    fun mapsUserResponseToDomainWithoutLeakingRemoteDto() = runTest {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = """
                            {
                              "status": "success",
                              "data": {
                                "id": "user-1",
                                "username": "alice",
                                "shared_enabled": true,
                                "shared_age": 31
                              }
                            }
                        """.trimIndent(),
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val result = ProfileDataSource(client).getUser()

        val user = assertIs<AppResult.Success<*>>(result).value as com.wealthvault.domain.profile.UserData
        assertEquals("user-1", user.id)
        assertEquals("alice", user.username)
        assertEquals(true, user.shareEnabled)
        assertEquals(31, user.sharedAge)
        client.close()
    }

    @Test
    fun mapsFriendCollectionsAndPreservesEmptyResponses() = runTest {
        val client = jsonClient(
            """
            {"data":[{"id":"close-1","username":"bob","email":"b@example.com","first_name":"Bob","last_name":"B","phone_number":"1","profile":"p","birthday":"2000-01-01","shared_age":20,"shared_enabled":true,"created_at":"now","updated_at":"now","is_close":true}]}
            """.trimIndent(),
            """
            {"data":{"friends":[{"id":"friend-1","username":"carol","share_enabled":false,"is_friend":true}]}}
            """.trimIndent(),
        )

        val closeFriends = assertIs<AppResult.Success<*>>(ProfileDataSource(client).getCloseFriends()).value as List<*>
        assertEquals("close-1", (closeFriends.single() as com.wealthvault.domain.profile.CloseFriendData).id)
        val friends = assertIs<AppResult.Success<*>>(ProfileDataSource(client).getAllFriends()).value as List<*>
        assertEquals("friend-1", (friends.single() as com.wealthvault.domain.profile.FriendData).id)
        client.close()

        val emptyClient = jsonClient("{\"data\":null}", "{\"data\":null}")
        assertEquals(emptyList<Any>(), assertIs<AppResult.Success<*>>(ProfileDataSource(emptyClient).getCloseFriends()).value)
        assertEquals(emptyList<Any>(), assertIs<AppResult.Success<*>>(ProfileDataSource(emptyClient).getAllFriends()).value)
        emptyClient.close()
    }

    @Test
    fun mapsMutationResponsesAndSendsOptionalMultipartFields() = runTest {
        val client = jsonClient(
            """
            {"data":{"id":"user-2","username":"updated","shared_age":35,"share_enabled":true}}
            """.trimIndent(),
            """
            {"data":{"success":true}}
            """.trimIndent(),
        )
        val source = ProfileDataSource(client)
        val updated = assertIs<AppResult.Success<*>>(source.updateUserData(
            com.wealthvault.domain.profile.UpdateUserDataRequest(
                username = "updated",
                firstName = "First",
                lastName = "Last",
                birthday = "2000-01-01",
                phoneNumber = "123",
                profileImage = byteArrayOf(1, 2, 3),
                sharedEnabled = true,
                sharedAge = 35,
            ),
        )).value as com.wealthvault.domain.profile.UpdateUserData
        assertEquals("user-2", updated.id)
        assertEquals(true, updated.shareEnabled)
        assertEquals(true, assertIs<AppResult.Success<*>>(source.updateCloseFriendStatus("friend-1", true)).value)
        client.close()
    }

    @Test
    fun convertsRemoteErrorsToFailureResults() = runTest {
        val client = jsonClient(
            "{\"error\":\"close friend failed\"}",
            "{\"error\":\"friends failed\"}",
            "{\"error\":\"user failed\"}",
            "{\"error\":\"update failed\"}",
            "{\"status\":\"close update failed\"}",
        )
        val source = ProfileDataSource(client)
        assertIs<AppResult.Failure>(source.getCloseFriends())
        assertIs<AppResult.Failure>(source.getAllFriends())
        assertIs<AppResult.Failure>(source.getUser())
        assertIs<AppResult.Failure>(source.updateUserData(
            com.wealthvault.domain.profile.UpdateUserDataRequest("u", "f", "l", "b", "p"),
        ))
        assertIs<AppResult.Failure>(source.updateCloseFriendStatus("friend-1", false))
        client.close()
    }

    private fun jsonClient(vararg payloads: String): HttpClient {
        val responses = ArrayDeque(payloads.toList())
        return HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = responses.removeFirst(),
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }
}
