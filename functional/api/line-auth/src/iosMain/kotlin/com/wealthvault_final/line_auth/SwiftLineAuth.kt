package com.wealthvault.line_auth

import com.wealthvault.line_auth.model.LineUser

interface SwiftLineAuth {
    fun login(onSuccess: (LineUser) -> Unit, onError: (String) -> Unit)
}
