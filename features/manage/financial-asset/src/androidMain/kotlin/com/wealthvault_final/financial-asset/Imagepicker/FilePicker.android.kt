package com.wealthvault_final.`financial-asset`.Imagepicker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
actual fun rememberFilePicker(
    onResult: (List<Attachment>) -> Unit
): FilePickerLauncher {

    val imageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onResult(listOf(Attachment(it.toString(), AttachmentType.IMAGE)))
        }
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onResult(listOf(Attachment(it.toString(), AttachmentType.PDF)))
        }
    }

    return object : FilePickerLauncher {
        override fun launchImage() {
            imageLauncher.launch("image/*")
        }

        override fun launchPdf() {
            pdfLauncher.launch("application/pdf")
        }
    }
}
