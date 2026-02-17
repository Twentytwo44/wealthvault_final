package com.wealthvault.`auth-api`.di

import com.wealthvault.`auth-api`.HttpClientBuilder
import com.wealthvault.`auth-api`.login.LoginApi
import com.wealthvault.`auth-api`.login.LoginApiImpl
import com.wealthvault.`auth-api`.register.RegisterApi
import com.wealthvault.`auth-api`.register.RegisterApiImpl
import com.wealthvault.config.Config
import com.wealthvault.core.KoinConst
import de.jensklingenberg.ktorfit.Ktorfit
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

        single<Ktorfit> {
            val httpClient: HttpClient = get(named(KoinConst.HttpClient.DEFAULT))
            println("🔥 ACTUAL BASE URL = ${Config.localhost_android}")
            Ktorfit.Builder()
                .baseUrl(Config.localhost_android)
                .httpClient(httpClient)
                .build()
        }
        single<LoginApi> { LoginApiImpl(get()) }
        single<RegisterApi> { RegisterApiImpl(get()) }
    }


}
