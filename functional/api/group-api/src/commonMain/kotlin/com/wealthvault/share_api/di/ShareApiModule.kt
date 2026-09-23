package com.wealthvault.share_api.di

import com.wealthvault.core.KoinConst
import com.wealthvault.share_api.getitemtoshare.GetItemToShareApi
import com.wealthvault.share_api.getitemtosharegroup.GetItemToShareApiImpl
import com.wealthvault.share_api.getsharefriend.GetShareFriendApi
import com.wealthvault.share_api.getsharefriend.GetShareFriendApiImpl
import com.wealthvault.share_api.getsharegroup.GetShareGroupApi
import com.wealthvault.share_api.getsharegroup.GetShareGroupApiImpl
import com.wealthvault.share_api.itemsharetargets.GetItemShareTargetsApi
import com.wealthvault.share_api.itemsharetargets.GetItemShareTargetsApiImpl
import com.wealthvault.share_api.shareitem.ShareItemApi
import com.wealthvault.share_api.shareitem.ShareItemApiImpl
import com.wealthvault.share_api.unsharefriend.UnShareFriendApi
import com.wealthvault.share_api.unsharefriend.UnShareFriendApiImpl
import com.wealthvault.share_api.unsharegroup.UnShareGroupApi
import com.wealthvault.share_api.unsharegroup.UnShareGroupApiImpl
import io.ktor.client.HttpClient
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

        single<ShareItemApi> { ShareItemApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetItemShareTargetsApi> { GetItemShareTargetsApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetShareGroupApi> { GetShareGroupApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetShareFriendApi> { GetShareFriendApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UnShareGroupApi> { UnShareGroupApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UnShareFriendApi> { UnShareFriendApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetItemToShareApi> { GetItemToShareApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }


    }


}
