package com.wealthvault.data.dashboard

import com.wealthvault.core.KoinConst
import com.wealthvault.data.dashboard.repository.DashboardDataSource
import com.wealthvault.data.dashboard.repository.DashboardRemoteDataSource
import com.wealthvault.data.dashboard.repository.DashboardRepositoryImpl
import com.wealthvault.domain.portfolio.DashboardRepository
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/** Composition-root entry point for dashboard transport and cache adapters. */
object DashboardDataModule {
    val allModules: List<Module> = listOf(
        module {
            single {
                DashboardDataSource(
                    client = get<HttpClient>(named(KoinConst.HttpClient.GLOBAL)),
                )
            }
            single<DashboardRemoteDataSource> { get<DashboardDataSource>() }
            single<DashboardRepository> {
                DashboardRepositoryImpl(
                    networkDataSource = get(),
                    cache = get(),
                    json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
                    logger = get(),
                    tracer = get(),
                )
            }
        },
    )
}
