package com.wealthvault.data.auth.transport.rspassword

import com.wealthvault.domain.auth.PasswordActionResult

internal interface ResetApi {
    suspend fun reset(resetToken: String, password: String): PasswordActionResult
}
