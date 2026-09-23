package com.wealthvault.data.auth.transport.fgpassword

import com.wealthvault.domain.auth.PasswordActionResult

interface ForgetApi {
    suspend fun forgetpassword(email: String): PasswordActionResult
}
