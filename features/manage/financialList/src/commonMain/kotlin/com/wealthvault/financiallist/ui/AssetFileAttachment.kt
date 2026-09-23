package com.wealthvault.financiallist.ui

import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.AttachmentType

fun AssetFile.toAttachment(): Attachment {
    val detectedType = when {
        fileType.startsWith("image/", ignoreCase = true) -> AttachmentType.IMAGE
        fileType.contains("pdf", ignoreCase = true) -> AttachmentType.PDF
        else -> AttachmentType.IMAGE
    }
    return Attachment(name = url, type = detectedType, platformData = this, id = id)
}
