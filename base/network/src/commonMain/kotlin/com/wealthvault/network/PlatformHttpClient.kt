package com.wealthvault.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

/**
 * Creates the platform-native Ktor engine for shared networking code.
 *
 * Keeping engine selection behind an expect/actual boundary prevents common
 * code from accidentally forcing CIO on iOS. The request pipeline remains
 * shared; only the platform transport is selected here.
 */
internal expect fun platformHttpClient(
    config: HttpClientConfig<*>.() -> Unit,
): HttpClient
