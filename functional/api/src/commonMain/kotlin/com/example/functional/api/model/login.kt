package com.example.functional.api.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames


@kotlinx.serialization.Serializable
data class NetworkCoinResponse(
    @JsonNames("coins")
    var coins: List<NetworkCoin>? = null,
) {

    @Serializable
    data class NetworkCoin(
        @JsonNames("uuid")
        var uuid: String? = null,
        @JsonNames("symbol")
        var symbol: String? = null,
        @JsonNames("name")
        var name: String? = null,
        @JsonNames("price")
        var price: String? = null,
    )

}