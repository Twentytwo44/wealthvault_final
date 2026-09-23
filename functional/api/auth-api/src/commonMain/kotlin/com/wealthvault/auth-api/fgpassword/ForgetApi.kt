package com.wealthvault.`auth-api`.fgpassword

import com.wealthvault.domain.auth.PasswordActionResult

interface ForgetApi {
    suspend fun forgetpassword(email: String): PasswordActionResult
}
