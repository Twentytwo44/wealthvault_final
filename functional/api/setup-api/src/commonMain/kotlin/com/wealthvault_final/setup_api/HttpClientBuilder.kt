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
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json


class HttpClientBuilder(
    private val json: Json,
    private val tokenStore: TokenStore? = null
) {
    fun build(withAuth: Boolean = true): HttpClient {
        val client = HttpClient(CIO) {
            val safeJson = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                isLenient = true
            }
            install(ContentNegotiation) {
                json(safeJson,contentType = ContentType.Any)
            }

            // ติดตั้ง Logging เอาไว้ดูผลลัพธ์เหมือนเดิม
//            install(Logging) {
//                logger = object : Logger {
//                    override fun log(message: String) {
//                        println("KtorAPI: $message")
//                    }
//                }
//                level = LogLevel.ALL
//            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            // 🌟 ลบ install(Auth) ทิ้ง แล้วใช้ install(HttpSend) แทน
            if (withAuth && tokenStore != null) {
                install(HttpSend) {
                    // กำหนดให้ลองยิงใหม่ได้สูงสุด 2 รอบ (กันลูปอินฟินิตี้)
                    maxSendCount = 2
                }
            }
        }

        // 🌟 เขียน Logic ดักจับ Request / Response ด้วยตัวเอง
        if (withAuth && tokenStore != null) {
            client.plugin(HttpSend).intercept { request ->

                // 1. เช็กว่าไม่ใช่เส้น Auth/Login ค่อยแปะ Token
                val isAuthRoute = request.url.pathSegments.contains("auth") ||
                        request.url.pathSegments.contains("login")

                if (!isAuthRoute) {
                    val accessToken = tokenStore.accessToken.first()
                    if (!accessToken.isNullOrBlank()) {
                        request.header(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }

                // 2. 🚀 ปล่อย Request วิ่งออกไปหา Backend
                var originalCall = execute(request)

                // 3. 🚨 ถ้า Backend ตอบ 401 กลับมา (โดยไม่ต้องสน Header WWW-Authenticate!)
                if (originalCall.response.status == HttpStatusCode.Unauthorized && !isAuthRoute) {
                    println("🔄 401 Detected! กำลังแอบไปขอ Token ใหม่ให้...")

                    val currentRefreshToken = tokenStore.refreshToken.first()

                    if (!currentRefreshToken.isNullOrBlank()) {
                        // สร้าง Client ตัวจิ๋วไปขอ Token
                        val refreshClient = HttpClient(CIO) {
                            install(ContentNegotiation) { json(json) }
                        }

                        try {
                            val response: RefreshResponse = refreshClient.post("${Config.localhost_android}auth/refresh/") {
                                setBody(RefreshRequest(currentRefreshToken))
                                contentType(ContentType.Application.Json)
                            }.body()

                            val newAccess = response.data?.accessToken
                            val newRefresh = response.data?.refreshToken

                            if (!newAccess.isNullOrBlank() && !newRefresh.isNullOrBlank()) {
                                println("✅ ได้ Token ใหม่มาแล้ว! บันทึกลงเครื่องและยิง API เดิมอีกรอบ...")
                                tokenStore.saveAuthToken(AuthToken(newAccess, newRefresh))

                                // 4. ลบ Token เก่าทิ้ง แปะ Token ใหม่ แล้วสั่งยิง API เส้นเดิม!
                                request.headers.remove(HttpHeaders.Authorization)
                                request.header(HttpHeaders.Authorization, "Bearer $newAccess")
                                originalCall = execute(request)

                            } else {
                                println("❌ ขอ Token ใหม่ไม่ผ่าน (API ส่งกลับมาว่างเปล่า)")
                                tokenStore.saveAuthToken(AuthToken(null, null))
                            }
                        } catch (e: Exception) {
                            println("❌ ขอ Token ใหม่พัง: ${e.message}")
                            tokenStore.saveAuthToken(AuthToken(null, null))
                        } finally {
                            refreshClient.close()
                        }
                    } else {
                        println("❌ ไม่มี Refresh Token ในเครื่อง บังคับ Logout")
                        tokenStore.saveAuthToken(AuthToken(null, null))
                    }
                }

                // คืนค่าผลลัพธ์กลับไปให้ UI
                originalCall
            }
        }

        return client
    }
}
