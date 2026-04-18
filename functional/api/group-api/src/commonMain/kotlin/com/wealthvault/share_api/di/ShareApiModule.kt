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
        single<GetItemShareTargetsApi> { GetItemShareTargetsApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetShareGroupApi> { GetShareGroupApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetShareFriendApi> { GetShareFriendApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<UnShareGroupApi> { UnShareGroupApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<UnShareFriendApi> { UnShareFriendApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetItemToShareApi> { GetItemToShareApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }


    }


}