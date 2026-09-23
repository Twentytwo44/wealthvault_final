package com.wealthvault.financiallist.ui.form

import androidx.compose.runtime.Composable
import com.wealthvault.core.model.Attachment

interface FilePickerLauncher {
    fun launchImage()
    fun launchPdf()
}

@Composable
expect fun rememberFilePicker(onResult: (List<Attachment>) -> Unit): FilePickerLauncher
