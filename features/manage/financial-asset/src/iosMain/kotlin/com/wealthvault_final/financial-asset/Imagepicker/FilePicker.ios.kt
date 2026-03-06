package com.wealthvault_final.`financial-asset`.Imagepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
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
@Composable
actual fun rememberFilePicker(
    onResult: (List<Attachment>) -> Unit
): FilePickerLauncher {

    // 1. Delegate สำหรับจัดการการเลือกรูปภาพ (PHPicker)
    val imageDelegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                // ปิดหน้าต่างเลือกรูปทันทีที่เลือกเสร็จ
                picker.dismissViewControllerAnimated(true, null)

                val results = didFinishPicking.filterIsInstance<PHPickerResult>()
                if (results.isNotEmpty()) {
                    // ดึงผลลัพธ์แรกมาใช้งาน
                    val result = results.first()

                    // หมายเหตุ: ใน iOS การดึง URL จริงจาก PHPicker จะต้องโหลดผ่าน itemProvider
                    // เบื้องต้นเพื่อให้ UI อัปเดตและมีข้อมูล เราจะส่งชื่อจำลองหรือ Identifier กลับไปก่อน
                    val fileIdentifier = result.assetIdentifier ?: "image_${results.hashCode()}"

                    // สร้าง Attachment (สมมติว่ารับค่าเป็น uri/name และ type แบบเดียวกับ Android)
                    onResult(listOf(Attachment(fileIdentifier, AttachmentType.IMAGE)))
                }
            }
        }
    }

    // 2. Delegate สำหรับจัดการการเลือกไฟล์ PDF (UIDocumentPicker)
    val pdfDelegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                val urls = didPickDocumentsAtURLs.filterIsInstance<NSURL>()
                if (urls.isNotEmpty()) {
                    val url = urls.first()
                    // ดึงชื่อไฟล์จาก URL (เช่น my_report.pdf)
                    val fileName = url.lastPathComponent ?: "document.pdf"

                    // ส่งข้อมูลกลับไปที่ Compose
                    onResult(listOf(Attachment(fileName, AttachmentType.PDF)))
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
