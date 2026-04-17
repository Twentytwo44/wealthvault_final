package com.wealthvault_final.setup_api


import com.wealthvault.`auth-api`.model.RefreshRequest
import com.wealthvault.`auth-api`.model.RefreshResponse
import com.wealthvault.config.Config
import com.wealthvault.data_store.AuthToken
import com.wealthvault.data_store.TokenStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientBuilder(
    private val json: Json,
    private val tokenStore: TokenStore? = null // ปรับเป็น Nullable เพื่อรองรับ Auth-API
) {
    // ปรับชื่อฟังก์ชันให้สั้นลง หรือใช้ชื่อเดิมตามสะดวก
    // เพิ่ม withAuth: Boolean เพื่อแยกการใช้งาน
    fun build(withAuth: Boolean = true): HttpClient {
        return HttpClient(CIO) {
            val safeJson = Json {
                ignoreUnknownKeys = true // ข้ามฟิลด์ที่ไม่รู้จัก (เช่น amount)
                coerceInputValues = true // แปลงค่าว่างเป็น default
                isLenient = true         // ยอมรับ JSON ที่หน้าตาแปลกๆ ได้
            }
            install(ContentNegotiation) {
                json(safeJson)
            }

            // ติดตั้ง Auth Plugin เฉพาะเมื่อต้องการ (Global) และมี TokenStore
            if (withAuth && tokenStore != null) {
                install(Auth) {
                    bearer {
                        loadTokens {
                            // เรียกผ่าน Namespace 'token' ตามที่คุณแบ่งไว้
//                            val authData = tokenStore.authData.first()
//
//                            if (authData.accessToken.isNullOrBlank()) return@loadTokens null

                            BearerTokens(
                                // U1
                                accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im5heW1lZ2FkcmFnb25AZ21haWwuY29tIiwiZXhwIjoxNzc3NjM5NTk4LCJ0eXBlIjoiYWNjZXNzIiwidXNlcl9pZCI6IjUzOGJmMWJmLTQ4NGYtNDdhYS04MDI4LTYzNDU0YjU4ZDI2OSJ9.nj7SYDJIOYKk7OeqfKiq1iwkjoyi2c3ZCSFAFZLQvdo",
                                // U2
//                                accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRlc3RAZ21haWwuY29tIiwiZXhwIjoxNzc5MDE1OTY4LCJ0eXBlIjoiYWNjZXNzIiwidXNlcl9pZCI6IjA2YjVmOWE2LWY1MGItNGFhOS1hOTVjLTBjZWI4MTJiOGYwYiJ9.JzYSGH9YZ2GmU-18WVlnxO-eKEXT2NrXAxXm43stTCI",
                                refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRlc3RAZ21haWwuY29tIiwiZXhwIjoxNzc0NTE5NzY2LCJ0eXBlIjoicmVmcmVzaCIsInVzZXJfaWQiOiIyNjhlYzIyZi0yOGQ5LTQzZmYtYjg5OS04ODdiMDI1MzZiN2QifQ.cVdNvwpAOMb8aHWcXr5IK1cyS1u9E4l_GyFJAidjSqI"

                            )
                        }

                        refreshTokens {
                            println("🔄 Access Token expired, refreshing...")

                            val client = HttpClient(CIO) {
                                install(ContentNegotiation) { json(json) }
                            }

                            try {
                                val response: RefreshResponse = client.post("${Config.localhost_android}auth/refresh/") {
                                    setBody(RefreshRequest(oldTokens?.refreshToken ?: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRlc3RAZ21haWwuY29tIiwiZXhwIjoxNzczOTE1ODY2LCJ0eXBlIjoiYWNjZXNzIiwidXNlcl9pZCI6IjI2OGVjMjJmLTI4ZDktNDNmZi1iODk5LTg4N2IwMjUzNmI3ZCJ9.C4rtORbHvK1hEFdKrNWNMUcARdohw_mbpLkay7FtaFM"))
                                    contentType(ContentType.Application.Json)
                                }.body()

                                val newAccess = response.data?.accessToken ?: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRlc3RAZ21haWwuY29tIiwiZXhwIjoxNzc0OTgwMzc1LCJ0eXBlIjoiYWNjZXNzIiwidXNlcl9pZCI6IjI2OGVjMjJmLTI4ZDktNDNmZi1iODk5LTg4N2IwMjUzNmI3ZCJ9.aRVlwjBWNnYLcFejyn_hHsTioh1-VkmBERn5lcRHtYQ"
                                val newRefresh = response.data?.refreshToken ?: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRlc3RAZ21haWwuY29tIiwidHlwZSI6InJlZnJlc2giLCJ1c2VyX2lkIjoiMjY4ZWMyMmYtMjhkOS00M2ZmLWI4OTktODg3YjAyNTM2YjdkIn0.P8QeHyBn1HAzjP9_WJFBVkwVDYhBiT8DabaJKjAs1y0"

                                if (newAccess.isNotBlank()) {
                                    // บันทึกผ่าน Namespace 'token'
                                    tokenStore.saveAuthToken(AuthToken(newAccess, newRefresh))

                                    BearerTokens(newAccess, newRefresh)
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                println("❌ Refresh Token failed: ${e.message}")
                                // กรณี refresh ไม่ผ่าน อาจจะสั่งล้าง token เพื่อให้เด้งไปหน้า login
                                tokenStore.saveAuthToken(AuthToken(null, null))
                                null
                            } finally {
                                client.close()
                            }
                        }

                        // ข้ามการแปะ Token สำหรับ Path ที่เกี่ยวกับ Auth
                        sendWithoutRequest { request ->
                            request.url.pathSegments.contains("auth") ||
                                    request.url.pathSegments.contains("login")
                        }
                    }
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }
}
