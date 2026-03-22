package com.wealthvault_final.line_auth

import androidx.compose.runtime.Composable
import com.wealthvault_final.line_auth.model.LineUser

@Composable
expect fun rememberLineAuth(
    onSuccess: (LineUser) -> Unit,
    onError: (String) -> Unit
): LineAuth
