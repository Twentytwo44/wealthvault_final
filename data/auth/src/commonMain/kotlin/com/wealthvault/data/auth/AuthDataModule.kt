package com.wealthvault.data.auth

import com.wealthvault.data.auth.transport.di.ApiModule
import com.wealthvault.data.auth.google.di.GoogleAuthMainModule
import org.koin.core.module.Module

/**
 * The only public DI entry point for the authentication data implementation.
 *
 * The endpoint implementations are still source-compatible archives while
 * their consumers migrate, but the composition root must not know those
 * transport packages. Keeping the list here makes that boundary explicit and
 * gives us one place to replace the adapters with typed repositories later.
 */
object AuthDataModule {
    val allModules: List<Module> = listOf(
        ApiModule.allModules,
        GoogleAuthMainModule.allModules,
        authRepositoriesModule,
    )
}
