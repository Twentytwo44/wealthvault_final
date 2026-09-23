package com.wealthvault.`auth-api`.linelink

import com.wealthvault.domain.auth.ProviderLinkResult

interface LineLinkApi {
    suspend fun link(token: String): ProviderLinkResult
}
