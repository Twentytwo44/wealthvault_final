package com.wealthvault.`user-api`.di

import com.wealthvault.core.KoinConst
import com.wealthvault.`user-api`.acceptfriend.AcceptFriendApi
import com.wealthvault.`user-api`.acceptfriend.AcceptFriendApiImpl
import com.wealthvault.`user-api`.addfriend.AddFriendApi
import com.wealthvault.`user-api`.addfriend.AddFriendApiImpl
import com.wealthvault.`user-api`.closefriend.CloseFriendApi
import com.wealthvault.`user-api`.dashboard.DashboardApi
import com.wealthvault.`user-api`.dashboard.DashboardApiImpl
import com.wealthvault.`user-api`.deletefriend.DeleteFriendApi
import com.wealthvault.`user-api`.deletefriend.DeleteFriendApiImpl
import com.wealthvault.`user-api`.friend.FriendApi
import com.wealthvault.`user-api`.friend.FriendApiImpl
import com.wealthvault.`user-api`.friendmsg.GetFriendMsgApi
import com.wealthvault.`user-api`.friendmsg.GetFriendMsgApiImpl
import com.wealthvault.`user-api`.friendprofile.GetFriendProfileApi
import com.wealthvault.`user-api`.friendprofile.GetFriendProfileApiImpl
import com.wealthvault.`user-api`.getuserbyemail.GetUserByEmailApi
import com.wealthvault.`user-api`.getuserbyemail.GetUserByEmailApiImpl
import com.wealthvault.`user-api`.pendingfriend.PendingFriendApi
import com.wealthvault.`user-api`.pendingfriend.PendingFriendApiImpl
import com.wealthvault.`user-api`.updateclosefriend.UpdateCloseFriendApi
import com.wealthvault.`user-api`.updateclosefriend.UpdateCloseFriendApiImpl
import com.wealthvault.`user-api`.updateuser.UpdateUserApi
import com.wealthvault.`user-api`.updateuser.UpdateUserApiImpl
import com.wealthvault.`user-api`.user.UserApi
import com.wealthvault.`user-api`.user.UserApiImpl
import com.wealthvault.user_api.closefriend.CloseFriendApiImpl
import io.ktor.client.HttpClient
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

        single<AcceptFriendApi> { AcceptFriendApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<AddFriendApi> { AddFriendApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<FriendApi> { FriendApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UserApi> { UserApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<PendingFriendApi> { PendingFriendApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateUserApi> { UpdateUserApiImpl(client = get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<CloseFriendApi> { CloseFriendApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateCloseFriendApi> {
            UpdateCloseFriendApiImpl(client = get<HttpClient>(named(KoinConst.HttpClient.GLOBAL)))
        }
        single<DashboardApi> {
            DashboardApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL)))
        }
        single<GetUserByEmailApi> { GetUserByEmailApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL)))}
        single<GetFriendMsgApi> { GetFriendMsgApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetFriendProfileApi> { GetFriendProfileApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteFriendApi> { DeleteFriendApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
    }


}
