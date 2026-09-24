package com.wealthvault.security.line

import com.wealthvault.security.line.model.LineUser

interface SwiftLineAuth {
    fun login(onSuccess: (LineUser) -> Unit, onError: (String) -> Unit)
}
