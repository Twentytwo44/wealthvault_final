package com.wealthvault.security.line

import androidx.compose.runtime.Composable
import com.wealthvault.security.line.model.LineUser

@Composable
expect fun rememberLineAuth(
    onSuccess: (LineUser) -> Unit,
    onError: (String) -> Unit
): LineAuth
