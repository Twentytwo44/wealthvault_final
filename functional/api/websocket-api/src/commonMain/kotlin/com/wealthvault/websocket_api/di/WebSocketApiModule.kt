package com.wealthvault.websocket_api.di

import com.wealthvault.core.KoinConst
import com.wealthvault.websocket_api.WebSocketService
import com.wealthvault.websocket_api.WebSocketServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

object WebSocketApiModule {
    val module = module {
        // Fix: Provide as both Json AND SerialFormat using 'createdAtStart' or 'bind'
        single(named(KoinConst.KotlinSerialization.WEBSOCKET)) {
            Json {
                ignoreUnknownKeys = true
            }
        } bind SerialFormat::class // This is the magic line

        single<HttpClient>(named(KoinConst.HttpClient.WEBSOCKET)) {
            HttpClient {
                install(WebSockets) {
                    // Ktor looks for SerialFormat here
                    contentConverter = KotlinxWebsocketSerializationConverter(
                        get<Json>(named(KoinConst.KotlinSerialization.WEBSOCKET))
                    )
                }
                install(ContentNegotiation) {
                    json(get<Json>(named(KoinConst.KotlinSerialization.WEBSOCKET)))
                }
            }
        }

        single<WebSocketService> {
            WebSocketServiceImpl(get(named(KoinConst.HttpClient.WEBSOCKET)))
        }
    }
}
