package com.wealthvault.core.model

/**
 * A platform-neutral attachment selected by the user or returned by the API.
 *
 * The optional platformData value is intentionally opaque to domain code.  It
 * is populated by the platform file picker and converted to upload bytes at
 * the data boundary.
 */
enum class AttachmentType { IMAGE, PDF }

data class Attachment(
    val name: String,
    val type: AttachmentType,
    val platformData: Any? = null,
    val id: String? = null,
)
