package com.wealthvault.`auth-api`

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class HttpClientBuilder(
    private val json: Json,
) {
    fun buildDefaultHttpClient(): HttpClient {
        return HttpClient {
            // 1. ตรวจสอบว่ามี Plugin นี้แล้ว (ถูกแล้ว)
            install(ContentNegotiation) {
                json(json)
            }

            // 2. เพิ่มส่วนนี้: บังคับ Header ให้เป็น JSON ทุก Request
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }


        }
    }
}
