package com.example.functional.api.di

import com.example.core.KoinConst
import com.example.functional.api.HttpClientBuilder
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module


object ApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.DEFAULT)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<HttpClient>(named(KoinConst.HttpClient.DEFAULT)) {
            HttpClientBuilder(
                get(named(KoinConst.KotlinSerialization.DEFAULT)),
            ).buildDefaultHttpClient()
        }

//        single<Ktorfit> {
//            val httpClient: HttpClient = get(named(KoinConst.HttpClient.DEFAULT))
//            Ktorfit.Builder()
//                .baseUrl(Config.coinRankingUrl)
//                .httpClient(httpClient)
//                .build()
//        }
//        single<CoinRankingApi> { get<Ktorfit>().createCoinRankingApi() }


    }
}