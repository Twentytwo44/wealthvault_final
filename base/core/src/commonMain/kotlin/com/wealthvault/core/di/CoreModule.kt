package com.wealthvault.core.di

import com.wealthvault.core.architecture.DefaultDispatcherProvider
import com.wealthvault.core.architecture.DispatcherProvider
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.core.observability.LoggingPerformanceTracer
import com.wealthvault.core.observability.PerformanceTracer
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module

/** Shared process-wide infrastructure; feature modules should not register their own dispatcher. */
val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider }
    single<AppLogger> { platformLogger() }
    single<PerformanceTracer> { LoggingPerformanceTracer(get()) }
    single<CoroutineDispatcher> { get<DispatcherProvider>().io }
}
