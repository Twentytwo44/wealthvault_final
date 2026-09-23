package com.wealthvault.group_api.grantaccess

import com.wealthvault.domain.social.GrantAccess

interface GrantAccessApi {
    suspend fun grantAccess(
        id: String,
        request: GrantAccess
    ): Boolean
}
