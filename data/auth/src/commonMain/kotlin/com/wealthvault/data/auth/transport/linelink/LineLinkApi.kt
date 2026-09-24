package com.wealthvault.data.auth.transport.linelink

import com.wealthvault.domain.auth.ProviderLinkResult

internal interface LineLinkApi {
    suspend fun link(token: String): ProviderLinkResult
}
