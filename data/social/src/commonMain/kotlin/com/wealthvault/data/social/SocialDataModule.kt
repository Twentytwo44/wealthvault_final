package com.wealthvault.data.social

import com.wealthvault.core.KoinConst
import com.wealthvault.domain.social.GroupChatGateway
import com.wealthvault.domain.social.SocialRepository
import com.wealthvault.data.social.group.transport.di.GroupApiModule
import com.wealthvault.data.social.share.transport.di.ShareApiModule
import com.wealthvault.social.data.KtorSocialUserTransport
import com.wealthvault.social.data.SocialDataSource
import com.wealthvault.social.data.SocialRemoteDataSource
import com.wealthvault.social.data.SocialRepositoryImpl
import com.wealthvault.social.data.SocialUserTransport
import com.wealthvault.social.data.websocket.KtorWebSocketTransport
import com.wealthvault.social.data.websocket.WebSocketGroupChatGateway
import com.wealthvault.social.data.websocket.WebSocketTransport
import com.wealthvault.social.data.share.ShareItemNetworkDataSource
import com.wealthvault.social.data.share.ShareItemRepositoryImpl
import com.wealthvault.social.data.share.ShareTargetsNetworkDataSource
import com.wealthvault.social.data.share.ShareTargetsRepositoryImpl
import com.wealthvault.social.data.share.UnshareRepositoryImpl
import com.wealthvault.domain.social.ShareItemRepository
import com.wealthvault.domain.social.ShareTargetsRepository
import com.wealthvault.domain.social.UnshareRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/** Composition boundary for social transport, cache, and repository wiring. */
object SocialDataModule {
    val allModules: List<Module> = listOf(
        GroupApiModule.allModules,
        ShareApiModule.allModules,
        socialWebSocketModule,
        socialRepositoriesModule,
    )
}

private val socialWebSocketModule = module {
    single(named(KoinConst.KotlinSerialization.WEBSOCKET)) {
        Json { ignoreUnknownKeys = true }
    }
    single<HttpClient>(named(KoinConst.HttpClient.WEBSOCKET)) {
        HttpClient {
            install(WebSockets)
            install(ContentNegotiation) {
                json(get<Json>(named(KoinConst.KotlinSerialization.WEBSOCKET)))
            }
        }
    }
    single<WebSocketTransport> {
        KtorWebSocketTransport(get(named(KoinConst.HttpClient.WEBSOCKET)))
    }
    single<GroupChatGateway> {
        WebSocketGroupChatGateway(
            service = get(),
            json = get(named(KoinConst.KotlinSerialization.WEBSOCKET)),
        )
    }
}

private val socialRepositoriesModule = module {
    factory { ShareItemNetworkDataSource(get()) }
    single<ShareItemRepository> { ShareItemRepositoryImpl(get()) }
    factory { ShareTargetsNetworkDataSource(get()) }
    single<ShareTargetsRepository> { ShareTargetsRepositoryImpl(get()) }
    single<UnshareRepository> { UnshareRepositoryImpl(get(), get(), get(), get()) }

    factory<SocialUserTransport> {
        KtorSocialUserTransport(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL)))
    }
    factory<SocialRemoteDataSource> {
        SocialDataSource(
            profileRepository = get(),
            groupApi = get(),
            createGroupApi = get(),
            userTransport = get(),
            portfolioRepository = get(),
            getGroupMsgApi = get(),
            getShareFriendApi = get(),
            getShareGroupApi = get(),
            grantAccessApi = get(),
            getGroupApi = get(),
            getGroupMemberApi = get(),
            updateGroupApi = get(),
            addMemberApi = get(),
            removeMemberApi = get(),
            leaveGroupApi = get(),
            getItemToShareApi = get(),
            shareItemApi = get(),
            unShareFriendApi = get(),
            unShareGroupApi = get(),
            deleteGroupApi = get(),
        )
    }
    single<SocialRepository> {
        SocialRepositoryImpl(
            dataSource = get(),
            cache = get(),
            json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
        )
    }
}
