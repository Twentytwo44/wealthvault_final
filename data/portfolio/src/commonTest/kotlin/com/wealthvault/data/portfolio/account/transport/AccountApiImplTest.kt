package com.wealthvault.data.portfolio.account.transport

import com.wealthvault.data.portfolio.account.transport.getaccount.GetAccountApiImpl
import com.wealthvault.data.portfolio.account.transport.getaccountbyid.GetAccountByIdApiImpl
import com.wealthvault.core.model.Money
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

class AccountApiImplTest {
    @Test
    fun mapsAccountListToDomainMoney() = runTest {
        val client = mockClient(
            """
            {
              "data": [{
                "id": "account-1",
                "user_id": "user-1",
                "name": "Checking",
                "bank_name": "Example Bank",
                "bank_account": "1234",
                "type": "savings",
                "amount": 1000.25,
                "description": "primary"
              }]
            }
            """.trimIndent(),
        )

        val result = GetAccountApiImpl(client).getAccount().single()

        assertEquals("account-1", result.id)
        assertEquals(Money.fromDecimal("1000.25"), result.amount)
        assertEquals("Example Bank", result.bankName)
        client.close()
    }

    @Test
    fun mapsAccountDetailAndFilesToDomain() = runTest {
        val client = mockClient(
            """
            {
              "data": {
                "id": "account-2",
                "user_id": "user-2",
                "amount": 250.50,
                "files": [{"id": "file-1", "url": "https://cdn/account.png", "file_type": "image/png"}]
              }
            }
            """.trimIndent(),
        )

        val result = GetAccountByIdApiImpl(client).getAccountById("account-2")

        assertEquals("account-2", result?.id)
        assertEquals(Money.fromDecimal("250.50"), result?.amount)
        assertEquals("file-1", result?.files?.single()?.id)
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
