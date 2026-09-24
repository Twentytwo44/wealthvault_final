package com.wealthvault.data.auth.google

import com.wealthvault.domain.auth.GoogleIdentity

internal interface GoogleAuth {
    /**
     * Provider SDK details stay in the platform adapter.  The data layer
     * exposes only the domain identity required by the backend login flow.
     */
    suspend fun signIn(): GoogleIdentity?
}
