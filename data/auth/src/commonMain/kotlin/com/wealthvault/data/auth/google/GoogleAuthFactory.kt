package com.wealthvault.data.auth.google

internal expect class GoogleAuthFactory {
    fun create(): GoogleAuth
}
