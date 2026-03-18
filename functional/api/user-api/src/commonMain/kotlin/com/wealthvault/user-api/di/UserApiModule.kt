package com.wealthvault.`user-api`.di

import com.wealthvault.`user-api`.HttpClientBuilder
import com.wealthvault.`user-api`.acceptfriend.AcceptFriendApi
import com.wealthvault.`user-api`.acceptfriend.AcceptFriendApiImpl
import com.wealthvault.`user-api`.addfriend.AddFriendApi
import com.wealthvault.`user-api`.addfriend.AddFriendApiImpl
import com.wealthvault.`user-api`.friend.FriendApi
import com.wealthvault.`user-api`.friend.FriendApiImpl
import com.wealthvault.`user-api`.pendingfriend.PendingFriendApi
import com.wealthvault.`user-api`.pendingfriend.PendingFriendApiImpl
import com.wealthvault.`user-api`.updateuser.UpdateUserApi
import com.wealthvault.`user-api`.updateuser.UpdateUserApiImpl
import com.wealthvault.`user-api`.user.UserApi
import com.wealthvault.`user-api`.user.UserApiImpl

import com.wealthvault.config.Config
import com.wealthvault.core.KoinConst
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module


object UserApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.USER)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<HttpClient>(named(KoinConst.HttpClient.USER)) {
            HttpClientBuilder(
                get(named(KoinConst.KotlinSerialization.USER)),get()
            ).buildDefaultHttpClient()
        }

        single<Ktorfit>(named(KoinConst.Ktor.USER))  {
            val httpClient: HttpClient = get(named(KoinConst.HttpClient.USER))
            Ktorfit.Builder()
                .baseUrl(Config.localhost_android)
                .httpClient(httpClient)
                .build()
        }

        single<AcceptFriendApi> { AcceptFriendApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<AddFriendApi> { AddFriendApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<FriendApi> { FriendApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<UserApi> { UserApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<PendingFriendApi> { PendingFriendApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<UpdateUserApi> { UpdateUserApiImpl(get(named(KoinConst.Ktor.USER))) }



    }


}
