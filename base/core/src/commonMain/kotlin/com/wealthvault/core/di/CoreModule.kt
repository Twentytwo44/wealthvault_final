package com.wealthvault.core.di

import com.wealthvault.core.architecture.DefaultDispatcherProvider
import com.wealthvault.core.architecture.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module

/** Shared process-wide infrastructure; feature modules should not register their own dispatcher. */
val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider }
    single<CoroutineDispatcher> { get<DispatcherProvider>().io }
}
