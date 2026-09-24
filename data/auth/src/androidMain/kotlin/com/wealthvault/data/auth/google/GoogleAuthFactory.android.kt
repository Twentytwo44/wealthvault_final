package com.wealthvault.data.auth.google

import android.content.Context

internal actual class GoogleAuthFactory(
    private val context: Context
) {
    actual fun create(): GoogleAuth {
        return GoogleAuthAndroid(context)
    }
}
