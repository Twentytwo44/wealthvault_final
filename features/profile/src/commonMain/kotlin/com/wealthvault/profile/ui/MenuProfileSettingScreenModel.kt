package com.wealthvault.profile.ui

import LineRepositoryImpl
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.`auth-api`.model.TokenRequest
import com.wealthvault.data_store.TokenStore
import com.wealthvault.notification_api.model.UnDeviceRequest
import com.wealthvault.profile.data.device.UnRegisterDeviceRepositoryImpl
import com.wealthvault_final.line_auth.LineAuth
import com.wealthvault_final.line_auth.model.LineUser
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MenuProfileSettingScreenModel(
    private val unRegisterDeviceRepository: UnRegisterDeviceRepositoryImpl,
    private val tokenStore: TokenStore,
    private val lineRepositoryImpl: LineRepositoryImpl
) : ScreenModel {


    fun onLineClick(lineAuth: LineAuth) {
        println("🚀 [LoginScreenModel] onLineClick triggered")

        // สั่งให้ตัวจัดการ LINE ที่หน้าจอส่งมา เริ่มทำงาน
        lineAuth.login()
    }
    fun onLineSuccess(user: LineUser, onSuccess: () -> Unit) {
        println("🎉 [LoginScreenModel] LINE Success: ${user.displayName} (${user.userId})")

        // ✅ 1. เปิด Coroutine Scope ตรงนี้ เพื่ออนุญาตให้เรียกใช้ suspend function ด้านในได้
        screenModelScope.launch {

            // สมมติว่ามีตัวแปรจัดการ UI (ถ้ามีก็ปลดคอมเมนต์ออกได้เลยครับ)
            // isLoading = true
            // errorMessage = null

            try {
                val request = TokenRequest(
                    token = user.idToken ?: "" // หรืออาจจะใช้ user.accessToken ขึ้นอยู่กับที่ Backend กำหนด
                )

                // ✅ 2. ตอนนี้บรรทัดนี้จะไม่แจ้ง Error แล้วครับ เพราะมันอยู่ใน launch
                val response = lineRepositoryImpl.link(request)

                if (response.isSuccess) {
                    println("✅ เชื่อมต่อ LINE กับ Backend สำเร็จ!")
                    onSuccess()
                } else {
                    // แนะนำ: กรณีใช้ Result<T> สามารถดึง Error ออกมาดูได้แบบนี้ครับ
                    val errorMsg = response.exceptionOrNull()?.message
                    println("❌ Backend แจ้งว่าไม่สำเร็จ: $errorMsg")
                    // errorMessage = errorMsg
                }

            } catch (e: Exception) {
                println("❌ เกิดข้อผิดพลาดในการเชื่อมต่อ Backend: ${e.message}")
                e.printStackTrace()
                // errorMessage = "การเชื่อมต่อเซิร์ฟเวอร์ล้มเหลว: ${e.message}"

            } finally {
                // ปิด Loading เสมอ ไม่ว่าจะสำเร็จหรือพัง
                // isLoading = false
            }
        } // <-- อย่าลืมปีกกาปิดของ launch ตรงนี้นะครับ
    }    // 🟢 3. รับผลลัพธ์กลับมาเมื่อพัง หรือกดยกเลิก
    fun onLineError(error: String) {
        println("❌ [LoginScreenModel] LINE Error: $error")

   }

    fun unRegisterDevice(onLogoutComplete: () -> Unit) {
        screenModelScope.launch {
            try {
                val token = tokenStore.fcmToken.firstOrNull()

                // 1. ดึงค่าก่อนลบมาดู (จดใส่กระดาษโน้ตใบที่ 1)
                val accBefore = tokenStore.accessToken.firstOrNull()
                println("token before delete: $accBefore")

                val request = UnDeviceRequest(token = token.toString())
                unRegisterDeviceRepository.unDevice(request) // รอจนกว่าจะยิง API เสร็จ

                // 2. เผาสมุดทิ้ง (ลบ DataStore)
                tokenStore.clear()

                // 🚨 3. พิสูจน์ด้วยการไปค้นในสมุดเล่มเดิมใหม่อีกครั้ง (จดใส่กระดาษโน้ตใบที่ 2)
                val accAfter = tokenStore.accessToken.firstOrNull()
                println("token after delete: $accAfter") // 👈 ตรงนี้ Log จะปริ้นออกมาเป็น "null" แน่นอนครับ!

                println("✅ [UnregisterDevice] ยกเลิกการเชื่อมต่ออุปกรณ์สำเร็จ!")

            } catch (e: Exception) {
                println("❌ [UnregisterDevice] ยกเลิกอุปกรณ์ไม่สำเร็จ: ${e.message}")
            } finally {
                onLogoutComplete()
            }
        }
    }


}
