package com.example.`user-api`.di

import com.example.`user-api`.HttpClientBuilder
import com.example.`user-api`.acceptfriend.AcceptFriendApi
import com.example.`user-api`.acceptfriend.AcceptFriendApiImpl
import com.example.`user-api`.addfriend.AddFriendApi
import com.example.`user-api`.addfriend.AddFriendApiImpl
import com.example.`user-api`.friend.FriendApi
import com.example.`user-api`.friend.FriendApiImpl
import com.example.`user-api`.pendingfriend.PendingFriendApi
import com.example.`user-api`.pendingfriend.PendingFriendApiImpl
import com.example.`user-api`.updateuser.UpdateUserApi
import com.example.`user-api`.updateuser.UpdateUserApiImpl
import com.example.`user-api`.user.UserApi
import com.example.`user-api`.user.UserApiImpl

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
