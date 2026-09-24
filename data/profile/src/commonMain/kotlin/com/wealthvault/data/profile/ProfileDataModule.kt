package com.wealthvault.data.profile

import com.wealthvault.core.KoinConst
import com.wealthvault.domain.profile.ProfileRepository
import com.wealthvault.domain.profile.CurrentUserRepository
import com.wealthvault.data.profile.repository.ProfileDataSource
import com.wealthvault.data.profile.repository.ProfileRemoteDataSource
import com.wealthvault.data.profile.repository.ProfileRepositoryImpl
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private class CurrentUserRepositoryImpl(
    private val profileRepository: ProfileRepository,
) : CurrentUserRepository {
    override suspend fun getUser() = profileRepository.getUser(force = false)
}

/** Composition-root entry point for profile transport and cache adapters. */
object ProfileDataModule {
    val allModules: List<Module> = listOf(
        module {
            factory { ProfileDataSource(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
            factory<ProfileRemoteDataSource> { get<ProfileDataSource>() }
            single<ProfileRepository> {
                ProfileRepositoryImpl(
                    networkDataSource = get(),
                    logger = get(),
                    cache = get(),
                    json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
                )
            }
            single<CurrentUserRepository> { CurrentUserRepositoryImpl(get()) }
        },
    )
}
