package com.wealthvault.data.auth.wire

import com.wealthvault.data.auth.transport.model.DeviceMutationResponse
import com.wealthvault.domain.notification.DeviceMutationResult

internal fun DeviceMutationResponse.toDomain() = DeviceMutationResult(
    message = message,
    success = success,
)
