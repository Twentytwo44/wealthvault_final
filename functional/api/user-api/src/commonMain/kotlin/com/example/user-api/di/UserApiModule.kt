package com.example.`user-api`.di

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
import com.wealthvault.core.KoinConst
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module


object UserApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<AcceptFriendApi> { AcceptFriendApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<AddFriendApi> { AddFriendApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<FriendApi> { FriendApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<UserApi> { UserApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<PendingFriendApi> { PendingFriendApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<UpdateUserApi> { UpdateUserApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }

    }


}
