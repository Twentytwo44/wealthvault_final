package com.wealthvault_final.`financial-asset`.Imagepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
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

@Composable
actual fun rememberFilePicker(
    onResult: (List<Attachment>) -> Unit
): FilePickerLauncher {

    // 1. Delegate สำหรับจัดการการเลือกรูปภาพ (PHPicker)
    val imageDelegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)

                val results = didFinishPicking.filterIsInstance<PHPickerResult>()
                val itemProvider = results.firstOrNull()?.itemProvider

                if (itemProvider != null && itemProvider.hasItemConformingToTypeIdentifier("public.image")) {
                    itemProvider.loadFileRepresentationForTypeIdentifier("public.image") { url, error ->
                        if (url != null) {
                            // ✅ อ่านข้อมูลเป็น NSData แล้วแปลงเป็น ByteArray ทันที
                            val nsData = platform.Foundation.NSData.dataWithContentsOfURL(url)
                            val byteArray = nsData?.toByteArray()

                            onResult(listOf(
                                Attachment(
                                    name = url.lastPathComponent ?: "image.jpg",
                                    type = AttachmentType.IMAGE,
                                    platformData = byteArray // ✅ เก็บ ByteArray
                                )
                            ))
                        }
                    }
                }
            }
        }
    }

    val pdfDelegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                val urls = didPickDocumentsAtURLs.filterIsInstance<NSURL>()
                val url = urls.firstOrNull()

                if (url != null) {
                    // ✅ อ่านไฟล์ PDF ทันที
                    val nsData = platform.Foundation.NSData.dataWithContentsOfURL(url)
                    val byteArray = nsData?.toByteArray()

                    onResult(listOf(
                        Attachment(
                            name = url.lastPathComponent ?: "document.pdf",
                            type = AttachmentType.PDF,
                            platformData = byteArray // ✅ เก็บ ByteArray
                        )
                    ))
                }
            }
        }
    }

    // 3. คืนค่า FilePickerLauncher
    return remember {
        object : FilePickerLauncher {
            override fun launchImage() {
                // ตั้งค่า Picker ให้เลือกได้เฉพาะรูปภาพ และเลือกได้ 1 รูป
                val config = PHPickerConfiguration()
                config.selectionLimit = 1
                config.filter = PHPickerFilter.imagesFilter

                val picker = PHPickerViewController(config)
                picker.delegate = imageDelegate // ผูก Delegate ที่เราสร้างไว้

                // หาหน้าต่างหลัก (Root View Controller) ของแอปเพื่อสั่งเปิด Popup
                val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
                rootVC?.presentViewController(picker, true, null)
            }

            override fun launchPdf() {
                // ตั้งค่า Picker ให้มองเห็นเฉพาะไฟล์ PDF
                val picker = UIDocumentPickerViewController(
                    documentTypes = listOf("com.adobe.pdf"),
                    inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
                )
                picker.delegate = pdfDelegate // ผูก Delegate

                val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
                rootVC?.presentViewController(picker, true, null)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
fun platform.Foundation.NSData.toByteArray(): ByteArray {
    val bytes = ByteArray(this.length.toInt())
    memcpy(bytes.refTo(0), this.bytes, this.length)
    return bytes
}
