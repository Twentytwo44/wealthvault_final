package com.wealthvault.data.social.share.transport.di

import com.wealthvault.core.KoinConst
import com.wealthvault.data.social.share.transport.getitemtoshare.GetItemToShareApi
import com.wealthvault.data.social.share.transport.getitemtosharegroup.GetItemToShareApiImpl
import com.wealthvault.data.social.share.transport.getsharefriend.GetShareFriendApi
import com.wealthvault.data.social.share.transport.getsharefriend.GetShareFriendApiImpl
import com.wealthvault.data.social.share.transport.getsharegroup.GetShareGroupApi
import com.wealthvault.data.social.share.transport.getsharegroup.GetShareGroupApiImpl
import com.wealthvault.data.social.share.transport.itemsharetargets.GetItemShareTargetsApi
import com.wealthvault.data.social.share.transport.itemsharetargets.GetItemShareTargetsApiImpl
import com.wealthvault.data.social.share.transport.shareitem.ShareItemApi
import com.wealthvault.data.social.share.transport.shareitem.ShareItemApiImpl
import com.wealthvault.data.social.share.transport.unsharefriend.UnShareFriendApi
import com.wealthvault.data.social.share.transport.unsharefriend.UnShareFriendApiImpl
import com.wealthvault.data.social.share.transport.unsharegroup.UnShareGroupApi
import com.wealthvault.data.social.share.transport.unsharegroup.UnShareGroupApiImpl
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

object ShareApiModule {
    val allModules = module {
        single<ShareItemApi> { ShareItemApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetItemShareTargetsApi> { GetItemShareTargetsApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetShareGroupApi> { GetShareGroupApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetShareFriendApi> { GetShareFriendApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UnShareGroupApi> { UnShareGroupApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UnShareFriendApi> { UnShareFriendApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetItemToShareApi> { GetItemToShareApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }


    }


}
