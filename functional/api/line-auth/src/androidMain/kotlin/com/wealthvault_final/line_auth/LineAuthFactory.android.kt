package com.wealthvault_final.line_auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.auth.LineLoginApi
import com.wealthvault_final.line_auth.model.LineUser

@Composable
actual fun rememberLineAuth(
    onSuccess: (LineUser) -> Unit,
    onError: (String) -> Unit
): LineAuth {
    val context = LocalContext.current

    // ตัวรับผลลัพธ์พอกลับมาจากหน้า LINE
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val lineResult = LineLoginApi.getLoginResultFromIntent(result.data)
        when (lineResult.responseCode) {
            LineApiResponseCode.SUCCESS -> {
                val profile = lineResult.lineProfile
                val credential = lineResult.lineCredential // 👈 ตัวเก็บ Access Token
                val idToken = lineResult.lineIdToken // 👈 ตัวเก็บ ID Token (ถ้าขอสิทธิ์ OPENID_CONNECT ไว้)

                // 🟢 สั่ง Log ออกมาดูแบบจัดเต็ม
                println("=========================================")
                println("🟢 [LINE SDK] Login Success!")
                println("🟢 ID: ${profile?.userId}")
                println("🟢 Name: ${profile?.displayName}")
                println("🟢 Access Token: ${credential?.accessToken?.tokenString}")
                println("🟢 ID Token: ${idToken?.rawString}")
                println("=========================================")

                // จับใส่ก้อน LineUser
                val user = LineUser(
                    userId = profile?.userId ?: "",
                    displayName = profile?.displayName ?: "",
                    pictureUrl = profile?.pictureUrl?.toString(),
                    accessToken = credential?.accessToken?.tokenString,
                    idToken = idToken?.rawString
                )
                onSuccess(user)
            }
            LineApiResponseCode.CANCEL -> onError("ผู้ใช้ยกเลิกการเข้าสู่ระบบ")
            else -> onError(lineResult.errorData.message ?: "เกิดข้อผิดพลาดในการเข้าสู่ระบบ")
        }
    }

    // สร้างคลาสและผูก Launcher เข้าไปด้วยกัน
    return remember {
        LineAuthAndroid(context) { intent -> launcher.launch(intent) }
    }
}
