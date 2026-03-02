package com.wealthvault.`auth-api`.di

import com.wealthvault.`auth-api`.HttpClientBuilder
import com.wealthvault.`auth-api`.fgpassword.ForgetApi
import com.wealthvault.`auth-api`.fgpassword.ForgetApiImpl
import com.wealthvault.`auth-api`.login.LoginApi
import com.wealthvault.`auth-api`.login.LoginApiImpl
import com.wealthvault.`auth-api`.otp.OTPApi
import com.wealthvault.`auth-api`.otp.OTPApiImpl
import com.wealthvault.`auth-api`.refreshtoken.RefreshTokenApi
import com.wealthvault.`auth-api`.refreshtoken.RefreshTokenImpl
import com.wealthvault.`auth-api`.register.RegisterApi
import com.wealthvault.`auth-api`.register.RegisterApiImpl
import com.wealthvault.`auth-api`.rspassword.ResetApi
import com.wealthvault.`auth-api`.rspassword.ResetApiImpl
import com.wealthvault.config.Config
import com.wealthvault.core.KoinConst
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module


object ApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.AUTH)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<HttpClient>(named(KoinConst.HttpClient.AUTH)) {
            HttpClientBuilder(
                get(named(KoinConst.KotlinSerialization.AUTH)),
            ).buildDefaultHttpClient()
        }

        single<Ktorfit>(named(KoinConst.Ktor.AUTH)) {
            val httpClient: HttpClient = get(named(KoinConst.HttpClient.AUTH))
            Ktorfit.Builder()
                .baseUrl(Config.localhost_android)
                .httpClient(httpClient)
                .build()
        }
        single<LoginApi> { LoginApiImpl(get(named(KoinConst.Ktor.AUTH))) }
        single<RegisterApi> { RegisterApiImpl(get(named(KoinConst.Ktor.AUTH))) }
        single<RefreshTokenApi> { RefreshTokenImpl(get(named(KoinConst.Ktor.AUTH))) }
        single<ForgetApi> { ForgetApiImpl(get(named(KoinConst.Ktor.AUTH))) }
        single<ResetApi> { ResetApiImpl(get(named(KoinConst.Ktor.AUTH))) }
        single<OTPApi> { OTPApiImpl(get(named(KoinConst.Ktor.AUTH))) }

    }


}
