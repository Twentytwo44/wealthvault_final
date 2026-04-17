package com.wealthvault_final.`financial-asset`.Imagepicker

import com.wealthvault.core.model.FileDataModel

enum class AttachmentType { IMAGE, PDF }

data class Attachment(
    val name: String,
    val type: AttachmentType,
    val platformData: Any? = null,
    val id: String? = null
)


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
