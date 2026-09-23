package com.wealthvault.core.security

import androidx.compose.runtime.Composable
import com.wealthvault.domain.profile.LineSignInProvider
import com.wealthvault.domain.profile.LineUser
import com.wealthvault.security.line.model.LineUser as PlatformLineUser
import com.wealthvault.security.line.rememberLineAuth

/**
 * Composition-safe platform adapter. Features depend on this capability
 * boundary instead of importing the legacy provider package directly.
 */
@Composable
fun rememberLineSignInProvider(
    onSuccess: (LineUser) -> Unit,
    onError: (String) -> Unit,
): LineSignInProvider = rememberLineAuth(
    onSuccess = { platformUser: PlatformLineUser ->
        onSuccess(
            LineUser(
                userId = platformUser.userId,
                displayName = platformUser.displayName,
                pictureUrl = platformUser.pictureUrl,
                accessToken = platformUser.accessToken,
                statusMessage = platformUser.statusMessage,
                idToken = platformUser.idToken,
            ),
        )
    },
    onError = onError,
)
