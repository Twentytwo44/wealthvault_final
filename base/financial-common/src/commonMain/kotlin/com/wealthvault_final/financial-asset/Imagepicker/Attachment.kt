package com.wealthvault.`financial-asset`.Imagepicker

import com.wealthvault.core.model.FileDataModel

/** Compatibility aliases for the legacy financial-common package. */
typealias AttachmentType = com.wealthvault.core.model.AttachmentType
typealias Attachment = com.wealthvault.core.model.Attachment


fun FileDataModel.toAttachment(): Attachment {
    val detectedType = when {
        fileType.startsWith("image/", ignoreCase = true) -> AttachmentType.IMAGE
        fileType.contains("pdf", ignoreCase = true) -> AttachmentType.PDF
        else -> AttachmentType.IMAGE
    }

    return Attachment(
        // ใช้ url ทั้งหมดเป็น name ตามที่คุณต้องการ
        name = this.url,
        type = detectedType,
        platformData = this,
        id = this.id
    )
}
