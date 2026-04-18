package com.wealthvault.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileDataModel(
    @SerialName("id") val id: String = "",
    @SerialName("url") override val url: String = "",
    @SerialName("file_type") override val fileType: String = ""
) : HasImageUrl
