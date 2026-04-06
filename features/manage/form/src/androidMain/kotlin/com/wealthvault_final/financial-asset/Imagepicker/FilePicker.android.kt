package com.wealthvault_final.`financial-asset`.Imagepicker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberFilePicker(
    onResult: (List<Attachment>) -> Unit
): FilePickerLauncher {

    // ✅ 1. ดึง Context ของ Android มาใช้งานตรงนี้เลย
    val context = LocalContext.current

    val imageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // ✅ 2. อ่านไฟล์เป็น ByteArray ทันที
            val bytes = context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }

            onResult(listOf(
                Attachment(
                    name = it.lastPathSegment ?: "image.jpg",
                    type = AttachmentType.IMAGE,
                    platformData = bytes // ✅ เก็บ ByteArray ลงไปเลย ไม่ใช่ String
                )
            ))
        }
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // ✅ 2. อ่านไฟล์ PDF เป็น ByteArray ทันที
            val bytes = context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }

            onResult(listOf(
                Attachment(
                    name = it.lastPathSegment ?: "document.pdf",
                    type = AttachmentType.PDF,
                    platformData = bytes // ✅ เก็บ ByteArray
                )
            ))
        }
    }

    return object : FilePickerLauncher {
        override fun launchImage() = imageLauncher.launch("image/*")
        override fun launchPdf() = pdfLauncher.launch("application/pdf")
    }
}
