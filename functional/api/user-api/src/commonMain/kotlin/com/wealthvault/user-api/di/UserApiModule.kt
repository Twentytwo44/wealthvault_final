package com.wealthvault.`user-api`.di

import com.wealthvault.core.KoinConst
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
