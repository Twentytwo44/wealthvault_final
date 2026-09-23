package com.wealthvault.financiallist.ui.form

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.AttachmentType

@Composable
actual fun rememberFilePicker(onResult: (List<Attachment>) -> Unit): FilePickerLauncher {
    val context = LocalContext.current
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }
            onResult(listOf(Attachment(it.lastPathSegment ?: "image.jpg", AttachmentType.IMAGE, bytes)))
        }
    }
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }
            onResult(listOf(Attachment(it.lastPathSegment ?: "document.pdf", AttachmentType.PDF, bytes)))
        }
    }
    return object : FilePickerLauncher {
        override fun launchImage() = imageLauncher.launch("image/*")
        override fun launchPdf() = pdfLauncher.launch("application/pdf")
    }
}
