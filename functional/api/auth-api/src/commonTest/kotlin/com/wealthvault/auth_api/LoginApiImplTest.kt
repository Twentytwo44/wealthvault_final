package com.wealthvault.auth_api

import com.wealthvault.`auth-api`.login.LoginApiImpl
import com.wealthvault.`auth-api`.model.LoginRequest
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals


class LoginApiImplTest {

    @Test
    fun login_success_with_Mokkery_and_MockEngine() = runTest {
        // 1. เตรียม JSON จำลองสำหรับ Server
        val jsonResponse = """
    {
        "data": {
            "success": true,
            "access_token": "eyJhbGciOiJIUzI1Ni...",
            "refresh_token": "eyJhbGciOiJIUzI1Ni...",
            "user_id": "02d120bb-0f5d-4684-af22-14f78a0db1dd"
        },
        "status": "login success"
    }
    """.trimIndent()

        // 2. ตั้งค่า MockEngine ของ Ktor เพื่อดักจับ Request
        val mockEngine = MockEngine { request ->
            respond(
                content = jsonResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        // 3. สร้าง HttpClient และ Ktorfit
        val client = HttpClient(mockEngine) {
            // เพิ่มส่วนนี้เข้าไปเพื่อให้ HttpClient รู้จักวิธีจัดการ LoginRequest เป็น JSON
            install(ContentNegotiation) {
                json(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })

            }
            defaultRequest {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }

        val ktorfit = Ktorfit.Builder()
            .httpClient(client)
            .baseUrl("http://localhost:8080/api/")
            .build()

        // 4. สร้าง LoginApiImpl (ตัวที่เราต้องการเทสจริงๆ)
        val apiImpl = LoginApiImpl(ktorfit)

        // 5. รันการทดสอบ
        val response = apiImpl.login(LoginRequest("naymegadragon@gmail.com", "dino2064"))
        println("--- API Response Result ---")
        println("Status: ${response.status}")
        println("User ID: ${response.data?.userId}")
        println("Access Token Length: ${response.data?.accessToken?.length}")

        // ตรวจสอบเงื่อนไขตาม JSON จริง
        assertEquals("login success", response.status) // แก้ให้ตรงกับ JSON จริง "login success"
        assertEquals("02d120bb-0f5d-4684-af22-14f78a0db1dd", response.data?.userId)

    }
}
