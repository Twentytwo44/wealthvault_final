package com.wealthvault.line_auth.model

/**
 * Objective-C/Swift-friendly transport value for the platform LINE bridge.
 *
 * The platform adapter maps this value to the domain `LineUser` at the
 * security boundary. Keeping a real class here (instead of a typealias)
 * ensures Kotlin/Native emits the callback type in the generated framework.
 */
data class LineUser(
    val userId: String,
    val displayName: String,
    val pictureUrl: String? = null,
    val accessToken: String? = null,
    val statusMessage: String? = null,
    val idToken: String? = null,
)
