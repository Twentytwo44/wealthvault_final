package com.wealthvault.financiallist.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.AttachmentType
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberFilePicker(onResult: (List<Attachment>) -> Unit): FilePickerLauncher {
    val imageDelegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)
                val result = didFinishPicking.filterIsInstance<PHPickerResult>().firstOrNull()
                val provider = result?.itemProvider
                if (provider != null && provider.hasItemConformingToTypeIdentifier("public.image")) {
                    provider.loadFileRepresentationForTypeIdentifier("public.image") { url, _ ->
                        if (url != null) {
                            val bytes = NSData.dataWithContentsOfURL(url)?.toByteArray()
                            onResult(listOf(Attachment(url.lastPathComponent ?: "image.jpg", AttachmentType.IMAGE, bytes)))
                        }
                    }
                }
            }
        }
    }
    val pdfDelegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                val url = didPickDocumentsAtURLs.filterIsInstance<NSURL>().firstOrNull()
                if (url != null) {
                    val bytes = NSData.dataWithContentsOfURL(url)?.toByteArray()
                    onResult(listOf(Attachment(url.lastPathComponent ?: "document.pdf", AttachmentType.PDF, bytes)))
                }
            }
        }
    }
    return remember {
        object : FilePickerLauncher {
            override fun launchImage() {
                val config = PHPickerConfiguration()
                config.selectionLimit = 1
                config.filter = PHPickerFilter.imagesFilter
                val picker = PHPickerViewController(config)
                picker.delegate = imageDelegate
                UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(picker, true, null)
            }

            override fun launchPdf() {
                val picker = UIDocumentPickerViewController(
                    documentTypes = listOf("com.adobe.pdf"),
                    inMode = UIDocumentPickerMode.UIDocumentPickerModeImport,
                )
                picker.delegate = pdfDelegate
                UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(picker, true, null)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val bytes = ByteArray(length.toInt())
    if (bytes.isNotEmpty()) memcpy(bytes.refTo(0), this.bytes, length)
    return bytes
}
