package com.wealthvault.network.di

import com.wealthvault.core.KoinConst
import com.wealthvault.domain.auth.SessionTokenStore
import com.wealthvault.network.HttpClientBuilder
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

object GlobalApiModule {
    val allModules = module {

        // One process-wide serializer keeps transport adapters and cache
        // mappers on the same decoding policy.  Previously every legacy
        // endpoint module registered the same qualified Json singleton,
        // making the active definition depend on module ordering.
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                isLenient = true
            }
        }

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
