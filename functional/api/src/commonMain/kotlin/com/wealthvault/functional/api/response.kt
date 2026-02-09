package com.wealthvault.functional.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Response<T>(
    @JsonNames("data")
    var data: T? = null,
)
