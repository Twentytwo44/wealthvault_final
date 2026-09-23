package com.wealthvault.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.wealthvault.domain.profile.LineSignInProvider
import com.wealthvault.domain.profile.LineUser

/**
 * Composition-root capability for platform LINE sign-in.
 *
 * The profile feature owns the contract, but never the platform adapter. This
 * keeps Android ActivityResult and iOS Swift callbacks outside presentation
 * while preserving the existing callback-based screen flow.
 */
fun interface LineSignInProviderFactory {
    @Composable
    fun rememberProvider(
        onSuccess: (LineUser) -> Unit,
        onError: (String) -> Unit,
    ): LineSignInProvider
}

val LocalLineSignInProviderFactory = staticCompositionLocalOf<LineSignInProviderFactory> {
    error("LineSignInProviderFactory must be provided by the composition root")
}
