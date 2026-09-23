package com.wealthvault.setup_api.di

import com.wealthvault.core.KoinConst
import com.wealthvault.domain.auth.SessionTokenStore
import com.wealthvault.setup_api.HttpClientBuilder
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

object GlobalApiModule {
    val allModules = module {

        // Public endpoints (login, registration, recovery, and refresh) use a
        // separate client so they can never accidentally inherit a stale
        // Authorization header or the authenticated 401 interceptor.
        single<HttpClient>(named(KoinConst.HttpClient.PUBLIC)) {
            HttpClientBuilder(
                json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
            ).build(withAuth = false)
        }

        // The authenticated client remains a singleton and owns the
        // single-flight refresh interceptor. GLOBAL is an alias for this
        // qualifier, so compatibility API adapters keep their existing Koin
        // wiring while auth APIs move to PUBLIC.
        single<HttpClient>(named(KoinConst.HttpClient.AUTHENTICATED)) {
            HttpClientBuilder(
                json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
                tokenStore = get<SessionTokenStore>(),
            ).build(withAuth = true)
        }
    }
}
