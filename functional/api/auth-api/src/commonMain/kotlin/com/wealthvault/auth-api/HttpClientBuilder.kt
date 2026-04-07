package com.wealthvault.`auth-api`

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpSend // 🌟 Import เพิ่มเติม
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin // 🌟 Import เพิ่มเติม
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientBuilder(
    private val json: Json,
) {
    fun buildDefaultHttpClient(): HttpClient {
        val client = HttpClient {
            // 1. ตรวจสอบว่ามี Plugin นี้แล้ว (ถูกแล้ว)
            install(ContentNegotiation) {
                json(json)
            }

            // 2. บังคับ Header ให้เป็น JSON ทุก Request
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }

        // 🌟 3. ไม้ตายดักจับ URL ก่อนส่งออกไปจริงๆ!
        client.plugin(HttpSend).intercept { request ->
            println("============================================================")
            println("API : ${request.url}")
            println("============================================================")
            execute(request)
        }

        return client
    }
}