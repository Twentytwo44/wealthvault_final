package com.wealthvault_final.`financial-asset`.Imagepicker

enum class AttachmentType { IMAGE, PDF }

data class Attachment(
    val name: String,
    val type: AttachmentType,
    val platformData: Any? = null // เก็บ Uri (Android) หรือ PHAsset (iOS)
)
