package com.wealthvault.network

import com.wealthvault.network.di.GlobalApiModule
import org.koin.core.module.Module

/** Public DI boundary for the shared Ktor client implementation. */
object NetworkDataModule {
    val allModules: List<Module> = listOf(GlobalApiModule.allModules)
}
