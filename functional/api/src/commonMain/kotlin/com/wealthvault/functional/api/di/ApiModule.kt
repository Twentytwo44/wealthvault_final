package com.wealthvault.functional.api.di

import com.wealthvault.core.KoinConst
import com.wealthvault.functional.api.HttpClientBuilder
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
