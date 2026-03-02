package com.example.account_api

import com.wealthvault.`auth-api`.model.RefreshRequest
import com.wealthvault.`auth-api`.model.RefreshResponse
import com.wealthvault.config.Config
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
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

internal class HttpClientBuilder(
    private val json: Json,
    private val tokenStore: TokenStore
) {
    fun buildDefaultHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) { json(json) }

            // 1. ใช้ Auth Plugin แทนการใส่ Header เองใน DefaultRequest
            install(Auth) {
                bearer {
                    // ดึง Access Token และ Refresh Token มาจาก Store
                    loadTokens {
                        val accessToken = tokenStore.accessToken.first() ?: ""
                        val refreshToken = tokenStore.refreshToken.first() ?: ""
                        BearerTokens(accessToken, refreshToken)
                    }

                    // จุดสำคัญ: ถ้า Server ตอบกลับมาเป็น 401 (Unauthorized)
                    // Ktor จะวิ่งมาที่บล็อกนี้เพื่อขอ Refresh Token ทันที
                    refreshTokens {
                        println("🔄 Access Token expired, refreshing...")

                        // สร้าง Client ใหม่ตัวเล็กๆ เพื่อไปยิงเฉพาะ Route Refresh
                        val client = HttpClient(CIO) {
                            install(ContentNegotiation) { json(json) }
                        }

                        try {
                            // ยิงไปที่ Route Refresh ของคุณ
                            val response: RefreshResponse = client.post("${Config.localhost_android}auth/refresh") {
                                // ส่ง Refresh Token ไปตามที่ Server กำหนด (เช่นใน Body หรือ Header)
                                setBody(RefreshRequest(oldTokens?.refreshToken ?: ""))
                                contentType(ContentType.Application.Json)
                            }.body()

                            // บันทึก Token ใหม่ลงใน Store
//                            tokenStore.saveToken(response.accessToken, response.refreshToken)

                            // ส่ง Token ใหม่กลับไปให้ Ktor เพื่อยิง API เดิมซ้ำอีกครั้ง
                            BearerTokens(
                                response.data?.accessToken ?: "",
                                response.data?.refreshToken ?: ""
                            )                        } catch (e: Exception) {
                            println("❌ Refresh Token failed: ${e.message}")
                            null // ถ้า Refresh ไม่ผ่าน (เช่น Refresh Token หมดอายุด้วย) จะส่ง null เพื่อให้แอป Logout
                        } finally {
                            client.close()
                        }
                    }

                    // กำหนดว่า API ไหนบ้างที่ "ไม่ต้อง" ใส่ Token (เช่น Login/Register)
                    sendWithoutRequest { request ->
                        request.url.pathSegments.contains("auth")
                    }
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }
}
