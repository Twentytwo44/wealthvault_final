package com.wealthvault.api

import com.wealthvault.data.portfolio.account.transport.createaccount.CreateAccountApiImpl
import com.wealthvault.data.portfolio.account.transport.deleteaccount.DeleteAccountApiImpl
import com.wealthvault.data.portfolio.account.transport.updateaccount.UpdateAccountApiImpl
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.BankAccountFileUploadData
import com.wealthvault.domain.portfolio.BankAccountRequest
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

class AccountCrudBoundaryTest {
    @Test
    fun createUpdateAndDeleteStayInsideTheApiBoundary() = runTest {
        val request = BankAccountRequest(
            name = "Checking",
            bankName = "Example Bank",
            bankAccount = "1234",
            type = "savings",
            amount = Money(12345),
            description = "primary",
            files = listOf(BankAccountFileUploadData(byteArrayOf(1, 2), "image/png", "account.png")),
            deleteListId = listOf("old-file"),
        )

        val created = CreateAccountApiImpl(mockClient("""{"data":{"id":"account-1","user_id":"user-1","amount":123.45}}""")).create(request)
        assertEquals("account-1", created.id)
        assertEquals(Money(12345), created.amount)

        val updated = UpdateAccountApiImpl(mockClient("""{"data":{"id":"account-1","user_id":"user-1","amount":200.00}}""")).updateAccount("account-1", request)
        assertEquals(Money(20000), updated.amount)

        DeleteAccountApiImpl(mockClient("""{"status":"success","data":{"success":"true"}}""")).deleteAccount("account-1")
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
