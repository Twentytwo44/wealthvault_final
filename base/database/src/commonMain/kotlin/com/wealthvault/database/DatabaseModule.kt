package com.wealthvault.database

import com.wealthvault.core.cache.DashboardCache
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.NotificationCache
import org.koin.dsl.module

val databaseModule = module {
    single { WealthVaultDatabase(get()) }
    single { DashboardCacheStore(get()) }
    single<DashboardCache> { get<DashboardCacheStore>() }
    single { NotificationCacheStore(get()) }
    single<NotificationCache> { get<NotificationCacheStore>() }
    single { FeatureCacheStore(get()) }
    single<FeatureCache> { get<FeatureCacheStore>() }
}
