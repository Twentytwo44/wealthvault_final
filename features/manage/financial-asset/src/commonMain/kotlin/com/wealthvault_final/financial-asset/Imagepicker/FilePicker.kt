package com.wealthvault_final.`financial-asset`.Imagepicker

import androidx.compose.runtime.Composable

interface FilePickerLauncher {
    fun launchImage()
    fun launchPdf()
}

@Composable
expect fun rememberFilePicker(
    onResult: (List<Attachment>) -> Unit
): FilePickerLauncher
