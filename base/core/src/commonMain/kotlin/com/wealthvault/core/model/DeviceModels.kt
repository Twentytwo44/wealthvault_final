package com.wealthvault.core.model

/**
 * Transport-neutral result shared by session/device registration adapters.
 *
 * Device registration is initiated by the authenticated session but the
 * endpoint is also used by notification settings. Keeping this small result
 * in core:model avoids coupling either bounded context to the other.
 */
data class DeviceMutationResult(
    val message: String? = null,
    val success: Boolean? = null,
)
