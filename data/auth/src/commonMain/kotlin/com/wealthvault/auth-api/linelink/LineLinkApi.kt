package com.wealthvault.data.auth.transport.linelink

import com.wealthvault.domain.auth.ProviderLinkResult

interface LineLinkApi {
    suspend fun link(token: String): ProviderLinkResult
}
