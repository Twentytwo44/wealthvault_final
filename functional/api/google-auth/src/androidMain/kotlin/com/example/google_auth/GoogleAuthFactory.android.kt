package com.example.google_auth

import android.content.Context

actual class GoogleAuthFactory(
    private val context: Context
) {
    actual fun create(): GoogleAuth {
        return GoogleAuthAndroid(context)
    }
}
