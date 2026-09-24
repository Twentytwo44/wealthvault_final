package com.wealthvault.data.auth.wire

import com.wealthvault.data.auth.transport.model.DeviceMutationResponse
import com.wealthvault.core.model.DeviceMutationResult

internal fun DeviceMutationResponse.toDomain() = DeviceMutationResult(
    message = message,
    success = success,
)
