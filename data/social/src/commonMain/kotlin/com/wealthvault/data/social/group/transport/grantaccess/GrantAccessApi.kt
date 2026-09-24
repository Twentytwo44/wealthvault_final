package com.wealthvault.data.social.group.transport.grantaccess

import com.wealthvault.domain.social.GrantAccess

interface GrantAccessApi {
    suspend fun grantAccess(
        id: String,
        request: GrantAccess
    ): Boolean
}
