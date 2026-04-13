package com.wealthvault.share_api.di

import com.wealthvault.core.KoinConst
import com.wealthvault.share_api.shareitem.ShareItemApi
import com.wealthvault.share_api.shareitem.ShareItemApiImpl
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

object ShareApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<ShareItemApi> { ShareItemApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }


    }


}
