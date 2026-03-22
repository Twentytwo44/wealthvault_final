package com.wealthvault_final.line_auth

import com.wealthvault_final.line_auth.model.LineUser

interface SwiftLineAuth {
    fun login(onSuccess: (LineUser) -> Unit, onError: (String) -> Unit)
}
